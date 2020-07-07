(ns clj-social.dropbox
  (:import [com.github.scribejava.apis DropboxApi]))

(def spec {:user-url "https://api.dropboxapi.com/2/users/get_current_account"
           :api (DropboxApi/instance)
           :params {"access_token" :access-token}})
