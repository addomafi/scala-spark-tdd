import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
  lazy val hadoopTest = "org.apache.hadoop" % "hadoop-hdfs" % "2.8.4"
  lazy val sparkCore = "org.apache.spark" %% "spark-core" % "2.3.1" % "provided"
  lazy val sparkSql = "org.apache.spark" %% "spark-sql" % "2.3.1" % "provided"
  lazy val sparkStreaming = "org.apache.spark" %% "spark-streaming" % "2.3.1" % "provided"
  lazy val sparkStreamingKinesis = "org.apache.spark" %% "spark-streaming-kinesis-asl" % "2.3.1"
  lazy val kcl = "com.amazonaws" % "amazon-kinesis-client" % "1.8.10_custom"
  lazy val es = "org.elasticsearch" % "elasticsearch-hadoop" % "6.4.1" % "provided"
  lazy val protobuf = "com.github.os72" % "protobuf-java-shaded-261" % "0.9"
  lazy val sparkXmlUtils = "elsevierlabs-os" % "spark-xml-utils" % "1.8.0" % "provided"
  lazy val sparkNTime = "com.github.nscala-time" %% "nscala-time" % "2.18.0" % "provided"
}
