package org.dominate.achp.common.cache;

import com.hwja.tool.clients.redis.RedisClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐内容缓存
 *
 * @author dominate
 * @since 2023-04-24
 */
public final class RecommendCache {

    /**
     * 推荐场景ID列表
     */
    private static final String CACHE_RECOMMEND_SCENE_ID_LIST = "cache:recommend:scene:id:list";

    /**
     * 更新推荐场景ID列表
     *
     * @param idList 待更新的场景ID列表
     */
    public static void updateSceneIdList(List<Integer> idList) {
        RedisClient.setPersist(CACHE_RECOMMEND_SCENE_ID_LIST, idList);
    }

    /**
     * 增加场景ID到推荐中
     *
     * @param sceneIds 场景ID
     */
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

    /**
     * 获取推荐场景ID列表
     *
     * @return 场景ID列表
     */
    public static List<Integer> getSceneIdList() {
        return (List<Integer>) RedisClient.get(CACHE_RECOMMEND_SCENE_ID_LIST);
    }
}
