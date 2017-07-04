package com.bizhub.spark;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.Function2;
//import org.apache.spark.api.java.function.VoidFunction;

@SuppressWarnings("resource")
public class App {

    public static void test1(SparkConf conf) {
        // SparkConf conf = new SparkConf();
        // conf.setAppName("wordcount");
        // conf.setMaster("spark://127.0.0.1:7077");

        JavaSparkContext sc = new JavaSparkContext(conf);
        System.out.println(sc);

        // JavaRDD<String> lines = sc.textFile("hdfs://127.0.0.1:9000/user/hadoop/nie.txt", 1);
        JavaRDD<String> lines = sc.textFile("file:///Users/vincent/workspace/examples/bizhub/README.md", 1);
        List<String> list = lines.collect();
        for (String string : list) {
            System.out.println(string);
        }
    }

    public static void test2(SparkConf conf) {
        // SparkConf conf = new SparkConf();
        // conf.set("spark.testing.memory", "471859200"); // 因为jvm无法获得足够的资源
        // JavaSparkContext sc = new JavaSparkContext("spark://127.0.0.1:7077", "First Spark App", conf);
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile("file:///Users/vincent/workspace/examples/bizhub/README.md", 1);
        List<String> list = lines.collect();
        for (String string : list) {
            System.out.println(string);
        }
    }

    public static void test3(SparkConf conf) {
        // SparkConf conf = new SparkConf();
        // conf.set("spark.testing.memory", "471859200"); // 因为jvm无法获得足够的资源
        // JavaSparkContext sc = new JavaSparkContext("spark://127.0.0.1:7077", "First Spark App", conf);
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 2, 3, 3), 2);

        List<Integer> list = rdd.collect();
        for (Integer string : list) {
            System.out.println(string);
        }
    }

    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("wordcount").setMaster("spark://127.0.0.1:7077");
        test1(sparkConf);
        // test2(sparkConf);
        // test3(sparkConf);
    }

}
