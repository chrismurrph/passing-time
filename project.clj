(defproject graphing "0.1.0-SNAPSHOT"
  :description "supports graphing by providing 'timer style' numbers for time"
  :url "http://localhost:3449/"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"]
                 [org.clojure/core.async "0.2.371"]]

  :plugins [[lein-cljsbuild "1.0.6"]]

  :source-paths ["src"]
  
  :clean-targets ^{:protect false} ["resources/public/js" "target" 
                                    "resources/public/css"]

  :cljsbuild { 
    :builds [{:id "passing"
              :source-paths ["src"]
              :compiler {
                :main graphing.core
                :output-to "resources/public/js/graphing.js"
                :output-dir "resources/public/js/out"
                :asset-path "js/out"
                :optimizations :none}}]                
                }

  :main ^:skip-aot graphing.passing-time)
