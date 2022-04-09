(ns ru.nsu.fit.titkov.task3
  (:require [clojure.test :as test]))

(defn handle-chunk [chunks, filter-f]
  (concat
    ( let [
        part-size (int (Math/ceil (/ (count (first chunks)) 8)))
        parts (partition-all part-size (first chunks))
      ]
      (->> parts
        (map
          (fn [part]
            (future 
              (doall (filter filter-f part))
            )
          )
        )
          
        (doall)
        (map deref)
        (flatten)
      )
    )
    (lazy-seq 
      (
        handle-chunk
        (rest chunks)
        filter-f
      )
    )
  )
)

(defn p-filter-task3
  (
    [filter-f sequence]
    (
      handle-chunk (partition-all 1000 sequence) filter-f
    )
  )
)

(defn heavy_even?
  [arg]
  (Thread/sleep 1)
  (even? arg))


(time (nth (p-filter-task3 heavy_even? (range)) 1000))
(time (nth (filter heavy_even? (range)) 1000))


(test/deftest test-adder
  (test/testing "Testing parallel filter"
    (test/is (=  '(4 9 14 19 24 29 34 39 44 49) (take 10 (p-filter-task3 #(= 4 (mod % 5)) (range)))))
    (test/is (=  80 (nth (p-filter-task3 heavy_even? (range 1000000)) 40)))
    (test/is (= '(0 2 4 6 8 10 12 14 16 18) (take 10 (p-filter-task3 heavy_even? (range)))))
  )
)

(test/run-tests)
