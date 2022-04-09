(ns task-c5)

(def tryTakeForkTimes (atom 0))

(defn Fork [] 
  (ref {:inUse 0 :timesUsed 0} 
       :validator (fn [state] (and (>= (state :inUse) 0) (<= (state :inUse) 1)))
  )
)

(defn takeFork [fork]
  (dosync
   (swap! tryTakeForkTimes inc)
   (alter fork (fn [state] (update (update state :inUse inc) :timesUsed inc)))
  )
)

(defn putFork [fork]
  (dosync
   (alter fork (fn [state]  (update state :inUse dec)))
  )
)

(defn take-forks [leftFork rightFork]
  (if 
    (nil? 
      (try
        (takeFork leftFork)
        (catch IllegalStateException _ nil)
      )
    )
    (recur leftFork rightFork)
    (if 
      (nil? 
        (try
          (takeFork rightFork)
          (catch IllegalStateException _ nil)
        )
      )
      (
       do 
         (putFork leftFork)
         (recur leftFork rightFork)
      )
      nil
    )
  )
)

(defn philosopher
  [thinkingTime diningTime leftFork rightFork id times eaten]
  (new Thread
       (fn []
         (println (str "Philosopher " id " thinking..."))
         (Thread/sleep thinkingTime)
         (println (str "Philosopher " id " trying to take forks..."))
         (take-forks leftFork rightFork)
         (println (str "Philosopher " id " eats..."))
         (Thread/sleep diningTime)
         (swap! (nth eaten id) inc)
         (putFork leftFork)
         (putFork rightFork)
         (println (str "Philosopher " id " puts forks..."))
         (if (>= times @(nth eaten id))
           (recur)
           nil
         )
       )
  )
)

(defn startLunch [philCount thinkingTime diningTime lunchCount]
  (let [
    forks (map (fn [_] (Fork)) (range philCount))
    lunchEaten (map (fn [_] (atom 0)) (range philCount))
    leftFork (fn [philId] (nth forks philId))
    rightFork (fn [philId] (nth forks (mod (inc philId) philCount)))
    philosophers (map 
                   (fn [philId] 
                     (philosopher 
                       thinkingTime 
                       diningTime 
                       (leftFork philId) 
                       (rightFork philId) 
                       philId 
                       lunchCount 
                       lunchEaten
                     )
                   ) 
                   (range philCount)
                 )
    ]
    (run! #(.start %) philosophers)
    (time (run! #(.join %) philosophers))
    (println (str "Transaction restart times: " @tryTakeForkTimes))
  )
)

(startLunch 5 100 100 20)

