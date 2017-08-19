(defproject aws-telegram-bot-example-clojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [uswitch/lambada "0.1.2"]
                 [clj-http "3.7.0"]
                 [cheshire "5.8.0"]]
  :aot :all
  :plugins [[lein-clj-lambda "0.10.3"]]
  :lambda {"test" [{:api-gateway {:name "MyTelegramBotApi_test"}
                    :handler "com.vsubhuman.telegram.example_clj.Main"
                    :memory-size 256
                    :timeout 60
                    :function-name "MyTelegramBotFunction_test"
                    :region "us-west-2"
                    :policy-statements [{:Effect "Allow"
                                         :Action ["sqs:*"]
                                         :Resource ["arn:aws:sqs:us-west-2:*"]}]
                    :s3 {:bucket "com.vsubhuman.lambda"
                         :object-key "MyTelegramBotFunction_test.jar"}}]
           "production" [{:api-gateway {:name "MyTelegramBotApi"}
                          :handler "com.vsubhuman.telegram.example_clj.Main"
                          :memory-size 512
                          :timeout 300
                          :function-name "MyTelegramBotFunction"
                          :region "us-west-2"
                          :policy-statements [{:Effect "Allow"
                                               :Action ["sqs:*"]
                                               :Resource ["arn:aws:sqs:us-west-2:*"]}]
                          :s3 {:bucket "com.vsubhuman.lambda"
                               :object-key "MyTelegramBotFunction.jar"}}]})
