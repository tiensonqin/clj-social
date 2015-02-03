(ns clj-social.qq
  (:import QQApi20))

(def spec {:id-url "https://graph.qq.com/oauth2.0/me"
           :user-url "https://graph.qq.com/user/get_user_info"
           :request-token nil
           :api QQApi20
           :id-fn (fn [body] (second (re-find #"openid\":\"([a-z0-9A-Z]+)" body)))
           :params {"openid" :id
                    "access_token" :access-token
                    "oauth_consumer_key" :app-key}})
