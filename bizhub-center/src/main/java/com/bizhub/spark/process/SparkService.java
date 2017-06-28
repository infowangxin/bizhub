package com.bizhub.spark.process;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bizhub.entity.Item;

@Service
public class SparkService {

    @Autowired
    @Qualifier("sparkContext")
    private SparkContextFactoryBean sparkContext;

    public String process(Item item) throws Exception {
        
        JavaSparkContext ctx = sparkContext.getObject();
        String s = JSON.toJSONString(item);
        JavaRDD<Integer> rdd = ctx.parallelize(Arrays.asList(1, 2, 3, 3), 2);

        List<Integer> list = rdd.collect();
        for (Integer string : list) {
            System.out.println(string);
        }

        return s;

    }

}
