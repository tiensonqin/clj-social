(ns clj-social.twitter
  (:import [com.github.scribejava.apis TwitterApi]))

(def spec {:user-url "https://api.twitter.com/1.1/account/verify_credentials.json"
           :request-token true
           :api (TwitterApi/instance)
           :params {"access_token" :access-token}})
