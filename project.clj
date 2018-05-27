(defproject clj-social "0.1.1"
  :description "A Clojure library to make social easy."
  :url "https://github.com/tiensonqin/clj-social"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.github.scribejava/scribejava-apis "4.2.0"]
                 [org.clojure/data.json "0.2.6"]]
  :aot :all
  :java-source-paths ["src/jvm"]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.8.0"]]}})
