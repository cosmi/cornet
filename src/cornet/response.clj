(ns cornet.response
  (:use [cornet utils])
  (:require [clojure.java.io :as io]
            [ring.util.response :as response])
  (:import [java.io File]))




(defn response [url-like]
  (response/url-response url-like))
   
(defn add-dependencies [resp deps]
  (update-in resp [::deps] concat deps))


;; (defn clear-dependencies [resp]
;;   (dissoc resp ::deps))

(defn get-dependencies [resp]
  (seq (get resp ::deps)))


