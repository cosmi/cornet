(ns cornet.processors.lesscss
  (:require [clj-rhino :as js])
  (:require [clojure.java.io :as io])
  (:use [cornet utils mode wrappers]))


(defn get-lesscss-script [version]
  (slurp (io/resource (str "cornet/vendor/less-rhino-" version ".js"))))

(defn compile-file-impl [path script  resource-fetcher]
  (let [sc (js/new-safe-scope)
        result (atom nil)
        deps (atom #{})]
    (doto sc
      (js/set! "print" (js/make-fn
                        (fn [ctx scope this [& args]]
                          (apply println (map str args))
                          )))
      (js/set! "readFile" (js/make-fn
                           (fn [ctx scope this [arg]]
                             (let [arg (str arg)]
                               (swap! deps conj arg)
                               (slurp (resource-fetcher arg))
                               ))))
      (js/set! "writeFile" (js/make-fn
                           (fn [ctx scope this [filename output]]
                             (let [filename (str filename)
                                   output (str output)]
                               (when (= filename "*output*")
                                 (reset! result output)
                                 )))))
      (js/eval "environment = {}")
      (js/eval "quit = function() {};")
      (js/eval (format "arguments = ['%s', '*output*']" path))
      (js/set! "cljError" (js/make-fn
                           (fn [ctx scope this [arg filename]]
                             (let [msg (str arg)]
                               (throw (Exception. (str "LessCSS error\n" msg)))))))
      (js/eval script))
    {:output @result
     :dependencies @deps}
      ))

(def lesscss-agent (agent nil))

;; (defn sync-compile-file [path script loader]
;;   (let [res (promise)]
;;     (send lesscss-agent
;;           (fn [a]
;;             (let [response (try (compile-file-impl script path loader)
;;                                 (catch Exception e
;;                                   {:error e}
;;                                   ))]
;;               (deliver res (response))
;;             )))
;;     (let [res @res]
;;       (if (:error res) (throw (:error res))
;;           res))))



(defn dev-wrap-lesscss-processor [loader & {:keys [version output-ext change-ext] :or {version "1.3.3" output-ext ".css" change-ext true}}]
  (let [state (atom {})
        script (try (get-lesscss-script version)
                    (catch Exception e (throw (Exception. (str "Cannot load lesscss script, version " version)))))]
    (cond-> 
     (fn [path]
       (when (loader path)
         (swap! state #(if (get % path)
                         %
                         (assoc % path (atom {}))))
         (let [path-atom (@state path)]
           (swap! path-atom
                  (fn [data]
                    (if (or (not (data :ts))
                            (url-deleted? (data :url)) 
                            (< (data :ts)
                               (->> (data :dependencies)
                                    (map loader)
                                    (map get-url-timestamp)
                                    (remove nil?)
                                    (apply max 0))))
                      (let [ts (System/currentTimeMillis)
                            res (compile-file-impl path script loader)
                            output (create-temp-file path output-ext)]
                        (spit output (res :output))
                        (when (data :url)
                          (let [file (io/as-file (data :url))]
                            (.delete file)
                            ))
                        {:ts ts :dependencies (:dependencies res)
                         :url (io/as-url output)})
                      data
                      )))
           (@path-atom :url)

           )))
     change-ext (wrap-change-extension ".css" ".less"))))


(defn prod-wrap-lesscss-processor [loader & {:keys [version output-ext change-ext] :or {version "1.3.3" output-ext ".css" change-ext true}}]
  (let [state (atom {})
        script (try (get-lesscss-script version)
                    (catch Exception e (throw (Exception. (str "Cannot load lesscss script, version " version)))))]
    (cond->
     (fn [path]
       (when (loader path)
         (swap! state #(if (get % path)
                         %
                         (assoc % path (atom {}))))
         (let [path-atom (@state path)]
           (swap! path-atom
                  (fn [data]
                    (if (or (not (data :ts))
                            (url-deleted? (data :url)))
                      (let [ts (System/currentTimeMillis)
                            res (compile-file-impl path script loader)
                            output (create-temp-file path output-ext)]
                        (spit output (res :output))
                        {:ts ts :dependencies (:dependencies res)
                         :url (io/as-url output)})
                      data
                      )))
           (@path-atom :url)

           )))
     change-ext (wrap-change-extension ".css" ".less"))))



(defn wrap-lesscss-processor [loader & {:keys [mode] :or {mode (get-cornet-mode)} :as opts}]
  (apply
   (case mode
         :dev dev-wrap-lesscss-processor
         :prod prod-wrap-lesscss-processor)
   loader
   (apply concat opts)))
