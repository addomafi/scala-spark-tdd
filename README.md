# Projeto Spark Streaming - Meetup Devops Alphaville

Projeto de exemplo que visa demonstrar as técnicas de testes unitários para códigos que fazem uso da framework Spark Streaming.

Features
========

* [ClockWrapper](src/test/scala/org/apache/spark/ClockWrapper.scala) for efficient clock management in Spark Streaming jobs.
* Base traits for testing [Spark](src/test/scala/org/mkuthan/spark/SparkSpec.scala), [Spark Streaming](src/test/scala/org/mkuthan/spark/SparkStreamingSpec.scala) and [Spark SQL](src/test/scala/org/mkuthan/spark/SparkSqlSpec.scala) to eliminate boilerplate code.
* Sample applications to show how to make your code testable.
* All tests can be run or debugged directly from IDE, or using SBT.
* All test fixtures are prepared as in-memory data structures.
* SBT is configured to avoid problems with multiple Spark contexts in the same JVM [SPARK-2243](https://issues.apache.org/jira/browse/SPARK-2243).
* SBT is configured to prepare project assembly for deployment on the cluster.

References
==========

* [http://mkuthan.github.io/blog/2015/03/01/spark-unit-testing/](http://mkuthan.github.io/blog/2015/03/01/spark-unit-testing/)
* [https://github.com/holdenk/spark-testing-base](https://github.com/holdenk/spark-testing-base)
