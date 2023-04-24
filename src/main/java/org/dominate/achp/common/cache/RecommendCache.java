package org.dominate.achp.common.cache;

import com.hwja.tool.clients.redis.RedisClient;

import java.util.ArrayList;
import java.util.List;

public class RecommendCache {

    private static final String CACHE_RECOMMEND_SCENE_ID_LIST = "cache:recommend:scene:id:list";


    public static void updateSceneIdList(List<Integer> idList) {
        RedisClient.setPersist(CACHE_RECOMMEND_SCENE_ID_LIST, idList);
    }

    public static void addSceneIdList(int... sceneIds) {
        if (!RedisClient.hasKey(CACHE_RECOMMEND_SCENE_ID_LIST)) {
            RedisClient.setPersist(CACHE_RECOMMEND_SCENE_ID_LIST, new ArrayList<Integer>());
        }
        List<Integer> idList = getSceneIdList();
        for (int sceneId : sceneIds) {
            if (idList.contains(sceneId)) {
                continue;
            }
            idList.add(sceneId);
        }
        RedisClient.setPersist(CACHE_RECOMMEND_SCENE_ID_LIST, idList);
    }

    public static List<Integer> getSceneIdList() {
        return (List<Integer>) RedisClient.get(CACHE_RECOMMEND_SCENE_ID_LIST);
    }
}
