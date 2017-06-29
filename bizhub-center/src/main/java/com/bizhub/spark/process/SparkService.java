package com.bizhub.spark.process;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bizhub.entity.Item;

@Service
public class SparkService {

    @Autowired
    @Qualifier("sparkContext")
    private SparkContextFactoryBean sparkContext;

    public Item process(Item item) throws Exception {

        JavaSparkContext ctx = sparkContext.getObject();
        JavaRDD<String> lines = ctx.textFile("file:///Users/vincent/workspace/examples/bizhub/README.md", 1);
        List<String> list = lines.collect();
        for (String string : list) {
            System.out.println(string);
        }
        return item;

    }

}
