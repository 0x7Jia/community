package me.echo.community.quartz;

import me.echo.community.entity.DiscussPost;
import me.echo.community.service.DiscussPostService;
import me.echo.community.service.ElasticsearchService;
import me.echo.community.service.LikeService;
import me.echo.community.util.CommunityConstant;
import me.echo.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 刷新帖子分数
 */
public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    private static Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-08-08 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0){
            logger.info("[任务取消] 没有需要刷新的帖子!");
            return;
        }

        logger.info("[任务开始] 正在刷新帖子分数:"+operations.size());
        while (operations.size()>0){
            this.refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完成!");
    }

    private void refresh(Integer postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null){
            logger.error("帖子不存在:id="+postId);
            return;
        }

        // 是否精华帖
        boolean wonderful = post.getStatus()==1;
        // 评论数
        int commentCount = post.getCommentCount();
        // 点赞数
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 计算权重
        double w = (wonderful ? 100 : 0) + commentCount*10 + likeCount*2;
        //
        double score = Math.log10(Math.max(w, 1)) + (post.getCreateTime().getTime()-epoch.getTime())/(1000*3600*24);

        // 更新帖子分数
        discussPostService.updateScore(postId, score);

        // 同步搜索数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}
