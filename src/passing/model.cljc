(ns passing.model
  (:require
    #?@(:clj  [[passing.interop :as i]
               [passing.clj-interop :as ci]]
        :cljs [[passing.interop :as i]
               [passing.cljs-interop :as ci]
               ]))
  )

(def months ["Jan" "Feb" "Mar" "Apr" "May" "Jun" "Jul" "Aug" "Sep" "Oct" "Nov" "Dec"])

#?(:cljs (def interop (ci/->CljsTime months)))
#?(:clj (def interop (ci/->CljTime months)))
(def log (partial i/log interop))

(def last-day-of-months {"Jan" 31 "Feb" 28 "Mar" 31 "Apr" 30 "May" 31 "Jun" 30 "Jul" 31 "Aug" 31 "Sep" 30 "Oct" 31 "Nov" 30 "Dec" 31})

;;
;; These amounts are in normal time, for which you can do date arithmetic if you are careful. Normal time is before
;; variances (and anomolies) are applied. Normal time uses standard rules, such as the last-day-of-months above.
;; A normal year is the minimum length year
;; :duration and :expected-diff are both in seconds
;;
(def one-hour (* 60 60))
(def one-day (* 24 one-hour))
(def one-year (reduce + (map #(* % one-day) (vals last-day-of-months))))
(log (str "Number seconds in a normal year: " one-year))

;;
;; :expected-diff is by
;; When these rules are recognised they are turned into variances.
;; The predicate is incorporated into a more general predicate that makes sure the :diff and the :expected-diff are the same
;; (within limits of error that used generally)
;;
;; When time-zero is first assigned there may already be a rule that should be turned into a variance. For instance it
;; is November now in Aust which means we are on DST (Daylight Savings Time), so at the moment that time-zero is worked
;; out the :dst rule will need to be added into variances.
;;
(def rules [
            {:id :dst
             :start {:year nil :month "Oct" :day-of-month (map inc (range 7)) :hour 2 :minute 0 :second 0}
             :expected-diff one-hour
             :duration one-year
             :predicate nil}
            {:id :leap-year
             :start {:year nil :month "Feb" :day-of-month 29 :hour nil :minute nil :second nil}
             :expected-diff one-day
             :duration one-day
             :predicate (fn [derived-time] (= 29 (:day-of-month derived-time)))}])
