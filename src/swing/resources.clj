(ns swing.resources
  (:import javax.swing.ImageIcon))


(defn create-image-from-resource
  [path]
  (-> (Thread/currentThread) .getContextClassLoader (.getResource path) (ImageIcon.)))