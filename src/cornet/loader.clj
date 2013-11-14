(ns cornet.loader
  (:use [cornet utils])
  (:require [clojure.java.io :as io]
            [clojure.core.cache :as cache])
  (:import [java.io File]))




(defn resource-loader [root]
  (fn [path]
    (io/resource (str root "/" path))
    ))



(defn wrap-processor [loader processor]
  (fn [path]
    (when-let [resource (loader path)]
      (let [to (create-temp-file path)]
        (processor resource to)))))



(defn wrap-loader-cache [loader & [cache-factory]]
  (let [cache (atom ((or cache-factory lu-cache-factory) {}))]
    (fn [path]
      (let [resp (cache/lookup 
    

      )))

