# clj-social
A Clojure library to make social easy,
for now support qq, sinaweibo and wechat.

## Latest version ##
[![Clojars Project](http://clojars.org/clj-social/latest-version.svg)](http://clojars.org/clj-social)

## Usage

### QQ Example ###
```clj
(require '[clj-social.core :refer [new-Social]])

;; scope default to nil, state default to UUID
(def qq (new-Social :type :qq :app-key app-key :app-secret app-secret :callback-uri callback-uri))

(def custom-qq (new-Social :type :qq :app-key app-key :app-secret app-secret :callback-uri callback-uri :scope scope :state state))

;; get authorization url, then open it to get the callback code
(def url (.getAuthorizationUrl qq))

;; get access-token
(def access-token (.getAccessToken qq code))

;; get openid
(def id (.getId qq token))

;; get user info
(def user (.getUserInfo qq {:access-token token
                            :id id}))

;; get all at once
(def all (.getAll qq code))
```


## License

Copyright © 2015 tiensonqin@gmail.com

Distributed under the Eclipse Public License version 1.0.
