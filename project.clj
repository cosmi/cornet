(defproject cornet "0.1.0-SNAPSHOT"
  :description "A simple Clojure asset pipeline for LESS, CoffeeScript, Uglify and others. "
  :url "https://github.com/cosmi/cornet"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.1"]
                 [org.marianoguerra/clj-rhino "0.2.1"]
                 [org.clojure/core.cache "0.6.3"]])
