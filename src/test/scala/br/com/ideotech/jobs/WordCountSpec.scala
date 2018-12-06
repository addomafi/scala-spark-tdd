package br.com.ideotech.jobs

import org.scalatest.concurrent.Eventually
import org.scalatest.GivenWhenThen
import br.com.ideotech.util.SparkStreamingSpec
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.time.Millis
import org.apache.spark.streaming.Seconds
import org.scalatest.time.Span
import scala.collection.mutable.ListBuffer
import br.com.ideotech.logic.SocialMediaWordCount
import org.apache.spark.rdd.RDD

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import br.com.ideotech.util.ExtractInstagram
import br.com.ideotech.util.ExtractTwitter
import br.com.ideotech.util.ExtractParseableLine
import br.com.ideotech.model.SocialEvent
import org.apache.spark.sql.SparkSession

class SocialMediaWordCountSpec extends FlatSpec with SparkStreamingSpec with GivenWhenThen with Matchers with Eventually {
  
  // default timeout for eventually trait
  implicit override val patienceConfig =
    PatienceConfig(timeout = scaled(Span(12000, Millis)))
  private val windowDuration = Seconds(4)
  private val slideDuration = Seconds(2)
  
  "Sample set" should "be counted" in {
    Given("streaming context is initialized")
    val lines = mutable.Queue[RDD[SocialEvent]]()

    var results = ListBuffer.empty[Array[SocialMediaWordCount]]

    SocialMediaWordCount.count(ssc,
      ssc.queueStream(lines),
      windowDuration,
      slideDuration) { (wordsCount: RDD[SocialMediaWordCount]) =>
        val wcRdd = wordsCount.sortBy(_.word)
        SparkSession.builder.master("local").getOrCreate.createDataFrame(wcRdd).show(false)
        results += wcRdd.collect()
    }

    ssc.start()

    When("first set of social events queued")
    results.clear()
    val tweets = ExtractTwitter.getTweets(ExtractParseableLine.get(sc.parallelize(Array(
      """{"type": "ERROR", "message":"Forced Disconnect: Too many connections. (Allowed Connections = 2)","sent":"2017-01-11T18:12:52+00:00"}""".getBytes(),
      """{"created_at":"Thu Apr 06 15:24:15 +0000 2017","id":850006245121695700,"id_str":"850006245121695744","text":"Veja bem esta promoção black friday é na verdade black fraude https://t.co/XweGngmxlP"}""".getBytes()))))
    val comments = ExtractInstagram.getComments(sc.parallelize(Array(
      """{"type": "ERROR", "message":"Forced Disconnect: Too many connections. (Allowed Connections = 2)","sent":"2017-01-11T18:12:52+00:00"}""".getBytes(),
      """{"created_time":"1280780324","text":"Olha esta black friday esta mesmo bacana","from":"snoopdogg","id":"420"}""".getBytes(),
      """{"created_time":"1280780324","text":"Olha esta black friday esta mesmo bacana","from":"snoopdogg","id":"420"""".getBytes())))
    lines += tweets.union(comments)

    Then("social events counted after first slide")
    advanceClock(slideDuration)
    eventually {
      results.last should equal(Array(
        SocialMediaWordCount("bacana",1), 
        SocialMediaWordCount("bem",1), 
        SocialMediaWordCount("black",3), 
        SocialMediaWordCount("esta",3), 
        SocialMediaWordCount("fraude",1), 
        SocialMediaWordCount("friday",2), 
        SocialMediaWordCount("https://t.co/xwegngmxlp",1), 
        SocialMediaWordCount("mesmo",1), 
        SocialMediaWordCount("na",1), 
        SocialMediaWordCount("olha",1), 
        SocialMediaWordCount("promoção",1), 
        SocialMediaWordCount("veja",1), 
        SocialMediaWordCount("verdade",1), 
        SocialMediaWordCount("é",1)))
    }
    
    When("second set of social events queued")
    results.clear()
    val tweetsB = ExtractTwitter.getTweets(ExtractParseableLine.get(sc.parallelize(Array(
      """{"created_at":"Thu Apr 06 15:24:15 +0000 2017","id":850006245121695700,"id_str":"850006245121695744","text":"Esta muito bacana esta black friday"}""".getBytes()))))
    val commentsB = ExtractInstagram.getComments(sc.parallelize(Array(
      """{"created_time":"1280780324","text":"Mas toda vez eu caio nesta black fraude","from":"snoopdogg","id":"420"}""".getBytes())))
    lines += tweetsB.union(commentsB)

    Then("social events counted after second slide")
    advanceClock(slideDuration)
    eventually {
      results.last should equal(Array(
        SocialMediaWordCount("bacana",2), 
        SocialMediaWordCount("bem",1), 
        SocialMediaWordCount("black",5), 
        SocialMediaWordCount("caio",1), 
        SocialMediaWordCount("esta",5), 
        SocialMediaWordCount("eu",1), 
        SocialMediaWordCount("fraude",2), 
        SocialMediaWordCount("friday",3), 
        SocialMediaWordCount("https://t.co/xwegngmxlp",1), 
        SocialMediaWordCount("mas",1), 
        SocialMediaWordCount("mesmo",1), 
        SocialMediaWordCount("muito",1), 
        SocialMediaWordCount("na",1), 
        SocialMediaWordCount("nesta",1), 
        SocialMediaWordCount("olha",1), 
        SocialMediaWordCount("promoção",1), 
        SocialMediaWordCount("toda",1), 
        SocialMediaWordCount("veja",1), 
        SocialMediaWordCount("verdade",1), 
        SocialMediaWordCount("vez",1), 
        SocialMediaWordCount("é",1)))
    }
    
    When("nothing more queued")
    results.clear()

    Then("social events counted after third slide")
    advanceClock(slideDuration)
    eventually {
      results.last should equal(Array(
        SocialMediaWordCount("bacana",1), 
        SocialMediaWordCount("bem",0), 
        SocialMediaWordCount("black",2), 
        SocialMediaWordCount("caio",1), 
        SocialMediaWordCount("esta",2), 
        SocialMediaWordCount("eu",1), 
        SocialMediaWordCount("fraude",1), 
        SocialMediaWordCount("friday",1), 
        SocialMediaWordCount("https://t.co/xwegngmxlp",0), 
        SocialMediaWordCount("mas",1), 
        SocialMediaWordCount("mesmo",0), 
        SocialMediaWordCount("muito",1), 
        SocialMediaWordCount("na",0), 
        SocialMediaWordCount("nesta",1), 
        SocialMediaWordCount("olha",0), 
        SocialMediaWordCount("promoção",0), 
        SocialMediaWordCount("toda",1), 
        SocialMediaWordCount("veja",0), 
        SocialMediaWordCount("verdade",0), 
        SocialMediaWordCount("vez",1), 
        SocialMediaWordCount("é",0)))
    }
   
    When("nothing more queued")
    results.clear()

    Then("social events counted after fourth slide")
    advanceClock(slideDuration)
    eventually {
      results.last should equal(Array(
        SocialMediaWordCount("bacana",0), 
        SocialMediaWordCount("bem",0), 
        SocialMediaWordCount("black",0), 
        SocialMediaWordCount("caio",0), 
        SocialMediaWordCount("esta",0), 
        SocialMediaWordCount("eu",0), 
        SocialMediaWordCount("fraude",0), 
        SocialMediaWordCount("friday",0), 
        SocialMediaWordCount("https://t.co/xwegngmxlp",0), 
        SocialMediaWordCount("mas",0), 
        SocialMediaWordCount("mesmo",0), 
        SocialMediaWordCount("muito",0), 
        SocialMediaWordCount("na",0), 
        SocialMediaWordCount("nesta",0), 
        SocialMediaWordCount("olha",0), 
        SocialMediaWordCount("promoção",0), 
        SocialMediaWordCount("toda",0), 
        SocialMediaWordCount("veja",0), 
        SocialMediaWordCount("verdade",0), 
        SocialMediaWordCount("vez",0), 
        SocialMediaWordCount("é",0)))
    }
  }
}
