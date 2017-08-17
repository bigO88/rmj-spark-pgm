package com.rmj.sorting

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by rajjanwa on 8/16/17.
  */
object SortRunner {

  def main(args: Array[String]): Unit = {

    println("Sorting application running....")

    val conf = new SparkConf().setAppName("Sorting Application").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val inFilePath = "/home/rajjanwa/input/unsorted_records.csv"
    val outFilePath = "/home/rajjanwa/output/sorted_records"
    val rdd = sc.textFile(inFilePath)
    val employeeRdd = rdd.map { x =>
      Employee(x.split(",", -1)(0), x.split(",", -1)(1), x.split(",", -1)(2), x.split(",", -1)(3))
    }

    val employeeKeyedRdd = employeeRdd.map(x => (x.empId, x))

    val sortedRdd = employeeKeyedRdd.sortByKey()

    val finalRdd = sortedRdd.map { x =>
      x._2.empId + "|" + x._2.name + "|" + x._2.rank + "|" + x._2.address
    }
    finalRdd.saveAsTextFile(outFilePath)

  }

  case class Employee(empId: String, name: String, address: String, rank: String)


}
