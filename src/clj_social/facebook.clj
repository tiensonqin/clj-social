(ns clj-social.facebook
  (:import [com.github.scribejava.apis FacebookApi]))

(def spec {:user-url "https://graph.facebook.com/v2.8/me"
           :api (FacebookApi/instance)
           :params {"access_token" :access-token}})
