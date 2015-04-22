(ns clj-social.core
  (:require [clojure.data.json :refer [read-str]])
  (:import [org.scribe.builder ServiceBuilder]
           [org.scribe.model Verifier OAuthRequest Verb]
           [org.scribe.model OAuthRequest]
           [org.scribe.model Verb]
           [java.util UUID]))

(defn uuid
  "Generate uuid."
  []
  (str (UUID/randomUUID)))

(defn- spec
  [type]
  (let [ns (symbol (str "clj-social." (name type)))]
    (require ns)
    (ns-resolve ns (symbol "spec"))))

(defn- build-service
  [{:keys [type app-key app-secret callback-uri scope state]}]
  (let [builder (.. (ServiceBuilder.)
                    (provider ((spec type) :api))
                    (apiKey app-key)
                    (apiSecret app-secret)
                    (state (if state state (uuid)))
                    (callback callback-uri))
        builder (cond
                  (and (nil? scope) (= :wechat type))
                  (.scope builder "snsapi_login")
                  (nil? scope)
                  builder
                  :else
                  (.scope builder scope))]
    (.build builder)))

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
  (getUserInfo [_ params])
  (getAll [_ verifier]))

(defmacro defrecord+defaults
  "Defines a new record, along with a new-RecordName factory function that
   returns an instance of the record initialized with the default values
   provided as part of the record's slot declarations.  e.g.
   (defrecord+ Foo [a 5 b \"hi\"])
   (new-Foo)
   => #user.Foo{:a 5, :b \"hi\"}"
  [name slots & etc]
  (let [fields   (->> slots (partition 2) (map first) vec)
        defaults (->> slots (partition 2) (map second))]
    `(do
       (defrecord ~name
           ~fields
         ~@etc)
       (defn ~(symbol (str "new-" name))
         ~(str "A factory function returning a new instance of " name
               " initialized with the defaults specified in the corresponding defrecord+ form.")
         [& {:as kwargs#}]
         (-> (~(symbol (str name \.)) ~@defaults)
             (merge kwargs#)))
       ~name)))

(defrecord+defaults Social [type nil
                            app-key nil
                            app-secret nil
                            callback-url nil
                            scope nil
                            state nil]
  ISocial
  (getAuthorizationUrl [this]
    (wrap-service this (.getAuthorizationUrl ((spec type) :request-token))))
  (getAccessToken [this verifier]
    (wrap-service this (.getAccessToken ((spec type) :request-token)
                                        (Verifier. verifier))))
  (getId [this access-token]
    (let [type (keyword type)]
      (cond
        (= type :wechat)
        (str (.getSecret access-token))
        (contains? #{:qq :weibo} type)
        (-> this
           (wrap-service
            (get-body access-token ((spec type) :id-url))
            (((spec type) :id-fn)))
           str))))
  (getUserInfo
    [this params]
    (let [params (build-params this params)
          req (OAuthRequest. Verb/GET ((spec type) :user-url))
          _ (doseq [[k v] params]
              (.addQuerystringParameter req k v))]
      (-> (.send req)
          (.getBody)
          read-str)))
  (getAll
    [this verifier]
    (let [token (getAccessToken this verifier)
          id (getId this token)
          token (.getToken token)
          user-info (getUserInfo this {:access-token token
                                       :id id})]
      {:access-token token
       :id (case type
             :github (get user-info "login")
             id)
       :user-info user-info})))
