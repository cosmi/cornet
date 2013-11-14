(ns cornet.processors.minifiers-test
  (:require [clojure.java.io :as io])
  (:use [cornet.processors.minifiers]
        [cornet utils]
        [clojure test]))

(defn test-file-loader [path]
  (let [file (io/as-file (str "test-resources/minifiers/" path))]
    (when (.exists file)
      (io/as-url file))))



(deftest dev-yui-css-test
  (let [processor (wrap-yui-css-compressor test-file-loader :mode :dev)]
    (let [res (processor "example.css")
          res2 (processor "example.css")]
      (is (= res res2))
      (is (.endsWith (str res) ".css"))
      (is (= (slurp res) (slurp (test-file-loader "output.css"))))
      (-> "example.css" test-file-loader io/as-file (.setLastModified (+ 1000 (System/currentTimeMillis))))
      
      (let [res3 (processor "example.css")]
        (is (not= res res3))
        (-> res3 io/as-file .delete)
      
        (let [res4 (processor "example.css")]
          (is (not= res4 res3))
        ))
      )))



(deftest prod-yui-css-test
  (let [processor (wrap-yui-css-compressor test-file-loader :mode :prod)]
    (let [res (processor "example.css")
          res2 (processor "example.css")]
      (is (= res res2))
      (is (.endsWith (str res) ".css"))
      (is (= (slurp res) (slurp (test-file-loader "output.css"))))
      (-> "example.css" test-file-loader io/as-file (.setLastModified (+ 1000 (System/currentTimeMillis))))
      
      (let [res3 (processor "example.css")]
        (is (= res res3))
        (-> res3 io/as-file .delete)
      
        (let [res4 (processor "example.css")]
          (is (not= res4 res3))
        ))
      )))




(deftest dev-uglify-js-test
  (let [processor (wrap-uglify-js-compressor test-file-loader :mode :dev)]
    (let [res (processor "example.js")
          res2 (processor "example.js")]
      (is (= res res2))
      (is (.endsWith (str res) ".js"))
      (is (= (slurp res) (slurp (test-file-loader "output.js"))))
      (-> "example.js" test-file-loader io/as-file (.setLastModified (+ 1000 (System/currentTimeMillis))))
      
      (let [res3 (processor "example.js")]
        (is (not= res res3))
        (-> res3 io/as-file .delete)
      
        (let [res4 (processor "example.js")]
          (is (not= res4 res3))
        ))
      )))



(deftest prod-uglify-js-test
  (let [processor (wrap-uglify-js-compressor test-file-loader :mode :dev)]
    (let [res (processor "example.js")
          res2 (processor "example.js")]
      (is (= res res2))
      (is (.endsWith (str res) ".js"))
      (is (= (slurp res) (slurp (test-file-loader "output.js"))))
      (-> "example.js" test-file-loader io/as-file (.setLastModified (+ 1000 (System/currentTimeMillis))))
      
      (let [res3 (processor "example.js")]
        (is (= res res3))
        (-> res3 io/as-file .delete)
      
        (let [res4 (processor "example.js")]
          (is (not= res4 res3))
        ))
      )))
