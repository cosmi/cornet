(ns cornet.route-test
  (:use [cornet.processors lesscss minifiers coffeescript]
        [compojure.core]
        [clojure.test]
        [cornet route loader])
  (:require [ring.server.standalone :as server]
            [compojure.handler :as handler]
            [clj-http.client :as http]
            [clojure.java.io :as io]))


(def test-file-loader (file-loader  "test-resources/"))

(def less-provider (-> test-file-loader
                       (wrap-lesscss-processor)))

(def app (-> less-provider
             wrap-url-response
             handler/site))



(defmacro with-server [server & body]
  `(let [server# ~server]
     (try
       ~@body
       (finally (.stop server#)))))

(defn http-get [port uri]
  (http/get (str "http://localhost:" port uri)
            {:conn-timeout 8000
             :throw-exceptions false}))



(deftest compojure-handler-test
  (with-server (server/serve app {:join? false, :open-browser? false :port 7687})

    (let [resp (http-get 7687 "/lesscss/bootstrap.less")
          headers (resp :headers)]
      (is (= (headers "content-type") "text/css"))
      (is (= (headers "content-length") "119179"))
      (is (= (-> resp :body count) 119179))
      )
    (let [resp (http-get 7687 "/lesscss/bootstrap.css")
          headers (resp :headers)]
      (is (= (headers "content-type") "text/css"))
      (is (= (headers "content-length") "119179"))
      (is (= (-> resp :body count) 119179))
      )
    
    (let [resp (http-get 7687 "/lesscss/a.css")
          headers (resp :headers)]
      (is (= (-> resp :status (= 404))))
      )
    (let [resp (http-get 7687 "/lesscss/")
          headers (resp :headers)]
      (is (= (-> resp :status (= 404))))
      )
    )
  )

