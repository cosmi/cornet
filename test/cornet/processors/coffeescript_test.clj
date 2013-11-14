(ns cornet.processors.coffeescript-test
  (:require [clojure.java.io :as io])
  (:use [cornet.processors.coffeescript]
        [cornet utils]
        [clojure test]))

(defn test-file-loader [path]
  (let [file (io/as-file (str "test-resources/coffee/" path))]
    (when (.exists file)
      (io/as-url file))))



(deftest dev-compile-coffeescript-test
  (let [processor (wrap-coffee-script-processor test-file-loader :mode :dev)]
    (let [res (processor "example.js")
          res2 (processor "example.js")]
      (is (= res res2))
      (is (.endsWith (str res) ".js"))
      (is (= (slurp res) (slurp (test-file-loader "output.js"))))
      (-> "example.coffee" test-file-loader io/as-file (.setLastModified (+ 1000 (System/currentTimeMillis))))
      
      (let [res3 (processor "example.js")]
        (is (not= res res3))
        (-> res3 io/as-file .delete)
      
        (let [res4 (processor "example.js")]
          (is (not= res4 res3))
        ))
      )))

(deftest dev-compile-coffeescript-test
  (let [processor (wrap-coffee-script-processor test-file-loader :mode :prod)]
    (let [res (processor "example.js")
          res2 (processor "example.js")]
      (is (= res res2))
      (is (.endsWith (str res) ".js"))
      (is (= (slurp res) (slurp (test-file-loader "output.js"))))
      (-> "example.coffee" test-file-loader io/as-file (.setLastModified (+ 1000 (System/currentTimeMillis))))
      
      (let [res3 (processor "example.js")]
        (is (= res res3))
        (-> res3 io/as-file .delete)
      
        (let [res4 (processor "example.js")]
          (is (not= res4 res3))
        ))
      )))
