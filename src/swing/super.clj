; Copyright (c) Gunnar Völkel. All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v1.0.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns swing.super
  {:author "Gunnar Völkel"})


(defmacro proxy-super-class
  [class-symbol, meth, & args]
  (let [class (resolve class-symbol),
        _ (assert (class? class) "class-symbol must refer to a class")
        this-symbol (with-meta 'this
                      {:tag (symbol (.getCanonicalName ^Class class))})]
    `(proxy-call-with-super (fn [] (. ~this-symbol ~meth ~@args)) ~this-symbol ~(name meth))))