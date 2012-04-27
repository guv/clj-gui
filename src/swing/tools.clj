(ns swing.tools)


(defn close-all-windows []
  (doseq [wnd (java.awt.Window/getWindows)] (.dispose wnd)))