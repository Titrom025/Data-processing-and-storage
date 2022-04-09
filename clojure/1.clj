;; ("a" "b" "c")
(def user_list (read))
(def N 3)


;; (defn checkCharForUnique [newChar, word] 
;;   (if (= (reduce (fn [acc, char] (* acc (compare (str char) (str newChar)))) 1 word) 0) 
;;      (boolean false)
;;      (boolean true)
;;   )
;; )

(defn checkCharForUnique [newChar, word]
  (if (= (compare (str newChar) (str (first word))) 0)
    (boolean false)
    (boolean true)
  )
)

(defn addLettersToWords [words] 
   (reduce
      (fn [result, newletter]
         (concat 
            result
            (reduce 
               (fn [letterRes, word] 
                  (conj 
                     letterRes 
                     (concat newletter word)
                  )
               ) 
               (list)
               (filter 
                  (fn [word] 
                     (checkCharForUnique newletter word)
                  ) 
                  words
               )
            )
         )
      )
      (list)
      user_list
   )
)

;; Calculate result
(reduce 
  (fn [final, ls] 
    (cons 
       (reduce 
          (fn [acc, char] 
              (.concat acc (str char))
          ) 
          "" 
          ls
       ) 
       final
    )
  )
  (list)
  (filter 
     (fn [word] 
        (= (count word) N)
     ) 
     (reduce 
        (fn [acc, length] 
           (concat 
              acc 
              (addLettersToWords acc)
           )  
        ) 
        user_list 
        (range 1 N)
     )
  )
)