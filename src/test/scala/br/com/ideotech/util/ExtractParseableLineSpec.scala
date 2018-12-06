package br.com.ideotech.util

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually

class ExtractParseableLineSpec extends FlatSpec with SparkSpec with GivenWhenThen with Matchers with Eventually {

  "Empty set" should "be counted" in {
    Given("empty set")
    val lines = Array("".getBytes())

    When("count lines")
    val parseableLines = ExtractParseableLine.get(sc.parallelize(lines)).collect()

    Then("empty count")
    parseableLines shouldBe empty
  }

  "A valid extraction" should "be counted" in {
    Given("valid and invalid set")
    val lines = Array("""{"created_time":"1280780324","text":"Olha esta black friday esta mesmo bacana!","from":"snoopdogg","id":"420"}""".getBytes())

    When("extract only a valid line")
    val parseableLines = ExtractParseableLine.get(sc.parallelize(lines)).collect()

    Then("confirm that extraction")
    parseableLines should have size 1
  }

  "An invalid extraction" should "be counted" in {
    Given("valid and invalid set")
    val lines = Array("""{"created_time":"1280780324","text":"Olha esta black friday esta mesmo bacana!","from":"snoopdogg","id":"420"}""".getBytes(),
          """{"@timestamp":"2018-10-09T14:58:18.846Z","flowId":"0000MPPLVMmDGf4Lz{"type":"plain"}""".getBytes())

    When("extract an invalid line")
    val parseableLines = ExtractParseableLine.getTry(sc.parallelize(lines)).filter(_.isFailure).collect()

    Then("has only one")
    parseableLines should have size 1
  }
}
