package me.echo.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));

        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashes(){
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "jiaqi");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    @Test
    public void testBoundOperations(){
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    // 编程式事务
    @Test
    public void testTransaction(){
    Object object =
        redisTemplate.execute(
            new SessionCallback() {
              @Override
              public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";
                redisOperations.multi();

                redisOperations.opsForSet().add(redisKey, "张三");
                redisOperations.opsForSet().add(redisKey, "李四");
                redisOperations.opsForSet().add(redisKey, "王五");
                redisOperations.opsForSet().add(redisKey, "赵六");

                System.out.println(redisOperations.opsForSet().members(redisKey));
                return redisOperations.exec();
              }
            });
        System.out.println(object);
    }


    // 统计一组数的布尔值
    @Test
    public void testBitMap(){
        String redisKey = "test:bm:01";

        redisTemplate.opsForValue().setBit(redisKey, 1, true);
        redisTemplate.opsForValue().setBit(redisKey, 4, true);
        redisTemplate.opsForValue().setBit(redisKey, 7, true);

//        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
//        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
//        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));
//        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 3));
//        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 4));
//        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 5));
//        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 6));
//        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 7));

        // 统计
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
    }

    // 统计三组数据的布尔运算值
    @Test
    public void testBitMapOp(){
        String redisKey2 = "test:bn:02";
        redisTemplate.opsForValue().setBit(redisKey2, 0, true);
        redisTemplate.opsForValue().setBit(redisKey2, 1, true);
        redisTemplate.opsForValue().setBit(redisKey2, 2, true);

        String redisKey3 = "test:bn:03";
        redisTemplate.opsForValue().setBit(redisKey3, 2, true);
        redisTemplate.opsForValue().setBit(redisKey3, 3, true);
        redisTemplate.opsForValue().setBit(redisKey3, 4, true);

        String redisKey4 = "test:bn:04";
        redisTemplate.opsForValue().setBit(redisKey4, 4, true);
        redisTemplate.opsForValue().setBit(redisKey4, 5, true);
        redisTemplate.opsForValue().setBit(redisKey4, 6, true);

        String redisKey = "test:bn:or";

        Object execute = redisTemplate.execute((RedisCallback) (connection) -> {
            connection.bitOp(RedisStringCommands.BitOperation.OR, redisKey.getBytes(), redisKey2.getBytes(), redisKey3.getBytes(), redisKey4.getBytes());
            return connection.bitCount(redisKey.getBytes());
        });
        System.out.println(execute);
    }
}
