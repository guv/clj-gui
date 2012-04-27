(ns swing.print
  (:use [clojure.contrib.core :only (-?>)]))

(defn print-component
  [c, title]
  (when-let [job (-?> c .getToolkit (.getPrintJob c, title, nil, nil))]    
    (when-let [g (.getGraphics job)]
      (.printAll c, g)
      (.dispose g))
    (.end job)))