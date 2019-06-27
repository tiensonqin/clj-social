(ns clj-social.google
  (:import [com.github.scribejava.apis GoogleApi20]))

(def spec {:user-url "https://www.googleapis.com/oauth2/v3/userinfo"
           :api (GoogleApi20/instance)
           :params {"access_token" :access-token}})
