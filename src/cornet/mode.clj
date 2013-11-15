(ns cornet.mode)

(def ^:dynamic *cornet-mode* nil)


(defn get-cornet-mode []
  (or @#'*cornet-mode*
      (when-let [prop (System/getProperty "cornetMode")]
        (keyword prop))
      :prod))




(defmacro with-cornet-mode [mode & body]
  `(binding [*cornet-mode* ~mode]
     ~@body
     ))
