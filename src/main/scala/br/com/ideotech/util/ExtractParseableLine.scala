package br.com.ideotech.util

import scala.util.matching.Regex
import org.apache.spark.rdd.RDD
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import scala.util.Failure
import scala.util.Try
import scala.util.Success
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.spark.streaming.dstream.DStream

/**
 * Extract text based on a regex that is JSON parseable
 *
 * @author adautomartins
 *
 */
object ExtractParseableLine {

  /**
   * Get parseable JSON RDD
   *
   * @param regex Given regex to test
   * @param rdd Given RDD with text
   */
  def getTry(lines: RDD[Array[Byte]]): RDD[Try[Map[String, String]]] = {
    lines.map(item => {
      val itemStr = new String(item)
      val mapper = new ObjectMapper() with ScalaObjectMapper
      mapper.registerModule(DefaultScalaModule)
      val parsedJson = Try(mapper.readValue[Map[String, String]](itemStr))
      if (parsedJson.isSuccess == true) {
        Success(parsedJson.get)
      } else {
        Failure(new Exception(s"Non JSON parseable line: $itemStr"))
      }
    })
  }
  
  def get(lines: RDD[Array[Byte]]): RDD[Map[String, String]] = {
    getTry(lines).filter(_.isSuccess).map(_.get)
  }
}