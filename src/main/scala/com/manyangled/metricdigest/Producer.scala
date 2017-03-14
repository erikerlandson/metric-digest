package com.manyangled.metricdigest

import java.util.{Date, Properties}

import scala.util.Random
import scala.util.{ Try, Success, Failure }

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

import org.isarnproject.sketches.TDigest


object td2json {
  def apply(td: TDigest, bins: Int = 10): String = {
    require(bins > 1)
    val (xmin, xmax) = (td.cdfInverse(0.0), td.cdfInverse(1.0))
    val xrange = xmax - xmin
    require(xrange > 0.0)
    val cdfx = for { k <- 0 to bins } yield (k.toDouble / bins.toDouble)
    val x = cdfx.map(td.cdfInverse[Double])
    val json = ("x" -> x) ~ ("bins" -> bins) ~ ("binsize" -> 1.0 / bins.toDouble)
    pretty(render(json))
  }
}

object MetricDigestProducer extends App {
  require(args.length >= 3)
  val broker = args(0)
  println(s"kafka broker= $broker")
  val pubInterval = args(1).toLong
  val minutes = args(2).toInt

  val props = new Properties()
  props.put("bootstrap.servers", broker)
  props.put("client.id", "MetricDigestProducer")
  props.put("block.on.buffer.full", "false")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
  println(s"producer= $producer")

  var metricDigest = TDigest.empty(delta = 0.05)

  var tLast = System.currentTimeMillis()
  var tAcc = 0L
  var tTot = 0L
  while (tTot <= (minutes * 60 * 1000).toLong) {
    val tCur = System.currentTimeMillis()
    val t = tCur - tLast
    tLast = tCur
    tAcc += t
    tTot += t

    metricDigest += Random.nextGaussian()

    if (tAcc >= pubInterval) {
      tAcc -= pubInterval
      val status = for {
        jsonCDF <- Try { td2json(metricDigest) }
        r <- Try {
          val rec = new ProducerRecord[String, String]("test", jsonCDF)
          producer.send(rec)
        }
      } yield (r)
      status match {
        case Success(_) => {
          println(s"Sent CDF at time ${tTot/1000L} seconds")
        }
        case Failure(e) => {
          println(s"Send FAILED: $e")
        }
      }
    }

  }
}
