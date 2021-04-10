(ns resultful-crud.core
  (:require [touchcan.db :as db]
            [touchcan.models :as models]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.api.sweet :refer [routes]]
            [resultful-crud.user :refer [user-routes]]
            [compojure.api.sweet :refer [api routes]])
  (:gen-class))

(def db-spec ;;change me please
  {:dbtype "postgres"
   :dbname "restful-crud"
   :user "postgres"
   :password "postgres"})

(def swagger-config
  {:ui "/swagger"
   :spec "/swagger.json"
   :options {:ui {:validatorUrl nil}
             :data {:info {:version "1.0.0", :title "Restful CRUD API"}}}})

(def app (apply routes user-routes))

(def app (api {:swagger swagger-config} (apply routes user-routes)))

(defn -main
  [& args]
  (db/set-default-db-connection! db-spec)
  (models/set-root-namespace! 'resultful-crud.models')
  (run-jetty app {:port 3000}))
