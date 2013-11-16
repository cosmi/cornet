(ns cornet.fileset
   (:use [cornet wrappers utils mode])
   (:require [clojure.java.io :as io]
             [clojure.string :as strings]))

(defn parse-file-list [s]
  (->> s
      strings/split-lines
      (map strings/trim)
      (remove empty?)))

(defn fileset-from-url-loader [url & {:keys [mode] :or {mode (get-cornet-mode)}}]
  (assert url (str "Fileset file not found."))
  (case mode
    :dev
    (let [src (atom {})]
      (fn []
        (swap! src
               (fn [data]
                 (let [ts (get-url-timestamp url)]
                   (if (or (not (data :ts))
                           (> ts (data :ts)))
                     {:ts ts
                      :output (-> url slurp
                                  parse-file-list
                                  set)}
                     data))))
        (:output @src)))
    :prod
    (-> url slurp
        parse-file-list
        set
        constantly)))

