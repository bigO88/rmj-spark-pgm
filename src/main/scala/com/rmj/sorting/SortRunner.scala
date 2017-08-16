package com.rmj.sorting

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
/**
  * Created by rajjanwa on 8/16/17.
  */
object SortRunner {

  def main(args: Array[String]): Unit = {

    println("Sorting application running....")

    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd=sc.textFile("/home/rajjanwa/ins")
    rdd.map(x=>x.replaceAll("Raj","Mamta")).saveAsTextFile("/home/rajjanwa/in")
  }


}
