# What is iot-simulator
[![build](https://github.com/gszczutkowski/iot-simulator/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/gszczutkowski/iot-simulator/actions/workflows/maven-publish.yml) [![javadoc](https://img.shields.io/badge/javadoc-brightgreen.svg)](https://testcraftsmanship.com/iotsimulator/docs)

The iot-simulator allows you to mock AWS IoT devices connected to MQTT in your tests. You can easily define behaviors of this virtual device. It can work in three different ways:
  - subscriber connects to MQTT topic and is waiting for receiving previously defined message
  - publisher sends previously defined message to MQTT topic
  - responder connects to one MQTT topic, wait for defined message and when it is received then sends different message to other MQTT topic

In subscriber and responder we can wait for exact json message or message matching the mask. In mask we can ommit some json keys, we can define that some values should match defined regular expression. In responder we can pass values from received message to this which will be send in the response.
# How to use it
To create IotSimulator a few parameters are required by class constructor. We need to pass an endpoint which is IoT Core endpoint in the AWS. The clientId uniquely identify a MQTT connection. There are a few more parameters which are responsible for setting up connection and authentication. Detailed description of the argument you need to pass to set up connection can be found [here](http://aws-iot-device-sdk-java-docs.s3-website-us-east-1.amazonaws.com/com/amazonaws/services/iot/client/AWSIotMqttClient.html) as those arguments are just passed to the AWSIotMqttClient class constructor.
Example instantialization of IoTSimulator:
```java
IotSimulator deviceSimulator = new IotSimulator(
             "your-aws-iot-endpoint",
             "your-iot-simulator-id",
             awsCredentials,
             "eu-west-1");
```
#### Subscriber
With iot-simulator we can verify whether expected message reached expected topic. In example below we subscribing to topic tc/flatkrk100/settings/+ and verify that all messages which we get was send to topic tc/flatkrk100/settings/set and the message was {"id": 2, "state": 1}. When we replaced allMatch() to anyMatch() then we expect that at least one expected message reached expected topic. The start() method runs the subscribes which means that it is subscribed to the defined topic and gather all messages send to that topic.
```java
IotSubscriber subscriber = deviceSimulator
	.subscriber()
		.given()
			.subscribedTo("tc/flatkrk100/settings/+")
		.when()
			.topicIs("tc/flatkrk100/settings/set")
			.messageIs("{'id': 2, 'state': 1}")
		.then()
			.allMatch()
		.start();

    // Here should be test of application in which message {'id': 2, 'state': 1}
    // should be send to tc/flatkrk100/settings/set topic. 
    
assertTrue(subscriber.doesExpectedMessagesReachedTheTopic());
deviceSimulator.stop();
```
We can also use curly brackets when values of some arguments should match regular expression.
```java
IotSubscriber subscriber = deviceSimulator
	.subscriber()
		.given()
			.subscribedTo("tc/flatkrk100/settings/+")
			.strictMatchingDisabled()
		.when()
			.topicIs("tc/flatkrk100/settings/set")
			.messageIs("{'id': 2, 'state': '{[0-9]+}'}")
		.then()
			.allMatch()
		.start();

    // Here should be test of application in which message with any number 
    // as a state value should be send to tc/flatkrk100/settings/set topic.
    // So e.g. messages {'id': 2, 'state': 1} or {'id': 2, 'state': 190000}
    // are valid but {'id': 1, 'state': 1} is not.
    
assertTrue(deviceSimulator.doesExpectedMessagesReachedTheTopic());
deviceSimulator.stop();
```
With use of strictMatchingDisabled() method we can set that json keys not set in the mask can be ommited.
```java
IotSubscriber subscriber = deviceSimulator
	.subscriber()
		.given()
			.subscribedTo("tc/flatkrk100/settings/+")
			.strictMatchingDisabled()
		.when()
			.topicIs("tc/flatkrk100/settings/set")
			.messageIs("{'id': 2}")
		.then()
			.allMatch()
		.start();

    // Here should be test of application in which message with at least id 
    // with value 2 should be send to tc/flatkrk100/settings/set topic.
    // It does not matters what are the other keys in the message. So e.g.
    // messages {'id': 2}, {'id': 2, 'state': 1} or {'id': 2, 'state': 1,
    // 'timestamp': 1634240073} are valid but {'id': 1} is not.
    
assertTrue(deviceSimulator.doesExpectedMessagesReachedTheTopic());
deviceSimulator.stop();
```
#### Publisher
The next example ilustrate how just publish the message {'id': 2, 'fan': 1, 'light': 10, 'door':1} to topic 'tc/flatkrk100/settings/report'. 
```java
IotPublisher publisher = deviceSimulator
	.publisher()
		.given()
			.topic("tc/flatkrk100/settings/report")
			.message("{'id': 2, 'fan': 1, 'light': 10, 'door':1}");

publisher.publish();

    // Here should be tested that application which uses MQTT queue
    // reacted correctly on published message

deviceSimulator.stop();
```
We can also send many messages to different topics. With use of optional method publishingDelay(int) we can define what will be delay in seconds between every send message.
```java
String topic = "/myhome/groundfloor/livingroom";
List<String> messages = List.of("{'tmp': 23, 'id': 2}","{'id': 'AA', 'name': 'My room'}",
	 "{'a': {'b': {'c' : 21, 'd':'AWX'}, 'd': {'e': 0}}}");
Map<String, List<String>> messagesWithTopics = Map.of(topic, messages);

IotPublisher publisher = deviceSimulator
	.publisher()
		.given()
			.publishingDelay(1)
			.topicsWithMessages(messagesWithTopics);

publisher.publishAll();

    // Here should be test of application which verifies the applications 
    // behavior after sending all those messages.

deviceSimulator.stop();
```
#### Responder
Below is the example use of the iot-simulator which will publish {'id': 2, 'fan': 1, 'light': 10, 'door':1} to topic tc/flatkrk100/settings/report when message {'id': 2, 'fan': 1} will be received by iot-simulator from topic tc/flatkrk100/settings/set.
```java
IotResponder responder = deviceSimulator
	.responder()
		.given()
			.subscribedTo("tc/flatkrk100/settings/set")
		.when()
			.topicIs("tc/flatkrk100/settings/set")
			.messageIs("{'id': 2, 'fan': 1}")
		.then()
			.publishingTo("tc/flatkrk100/settings/report")
			.publishingMessage("{'id': 2, 'fan': 1, 'light': 10, 'door':1})
		.start();
		

    // Here should be test of application in which message {'id': 2, 'fan': 1}
    // should be send to tc/flatkrk100/settings/set topic and the answer of
    // the IoT device should be resend to tc/flatkrk100/settings/report
    // topic with message {'id': 2, 'fan': 1, 'light': 10, 'door':1}

deviceSimulator.stop();
```
The next example ilustrate how to force simulator to pass some information from message it gets as a subscriber to publishing message. Note that values written in the curly brackets in the body of when section are treated as an values to get. Values of those fields are written to field with value between curly brackets. The values with the curly brackets in the body of then section will be filled in with value from subscribed message with the same tag name. So in the example below when we get message {'id':2, 'state':0} then we will publish {'id': 2, 'fan': 0, 'light': 10, 'door':1}.
```java
IotResponder responder = deviceSimulator
	.responder()
		.given()
			.subscribedTo("tc/flatkrk100/settings/set")
		.when()
			.topicIs("tc/flatkrk100/settings/set")
			.messageIs("{'id': 2, 'state': '{fan_state}'}")
		.then()
			.publishingTo("tc/flatkrk100/settings/report")
			.publishingMessage("{'id': 2, 'fan': '{fan_state}', 'light': 10, 'door':1}")
		.start();

    // Here should be test of application in which message with any state
    // value (e.g. {'id': 2, 'state': 1}) should be send to 
    // tc/flatkrk100/settings/set topic and the answer of the IoT device
    // should be resend to tc/flatkrk100/settings/report topic with message
    // {'id': 2, 'fan': 1, 'light': 10, 'door':1} so the value from state
    // argument is rewritten to fan argument.

deviceSimulator.stop();
```
In responder we can also use strictMatchingDisabled() method in when() section. When strict matching is disabled then IotSimulator respond even after getting the message which contains more keys in the message then are specified in the mask.
# Contact

If you have any comment, remark or issue, please open an issue on
[IotSimulator Issue Tracker](https://github.com/gszczutkowski/iot-simulator/issues)
