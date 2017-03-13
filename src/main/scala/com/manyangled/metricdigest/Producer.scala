package com.manyangled.metricdigest

import java.util.{Date, Properties}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.util.Random

object ScalaProducerExample extends App {
  val broker = args(0)
  println(s"kafka broker= $broker")

  val props = new Properties()
  props.put("bootstrap.servers", broker)
  props.put("client.id", "MetricDigestProducer")
  props.put("block.on.buffer.full", "false")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
  println(s"producer= $producer")

  for {
    itr <- 1 to 10
  } {
    println(s"itr= $itr")

    val msg = new ProducerRecord[String, String]("test", "message")
    producer.send(msg)
    Thread.sleep(1)
  }
}
