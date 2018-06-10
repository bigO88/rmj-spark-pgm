name := "rmj-spark-pgm"

version := "1.0.0"

//sbt native package enable start here

lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging,DebianPlugin)

maintainer := "Raj Janwa <rajjanwa@gmail.com>"

packageSummary := "Debian Package"

packageDescription := """package description of our software,"""

daemonUser in Linux := "rajjanwa"

daemonGroup in Linux := "rmj"

// we specify the name for our fat jar
jarName in assembly := "assembly-spark-rmj-project.jar"

// removes all jar mappings in universal and appends the fat jar
mappings in Universal := {
    // universalMappings: Seq[(File,String)]
    val universalMappings = (mappings in Universal).value 
    val fatJar = (assembly in Compile).value
    // removing means filtering
    val filtered = universalMappings filter { 
        case (file, name) =>  ! name.endsWith(".jar") 
    }
    // add the fat jar
    filtered :+ (fatJar -> ("lib/" + fatJar.getName))
}

// sbt native package ennble end here

//Older Scala Version
scalaVersion := "2.11.8"

val overrideScalaVersion = "2.11.8"
val sparkVersion = "2.0.0"
val sparkXMLVersion = "0.3.3"
val sparkCsvVersion = "1.4.0"
val sparkElasticVersion = "2.3.4"
val sscKafkaVersion = "1.6.2"
val sparkMongoVersion = "1.0.0"
val sparkCassandraVersion = "1.6.0"

//Override Scala Version to the above 2.11.8 version
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

resolvers ++= Seq(
  "All Spark Repository -> bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/"
)

resolvers += "Typesafe" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

libraryDependencies ++= Seq(
  "org.apache.spark"      %% "spark-core"       % sparkVersion  exclude("jline", "2.12"),
  "org.apache.spark"      %% "spark-sql"        % sparkVersion excludeAll(ExclusionRule(organization = "jline"),ExclusionRule("name","2.12")),
  "org.apache.spark"      %% "spark-hive"       % sparkVersion,
  "org.apache.spark"      %% "spark-yarn"       % sparkVersion,
  "com.databricks"        %% "spark-xml"        % sparkXMLVersion,
  "com.databricks"        %% "spark-csv"        % sparkCsvVersion,
  "org.apache.spark"      %% "spark-graphx"     % sparkVersion,
  "org.apache.spark"      %% "spark-catalyst"   % sparkVersion,
  "org.apache.spark"      %% "spark-streaming"  % sparkVersion,
  "org.elasticsearch"     %% "elasticsearch-spark"        %     sparkElasticVersion,
  "org.apache.spark"      %% "spark-streaming-kafka"     % sscKafkaVersion,
  "org.mongodb.spark"      % "mongo-spark-connector_2.11" %  sparkMongoVersion
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

// https://mvnrepository.com/artifact/com.datastax.spark/spark-cassandra-connector
libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "2.3.0"
// https://mvnrepository.com/artifact/com.twitter/hbc-core
libraryDependencies += "com.twitter" % "hbc-core" % "2.2.0"



scalacOptions ++= Seq("-unchecked", "-deprecation")

assemblyMergeStrategy in assembly := {
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}
