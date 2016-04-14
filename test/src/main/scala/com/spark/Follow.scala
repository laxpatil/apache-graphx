package com.spark
import scala.math.random

import org.apache.spark._

object Follow {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Spark Pi").setMaster("local")
    val sc = new SparkContext(conf)
    var vertices = sc.textFile("metadata-raw.txt").
      flatMap { line => line.split("\\s+") }.distinct()
    vertices.map { vertex => vertex.replace("-", "") + "\t" + vertex }.
      saveAsTextFile("metadata-lookup3")
    sc.textFile("metadata-raw.txt").map { line =>
      var fields = line.split("\\s+")
      if (fields.length == 2) {
        fields(0).replace("-", "") + "\t" + fields(1).replace("-", "")
      }
    }.saveAsTextFile("metadata-processed3")
  }
}