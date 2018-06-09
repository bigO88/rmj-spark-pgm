package com.rmj.spark.cassandra

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by rajjanwa on 8/16/17.
  */
object PairedRddActionsByKey {

  def main(args: Array[String]): Unit = {

    println("PairedRddActionsByKey application running....")

    val conf = new SparkConf().setAppName("Sorting Application") //.setMaster("local[*]")
    val sc = new SparkContext(conf)
    val inFilePath = args(0) //"/home/rajjanwa/data/input/input_records.csv"
    val outFilePath = args(1)  //"/home/rajjanwa/data/output/"
    val rdd = sc.textFile(inFilePath)
    val employeeRdd = rdd.map { x =>
      Employee(x.split(",", -1)(0), x.split(",", -1)(1), x.split(",", -1)(2), x.split(",", -1)(3))
    }

    val employeeKeyedRdd = employeeRdd.map(x => (x.empId, x))
    
    // SortByKey action performed here
    
    val sortedRdd = employeeKeyedRdd.sortByKey()

    val finalRdd = sortedRdd.map { x =>
      x._2.empId + "|" + x._2.name + "|" + x._2.rank + "|" + x._2.address
    }
    finalRdd.saveAsTextFile(outFilePath+"/sortByKey")
   
     // groupByKey action performed here
    val groupByKeyRdd=employeeKeyedRdd.groupByKey()
    
    .saveAsTextFile(outFilePath+"/groupByKey")

     // reduceByKey action performed here
    val reduceByKeyRdd=employeeKeyedRdd.reduceByKey{ (x,y) => 
      x.rank > y.rank match
      { 
        case true => x 
        case false => y
    }
  }.saveAsTextFile(outFilePath+"/reduceByKey")
    
  val f = (x:Employee,y:Employee) => if(x.rank > y.rank) x.rank.toInt else y.rank.toInt
  
  val f1=(x:Employee,y:Employee) => if(x.rank > y.rank) x.rank.toInt else y.rank.toInt
  

//  val aggByKeyRdd = employeeKeyedRdd.aggregateByKey(0)(f, f1)
  
  }

  case class Employee(empId: String, name: String, address: String, rank: String)


}
