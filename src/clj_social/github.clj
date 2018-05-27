(ns clj-social.github
  (:import [com.github.scribejava.apis GitHubApi]))

(def spec {:user-url "https://api.github.com/user"
           :api (GitHubApi/instance)
           :params {"access_token" :access-token}})
