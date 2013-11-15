(ns cornet.core-test
  (:require [clojure.test :refer :all]
            [cornet.core :refer :all]))


(deftest compiled-assets-loader-test
  (let [loader (compiled-assets-loader "test-resources/"
                                       :from-filesystem true
                                       :files-list "files.list"
                                       :lesscss-list ["lesscss/bootstrap/bootstrap.less"])]
    (is (loader "lesscss/bootstrap.css"))
    (is (-> (loader "lesscss/bootstrap.css") str (.endsWith ".css")))
    (is (-> (loader "lesscss/bootstrap/bootstrap.css") str (.endsWith ".css")))
    (is (-> (loader "lesscss/bootstrap/variables.css") nil?))
    (is (loader "coffee/example.js"))
    (is (-> (loader "coffee/example.js") str (.endsWith ".js")))
    ))
