(ns cornet.processors.coffeescript
  (:import ro.isdc.wro.model.resource.Resource
           ro.isdc.wro.model.resource.ResourceType
           ro.isdc.wro.extensions.processor.js.RhinoCoffeeScriptProcessor)
  (:use [cornet.processors.wro]
        [cornet.wrappers]))


(defn coffee-script-processor []
  (let [processor (RhinoCoffeeScriptProcessor.)]
    (create-processor processor ResourceType/JS)))



(defn wrap-coffee-script-processor [fun & {:as opts :keys [change-ext] :or {change-ext true}}]
  (cond-> 
   (apply wrap-processor fun (coffee-script-processor) :output-ext ".js" (apply concat opts))
   change-ext (wrap-change-extension ".js" ".coffee")))




