; Copyright (c) Gunnar Völkel. All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v1.0.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns swing.treetable
  {:author "Gunnar Völkel"}
  (:import
    (org.jdesktop.swingx JXTreeTable)
    (org.jdesktop.swingx.treetable AbstractTreeTableModel)
    (org.jdesktop.swingx.tree DefaultXTreeCellRenderer)
    (java.awt Dimension Color FlowLayout)
    (javax.swing JScrollPane JFrame JTable JLabel SwingConstants)
    (javax.swing.table DefaultTableCellRenderer)))



(defprotocol ITreeTableNode
  (IsLeaf [this])
  (GetChildCount [this])
  (GetChild [this, index])
  (GetValueAt [this, column])
  (GetNodeType [this]))


(deftype ColumnSpecification [column-name, column-width, cell-renderer])
 

(defn- create-tree-table-model
  [root-node, column-specs]
  (let [ column-count (count column-specs) ]
	  (proxy [AbstractTreeTableModel] [ root-node ]
	    (getColumnCount [] column-count)
	    (getColumnName [column] (-> column-specs (nth column) (.column-name) ))     
	    (getChild [parent, index] (GetChild parent index))
	    (getChildCount [parent] (GetChildCount parent))
	    (isLeaf [node] (IsLeaf node))
	    (getValueAt [node, column] (GetValueAt node column))
      (getIndexOfChild [parent, child]        
        (first (filter (fn [idx] (= child (GetChild parent idx))) (range (GetChildCount parent))) )))))


(def ^{:dynamic true} *tree-cell-renderer-factory* nil)

(defmacro with-tree-cell-renderer-factory
  [renderer & body]
 `(binding [*tree-cell-renderer-factory* ~renderer]
   ~@body))


(defn show-tree-table
  ""
  ([root-node, column-specs, title, root-node-visible?]
    (show-tree-table   root-node, column-specs, title, root-node-visible?, 400, 300))  
  ([root-node, column-specs, title, root-node-visible?,  width, height]    
	  (let [model ^AbstractTreeTableModel (create-tree-table-model root-node, column-specs),
          tree-table (JXTreeTable. model), 
          dim (Dimension. width height)]
      (loop [idx 0, col-spec-list column-specs]
        (when-not (empty? col-spec-list)
          (do
            (let [ col-spec (first col-spec-list) ]
	            (-> tree-table (.getColumnModel) (.getColumn idx) (.setPreferredWidth (.column-width col-spec) ) )
	            (when-not (or (zero? idx) (nil? (.cell-renderer col-spec)) ) 
                (-> tree-table (.getColumnModel) (.getColumn idx) (.setCellRenderer (.cell-renderer col-spec) ) )))
            (recur (inc idx) (rest col-spec-list)))))	                     
      (.setColumnControlVisible tree-table true)
      (.setAutoResizeMode tree-table JTable/AUTO_RESIZE_OFF)
      (.setRowHeight tree-table 20)    
      (.setRootVisible tree-table root-node-visible?)
      (when-not (nil? *tree-cell-renderer-factory*)
        (.setTreeCellRenderer tree-table (*tree-cell-renderer-factory* model)))

	    (doto (JFrame. title)
	     (.add (JScrollPane. tree-table))               
	     (.setSize dim)
	     (.setMinimumSize dim)
	     (.setVisible true)
       (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)
	     (.pack)))))


(defn create-cell-renderer [align, format-func]
  (proxy [DefaultTableCellRenderer] []
    (getTableCellRendererComponent [^JTable table, ^Object obj, ^Boolean isSelected, ^Boolean hasFocus, ^Integer row, ^Integer column]
      (let [value (format-func obj),			      
            renderer ^JLabel (proxy-super getTableCellRendererComponent table, value, isSelected, hasFocus, row, column)]
        (doto renderer        
          (.setHorizontalAlignment align))))))


(defn create-float-cell-renderer [precision]
  (create-cell-renderer
    SwingConstants/RIGHT,
	  #(if (float? %)
	     (format (str "%,." precision "f") %)
	     %)))


(defn create-int-cell-renderer []
  (create-cell-renderer
    SwingConstants/RIGHT,
	  #(if (integer? %) (format (str "%,d") %) %)))


(defn create-string-cell-renderer
  "Align can be specified as :left, :center and :right"
  [align]
  (create-cell-renderer
    (cond
      (= :left align)  
        SwingConstants/LEFT
      (= :center align)
        SwingConstants/CENTER
      (= :right align)
        SwingConstants/RIGHT
      :else
        SwingConstants/CENTER),
	  (fn [value] value)))