(ns clj-social.github
  (:require [clojure.data.json :refer [read-str]])
  (:import GithubApi))

(def spec {
           ;; :id-url ""
           :user-url "https://api.github.com/user"
           :request-token nil
           :api GithubApi
           ;; :id-fn (fn [body] (-> body read-str (get "uid")))
           :params {"access_token" :access-token}})
