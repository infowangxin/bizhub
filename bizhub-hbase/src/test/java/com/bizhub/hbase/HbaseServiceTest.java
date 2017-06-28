package com.bizhub.hbase;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bizhub.hbase.common.HbaseService;
import com.bizhub.hbase.common.annotation.Table;
import com.bizhub.hbase.entity.Car;
import com.bizhub.hbase.entity.Person;
import com.bizhub.hbase.entity.User;
import com.bizhub.hbase.entity.User2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/applicationContext-hbase.xml", "classpath*:spring/applicationContext.xml" })
public class HbaseServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(HbaseServiceTest.class);

    @Autowired
    private HbaseService hbaseService;

    /**
     * 单一对象测试存储
     * hbase shell中通过scan "person" 查询是否正确
     */
    @Test
    public void testSave1() {
        Person person = new Person();
        person.setPassword("123qwe123");
        person.setUserId("123456789");
        person.setUserName("jannal");
        hbaseService.save(person);
    }

    /**
     * 复合对象测试存储
     */
    @Test
    public void testSave2() {
        User user = new User();
        user.setUserId("123456789");
        user.setAge(11);
        user.setCar(Car.newDefaultCar());
        user.setEmail("jannals@1263.com");
        hbaseService.save(user);
    }

    /**
     * 单一对象测试查询
     */
    @Test
    public void testFindAll1() {
        initPersonData(10);
        List<Person> personList = hbaseService.findAll(Person.class);
        logger.info("person表的信息如下:{}", personList);
    }

    /**
     * 插入多条数据并查询
     */
    @Test
    public void testFindAll2() {
        initUserData(10);
        List<User> userList = hbaseService.findAll(User.class);
        logger.info("user表的信息如下:{}", userList);
    }

    /**
     * 批量插入
     */
    @Test
    public void testSaveBatch() {
        List<Person> personList = new ArrayList<Person>();
        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            person.setPassword("password-" + i * 1000000);
            person.setUserName("userName-" + i * 1000000);
            person.setUserId("2000000" + i);
            personList.add(person);
        }
        hbaseService.saveBatch(personList);
    }

    /**
     * rowkey查询
     */
    @Test
    public void testFindOneByRowKeyValue() {
        long startTime = System.nanoTime();
        User user = hbaseService.findOneByRowKeyValue(User.class, "123456789");
        long endTime = System.nanoTime();
        logger.info("user:{},查询一条数据花费时间", user, (endTime - startTime) / (1000));
    }

    /**
     * rowkey范围查询
     */
    @Test
    public void testFindFromStartToEndRowKey() {
        initUser2BigData(10);
        List<User2> list = hbaseService.findFromStartToEndRowKey(User2.class, "10000002", "10000005");
        logger.info("数据大小:{}", list.size());
        logger.info("数据{}", list);
    }

    /**
     * 删除行
     */
    @Test
    public void testDeleteRow() {
        User user = hbaseService.findOneByRowKeyValue(User.class, "123456789");
        logger.info("删除前user:{}", user);
        hbaseService.deleteRow(User.class.getAnnotation(Table.class).tableName(), "123456789", User.class.getAnnotation(Table.class).columnFamilyName());
        user = hbaseService.findOneByRowKeyValue(User.class, "123456789");
        logger.info("删除后user:{}", user);
    }

    /**
     * 删除列
     */
    @Test
    public void testDeleteColumnFamily() {
        hbaseService.deleteColumnFamily(User.class.getAnnotation(Table.class).tableName(), "cfuser");
        User user = hbaseService.findOneByRowKeyValue(User.class, "123456789");
        logger.info("删除列后的user:{}", user);
    }

    /**
     * 删除表
     */
    @Test
    public void testDropTable() {
        hbaseService.dropTable(User.class.getAnnotation(Table.class).tableName());
        hbaseService.dropTable(Person.class.getAnnotation(Table.class).tableName());
    }

    private void initPersonData(int max) {
        for (int i = 0; i < max; i++) {
            Person person = new Person();
            person.setPassword("password-" + i * 1000000);
            person.setUserName("userName-" + i * 1000000);
            person.setUserId("123456780" + i);
            hbaseService.save(person);
        }
    }

    private void initUserData(int max) {
        for (int i = 0; i < max; i++) {
            User user = new User();
            user.setUserId("123456780" + i);
            user.setAge(i + 30);
            Car car = new Car();
            car.setCarName("宝马" + i);
            car.setCarType((long) i * 10);
            user.setCar(car);
            user.setEmail(i * 10 + "jannals@126.com");
            hbaseService.save(user);
        }
    }

    private void initUser2BigData(int max) {
        List<User2> userList = new ArrayList<User2>();
        for (int i = 0; i < max; i++) {
            User2 user = new User2();
            user.setUserId("1000000" + i);
            user.setAge(i + 30);
            Car car = new Car();
            car.setCarName("宝马" + i);
            car.setCarType((long) i * 10);
            user.setCar(car);
            user.setEmail(i * 10 + "jannals@126.com");
            userList.add(user);
        }
        hbaseService.saveBatch(userList);
    }
}
