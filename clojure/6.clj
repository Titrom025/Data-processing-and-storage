(ns task-c6)
(def Inf Integer/MAX_VALUE)

(def total_restarts (atom 0))
(def empty-map
  {:forward {}
   :backward {}})

(defn route
  "Add a new route (route) to the given route map
   route-map - route map to modify
   from - name (string) of the start point of the route
   to - name (string) of the end poiunt of the route
   price - ticket price
   tickets-num - number of tickets available"
  [route-map from to price tickets-num]
  (let [tickets (ref tickets-num :validator (fn [state] (>= state 0))),     ;reference for the number of tickets 
        orig-source-desc (or (get-in route-map [:forward from]) {})
        orig-reverse-dest-desc (or (get-in route-map [:backward to]) {})
        route-desc {:price price,                                            ;route descriptor
                    :tickets tickets}
        source-desc (assoc orig-source-desc to route-desc)
        reverse-dest-desc (assoc orig-reverse-dest-desc from route-desc)]
    (-> route-map
        (assoc-in [:forward from] source-desc)
        (assoc-in [:backward to] reverse-dest-desc))))

(defn find-minimal-node [nodes]
  (
    (reduce-kv 
      (fn [acc k v] 
        (if (v :isvisited)
          acc
          (if (<= (v :cost) (acc :cost))
              {:name k :cost (v :cost)}
              acc
          )
        )
      ) 
      {:name nil :cost Inf} 
      nodes
    ) 
    :name
  )
)


(defn DijkstraStep [nodes route-map]
  (let [current_node (find-minimal-node nodes)]
    (if (= nil current_node)
      nodes
      (let 
        [nodesUpdatedCosts (reduce-kv 
            (fn [visited_map end_node nested]
              (if ((visited_map end_node) :isvisited)
                  visited_map
                  (let [new_cost (+ (get-in visited_map [current_node :cost]) (nested :price))]
                       (if (and (> @(nested :tickets) 0) (< new_cost (get-in visited_map [end_node :cost])))
                           (update visited_map end_node (fn [_] {:cost new_cost :isvisited false :came_from current_node}))
                           visited_map
                       )
                  )
              )
            )
            nodes
            ((route-map :forward) current_node)
          )
          visitedCurrent (update nodesUpdatedCosts current_node 
            (fn [struct] 
                (update struct :isvisited (fn [_] true))
            )
          )
        ]
        (recur visitedCurrent route-map) 
      )
    )
  )
)

(defn find-path [from to visited]
  (if (= from to)
    []
    (if (= nil (get-in visited [to :came_from]))
      (throw (IllegalStateException. "No path!"))
      (conj 
        (try 
          (find-path from (get-in visited [to :came_from]) visited)
          (catch IllegalStateException _ (throw (IllegalStateException. "No path!")))
        )
        to)
    )
  )
)

(defn book-tickets
  "Tries to book tickets and decrement appropriate references in route-map atomically
   returns map with either :price (for the whole route) and :path (a list of destination names) keys 
          or with :error key that indicates that booking is impossible due to lack of tickets"
  [route-map from to]
  (if (= from to)
    {:path '(), :price 0}
    (dosync
     (let [startNode {from {:cost 0 :isvisited false :came_from nil}}
           nodes-inf-costs (reduce-kv 
             (fn [acc node _]
                 (if (= node from)
                     acc
                     (assoc acc node {:cost Inf :isvisited false :came_from nil})
                 )
             )
             startNode (route-map :forward))
           
           dijkstraResultNodes (DijkstraStep nodes-inf-costs route-map)
           
           foundedPath (try (find-path from to dijkstraResultNodes)
                     (catch IllegalStateException _ nil)
           )
       ]
       (swap! total_restarts inc)

       (if (= nil foundedPath)
         {:error true}
         (let [result 
                (reduce 
                  (fn [source destination]
                      (if (= source nil)
                          nil
                          (try 
                            (alter (get-in route-map [:forward source destination :tickets]) dec)
                            (catch IllegalStateException _ nil)
                          )
                      ) 
                      destination
                  )
                  from foundedPath
                )
              ] 
              (if (= result nil) 
                  {:error true} 
                  {:path foundedPath, :price (get-in dijkstraResultNodes [to :cost])}
              )
         )
       )
     )
    )
  )
)

;;;cities
(def spec1 (-> empty-map
               (route "City1" "Capital"    200 5)
               (route "Capital" "City1"    250 5)
               (route "City2" "Capital"    200 5)
               (route "Capital" "City2"    250 5)
               (route "City3" "Capital"    300 3)
               (route "Capital" "City3"    400 3)
               (route "City1" "Town1_X"    50 2)
               (route "Town1_X" "City1"    150 2)
               (route "Town1_X" "TownX_2"  50 2)
               (route "TownX_2" "Town1_X"  150 2)
               (route "Town1_X" "TownX_2"  50 2)
               (route "TownX_2" "City2"    50 3)
               (route "City2" "TownX_2"    150 3)
               (route "City2" "Town2_3"    50 2)
               (route "Town2_3" "City2"    150 2)
               (route "Town2_3" "City3"    50 3)
               (route "City3" "Town2_3"    150 2)))

(defn booking-future [route-map from to init-delay loop-delay]
  (future
    (Thread/sleep init-delay)
    (loop [bookings []]
      (Thread/sleep loop-delay)
      (let [booking (book-tickets route-map from to)]
        (if (booking :error)
          bookings
          (recur (conj bookings booking)))))))

(defn print-bookings [name ft]
  (println (str name ":") (count ft) "bookings")
  (doseq [booking ft]
    (println "price:" (booking :price) "path:" (booking :path))))

(defn run []
  ;;try to tune timeouts in order to all the customers gain at least one booking 
  (let [f1 (booking-future spec1 "City1" "City3" 100 1)
        f2 (booking-future spec1 "City1" "City2" 100 1)
        f3 (booking-future spec1 "City2" "City3" 100 1)
        ]
    (print-bookings "City1->City3:" @f1)
    (print-bookings "City1->City2:" @f2)
    (print-bookings "City2->City3:" @f3)
    (prn (str "Total (re-) starts: " @total_restarts))
    ))

(run)
