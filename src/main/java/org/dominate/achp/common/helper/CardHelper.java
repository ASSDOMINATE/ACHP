package org.dominate.achp.common.helper;

import com.hwja.tool.clients.redis.RedisClient;
import com.hwja.tool.utils.RandomUtil;
import com.hwja.tool.utils.StringUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.dominate.achp.entity.BaseConfig;
import org.dominate.achp.entity.BaseKey;
import org.dominate.achp.entity.BaseUserRecord;
import org.dominate.achp.entity.dto.CardRecordDTO;

import java.util.*;

public class CardHelper {


    private static final String TEMP_CARD_KEY_LIST = "temp:card:key:list";
    private static final int[] TEMP_OUT_TIME = {60 * 3, 60 * 6};

    private static final String CACHE_CARD_USER_USING_HASH_KEY = "cache:card:user:using:hash";
    private static final String CACHE_USER_DAILY_RECORD_HASH_KEY = "cache:card:user:daily:record:hash";

    private static final String LIST_UPDATE_USER_USING_ID = "list:update:user:using:id";
    private static final String LIST_UPDATE_USER_DAILY_RECORD_ID = "list:update:user:daily:record:id";


    public static void setUserRecordUpdate(int accountId) {
        RedisClient.leftPush(LIST_UPDATE_USER_DAILY_RECORD_ID, String.valueOf(accountId));
    }

    public static BaseUserRecord getUpdateUserRecord() {
        return getListTarget(LIST_UPDATE_USER_DAILY_RECORD_ID, CACHE_USER_DAILY_RECORD_HASH_KEY, BaseUserRecord.class);
    }

    public static long getUpdateUserRecordLength() {
        return RedisClient.listLength(LIST_UPDATE_USER_DAILY_RECORD_ID);
    }

    public static void setUserUsingUpdate(int accountId) {
        RedisClient.leftPush(LIST_UPDATE_USER_USING_ID, String.valueOf(accountId));
    }

    public static CardRecordDTO getUpdateUserUsing() {
        return getListTarget(LIST_UPDATE_USER_USING_ID, CACHE_CARD_USER_USING_HASH_KEY, CardRecordDTO.class);
    }

    public static long getUpdateUserUsingLength() {
        return RedisClient.listLength(CACHE_CARD_USER_USING_HASH_KEY);
    }


    private static <T> T getListTarget(String listKey, String hashTargetKey, Class<T> targetClass) {
        String field = RedisClient.rightPop(listKey, String.class);
        if (StringUtil.isEmpty(field)) {
            return null;
        }
        if (!RedisClient.hHasKey(hashTargetKey, field)) {
            return null;
        }
        return RedisClient.hGet(hashTargetKey, field, targetClass);
    }

    private static BaseConfig SYS_CONFIG = null;

    /**
     * 获取基础配置
     *
     * @return 基础配置
     */
    public static BaseConfig getConfig() {
        return SYS_CONFIG;
    }

    /**
     * 清除配置，记得下次使用从数据库更新
     */
    public static void clearConfig() {
        SYS_CONFIG = null;
    }

    /**
     * 更新基础配置
     *
     * @param config 基础配置
     */
    public static void updateConfig(BaseConfig config) {
        SYS_CONFIG = config;
    }


    /**
     * 获取用户当前使用卡密
     *
     * @param accountId 用户ID
     * @return 卡密记录
     */
    public static CardRecordDTO getUsingCard(int accountId) {
        if (!RedisClient.hHasKey(CACHE_CARD_USER_USING_HASH_KEY, String.valueOf(accountId))) {
            return null;
        }
        return RedisClient.hGet(CACHE_CARD_USER_USING_HASH_KEY, String.valueOf(accountId), CardRecordDTO.class);
    }

    /**
     * 保存用户使用卡密
     *
     * @param accountId 用户ID
     * @param card      卡密记录
     */
    public static void saveUsingCard(int accountId, CardRecordDTO card) {
        RedisClient.hSetPersist(CACHE_CARD_USER_USING_HASH_KEY, String.valueOf(accountId), card);
    }

    public static void removeUsingCard(int accountId) {
        RedisClient.hRemoveField(CACHE_CARD_USER_USING_HASH_KEY, String.valueOf(accountId));
    }

    public static Collection<CardRecordDTO> getUsingList() {
        Map<String, CardRecordDTO> cardRecordMap = RedisClient.hGetAll(CACHE_CARD_USER_USING_HASH_KEY, CardRecordDTO.class);
        return cardRecordMap.values();
    }

    /**
     * 获取用户每日记录
     *
     * @param accountId 账号ID
     * @return 每日记录
     */
    public static BaseUserRecord getUserDailyRecord(int accountId) {
        if (!RedisClient.hHasKey(CACHE_USER_DAILY_RECORD_HASH_KEY, String.valueOf(accountId))) {
            return null;
        }
        BaseUserRecord userRecord = RedisClient.hGet(CACHE_USER_DAILY_RECORD_HASH_KEY, String.valueOf(accountId),
                BaseUserRecord.class);
        if (!DateUtils.isSameDay(new Date(), userRecord.getCreateTime())) {
            return null;
        }
        return userRecord;
    }

    public static Collection<BaseUserRecord> getUserRecordList() {
        Map<String, BaseUserRecord> userRecordMap = RedisClient.hGetAll(CACHE_USER_DAILY_RECORD_HASH_KEY, BaseUserRecord.class);
        return userRecordMap.values();
    }

    /**
     * 保存用户每日记录
     *
     * @param userRecord 用户记录
     */
    public static void saveUserRecord(BaseUserRecord userRecord) {
        RedisClient.hSetPersist(CACHE_USER_DAILY_RECORD_HASH_KEY, userRecord.getAccountId().toString(), userRecord);
    }


    /**
     * 获取缓存中的卡密列表
     *
     * @return 卡密列表
     */
    public static List<BaseKey> getCacheKeyList() {
        if (!RedisClient.hasKey(TEMP_CARD_KEY_LIST)) {
            return Collections.emptyList();
        }
        return (List<BaseKey>) RedisClient.get(TEMP_CARD_KEY_LIST);
    }

    /**
     * 保存卡密列表缓存
     *
     * @param keyList 卡密列表
     */
    public static void saveCacheKeyList(List<BaseKey> keyList) {
        RedisClient.set(TEMP_CARD_KEY_LIST, keyList, RandomUtil.getRandNum(TEMP_OUT_TIME[0], TEMP_OUT_TIME[1]));
    }
}
