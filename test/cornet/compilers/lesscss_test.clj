(ns cornet.compilers.lesscss-test
  (:require [clojure.java.io :as io])
  (:use cornet.utils
        cornet.compilers.lesscss
        clojure.test))

(defmacro check-time [time & body]
  `(let [time# (System/currentTimeMillis)
         resp# (do ~@body)
         dt# (- (System/currentTimeMillis) time#)]
     (is (-> dt# ~time))
     (println "Time:" dt#)
     resp#
     ))

(defn test-file-loader [path]
  (let [file (io/as-file (str "test-resources/lesscss/" path))]
    (when (.exists file)
      (io/as-url file))))


(deftest dev-compile-bootstrap-test
  (let [processor (dev-lesscss-processor test-file-loader)]
    (let [res (check-time (> 100) (processor "bootstrap.css"))
          res2 (check-time (< 20) (processor "bootstrap.css"))]
      (is (= res res2))
      (is (= (slurp res) (slurp (test-file-loader "bootstrap.css"))))
      (let [file (io/as-file (test-file-loader "bootstrap/variables.less"))]
        (.setLastModified file (System/currentTimeMillis)))
      (let [res3 (check-time (> 100) (processor "bootstrap.css"))]
        (is (not= res res3)))
      (is (not (.exists (io/as-file res))))
      )))

(deftest prod-compile-bootstrap-test
  (let [processor (prod-lesscss-processor test-file-loader)]
    (let [res (check-time (> 100) (processor "bootstrap.css"))
          res2 (check-time (< 20) (processor "bootstrap.css"))]
      (is (= res res2))
      (is (= (slurp res) (slurp (test-file-loader "bootstrap.css"))))
      (let [file (io/as-file (test-file-loader "bootstrap/variables.less"))]
        (.setLastModified file (System/currentTimeMillis)))
      (let [res3 (check-time (< 20) (processor "bootstrap.css"))]
        (is (= res res3)))
      )))

    
