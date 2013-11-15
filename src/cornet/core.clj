(ns cornet.core
  (:use [cornet loader mode wrappers fileset]
        [cornet.processors lesscss coffeescript])
  (:require [clojure.java.io :as io]))






(defn compiled-assets-loader [root & {:keys [ from-filesystem mode
                                        files-list
                                        lesscss-list
                                        coffee-list]
                                 :or {from-filesystem false
                                      }} ]
  {:pre [(string? root)]}
  (with-cornet-mode (or mode *cornet-mode*)
    (let [loader ((if from-filesystem file-loader resource-loader)
                  root)
          files-list (when files-list
                       (fileset-from-url-loader
                        (cond-> files-list
                                (string? files-list)
                                loader
                                true io/as-url)))
          lesscss (-> loader
                   (wrap-lesscss-processor :change-ext false)
                   (wrap-ext-filter ".less")
                   )
          lesscss1 (when (not-empty lesscss-list)
                     (wrap-files-list lesscss lesscss-list)
                      )
          lesscss2 (when files-list
                     (-> lesscss
                         (wrap-preload (files-list))
                         (wrap-paths-filter-from-fn files-list)))
          lesscss (when-let [lesscss (not-empty (remove nil? [lesscss1 lesscss2]))]
                    (-> (apply compose-sources lesscss)
                        (wrap-change-extension ".css" ".less")
                        ))

          coffee (-> loader
                     (wrap-coffee-script-processor :change-ext false)
                     (wrap-ext-filter ".coffee")
                     )
          coffee1 (when (not-empty coffee-list)
                     (wrap-files-list coffee coffee-list)
                      )
          coffee2 (when files-list
                     (-> coffee
                         (wrap-preload (files-list))
                         (wrap-paths-filter-from-fn files-list)))
          coffee (when-let [coffee (not-empty (remove nil? [coffee1 coffee2]))]
                    (-> (apply compose-sources coffee)
                        (wrap-change-extension ".js" ".coffee")
                        ))
          procs (not-empty (remove nil? [lesscss coffee]))
          ]
      (if procs
        (apply compose-sources procs)
        (constantly nil)))))
  
