package edu.tongji.contentservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 阅读排行榜服务
 *
 * 设计要点：
 * 1. 使用 Redis Sorted Set（ZSet）作为排行榜存储介质，member 为小说ID（novelId），
 *    score 为累计阅读次数。ZSet 底层是跳表（skip list）+ 哈希表，范围查询与排名的
 *    时间复杂度为 O(log N)，远高于 MySQL 的 ORDER BY ... LIMIT 全表排序（O(N log N)）。
 * 2. 每次小说详情被访问即对该小说 score +1（incrementScore 是原子操作，天然并发安全）。
 * 3. 取榜单用 reverseRangeWithScores（按 score 倒序取 topN），天然得到"阅读最多"的排名。
 * 4. ZSet 自带去重与实时更新，无需定时任务重建；并支持 EXPIRE 做历史热度窗口（可选）。
 */
@Service
public class ReadingRankingService {

    private static final Logger logger = LoggerFactory.getLogger(ReadingRankingService.class);

    /** ZSet key：所有小说的阅读计数排行 */
    private static final String READ_RANK_KEY = "rank:novel:reads";

    private final RedisTemplate<String, Object> redisTemplate;

    public ReadingRankingService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 记录一次阅读行为：对指定小说阅读计数 +1（原子自增）
     * @param novelId 小说ID
     */
    public void recordRead(Long novelId) {
        if (novelId == null) {
            return;
        }
        try {
            redisTemplate.opsForZSet().incrementScore(READ_RANK_KEY, novelId, 1.0);
        } catch (Exception e) {
            // 阅读计数失败不应影响主流程（看小说），仅记录日志
            logger.warn("记录阅读计数失败, novelId={}: {}", novelId, e.getMessage());
        }
    }

    /**
     * 获取阅读排行榜前 topN 名
     * @param topN 取前几名
     * @return 有序 Map，key=novelId，value=阅读次数（按阅读次数从高到低）
     */
    public Map<Long, Double> getTopReads(int topN) {
        Map<Long, Double> result = new LinkedHashMap<>();
        try {
            Set<ZSetOperations.TypedTuple<Object>> tuples =
                    redisTemplate.opsForZSet().reverseRangeWithScores(READ_RANK_KEY, 0, topN - 1);
            if (tuples == null) {
                return result;
            }
            for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
                Object value = tuple.getValue();
                Double score = tuple.getScore();
                if (value instanceof Number && score != null) {
                    result.put(((Number) value).longValue(), score);
                }
            }
        } catch (Exception e) {
            logger.warn("获取阅读排行榜失败: {}", e.getMessage());
        }
        return result;
    }

    /**
     * 获取某本小说的阅读次数
     */
    public Double getReadCount(Long novelId) {
        if (novelId == null) {
            return 0.0;
        }
        try {
            return redisTemplate.opsForZSet().score(READ_RANK_KEY, novelId);
        } catch (Exception e) {
            logger.warn("获取小说阅读次数失败, novelId={}: {}", novelId, e.getMessage());
            return 0.0;
        }
    }

    /**
     * 获取某本小说在排行榜中的名次（从 1 开始；未上榜返回 -1）
     */
    public long getRank(Long novelId) {
        if (novelId == null) {
            return -1L;
        }
        try {
            Long rank = redisTemplate.opsForZSet().reverseRank(READ_RANK_KEY, novelId);
            return rank == null ? -1L : rank + 1;
        } catch (Exception e) {
            logger.warn("获取小说排名失败, novelId={}: {}", novelId, e.getMessage());
            return -1L;
        }
    }

    /**
     * 批量删除（小说下架/删除时调用，避免僵尸数据）
     */
    public void removeNovel(Long novelId) {
        if (novelId == null) {
            return;
        }
        try {
            redisTemplate.opsForZSet().remove(READ_RANK_KEY, novelId);
        } catch (Exception e) {
            logger.warn("移除小说阅读计数失败, novelId={}: {}", novelId, e.getMessage());
        }
    }
}
