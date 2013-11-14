(ns cornet.wrappers)


(defn wrap-change-extension [fun from to]
  {:pre [(.startsWith from ".") (.startsWith to ".")]}
  (fn [path]
    (if (.endsWith path from)
      (fun (str (subs path 0 (- (count path) (count from))) to))
      (fun path))))
