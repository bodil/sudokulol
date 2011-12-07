(ns sudokulol.views.common
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
        clojure.test))

(defpartial layout [& content]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:title "Sudoku"]
    (include-css "/css/screen.css")]
   [:body
    [:div#outer
     content]]))

(with-test
  (defn value-at [board y x]
    (nth board (+ (* y 9) x)))
  
  (is (= 5 (value-at [0 0 0 0 0 0 0 0 0
                      0 0 5 0 0 0 0 0 0] 1 2))))

(with-test
  (defn cell-name [y x]
    (str (+ (* y 9) x)))

  (is (= "21" (cell-name 2 3))))

(defpartial board [b]
  (html
   [:table#board
    [:tbody
     (for [y (range 9)]
       [:tr
        (for [x (range 9)]
          [:td
           [:input {:type "text"
                    :name (cell-name y x)
                    :value (let [v (value-at b y x)]
                             (if (= v 0) "" (str v)))}]])])
     [:tr
      [:td {:colspan "9"}
       [:input {:type "submit" :value "Go ⇒"}]]]]]))

