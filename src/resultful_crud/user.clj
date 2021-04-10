(ns restful-crud.user
    (:require   [schema.core :as s]
                [restful-crud.utils.string-util :as str]
                [resultful-crud.models.user :refer [User]]
                [buddy.hashers :as hashers]
                [clojure.set :refer [rename-keys]]
                [toucan.db :as db]
                [ring.util.http-response :refer [created]]
                [compojure.api.sweet :refer [POST]]))

(defn valid-username? [name]
    (str/non-blank-with-max-length? 50 name))

(defn valid-password? [password]
    (str/length-in-range? 5 50 password))

(s/defschema UserRequestSchema
    {:username (s/constrained s/Str valid-username?)
     :password (s/constrained s/Str valid-password?)
     :email (s/constrained s/Str str/email?)})

(defn id->created [id]
    (created (str "/users/" id) {:id id}))

(defn canonicalize-user-req [user-req] 
    (-> (update user-req :password hashers/derive)
        (rename-keys {:password :password_hash})))

(defn created-user-handler [create-user-req]
    (->> (canonicalize-user-req create-user-req)
         (db/insert! User)
         :id 
         id->created))

(def user-routes 
    [(POST "/user" []
        :body [create-user-req UserRequestSchema]
        (create-user-handler create-user-req))])