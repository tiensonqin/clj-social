(ns clj-social.weibo
  (:require [clojure.data.json :refer [read-str]])
  (:import [org.scribe.builder.api SinaWeiboApi20]))

(def spec {:id-url "https://api.weibo.com/2/account/get_uid.json"
           :user-url "https://api.weibo.com/2/users/show.json"
           :request-token nil
           :api SinaWeiboApi20
           :id-fn (fn [body] (-> body read-str (get "uid")))
           :params {"uid" :id
                    "access_token" :access-token}})
