package br.com.ideotech.util

import org.apache.spark.rdd.RDD
import br.com.ideotech.model.SocialEvent

object ExtractTwitter {

  def getTweets(events: RDD[Map[String, String]]): RDD[SocialEvent] = {
    events.filter(_.contains("text")).map(event => {
      val id = event.get("id").get
      val text = event.get("text").get
      SocialEvent("TWITTER", id, text)
    })
  }
}
