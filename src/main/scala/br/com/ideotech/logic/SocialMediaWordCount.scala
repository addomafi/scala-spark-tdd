package br.com.ideotech.logic

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Duration, StreamingContext, Time}
import br.com.ideotech.model.SocialEvent

case class SocialMediaWordCount(word: String, count: Int)

object SocialMediaWordCount {

  type WordHandler = (RDD[SocialMediaWordCount]) => Unit

  def count(sc: SparkContext, events: RDD[SocialEvent]): RDD[SocialMediaWordCount] = count(sc, events, Set())

  def count(sc: SparkContext, events: RDD[SocialEvent], stopWords: Set[String]): RDD[SocialMediaWordCount] = {
    val stopWordsVar = sc.broadcast(stopWords)

    val words = prepareWords(events, stopWordsVar)

    val wordCounts = words.map(word => (word, 1)).reduceByKey(_ + _).map {
      case (word: String, count: Int) => SocialMediaWordCount(word, count)
    }

    val sortedWordCounts = wordCounts.sortBy(_.word)

    sortedWordCounts
  }

  def count(ssc: StreamingContext,
    events: DStream[SocialEvent],
    windowDuration: Duration,
    slideDuration: Duration)
    (handler: WordHandler): Unit = count(ssc, events, windowDuration, slideDuration, Set())(handler)

  def count(ssc: StreamingContext,
    events: DStream[SocialEvent],
    windowDuration: Duration,
    slideDuration: Duration,
    stopWords: Set[String])
    (handler: WordHandler): Unit = {

    val sc = ssc.sparkContext
    val stopWordsVar = sc.broadcast(stopWords)

    val words = events.transform(prepareWords(_, stopWordsVar))

    val wordCounts = words.map(x => (x, 1)).reduceByKeyAndWindow(_ + _, _ - _, windowDuration, slideDuration).map {
      case (word: String, count: Int) => SocialMediaWordCount(word, count)
    }

    wordCounts.foreachRDD((rdd: RDD[SocialMediaWordCount]) => {
      handler(rdd.sortBy(_.word))
    })
  }

  private def prepareWords(events: RDD[SocialEvent], stopWords: Broadcast[Set[String]]): RDD[String] = {
    events.flatMap(_.text.replaceAll("""\\,\s+""", ",").replaceAll("""\\.\s+""", ".").split("\\s"))
      .map(_.toLowerCase)
      .filter(!stopWords.value.contains(_)).filter(!_.isEmpty)
  }

}