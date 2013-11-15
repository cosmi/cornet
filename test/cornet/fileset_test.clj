(ns cornet.fileset-test
  (:require [clojure.java.io :as io])
  (:use cornet.utils
        cornet.fileset
        clojure.test))


(deftest fileset-from-url-loader-test
  (let [url (-> "test-resources/files.list" io/as-file io/as-url)
        loader (fileset-from-url-loader url)]
    (is (= (loader) #{"lesscss/bootstrap.less"
                      "coffee/example.coffee"}))
    )
  )


(deftest dev-fileset-from-url-loader-test
  (let [list (-> "test-resources/files.list" io/as-file slurp)
        tmpfile (create-temp-file "files.list")
        loader (fileset-from-url-loader (io/as-url tmpfile) :mode :dev)]
    (is (= (loader) #{}))
    (spit tmpfile list)
    (-> tmpfile (.setLastModified (+ 3000 (System/currentTimeMillis))))
    (is (= (loader) #{"lesscss/bootstrap.less"
                      "coffee/example.coffee"}))
    
    )
  )
