package br.com.ideotech.util

import org.scalatest.concurrent.Eventually
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.FlatSpec
import br.com.ideotech.model.SocialEvent

class ExtractInstagramSpec extends FlatSpec with SparkSpec with GivenWhenThen with Matchers with Eventually {

  val fixedLines = Array(
    """{"type": "ERROR", "message":"Forced Disconnect: Too many connections. (Allowed Connections = 2)","sent":"2017-01-11T18:12:52+00:00"}""".getBytes(),
    """{"created_time":"1280780324","text":"Olha esta black friday esta mesmo bacana!","from":"snoopdogg","id":"420"}""".getBytes())

  "Empty set" should "be counted" in {
    Given("empty set")
    val lines = Array("".getBytes())

    When("extract lines")
    val parseableLines = ExtractParseableLine.get(sc.parallelize(lines))
    val tweets = ExtractTwitter.getTweets(parseableLines).collect()

    Then("empty set")
    tweets shouldBe empty
  }
  
  "A registry" should "be counted" in {
    Given("filled set")
    val lines = fixedLines

    When("extract lines")
    val tweets = ExtractInstagram.getComments(sc.parallelize(lines)).collect()

    Then("filled set")
    tweets should equal (Array(SocialEvent("INSTAGRAM","420","Olha esta black friday esta mesmo bacana!")))
  }
}
