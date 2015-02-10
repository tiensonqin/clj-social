(ns clj-social.core
  (:require [clojure.data.json :refer [read-str]])
  (:import [org.scribe.builder ServiceBuilder]
           [org.scribe.model Verifier OAuthRequest Verb]
           [org.scribe.model OAuthRequest]
           [org.scribe.model Verb]))

(defn- spec
  [type]
  (let [ns (symbol (str "clj-social." (name type)))]
    (require ns)
    (ns-resolve ns (symbol "spec"))))

(defn- build-service
  [{:keys [type app-key app-secret callback-uri]}]
  (.. (ServiceBuilder.)
      (provider ((spec type) :api))
      (apiKey app-key)
      (apiSecret app-secret)
      (callback callback-uri)
      build))

(defn- get-body
  [service access-token id-url]
  (let [request (OAuthRequest. Verb/GET id-url)
        _ (.signRequest service access-token request)
        response (.send request)]
    (.getBody response)))

(defmacro wrap-service
  [social & body]
  `(-> ~social
       build-service
       ~@body))

(defn- build-params
  [{:keys [type app-key]} replaces]
  (let [template ((spec type) :params)
        replaces (assoc replaces :app-key app-key)]
    (zipmap (map name (keys template)) (map replaces (vals template)))))

(defprotocol ISocial
  (getAuthorizationUrl [_])
  (getAccessToken [_ verifier])
  (getId [_ access-token])
  (getAccessTokenAndId [_ verifier])
  (getUserInfo [_ access-token id]))

(defrecord Social [type app-key app-secret callback-uri]
  ISocial
  (getAuthorizationUrl [this]
    (wrap-service this (.getAuthorizationUrl ((spec type) :request-token))))
  (getAccessToken [this verifier]
    (wrap-service this (.getAccessToken ((spec type) :request-token)
                         (Verifier. verifier))))
  (getId [this access-token]
    (wrap-service this
                  (get-body access-token ((spec type) :id-url))
                  (((spec type) :id-fn))))
  (getAccessTokenAndId [this verifier]
    (if (= :wechat (keyword type))
      (let [token (.getAccessToken this verifier)]
        ;; bind openid to secret
        [(.getToken token) (str (.getSecret token))])
      (let [access-token (getAccessToken this verifier)
            id (getId this access-token)]
        [(.getToken access-token) (str id)])))
  (getUserInfo [this access-token id]
    (let [params (build-params this {:access-token access-token :id id})
          req (OAuthRequest. Verb/GET ((spec type) :user-url))
          _ (doseq [[k v] params]
              (.addQuerystringParameter req k v))]
      (-> (.send req)
          (.getBody)
          read-str))))
