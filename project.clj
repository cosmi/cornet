(defproject cornet "0.1.0-SNAPSHOT"
  :description "A simple Clojure asset pipeline for LESS, CoffeeScript, Uglify and others. "
  :url "https://github.com/cosmi/cornet"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.1"]
                 [org.marianoguerra/clj-rhino "0.2.1"]
                 [org.clojure/core.cache "0.6.3"]

                 [ro.isdc.wro4j/wro4j-extensions "1.7.1"
                  :exclusions
                  [
                   [org.jruby/jruby-core]
                   [com.github.lltyk/dojo-shrinksafe]
                   [com.github.sommeri/less4j ]
                   [com.google.code.gson/gson ]
                   [com.google.javascript/closure-compiler]
                                        ;[commons-io]
                                        ;[commons-pool ]
                   [nz.co.edmi/bourbon-gem-jar]
                   [me.n4u.sass/sass-gems ]
                                        ;[org.apache.commons/commons-lang3]
                   [org.codehaus.gmaven.runtime/gmaven-runtime-1.7]
                   [org.jruby/jruby-complete]
                                        ;[org.slf4j/slf4j-api]
                   [org.springframework/spring-web]
                                        ;[org.webjars/coffee-script "1.6.2-1"]
                   [org.webjars/emberjs]
                   [org.webjars/handlebars]
                   [org.webjars/jshint]
                                        ;[org.webjars/jslint ]
                   [org.webjars/json2 ]
                   [org.webjars/less ]
                                        ;[org.webjars/webjars-locator ]
                                        ;[ro.isdc.wro4j/rhino]
                                        ;[ro.isdc.wro4j/wro4j-core ]
                   ]
                  ]
                 ])
