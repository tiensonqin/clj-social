(ns clj-social.twitch
  (:import [com.tiensonqin.Twitch TwitchApi]))

(def spec {:user-url "https://api.twitch.tv/kraken/user"
           :api (TwitchApi/instance)
           :params {"access_token" :access-token}})
