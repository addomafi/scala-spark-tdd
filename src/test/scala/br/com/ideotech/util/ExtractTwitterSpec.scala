package br.com.ideotech.util

import org.scalatest.concurrent.Eventually
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.FlatSpec
import br.com.ideotech.model.SocialEvent

class ExtractTwitterSpec extends FlatSpec with SparkSpec with GivenWhenThen with Matchers with Eventually {

  val fixedLines = Array(
    """{"type": "ERROR", "message":"Forced Disconnect: Too many connections. (Allowed Connections = 2)","sent":"2017-01-11T18:12:52+00:00"}""".getBytes(),
    """{"created_at":"Thu Apr 06 15:24:15 +0000 2017","id":850006245121695700,"id_str":"850006245121695744","text":"Veja bem esta promoção black friday é na verdade black fraude https://t.co/XweGngmxlP"}""".getBytes())

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
    val parseableLines = ExtractParseableLine.get(sc.parallelize(lines))
    val tweets = ExtractTwitter.getTweets(parseableLines).collect()

    Then("filled set")
    tweets should equal (Array(SocialEvent("TWITTER","850006245121695700","Veja bem esta promoção black friday é na verdade black fraude https://t.co/XweGngmxlP")))
  }
}
