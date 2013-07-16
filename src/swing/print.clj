; Copyright (c) Gunnar Völkel. All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v1.0.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns swing.print
  {:author "Gunnar Völkel"})

(defn print-component
  [c, title]
  (when-let [job (some-> c .getToolkit (.getPrintJob c, title, nil, nil))]    
    (when-let [g (.getGraphics job)]
      (.printAll c, g)
      (.dispose g))
    (.end job)))