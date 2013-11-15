(ns cornet.core-test
  (:require [clojure.test :refer :all]
            [cornet.core :refer :all]))


(deftest compiled-assets-loader-test
  (let [loader (compiled-assets-loader "test-resources/"
                                       :from-filesystem true
                                       :mode :prod
                                       :files-list "files.list"
                                       :lesscss-list ["lesscss/bootstrap/bootstrap.less"])]
    (is (loader "lesscss/bootstrap.css"))
    (is (-> (loader "lesscss/bootstrap.css") str (.endsWith ".css")))
    (is (-> (loader "lesscss/bootstrap/bootstrap.css") str (.endsWith ".css")))
    (is (-> (loader "lesscss/bootstrap/variables.css") nil?))
    (is (loader "coffee/example.js"))
    (is (-> (loader "coffee/example.js") str (.endsWith ".js")))
    (is (= 429 (->  (loader "coffee/example.js") slurp count)) "Minification failed")
    ))




(deftest static-assets-loader-test
  (let [loader (static-assets-loader "test-resources/"
                                       :from-filesystem true
                                       :mode :prod)]
    (is (= 97344 (count (slurp (loader "lesscss/output.css")))))
    (is (= 429 (count (slurp (loader "coffee/output.js")))))
    ))

(deftest dev-static-assets-loader-test
  (let [loader (static-assets-loader "test-resources/"
                                       :from-filesystem true
                                       :mode :dev)]
    (is (= 119179 (count (slurp (loader "lesscss/output.css")))))
    (is (= 875 (count (slurp (loader "coffee/output.js")))))
    ))
