package com.bizhub.spark;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/applicationContext-spark.xml", "classpath*:spring/applicationContext.xml" })
public class SparkContextFactoryBeanTest {

    @Autowired
    @Qualifier("sparkContext")
    private SparkContextFactoryBean sparkContext;

    @Test
    public void test1() {
        try {

            JavaSparkContext sc = sparkContext.getObject();
            // JavaRDD<String> lines = sc.textFile("hdfs://127.0.0.1:9000/user/hadoop/nie.txt", 1);
            JavaRDD<String> lines = sc.textFile("file:///Users/vincent/workspace/examples/bizhub/README.md", 1);
            List<String> list = lines.collect();
            for (String string : list) {
                System.out.println(string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
