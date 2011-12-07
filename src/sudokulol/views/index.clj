(ns sudokulol.views.index
  (:require [sudokulol.views.common :as common]
            [sudokulol.sudoku :as sudoku])
  (:use noir.core
        clojure.test))

(with-test
  (defn- int-or-0 [string]
   (try
     (Integer. string)
     (catch Exception e 0)))
  (is (= 0 (int-or-0 "foo")))
  (is (= 1337 (int-or-0 "1337")))
  (is (= 0 (int-or-0 nil))))

(with-test
  (defn- map-args [args]
    (map (fn [arg] [(int-or-0 (name (first arg))) (int-or-0 (fnext arg))]) args))
  (is (= '([2 3] [4 5])
         (map-args '([:2 "3"] [:4 "5"])))))

(with-test
  (defn- args-to-board [args]
    (loop [list (map-args args)
           board (vec (repeat (* 9 9) 0))]
      (if (empty? list) board
          (let [arg (first list)]
            (recur (rest list)
                   (assoc board (arg 0) (arg 1)))))))
  (is (= [0 0 3 0 5 0 0 0 0
          0 0 0 0 0 0 0 0 0
          0 0 0 0 0 0 0 0 0
          0 0 0 0 0 0 0 0 0
          0 0 0 0 0 0 0 0 0
          0 0 0 0 0 0 0 0 0
          0 0 0 0 0 0 0 0 0
          0 0 0 0 0 0 0 0 0
          0 0 0 0 0 0 0 0 0]
         (args-to-board '([:2 "3"] [:4 "5"])))))

(defpage "/" [& args]
  (common/layout
   [:form {:method "get"}
    (common/board (if (seq? args) (sudoku/solve (args-to-board args))
                      (sudoku/generate 10 40)))]))

