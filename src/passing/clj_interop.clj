(ns passing.clj-interop
  (:require
    [clojure.string :as str]
    [passing.interop :as i]
    )
  (:import (java.text SimpleDateFormat)
           (java.util Arrays Date)))

(defrecord CljTime [months]
  i/ITimeInterop
  (month-as-number [_ month-str]
    (Arrays/binarySearch (to-array months) month-str))
  (host-time [_]
    (Date.))
  (host-time [_ millis]
    (Date. millis))
  (host-time [_ year month-num day-of-month hour minute second]
    (Date. year month-num day-of-month hour minute second))
  (millis-component-of-host-time [_ host-time]
    (let [millis-per-second 1000
          total-millis (.getTime host-time)
          res (mod total-millis millis-per-second)]
      res))
  (parse-to-int [_ str]
    (Integer. (re-find #"[0-9]*" str)))
  ;;
  ;; example
  ;; Nov 10 2015 19:09:31
  ;; MM dd yyyy HH:mm:ss
  ;;
  (format-from-time [this host-time]
    (let [specific-format (SimpleDateFormat. "MM dd yyyy HH:mm:ss")
          as-str (.format specific-format host-time)
          [month day-of-month year time-str] (str/split as-str #" ")
          month-as-idx (dec (i/parse-to-int this month))
          [hour min sec] (str/split time-str #":")]
      {:month (nth months month-as-idx) :day-of-month day-of-month :year year :hour hour :min min :sec sec}))
  (crash [_ msg]
    (throw (Throwable. msg)))
  (crash [this]
    (i/crash this "Purposeful crash"))
  (log [_ txt]
    (println txt))
  (no-log [_ txt]
    ())
  )

