package me.echo.community.service;

import me.echo.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 点赞
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void like(Integer userId, int entityType, int entityId, Integer entityUserId){
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if (isMember){
//            redisTemplate.opsForSet().remove(entityLikeKey, userId);
//        }else {
//            redisTemplate.opsForSet().add(entityLikeKey, userId);
//        }
        // 以事务的形式实现
        redisTemplate.execute(new SessionCallback<Object>() {

            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

                operations.multi();

                if (isMember){
                    operations.opsForSet().remove((K) entityLikeKey, userId);
                    operations.opsForValue().decrement((K) userLikeKey);
                }else {
                    operations.opsForSet().add((K) entityLikeKey, (V) userId);
                    operations.opsForValue().increment((K) userLikeKey);
                }

                return operations.exec();
            }
        });
    }


    /**
     * 查询某实体点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }


    /**
     * 查询某人对某实体的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int findEntityLikeStatus(Integer userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId)?1:0;
    }

    public int findUserLikeCount(Integer userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}
