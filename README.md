# clj-social
A Clojure library to make social easy,
for now support facebook and twitter.

## Latest version ##
[![Clojars Project](http://clojars.org/clj-social/latest-version.svg)](http://clojars.org/clj-social)

## Usage

### QQ Example ###
```clj
(require '[clj-social.core :refer [make-social]])

;; scope default to nil, state default to UUID
(def fb (make-social :facebook app-key app-secret callback-uri :state state))

;; get authorization url, then open it to get the callback code
(def url (.getAuthorizationUrl fb))

;; get access-token
(def access-token (.getAccessToken fb code))

;; get user info
(def user (.getUserInfo fb access-token))
```


## License

Copyright © 2019 tiensonqin@gmail.com

Distributed under the Eclipse Public License version 1.0.
