(ns aws-telegram-bot-example-clojure.core
  (:require [uswitch.lambada.core :refer [deflambdafn]]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [clj-http.client :as web])
  (:import (java.time Instant)
           (java.time.format DateTimeFormatter)))

(def handler-class-name 'com.vsubhuman.telegram.example_clj.Main)
(def base-url (str  "https://api.telegram.org/bot" (System/getenv "bot_token")))

(defn post [url data]
  (web/post url {:form-params data :content-type :json :accept :json}))

(defn send-message [chat-id text]
  (let [msg {:chat_id chat-id :text text}]
    (println "Sending message:" msg)
    (let [resp (post (str base-url "/sendMessage") msg)]
      (println "Sent message:" resp)
      resp)))

(defn format-seconds [seconds]
  (some->> seconds
           Instant/ofEpochSecond
           (.format DateTimeFormatter/ISO_INSTANT)))

(def version (delay
   (-> (Class/forName (str handler-class-name))
       (.getResourceAsStream "/project.clj")
       slurp
       read-string
       (nth 2))))

(deflambdafn handler-class-name
   [in out ctx]
   (let [evt (json/parse-stream (io/reader in) keyword)
         up-id (:update_id evt)
         msg (:message evt)
         text (:text msg)
         tstamp (format-seconds (:date msg))
         chat (:chat msg)
         chat-id (:id chat)]
     (println "Update@" tstamp ":" evt)
     (if chat-id
       (let [txt (if (.equalsIgnoreCase "/version" text)
                   (str "Version: " @version)
                   (str "Echo> " text))]
         (send-message chat-id txt)))
     (println "Finished processing" up-id)))