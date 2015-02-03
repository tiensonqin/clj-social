(defproject clj-social "0.1.0-SNAPSHOT"
  :description "A Clojure library to make social easy."
  :url "https://github.com/tiensonqin/clj-social"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.scribe/scribe "1.3.6"]
                 [org.clojure/data.json "0.2.5"]]
  :aot :all
  :java-source-paths ["src/jvm"]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]]}})
