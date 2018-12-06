import Dependencies._
  
// Create a default Scala style task to run with tests
lazy val testScalastyle = taskKey[Unit]("testScalastyle")
testScalastyle := scalastyle.in(Test).toTask("").value

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := scalastyle.in(Compile).toTask("").value

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "br.com.ideotech",
      scalaVersion := "2.11.11",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "ScalaSparkTDD",
    javaOptions in Test ++= Seq(
        "-Xmx2048m",
        "-Xdebug",
        "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
    ),
    scalacOptions ++= Seq(
	    "-deprecation",
	    "-encoding", "UTF-8",
	    "-feature",
	    "-language:existentials",
	    "-language:higherKinds",
	    "-language:implicitConversions",
	    "-unchecked",
	    "-Xfatal-warnings",
	    "-Xfuture",
	    "-Xlint",
	    "-Yno-adapted-args",
	    "-Ywarn-dead-code"
	    //"-Ywarn-numeric-widen",
	   // "-Ywarn-unused-import"
	  ),
	parallelExecution in Test := false,
	fork in Test := true,
    (test in Test) := ((test in Test) dependsOn testScalastyle).value,
	(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value,
    libraryDependencies ++= Seq (
		scalaTest % Test,
		sparkXmlUtils,
		sparkCore,
		sparkSql,
		sparkStreaming,
		(sparkStreamingKinesis).
	    exclude("org.slf4j", "jcl-over-slf4j").
	    exclude("commons-beanutils", "commons-beanutils-core").
	    exclude("commons-collections", "commons-collections").
	    exclude("commons-logging", "commons-logging").
	    exclude("org.apache.hadoop", "hadoop-yarn-common").
	    exclude("org.glassfish.hk2.external", "javax.inject").
	    exclude("javax.inject", "javax.inject").
	    exclude("aopalliance", "aopalliance").
	    exclude("net.java.dev.jets3t", "jets3t").
	    exclude("com.amazonaws", "aws-java-sdk-s3").
	    exclude("com.amazonaws", "amazon-kinesis-client").
	   	exclude("org.apache.http", "*").
	   	exclude("org.apache.log4j", "*").
	   	exclude("org.apache.oro", "*").
	   	exclude("org.apache.ivy", "*").
	   	exclude("org.apache.xerces", "*").
	   	exclude("org.apache.ivy", "*").
	   	exclude("org.apache.hadoop", "*").
	   	exclude("org.apache.spark", "spark-core_2.11").
	   	exclude("com.amazonaws", "*").
	   	exclude("org.apache.zookeeper", "zookeeper").
	   	exclude("org.apache.curator", "*").
	   	exclude("com.fasterxml.jackson.core", "*").
	   	exclude("org.apache.commons", "*").
	   	exclude("org.joda.time", "*").
	   	exclude("org.apache.spark", "spark-tags_2.11").
	   	exclude("org.apache.spark", "spark-streaming_2.11").
	   	exclude("org.spark-project.spark", "*"),
		kcl,
		protobuf,
		es,
		sparkNTime
	),
    resolvers ++= Seq(
	    "Spark Packages" at "https://dl.bintray.com/spark-packages/maven",
	    Resolver.mavenLocal
	)
  )
