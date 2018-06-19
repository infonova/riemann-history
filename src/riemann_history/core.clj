(ns riemann-history.core)

(defn history
  "Just messing around..."
  [event]
  (str "Called plugin with event service: " (:service event)))
