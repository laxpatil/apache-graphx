package com.spark
import scala.math.random

import org.apache.spark._
import org.apache.spark.graphx._
// To make some of the examples work we will also need RDD
import org.apache.spark.rdd.RDD
import scala.util.MurmurHash


object versium2 {
  
  def parseData(str:String) : Record={
    val line= str.split("\\|")
    Record(line(0), line(1), line(2), line(3), line(4), line(5))
    
    
  }
  
   def max(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
    if (a._2 > b._2) a else b
  }

  // Compute the max degrees

  
   def main(args: Array[String]) {
     
     val conf = new SparkConf().setAppName("Spark Pi").setMaster("local")
    val sc = new SparkContext(conf)
    
     
     val graph = GraphLoader.edgeListFile(sc, "followers.txt")
// Find the connected components
val cc = graph.connectedComponents().vertices
// Join the connected components with the usernames
val users = sc.textFile("users.txt").map { line =>
  val fields = line.split(",")
  (fields(0).toLong, fields(1))
}
val ccByUsername = users.join(cc).map {
  case (id, (username, cc)) => (username, cc)
}
// Print the result
println(ccByUsername.collect().mkString("\n"))
     
     
   }
}