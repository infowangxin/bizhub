package com.bizhub.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.FactoryBean;

public class SparkContextFactoryBean implements FactoryBean<JavaSparkContext> {

    private String master;// 主

    private String appName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    @Override
    public JavaSparkContext getObject() throws Exception {
        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
        // 另外一些环境变量的添加
        // conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        // .set("spark.executor.memory", "1g")
        // .set("spark.executor.memory", "128m")
        // .set("spark.cores.max", "1")
        // .set("spark.default.parallelism", "3");

        return new JavaSparkContext(conf);
    }

    @Override
    public Class<?> getObjectType() {

        return JavaSparkContext.class;
    }

    @Override
    public boolean isSingleton() {

        return true;
    }

}