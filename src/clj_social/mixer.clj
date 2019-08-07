(ns clj-social.mixer
  (:import [com.tiensonqin.Mixer MixerApi]))

(def spec {:user-url "https://mixer.com/api/v1/users/current"
           :api (MixerApi/instance)
           :params {"access_token" :access-token}})
