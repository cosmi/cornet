(ns cornet.processors.wro
  (:require [clojure.java.io :as io])
  (:import ro.isdc.wro.model.resource.Resource))




(defn create-processor [processor resource-type]
  (fn [path from-url to-url]
    (doto processor
      (.process (Resource/create (.getPath from-url) resource-type)
                (io/make-reader from-url nil)
                (io/make-writer to-url nil)))))
