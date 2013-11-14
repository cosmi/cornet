(ns cornet.route
  (:require [compojure.response]
            [ring.util.response :as response])
  (:use compojure.core
        ring.middleware.content-type
        ring.middleware.file-info
        ring.middleware.head))


(defn wrap-url-response
  "Creates a response from given url-provider"
  [url-provider & [options]]
  (-> (GET "/*" {{resource-path :*} :route-params}
        (when-let [resp (url-provider resource-path)]
          (response/url-response resp)))
      (wrap-file-info (:mime-types options))
      (wrap-content-type options)
      (wrap-head)))
