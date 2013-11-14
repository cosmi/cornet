(ns cornet.processors.minifiers
  (:import ro.isdc.wro.model.resource.Resource
           ro.isdc.wro.model.resource.ResourceType
           ro.isdc.wro.extensions.processor.js.UglifyJsProcessor
           ro.isdc.wro.extensions.processor.css.YUICssCompressorProcessor)
  (:use [cornet.processors.wro]
        [cornet wrappers]))




(defn yui-css-compressor []
  (let [processor (YUICssCompressorProcessor.)]
    (create-processor processor ResourceType/CSS)))

(defn uglify-js-compressor []
  (let [processor (UglifyJsProcessor.)]
    (create-processor processor ResourceType/JS)))



(defn wrap-yui-css-compressor [fun & {:as opts}]
  (apply wrap-processor fun (yui-css-compressor) (apply concat opts)))

(defn wrap-uglify-js-compressor [fun & {:as opts}]
  (apply wrap-processor fun (uglify-js-compressor) (apply concat opts)))


