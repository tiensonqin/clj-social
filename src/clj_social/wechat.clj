(ns clj-social.wechat
  (:require [clojure.data.json :refer [read-str]])
  (:import [org.scribe.builder ServiceBuilder]
           WechatApi
           [org.scribe.model Verifier OAuthRequest Verb]))

(def spec {:id-url "https://api.weixin.qq.com/sns/oauth2/access_token"
           :user-url "https://api.weixin.qq.com/sns/userinfo"
           :request-token nil
           :api WechatApi
           :params {"openid" :id
                    "access_token" :access-token}})
