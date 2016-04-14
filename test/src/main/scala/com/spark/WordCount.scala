package com.spark


import org.apache.spark.{
  SparkContext,
  SparkConf
}
import SparkContext._

object WordCount {
  
  private val AppName = "WordCountJob"

  // Run the word count. Agnostic to Spark's current mode of operation: can be run from tests as well as from main
   def main(args: Array[String]) {
  
    val sc = {
      val conf = new SparkConf().setAppName(AppName).setMaster("local")
      
      new SparkContext(conf)
    }
   
    // Adapted from Word Count example on http://spark-project.org/examples/
    val file = sc.textFile("/home/lpatil1/Desktop/spark-1.6.0/README.md")
    val words = file.flatMap(line => tokenize(line))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
    System.out.println("Laxmikant");
    wordCounts.saveAsTextFile("/home/lpatil1/Desktop/spark-1.6.0/wordc.txt")
  }

  // Split a piece of text into individual words.
  private def tokenize(text : String) : Array[String] = {
    // Lowercase each word and remove punctuation.
    text.toLowerCase.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+")
  }
}