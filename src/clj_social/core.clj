(ns clj-social.core
  (:require [clojure.data.json :refer [read-str]])
  (:import [com.github.scribejava.core.builder ServiceBuilder]
           [com.github.scribejava.core.model OAuthRequest Verb]
           [com.github.scribejava.core.oauth OAuth20Service]
           [com.github.scribejava.apis FacebookApi]
           [com.github.scribejava.apis TwitterApi]
           [java.util UUID]))

(defn uuid
  []
  (str (UUID/randomUUID)))

(defn- add-params-to-request
  [request params]
  (doseq [key (keys params)] (.addParameter request key (params key))))

(defn- get-body
  [service access-token me-url & [params]]
  (let [request (OAuthRequest. Verb/GET me-url)
        _ (when params (add-params-to-request request params))
        _ (.signRequest service access-token request)
        response (.execute service request)]
    (-> (.getBody response)
        (read-str :key-fn keyword))))

(defn- spec
  [type]
  (let [ns (symbol (str "clj-social." (name type)))]
    (require ns)
    (ns-resolve ns (symbol "spec"))))

(defn- build-service
  [{:keys [type app-key app-secret callback-uri state scope]}]
  (let [builder (if state
                  (.. (ServiceBuilder. app-key)
                      (apiSecret app-secret)
                      (state state)
                      (callback callback-uri))
                  (.. (ServiceBuilder. app-key)
                      (apiSecret app-secret)
                      (callback callback-uri)))
        builder (cond
                  (and (nil? scope) (= :wechat type))
                  (.defaultScope builder "snsapi_login")
                  (nil? scope)
                  builder
                  :else
                  (.defaultScope builder scope))]
    (.build builder ((spec type) :api))))

(defprotocol ISocial
  (getAuthorizationUrl [_])
  (getAccessToken [_ code])
  (getRequestAccessToken [_ request-token verifier])
  (getUserInfo [_ access-token]
    [_ access-token params] ))

(defrecord Social [service type]
  ISocial
  (getAuthorizationUrl [this]
    (let [service-spec (spec type)]
      (if (service-spec :request-token)
        (let [request-token (.getRequestToken service)]
          {:request-token request-token
           :authorization-url (.getAuthorizationUrl service request-token)})
        (.getAuthorizationUrl service))))
  (getAccessToken [this code]
    (.getAccessToken service code))

  (getRequestAccessToken [this request-token verifier]
    (.getAccessToken service request-token verifier))

  (getUserInfo [this access-token]
    (get-body service access-token ((spec type) :user-url)))

  (getUserInfo [this access-token request-params]
    (get-body service access-token ((spec type) :user-url) request-params)))

(defn make-social
  [type app-key app-secret callback-uri & {:keys [scope state]}]
  (->Social (build-service {:type type
                            :app-key app-key
                            :app-secret app-secret
                            :callback-uri callback-uri
                            :scope scope
                            :state state})
            type))
