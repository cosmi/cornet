(ns cornet.core
  (:use [cornet loader mode wrappers fileset]
        [cornet.processors lesscss coffeescript minifiers])
  (:require [clojure.java.io :as io]))






(defn compiled-assets-loader [root & {:keys [ from-filesystem mode
                                             files-list
                                             lesscss-list
                                             coffee-list
                                             minify]
                                 :or {from-filesystem false
                                      } :as opts} ]
  {:pre [(string? root)]}
  (with-cornet-mode (or mode *cornet-mode*)
    (let [loader ((if from-filesystem file-loader resource-loader)
                  root)
          minify (if (contains? opts :minify) minify (= (get-cornet-mode) :prod))
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
                        (cond-> minify
                                (wrap-yui-css-compressor))
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
                        (cond-> minify
                                (wrap-uglify-js-compressor))
                        ))
          procs (not-empty (remove nil? [lesscss coffee]))
          ]
      (if procs
        (apply compose-sources procs)
        (constantly nil)))))
  
(defn static-assets-loader [root & {:keys [ from-filesystem mode]
                                      :or {from-filesystem false}} ]
  
  {:pre [(string? root)]}
  (with-cornet-mode (or mode *cornet-mode*)
    (let [loader ((if from-filesystem file-loader resource-loader)
                  root)]
      (if (= (get-cornet-mode) :prod)
        (let [js-loader (-> loader
                            wrap-uglify-js-compressor
                            (wrap-ext-filter ".js"))
              css-loader (-> loader
                             wrap-yui-css-compressor
                             (wrap-ext-filter ".css"))]
          (compose-sources js-loader css-loader loader))
        loader))))
