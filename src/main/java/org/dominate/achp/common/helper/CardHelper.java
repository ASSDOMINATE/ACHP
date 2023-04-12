package org.dominate.achp.common.helper;

import com.hwja.tool.clients.redis.RedisClient;
import com.hwja.tool.utils.RandomUtil;
import org.dominate.achp.entity.BaseConfig;
import org.dominate.achp.entity.BaseKey;
import org.dominate.achp.entity.dto.CardRecordDTO;

import java.util.Collections;
import java.util.List;

public class CardHelper {


    private static final String TEMP_CARD_KEY_LIST = "temp:card:key:list";
    private static final int[] TEMP_OUT_TIME = {60 * 3, 60 * 6};

    private static final String CACHE_CARD_USER_USING_HASH_KEY = "case:card:user:using:hash";
    private static final String CACHE_CARD_USER_REQUEST_COUNT_HASH_KEY = "case:card:user:request:count:hash";
    private static final String CACHE_CARD_USER_DAILY_REQUEST_COUNT_HASH_KEY = "case:card:user:daily:request:count:hash";

    private static BaseConfig SYS_CONFIG = null;

    public static BaseConfig getConfig() {
        return SYS_CONFIG;
    }

    public static void clearConfig() {
        SYS_CONFIG = null;
    }

    public static void updateConfig(BaseConfig config) {
        SYS_CONFIG = config;
    }


    public static int getUserRequestCount(int accountId) {
        return getUserRequestCount(accountId, CACHE_CARD_USER_REQUEST_COUNT_HASH_KEY);
    }

    public static void addUserRequestCount(int accountId) {
        int requestCount = getUserRequestCount(accountId);

    }

    public static int getUserRequestCount(int accountId, String key) {
        if (!RedisClient.hHasKey(key, String.valueOf(accountId))) {
            return 0;
        }
        return RedisClient.hGet(key, String.valueOf(accountId), Integer.class);
    }

    public static int getUserDailyRequestCount(int accountId) {
        return getUserRequestCount(accountId, CACHE_CARD_USER_DAILY_REQUEST_COUNT_HASH_KEY);
    }


    public static CardRecordDTO getUsingCard(int accountId) {
        if (!RedisClient.hHasKey(CACHE_CARD_USER_USING_HASH_KEY, String.valueOf(accountId))) {
            return null;
        }
        return RedisClient.hGet(CACHE_CARD_USER_USING_HASH_KEY, String.valueOf(accountId), CardRecordDTO.class);
    }

    public static void saveUsingCard(int accountId, CardRecordDTO card) {
        RedisClient.hSetPersist(CACHE_CARD_USER_USING_HASH_KEY, String.valueOf(accountId), card);
    }


    public static List<BaseKey> getCacheKeyList() {
        if (!RedisClient.hasKey(TEMP_CARD_KEY_LIST)) {
            return Collections.emptyList();
        }
        return (List<BaseKey>) RedisClient.get(TEMP_CARD_KEY_LIST);
    }

    public static void saveCacheKeyList(List<BaseKey> keyList) {
        RedisClient.set(TEMP_CARD_KEY_LIST, keyList, RandomUtil.getRandNum(TEMP_OUT_TIME[0], TEMP_OUT_TIME[1]));
    }
}
