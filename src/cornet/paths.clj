(ns cornet.paths)

(defn get-root
  "Returns path minus the last segment (includes the trailing slash)."
  [^String path]
  (let [idx (.lastIndexOf path "/" (- (count path) 2))]
    (if (== idx -1)
      nil
      (subs path 0 (inc idx)))))

(defn relative-filename [^String root ^String subpath]
  {:pre [(.startsWith root "/") (.endsWith root "/")]}
  (cond
   (.startsWith subpath "/")
   subpath
   (.startsWith subpath "../")
   (if-let [root1 (get-root root)]
     (relative-filename root1 (subs subpath 3))
     (throw (ex-info "Cannot go up in path" {:root root :subpath subpath})))
   (.startsWith subpath "./")
   (relative-filename root (subs subpath 2))
   :else
   (str root subpath)))
