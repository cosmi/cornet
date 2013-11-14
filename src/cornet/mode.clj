(ns cornet.mode)

(def cornet-mode-force nil)


(defn get-cornet-mode []
  (or @#'cornet-mode-force
      (when-let [prop (System/getProperty "cornetMode")]
        (keyword prop))
      :prod))



