package com.rmj.sorting

import com.datastax.spark.connector._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.types._
import org.apache.spark.sql.Row

/**
 *
 * Desc: This program has below concepts:
 *
 * 1.) Connect to Cassandra table and read data from cassandra table
 * 2.) Apply some business logic here just increase salary of employee by specified hike %.
 * 3.) Update the Cassandra table with latest update salary
 * 4.) Cassandra table shema here:
 *      cqlsh:rmj_test> describe emp;
 *      CREATE TABLE rmj_test.emp (
 *               emp_id int PRIMARY KEY,
 *               emp_city text,
 *               emp_name text,
 *               emp_phone int,
 *               emp_sal int
 *         ) WITH bloom_filter_fp_chance = 0.01
 *         AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
 *         AND comment = ''
 *         AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
 *         AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
 *         AND crc_check_chance = 1.0
 *         AND dclocal_read_repair_chance = 0.1
 *         AND default_time_to_live = 0
 *         AND gc_grace_seconds = 864000
 *         AND max_index_interval = 2048
 *         AND memtable_flush_period_in_ms = 0
 *         AND min_index_interval = 128
 *         AND read_repair_chance = 0.0
 *         AND speculative_retry = '99PERCENTILE';
 *
 *  5.) KEYSPACE :
 *
 *  cqlsh:rmj_test> describe rmj_test;
 *    CREATE KEYSPACE rmj_test WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;
 *
 *
 */

object SparkCassandraConnectExample {

  case class Employee(id: Int, city: String, name: String, number: Int, salary: Int) {
    def copy(other: Employee, salary: Int) = Employee(other.id, other.city, other.name, other.number, salary)
  }

  val increaseSalary = (salary: Int, hike: Int) => salary + (salary * hike / 100)

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf(true)
      .set("spark.cassandra.connection.host", "localhost")
      .setAppName(this.getClass.getName)
      .setMaster("local[2]")
    val sc = new SparkContext(conf)

    val hike = 8

    val KEYSPACE = "rmj_test"

    val TABLE = "emp"

    val inputRdd = sc.cassandraTable(KEYSPACE, TABLE)
    val empRdd = inputRdd.map { x =>
      Employee(
        x.getInt(0),
        x.getString(1),
        x.getString(2),
        x.getInt(3),
        x.getInt(4))

    }

    val finalRdd = empRdd.map(x => x.copy(x, increaseSalary(x.salary, hike))).map(x => Row.fromTuple((x.id.toLong, x.city, x.name, x.number.toLong, x.salary.toLong)))

    val tblStruct = new StructType(
      Array(
        StructField("emp_id", LongType, nullable = false),
        StructField("emp_city", StringType, nullable = false),
        StructField("emp_name", StringType, nullable = false),
        StructField("emp_phone", LongType, nullable = false),
        StructField("emp_sal", LongType, nullable = false)))

    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    val updateDf = sqlContext.createDataFrame(finalRdd, tblStruct)

    updateDf.write.format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> KEYSPACE, "table" -> TABLE))
      .mode("append")
      .save()

    sc.stop()

  }
}