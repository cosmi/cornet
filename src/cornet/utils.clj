(ns cornet.utils
  (:require [clojure.java.io :as io])
  (:import [java.io File]))



(defn get-path-timestamp
  [path]
  (or 
   (when-let [resource (io/resource path)]
     (when (-> resource .getProtocol (= "file"))
       (-> resource io/as-file .lastModified)))
   0))

(defn get-url-timestamp [url]
  (or 
   (when (-> url .getProtocol (= "file"))
     (let [file (io/as-file url)]
       (when (.exists file)
         (.lastModified file))))
   0))

(defn url-deleted? [url]
  (when (-> url .getProtocol (= "file"))
    (let [file (io/as-file url)]
      (not (.exists file)))))

  
(defn create-temp-file
  ([^String for-path]
     (let [idx-lim (.lastIndexOf for-path "/")
           idx (.lastIndexOf for-path ".")
           [pref suf]
           (map
            #(.replace % \/ \$)
            (if (> idx idx-lim)
              [(subs for-path 0 idx) (subs for-path idx)]
              [for-path ".tmp"]))
           ]
       (File/createTempFile pref suf)))
  ([^String path ^String ext]
     (let [idx-lim (.lastIndexOf path "/")
           idx (.lastIndexOf path ".")
           [pref suf]
           (map
            #(.replace % \/ \$)
            (if (> idx idx-lim)
              [(subs path 0 idx) ext]
              [path ext]))
           ]
       (File/createTempFile pref suf)
     )))
