(ns sudokulol.sudoku
  (:use clojure.test
        [clojure.set :only (difference)]
        [clojure.contrib.math :only (sqrt)]))

(def default-problem
  [3 0 0 0 0 5 0 1 0
   0 7 0 0 0 6 0 3 0
   1 0 0 0 9 0 0 0 0
   7 0 8 0 0 0 0 9 0
   9 0 0 4 0 8 0 0 2
   0 6 0 0 0 0 5 0 1
   0 0 0 0 4 0 0 0 6
   0 4 0 7 0 0 0 2 0
   0 2 0 6 0 0 0 0 3])

(def default-solution
  [3 8 6 2 7 5 4 1 9
   4 7 9 8 1 6 2 3 5
   1 5 2 3 9 4 8 6 7
   7 3 8 5 2 1 6 9 4
   9 1 5 4 6 8 3 7 2
   2 6 4 9 3 7 5 8 1
   8 9 3 1 4 2 7 5 6
   6 4 1 7 5 3 9 2 8
   5 2 7 6 8 9 1 4 3])

(with-test
  (defn- every-nth [vector n]
    (map #(nth % n) vector))
  (is (= [1 2 3]
         (every-nth [[0 1 0] [0 2 0] [0 3 0]] 1))))

(with-test
  (defn constraints
    "For a given index in the board, find all digits that are already taken."
    [board index]
    (let [board (partition 9 board) ;; break the board up into a 9x9 2d array
          row (/ index 9)           ;; the row number of index
          col (mod index 9)         ;; the column number of index
          grp-col (/ col 3)         ;; the group column of index
          grp-row (/ row 3)         ;; the group row of index
          ;; partition each row into 3 group columns, and pick out the
          ;; one we want:
          grp-cols (every-nth (map #(partition 3 %) board) grp-col)
          ;; grp-cols is now 9 rows of 3 items; let's pick out the 3x3
          ;; group we're interested in:
          grp (take 3 (drop (* 3 (int grp-row)) grp-cols))]
      ;; take the row, the column and the group and add them up into a
      ;; set which will thus contain all digits we know index can't
      ;; contain.
      (into #{} (flatten [(nth board row) (every-nth board col) grp]))))
  (is (= #{0 1 3 5 7 9} (constraints default-problem 0)))
  (is (= #{0 2 4 6 7} (constraints default-problem 64))))

(with-test
  (defn- candidates
    "For a given empty index, find every possible number it could contain."
    [board index]
    ;; this is simply the set of all digits minus the set of constraints.
    (difference #{1 2 3 4 5 6 7 8 9} (constraints board index)))
  (is (= #{2 4 6 8} (candidates default-problem 0))))

(with-test
  (defn solve [board]
    (if (not (.contains board 0)) ;; if no 0s, it's already solved
      board
      (let [index (.indexOf board 0)]
        (flatten (map #(solve (assoc board index %))
                      (candidates board index))))))
  (is (= default-solution (solve default-problem))))



(with-test
  (defn- switch-digits [board a b]
    (if (= a b) board
        (replace {a b b a} board)))
  (is (= [1 3 2] (switch-digits [1 2 3] 2 3))))

(defn- switch-random-digits [board]
  (let [a (rand-nth board)
        b (rand-nth board)]
    (switch-digits board a b)))

(defn- remove-random-digit [board]
  (assoc board
    (rand-nth (for [x (range (count board)) :when (pos? (nth board x))] x))
    0))

(with-test
  (defn repeat-call
    "Calls a function n times with the result of itself as the argument."
    [n func arg]
    (loop [ct n
           arg arg]
      (if (zero? ct) arg
          (recur (dec ct)
                 (func arg)))))
  (is (= 5 (repeat-call 3 inc 2))))

(defn randomise-board [board times]
  (repeat-call times switch-random-digits board))

(defn remove-digits [board times]
  (repeat-call times remove-random-digit board))

(defn generate [switches removes]
  (remove-digits (randomise-board default-solution switches) removes))

