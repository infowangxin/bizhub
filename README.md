# bizhub
Business hub 简称bizhub, 业务总线

`它是一个典型的MVC三层框架数据分析系统，用于公司里面的大数据分析。`

`集成框架有：SpringMVC、log4j2、kafka、spark、hbase`

##### 环境准备
|组件名|访问地址|版本号|
|:----:|:----|:----|
|kafka|127.0.0.1:2181、127.0.0.1:9092|kafka_2.10-0.10.2.1.tgz|
|spark|127.0.0.1:7077、[127.0.0.1:8080](http://127.0.0.1:8080)|scala-2.11.8.tgz、spark-2.1.1-bin-hadoop2.7.tgz|
|hadoop|[127.0.0.1:50070](http://127.0.0.1:50070)、[127.0.0.1:8088](http://127.0.0.1:8088)|hadoop-2.7.3.tar.gz|
|hbase|127.0.0.1:16010|hbase-1.2.6-bin.tar.gz|
***

#### 包含工程有：
* bizhub-api：公共类、工具类
* bizhub-center：集成kafka、spark、hbase并附带示例
* bizhub-hbase：集成hbase的示例 
* bizhub-kafka-consumer：Kafka消息消费者
* bizhub-kafka-producer：Kafka消息生产者
* bizhub-spark：集成spark的示例 

[GitHub](https://github.com/wangxinforme) [issues](https://github.com/wangxinforme/bizhub/issues)

![Markdown](http://wx4.sinaimg.cn/mw690/005OXyHfgy1fh6evxykwhj30ag0as3zv.jpg)
