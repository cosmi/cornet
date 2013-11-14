(ns cornet.processors.lesscss-test
  (:require [clojure.java.io :as io])
  (:use cornet.utils
        cornet.processors.lesscss
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
  (let [processor (dev-wrap-lesscss-processor test-file-loader)]
    (let [res (check-time (> 100) (processor "bootstrap.css"))
          res2 (check-time (< 20) (processor "bootstrap.css"))]
      (is (= res res2))
      (is (.endsWith (str res) ".css"))
      (is (= (slurp res) (slurp (test-file-loader "output.css"))))
      (let [file (io/as-file (test-file-loader "bootstrap/variables.less"))]
        (.setLastModified file (System/currentTimeMillis)))
      (let [res3 (check-time (> 100) (processor "bootstrap.css"))]
        (is (not= res res3)))
      (is (not (.exists (io/as-file res))))
      )))

(deftest prod-compile-bootstrap-test
  (let [processor (prod-wrap-lesscss-processor test-file-loader)]
    (let [res (check-time (> 100) (processor "bootstrap.css"))
          res2 (check-time (< 20) (processor "bootstrap.css"))]
      (is (= res res2))
      (is (.endsWith (str res) ".css"))
      (is (= (slurp res) (slurp (test-file-loader "output.css"))))
      (-> (io/as-file (test-file-loader "bootstrap/variables.less"))
          (.setLastModified (System/currentTimeMillis)))
      (let [res3 (check-time (< 20) (processor "bootstrap.css"))]
        (is (= res res3)))
      (-> res io/as-file .delete)
      (let [res3 (check-time (> 100) (processor "bootstrap.css"))]
        (is (not= res res3)))
      
      
      )))

    
