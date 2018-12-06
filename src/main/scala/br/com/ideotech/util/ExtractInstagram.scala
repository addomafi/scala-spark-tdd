package br.com.ideotech.util

import org.apache.spark.rdd.RDD

import br.com.ideotech.model.SocialEvent

object ExtractInstagram {

  def getComments(events: RDD[Array[Byte]]): RDD[SocialEvent] = {
    ExtractParseableLine.get(events).filter(_.contains("text")).map(event => {
      val id = event.get("id").get
      val text = event.get("text").get
      SocialEvent("INSTAGRAM", id, text)
    })
  }
}
