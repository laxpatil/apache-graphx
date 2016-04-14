package com.spark
import scala.math.random
import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import scala.util.MurmurHash
import org.apache.spark.graphx.lib.ConnectedComponents

case class Record ( clusterID : String, recordID:String, name : String,address: String, phone: String, email:String)

object versium {
  
  def parseData(str:String) : Record={
    val line= str.split("\\|")
    Record(line(0), line(1), line(2), line(3), line(4), line(5))
    
    
  }
  
   def max(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
    if (a._2 > b._2) a else b
   }
  
  
   def main(args: Array[String]) {
     
     val conf = new SparkConf().setAppName("Spark Pi").setMaster("local")
    val sc = new SparkContext(conf)
     
     val textRDD= sc.textFile("small.csv",3)
     
     val recordRDD= textRDD.map(parseData).distinct()
     
     val recordNodes : RDD[(VertexId, (String, String) )]=  recordRDD.flatMap { r => List((MurmurHash.stringHash(r.clusterID).toLong, (r.clusterID, "clusterID")), 
                       (MurmurHash.stringHash(r.recordID).toLong, (r.recordID, "recordID")),
                       (MurmurHash.stringHash(r.name).toLong, (r.name, "name")),
                       (MurmurHash.stringHash(r.phone).toLong, (r.phone, "phone")),
                       (MurmurHash.stringHash(r.email).toLong, (r.email,"email"))
                       
     
     ) }
     
     val recs= recordNodes.distinct()
   
    // println("count "+recs.count())
     
     val relations =  recordRDD.flatMap { 
       
       record=>
         List(((MurmurHash.stringHash(record.clusterID),MurmurHash.stringHash(record.recordID)), "has recordID"),
             ((MurmurHash.stringHash(record.clusterID),MurmurHash.stringHash(record.name)), "has name"),
             ((MurmurHash.stringHash(record.clusterID),MurmurHash.stringHash(record.address)), "has adddress"),
             ((MurmurHash.stringHash(record.clusterID),MurmurHash.stringHash(record.email)), "has email"),
             ((MurmurHash.stringHash(record.clusterID),MurmurHash.stringHash(record.phone)), "has phone")
             )
       
       
        }
     
     val relationsRDD : RDD[((VertexId, VertexId), String)] =  recordRDD.flatMap { 
       
       record=>
         List(((MurmurHash.stringHash(record.clusterID),MurmurHash.stringHash(record.recordID)), "has recordID"),
             ((MurmurHash.stringHash(record.clusterID),MurmurHash.stringHash(record.name)), "has name"),
             ((MurmurHash.stringHash(record.clusterID),MurmurHash.stringHash(record.address)), "has adddress"),
             ((MurmurHash.stringHash(record.clusterID),MurmurHash.stringHash(record.email)), "has email"),
             ((MurmurHash.stringHash(record.clusterID),MurmurHash.stringHash(record.phone)), "has phone")
             )
       
       
        }
    
     val edges= relationsRDD.distinct().map{
         case((n1, n2),rel) => Edge(n1,n2, rel)
     
   }
     
     
     
     
     val defaultUser = ("record", "Missing")
     val graph :Graph[(String, String), String]= Graph(recordNodes, edges, defaultUser)
     
    
     val Gr = Graph.apply(recs, edges)
     
     println("Vertices :"+Gr.numEdges)
     println("Edges:" + Gr.edges)
       
     
       val maxInDegree: (VertexId, Int) = Gr.inDegrees.reduce(max)
  // maxInDegree: (org.apache.spark.graphx.VertexId, Int) = (10397,152)
  val maxOutDegree: (VertexId, Int) = Gr.outDegrees.reduce(max)
  
  println("maxout : "+maxOutDegree.toString())
     
    //Gr.edges.collect.foreach(println(_
  
  val cc= Gr.connectedComponents.vertices
  val ttr = recordNodes.join(cc)
       
     println(ttr.collect().mkString("\n"))
   
   
   }
}