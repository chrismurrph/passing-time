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
(def one-minute 60)
(def one-hour (* 60 one-minute))
(def one-day (* 24 one-hour))
(def one-year (reduce + (map #(* % one-day) (vals last-day-of-months))))

(defn seconds->days [seconds] (/ seconds one-day))

;;
;; If the month is Jan the value s/be 0
;;
(defn month->seconds [month-str])

(defn year->seconds [num-years]
  (* num-years one-year))

(defn day->seconds [num-days]
  (* num-days one-day))

(defn hour->seconds [num-hours]
  (* num-hours one-hour))

(defn minute->seconds [num-minutes]
  (* num-minutes one-minute))

;;
;; times are in seconds.
;;
(defn approx= [comparison-diff actual-diff]
  (let [diff (- comparison-diff actual-diff)]
    (<= -2 diff 2)))

;; If we just generate browser session data then time-zero being when the browser app starts is fine
(def time-zero (atom nil))

;;
;; indexed by when discovered in passing-time (so is just a number).
;; put in:
;; (swap! variances conj [10 :a])
;; take out:
;; (swap! variances dissoc 10)
;; just get value:
;; (get @variances 10) ;;=> returns nil if not there
;;

;;
;; Record variances in order as they happen. This, time-zero and anomolies will all be durable i.e. be kept
;; in the database. key is passing-time, and val is the variance at that time.
;;
(def variances (atom (sorted-map)))

(def leap-years [2008 2012 2016 2020])

;;
;; These are the start times. The end times are always in early April.
;; When it should be 2am the clock changes to be 3am. We loose some sleep,
;; but it appears to be lighter later (by wallclock time).
;;
(def daylight-savings-times [{:year 2015 :month "Oct" :day-of-month 4 :hour 2}
                             {:year 2016 :month "Oct" :day-of-month 2 :hour 2}
                             {:year 2017 :month "Oct" :day-of-month 1 :hour 2}
                             {:year 2018 :month "Oct" :day-of-month 7 :hour 2}
                             {:year 2019 :month "Oct" :day-of-month 6 :hour 2}])

(defn right-hour [established-time wallclock-time]
  (= (inc (:hour established-time)) (:hour wallclock-time)))

(defn same-year [established-time wallclock-time]
  (= (:year established-time) (:year wallclock-time)))

;;
;; diff is in seconds, the amount that the wallclock has suddenly gone ahead (or behind if -ive)
;; wallclock is when it happened, so we can easily see for example if it was on the 29th.
;;
(defn recognised-dst-start? [wallclock-time diff]
  (let [clock-on-an-hour (approx= one-hour diff)
        _ (log (str "In recognised-dst-start? clock-on-an-hour is: " clock-on-an-hour))
        found-year-and-hour (filter (fn [time-map] (and (right-hour time-map wallclock-time) (same-year time-map wallclock-time))) daylight-savings-times)
        _ (log (str "In recognised-dst-start? found-year-and-hour is: " found-year-and-hour))
        ]
    (and found-year-and-hour clock-on-an-hour)))

(defn recognised-dst-end? [wallclock-time diff]
  (let [clock-back-an-hour (approx= one-hour (* -1 diff))
        _ (log (str "In recognised-dst-end? clock-back-an-hour is: " clock-back-an-hour))
        ]
    clock-back-an-hour))

(defn recognised-leap-year? [wallclock-time diff]
  (let [into-29th (= 29 (:day-of-month wallclock-time))
        _ (log (str "In recognised-leap-year? into-29th is: " into-29th))
        year (:year wallclock-time)
        during-leap (filter #(= year %) leap-years)
        _ (log (str "In recognised-leap-year? during-leap is: " during-leap))
        ]
    (and into-29th during-leap)))

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
;(def rules [
;            {:id :dst
;             :start {:year nil :month "Oct" :day-of-month (map inc (range 7)) :hour 2 :minute 0 :second 0}
;             :expected-diff one-hour
;             :duration one-year
;             :predicate nil}
;            {:id :leap-year
;             :start {:year nil :month "Feb" :day-of-month 29 :hour nil :minute nil :second nil}
;             :expected-diff one-day
;             :duration one-day
;             :predicate (fn [derived-time] (= 29 (:day-of-month derived-time)))}])
