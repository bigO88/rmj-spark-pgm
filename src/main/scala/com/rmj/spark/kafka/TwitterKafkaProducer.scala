package com.rmj.spark.kafka

import java.util.Properties
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

import kafka.producer.KeyedMessage
import kafka.producer.ProducerConfig

import com.google.common.collect.Lists
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Client
import com.twitter.hbc.core.Constants
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.auth.Authentication
import com.twitter.hbc.httpclient.auth.OAuth1

object TwitterKafkaProducer {

  val topic = "twitter-topic"

  def run(consumerKey: String, consumerSecret: String,
          token: String, secret: String,hashTag:String): Unit = {

    val properties = new Properties()
    properties.put("metadata.broker.list", "localhost:9092")
    properties.put("serializer.class", "kafka.serializer.StringEncoder")
    properties.put("client.id", "camus")

    val producerConfig = new ProducerConfig(properties)

    val producer = new kafka.javaapi.producer.Producer[String, String](producerConfig)

    val queue = new LinkedBlockingQueue[String](10000)
    val endpoint = new StatusesFilterEndpoint()
    // add some track terms
    endpoint.trackTerms(Lists.newArrayList("twitterapi",hashTag))

    val auth = new OAuth1(consumerKey, consumerSecret, token,
      secret)
    // Authentication auth = new BasicAuth(username, password);

    // Create a new BasicClient. By default gzip is enabled.
    val client = new ClientBuilder().hosts(Constants.STREAM_HOST)
      .endpoint(endpoint).authentication(auth)
      .processor(new StringDelimitedProcessor(queue)).build();

    // Establish a connection
    client.connect();

    // Do whatever needs to be done with messages
    for (msgRead <- 0 to 1000) {
      var message: KeyedMessage[String, String] = null
      try {

        message = new KeyedMessage[String, String](topic, queue.take())

      } catch {
        case e: InterruptedException => {

          e.printStackTrace()
        }
      }
      producer.send(message)
    }
    producer.close
    client.stop()
  }

  def main(args: Array[String]): Unit = {

    val consumerKey=args(0)
    val consumerSecret=args(1)
    val token=args(2)
    val secret=args(3)
    val hashTag=args(4)
    
    try {
			TwitterKafkaProducer.run(consumerKey, consumerSecret, token,secret,hashTag)
		} catch {
		  case e:InterruptedException => {
			System.out.println(e)
     }
  }
  }
}