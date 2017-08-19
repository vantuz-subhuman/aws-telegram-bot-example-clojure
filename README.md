# AWS-Telegram-Bot-Example-Clojure
- Telegram bot
- Webhook on [AWS-Lambda]
- Implemented in [Clojure] with [Leiningen]
- [clj-lambda-utils] is used to install and update lambdas
- [uswitch/lambada] is used to AOT compile a handler class
- [clj-http] is used to send Telegram requests
- [cheshire] is used to read and write JSON

## About
Just an example (and a template) of how easily you can create a Telegram bot
hosted on AWS-Lambda service as webhook.

Result bot can't do much, it just sends you its version in response to `/version`
message, or echoes any other message.

## Usage
Leiningen plugin allows you to easily create and update the lambda function.
In order to use it, you have to have an Amazon AWS account. Then you need to
[create an access key](http://docs.aws.amazon.com/general/latest/gr/managing-aws-access-keys.html)
for your account and to [configure the AWS CLI](http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html)
on your dev machine. At this point you should have a `default` profile configuration available
as [environment variables](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html) in your OS.
This should be enough for the plugin to find the right credentials.

Then you can configure your future (or existing) lambda parameters:
```
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
                               :object-key "MyTelegramBotFunction.jar"}}]}
```

Sadly the way lein plugin is working does not allow us to extract variables =(

**Note!** That if you already have a prepared lambda, then you only have to specify:
  1. `:function-name ""`
  2. `:s3 {:bucket "" :object-key ""}`

All the other properties are only required for lambda installation.

**Note2!** The new roles will be created for both: the lambda and the gateway API!
You may manage roles in the [IAM console](https://console.aws.amazon.com/iam/home).

Handler points to the class that implements the lambda interface
and that will be used as the entry point.

### Create lambda

When all parameters are set you may call commands:
```
lein lambda install test
```
or
```
lein lambda install production
```
These commands will attempt to:
  1. Build uberjar
  2. Upload it to S3 bucket
  3. Create roles for API Gateway
  4. Create API for testing or production
  5. Create roles for lambda
  6. Create test or prod lambda
  7. Upload the code
 
### Update lambda

After you already got an existing lambda and you just want to update
its code, call:
```
lein lambda update test
```
or
```
lein lambda update production
```
In order to build an uberjar and to upload it to either test or production
lambda respectively.

## `bot_token`

**Note!** That your lambda must have an environment property named `bot_token`
with the value of the telegram token of your either test or production bot.
You may find and configure youe lambda function in the console: https://console.aws.amazon.com/lambda/home

## API Gateway

If you specified the `:api-gateway` in the config - the API is already created for you.
So you may check out [API-Gateway console](https://console.aws.amazon.com/apigateway/home)
to find out fully qualified URL addresses and proceed to set the webhook.
 
But if you didn't specify the `:api-gateway` - you need to create it manually:

Once lambda is ready and running, you need to create two publicly available endpoints in order for telegram
to be able to send you updates. You can manage it with [API-Gateway console](https://console.aws.amazon.com/apigateway/home)

You may create a single API with two resources: `POST /test` and `POST /prod`, and wire those methods to call your test
and production lambdas respectively. You also may include your telegram bot token into resource path
as [Telegram suggests](https://core.telegram.org/bots/api#setwebhook)

**NOTE!** Do not forget to stage your AWS-API when you done configuring it. The staging will provide you
with the fully qualified URL address of the created resources.

### Set webhook

When you have your API staged, you need to register URL addresses in the Telegram, so they will know
where to send all the updates for your bot. You can do it by sending a POST request like this:
```
curl -X POST "https://api.telegram.org/bot${token}/setWebhook?url=${aws_url}"
```

Insert the token of your bot and the corresponding URL address and you good to go.
After successull request like that - your lambda should start to receive all the messages sent to your bot.

## Fire up

Note that when you call your bot first time after an update, or ufter a long time
pause - it needs a time to fire up your lambda environment. Amazon actually starts up
a lambda only when it's called and kills it when it not called for some time.

So give it a few seconds.

## Logs

Do not forget that a LogGroup with a name `/aws/lambda/${LambdaName}` is automatically created for each lambda.
You can find logs in [CloudWatch console](https://console.aws.amazon.com/cloudwatch/home#logs:)

[AWS-Lambda]: https://aws.amazon.com/lambda/
[Clojure]: https://clojure.org/
[Leiningen]: https://leiningen.org/
[clj-lambda-utils]: https://github.com/mhjort/clj-lambda-utils
[uswitch/lambada]: https://github.com/uswitch/lambada
[clj-http]: https://github.com/dakrone/clj-http
[cheshire]: https://github.com/dakrone/cheshire
