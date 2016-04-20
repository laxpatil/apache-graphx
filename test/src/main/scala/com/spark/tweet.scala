package com.spark

import scala.math.random
import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import scala.util.MurmurHash
import org.apache.spark.graphx.lib.ConnectedComponents



object tweet {
  
  
  
  def main(args: Array[String]) {
    
     val conf = new SparkConf().setAppName("Spark Pi").setMaster("local")
    val sc = new SparkContext(conf)
     
     val textRDD= sc.textFile("twitter_combined.txt",3)
     
     
     
     val edges :  RDD[Edge[String]]=  textRDD.map{ line=> 
       
       val fields = line.split(" ")
       Edge(fields(0).toLong, fields(1).toLong, "Friend")
       
       
     }
     
     
     
     val graph : Graph[String , String]= Graph.fromEdges(edges,"Property")
     
     println("Num vertices = "+graph.numVertices)
     println("Num Edges = "+graph.numEdges)
     
     val fact:RDD[String] =graph.triplets.map(trip=> trip.srcId +" is a friend of "+ trip.dstId)  // to find a friend
     
     //println(fact.foreach(println(_)))
     
     
     val g_indeg= graph.outDegrees
     
     val maxInDeg= g_indeg.sortBy(_._2, false).take(10)
     
    maxInDeg.foreach(println(_))
     
    
    
    val pggraph=graph.pageRank(0.01)
    
    val top10= pggraph.vertices.top(10)(Ordering.by { pageRank => pageRank._2 })
    
    top10.foreach(println(_))
  }
}