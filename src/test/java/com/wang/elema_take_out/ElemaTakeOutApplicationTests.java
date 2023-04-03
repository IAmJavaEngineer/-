package com.wang.elema_take_out;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ElemaTakeOutApplicationTests {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Test
    /**
     * 操作String类型的数据
     */
    public void testString(){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        //默认的Key序列化器为：JdkSerializationRedisSerializer
        valueOperations.set("city","beijing");
        String name = valueOperations.get("name");
        System.out.println(name);
        valueOperations.set("key1","value1",10l, TimeUnit.DAYS);
        String key1 = valueOperations.get("key1");
        System.out.println(key1);
        Boolean aBoolean = valueOperations.setIfAbsent("city", "广州");
        System.out.println(aBoolean);
    }

    @Test
    /**
     * 操作Hash类型的数据
     */
    public void testHash(){
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put("002","name","小明");
        hashOperations.put("002","age","20");
        hashOperations.put("002","city","广州");

        String age = (String) hashOperations.get("002", "age");
        System.out.println(age);
        //获取set中所有的key
        Set keys = hashOperations.keys("002");
        keys.forEach(System.out::println);
    }

    /**
     * 操作List类型的数据
     */
    @Test
    public void testList(){
        ListOperations<String, String> opsForList = redisTemplate.opsForList();
        //一次存一个值
        opsForList.leftPush("myList","a");
        //一次存多个值
        opsForList.leftPushAll("myList","b","c","d");
        List<String> myList = opsForList.range("myList", 0, -1);
        myList.forEach(System.out::println);
    }
    
    @Test
    /**
     * Set集合的操作
     */
    public void testSet(){
        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
        //添加元素
        opsForSet.add("mySet","a","b","c","d");
        //遍历取值
        Set<String> mySet = opsForSet.members("mySet");
        mySet.forEach(System.out::println);
        //删除元素
        opsForSet.remove("mySet","a","b");

    }

    @Test
    /**
     * ZSet操作
     */
    public void testZSet(){
        ZSetOperations<String, String> forZSet = redisTemplate.opsForZSet();
        //存值
        forZSet.add("myZSet","a",10.0);
        forZSet.add("myZSet","b",11.0);
        forZSet.add("myZSet","c",12.0);
        forZSet.add("myZSet","d",10.0);
        forZSet.add("myZSet","e",1.0);
        forZSet.add("myZSet","a",13.0);
        System.out.println("存值完毕：");
        //取值
        Set<String> myZSet = forZSet.range("myZSet", 0, -1);
        myZSet.forEach(System.out::println);
        //修改分数
        forZSet.incrementScore("myZSet","b",20.0);
        System.out.println("修改完毕：");
        //重新排序
        Set<String> myZSet2 = forZSet.range("myZSet", 0, -1);
        myZSet.forEach(System.out::println);
        //删除成功
        forZSet.remove("myZSet","a","b");
        System.out.println("删除完毕：");
        //重新取值
        Set<String> myZSet3 = forZSet.range("myZSet", 0, -1);
        myZSet.forEach(System.out::println);
    }

    @Test
    /**
     * 通用操作，针对不同数据类型都可以操作
     */
    public void testCommon(){
        //获取Redis中所有的key
        Set<String> keys = redisTemplate.keys("*");
        keys.forEach(System.out::println);

        //判断某个key是否存在
        Boolean name = redisTemplate.hasKey("name");
        System.out.println(name);
        //删除指定key
        redisTemplate.delete("age");
        //获取指定key对应的value的数据类型
        DataType type = redisTemplate.type("002");
        System.out.println(type);
    }

    @Test
    /**
     * 使用StringRedisTemplate，这样子就不用修改序列化器了
     */
    public void testStringRedisTemplate(){
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        opsForValue.set("name","小明");
        String name = opsForValue.get("name");
        System.out.println(name);
    }
}


