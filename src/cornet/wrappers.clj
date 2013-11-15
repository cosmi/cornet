(ns cornet.wrappers
  (:require [clojure.java.io :as io])
  (:use [cornet mode utils]))


(defn wrap-change-extension
  "Will change input extension, if it matches the given one."
  [fun from to]
  {:pre [(.startsWith from ".") (.startsWith to ".")]}
  (fn [path]
    (if (.endsWith path from)
      (fun (str (subs path 0 (- (count path) (count from))) to))
      (fun path))))


(defn wrap-paths-filter
  "Will block paths not on the list"
  [fun paths]
  (let [paths (set paths)]
    (fn [path]
      (when (contains? paths path)
        (fun path)))))


(defn wrap-ext-filter
  "Will block paths not on the list"
  [fun ^String ext]
    (fn [^String path]
      (when (.endsWith path ext)
        (fun path))))

(defn wrap-paths-filter-from-fn
  "Will block paths not on the list"
  [fun fltr]
    (fn [path]
      (when (contains? (fltr) path)
        (fun path))))


(defn wrap-preload
  [fun paths]
  (dorun (map fun paths))
  fun)



(defn wrap-files-list
  "Will call all the specified paths, and will block any other than the ones in specified list"
  [fun paths]
  (-> fun
      (wrap-preload paths)
      (wrap-paths-filter paths)))


(defn dev-wrap-processor [loader processor & {:keys [output-ext]}]
  (let [cache (atom {})
        create-temp-file (if output-ext #(create-temp-file % output-ext) create-temp-file)]
    (fn [path]
      (when-let [url (loader path)]
        (swap! cache #(if (% path) % (assoc % path (atom {}))))
        (let [path-atom (get @cache path)]
          (swap! path-atom (fn [data]
                             (let [ts (get-url-timestamp url)]
                               (if (and (data :ts)
                                        (>= (data :ts) ts)
                                        (not (url-deleted? (data :url))))
                                 data
                                 (do
                                   (when (data :url) (-> data :url io/as-file .delete))
                                   (let [output (io/as-url (create-temp-file path))]
                                     (processor path url output)
                                     {:ts ts :url output}
                                     ))))))
          (@path-atom :url)
          )))))


(defn prod-wrap-processor [loader processor & {:keys [output-ext]}]
  (let [cache (atom {})
        create-temp-file (if output-ext #(create-temp-file % output-ext) create-temp-file)]
    (fn [path]
      (when-let [url (loader path)]
        (swap! cache #(if (% path) % (assoc % path (atom {}))))
        (let [path-atom (get @cache path)]
          (swap! path-atom (fn [data]
                             (if (and (data :ts)
                                      (not (url-deleted? (data :url))))
                               data
                               (let [output (io/as-url (create-temp-file path))
                                     ts (System/currentTimeMillis)]
                                 (processor path url output)
                                 {:ts ts :url output}
                                 ))))
          (@path-atom :url)
          )))))


(defn wrap-processor [loader processor & {:keys [mode] :or {mode (get-cornet-mode)} :as opts}]
  (apply
   (case mode
         :dev dev-wrap-processor
         :prod prod-wrap-processor)
   loader
   processor
   (apply concat opts)))

(defn compose-sources
  ([source & sources]
     (apply some-fn source sources))
  ([source] source))
