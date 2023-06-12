package org.dominate.achp.common.cache;

import com.hwja.tool.clients.redis.RedisClient;
import org.dominate.achp.common.enums.SceneCountType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class StatisticCache {

    private static final String CACHE_SCENE_COUNT_LIST_HEAD = "list:statistic:scene:count:";

    public static void addSceneCount(int sceneId, SceneCountType countType) {
        RedisClient.leftPush(CACHE_SCENE_COUNT_LIST_HEAD + countType.getName(), sceneId);
    }

    public static List<Integer> popCountSceneList(SceneCountType countType) {
        return getSceneIdList(CACHE_SCENE_COUNT_LIST_HEAD + countType.getName());
    }

    private static List<Integer> getSceneIdList(String cacheKey) {
        if (!RedisClient.hasKey(cacheKey)) {
            return Collections.emptyList();
        }
        long listCount = RedisClient.listLength(cacheKey);
        List<Integer> sceneIdList = new ArrayList<>();
        for (int i = 0; i < listCount; i++) {
            Integer sceneId = RedisClient.rightPop(cacheKey, Integer.class);
            sceneIdList.add(sceneId);
        }
        return sceneIdList;
    }
}
