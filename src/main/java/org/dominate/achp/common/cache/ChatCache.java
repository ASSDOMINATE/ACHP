package org.dominate.achp.common.cache;

import com.hwja.tool.clients.redis.RedisClient;
import com.hwja.tool.utils.RandomUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.dominate.achp.entity.BaseKey;
import org.dominate.achp.entity.BaseUserRecord;
import org.dominate.achp.entity.ChatScene;
import org.dominate.achp.entity.dto.CardRecordDTO;
import org.dominate.achp.entity.dto.ChatDTO;
import org.dominate.achp.entity.dto.ContentDTO;
import org.dominate.achp.entity.dto.GroupCacheDTO;

import java.util.*;

/**
 * 对话缓存工具
 *
 * @author dominate
 * @since 2023-04-27
 */
@Slf4j
public final class ChatCache {

    /**
     * 临时缓存
     * 卡密列表
     */
    private static final String TEMP_CARD_KEY_LIST = "temp:card:key:list";

    /**
     * 临时缓存
     * 对话发送内容 拼接头部+Key
     */
    private static final String TEMP_CHAT_SEND_CACHE_HEAD = "temp:chat:send:cache:";

    /**
     * 临时缓存
     * 对话组 拼接头部+对话组ID
     */
    private static final String TEMP_CHAT_GROUP_CACHE_HEAD = "temp:chat:group:cache:";

    /**
     * 临时缓存
     * 对话场景 拼接头部+主健ID
     */
    private static final String TEMP_CHAT_SCENE_DB_CACHE = "temp:chat:scene:db:cache:";


    private static final int[] TEMP_OUT_TIME = {60 * 3, 60 * 6};


    /**
     * 哈希缓存
     * 用户当前使用的卡密
     */
    private static final String CACHE_CARD_USER_USING_HASH_KEY = "cache:card:user:using:hash";
    /**
     * 哈希缓存
     * 用户每日记录
     */
    private static final String CACHE_USER_DAILY_RECORD_HASH_KEY = "cache:card:user:daily:record:hash";

    /**
     * 列表缓存
     * 用户使用卡密 待更新
     */
    private static final String LIST_UPDATE_USER_USING_ID = "list:update:user:using:id";
    /**
     * 列表缓存
     * 用户每日记录 待更新
     */
    private static final String LIST_UPDATE_USER_DAILY_RECORD_ID = "list:update:user:daily:record:id";


    public static void saveSceneTemp(ChatScene chatScene) {
        RedisClient.set(TEMP_CHAT_SCENE_DB_CACHE + chatScene.getId(), chatScene,
                RandomUtil.getRandNum(TEMP_OUT_TIME[0], TEMP_OUT_TIME[1]));
    }

    public static ChatScene getScene(int id) {
        if (!RedisClient.hasKey(TEMP_CHAT_SCENE_DB_CACHE + id)) {
            return null;
        }
        return RedisClient.get(TEMP_CHAT_SCENE_DB_CACHE + id,ChatScene.class);
    }

    /**
     * 保存对话组内容 临时缓存
     *
     * @param groupCache 对话组内容
     */
    public static void saveChatGroupTemp(GroupCacheDTO groupCache) {
        RedisClient.set(TEMP_CHAT_GROUP_CACHE_HEAD + groupCache.getId(), groupCache,
                RandomUtil.getRandNum(TEMP_OUT_TIME[0], TEMP_OUT_TIME[1]));
    }

    /**
     * 保存对话内容 临时缓存
     *
     * @param groupId 对话组ID
     * @param content 对话内容
     */
    public static void saveChatGroupContentTemp(String groupId, ContentDTO content) {
        GroupCacheDTO groupCache = getChatGroup(groupId);
        if (null == groupCache) {
            groupCache = new GroupCacheDTO();
            groupCache.setId(groupId);
            groupCache.setContentList(new ArrayList<>());
        }
        groupCache.getContentList().add(content);
        saveChatGroupTemp(groupCache);
    }

    /**
     * 获取对话组内容
     *
     * @param groupId 对话组ID
     * @return 对话组内容
     */
    public static GroupCacheDTO getChatGroup(String groupId) {
        if (!RedisClient.hasKey(TEMP_CHAT_GROUP_CACHE_HEAD + groupId)) {
            return null;
        }
        return RedisClient.get(TEMP_CHAT_GROUP_CACHE_HEAD + groupId, GroupCacheDTO.class);
    }


    /**
     * 保存对话发送内容 临时缓存
     *
     * @param chat 对话发送内容
     * @return 缓存Key
     */
    public static String saveChatSendTemp(ChatDTO chat) {
        String key = RandomUtil.createUniqueCode(24,chat.getChatGroupId());
        RedisClient.set(TEMP_CHAT_SEND_CACHE_HEAD + key, chat,
                RandomUtil.getRandNum(TEMP_OUT_TIME[0], TEMP_OUT_TIME[1]));
        return key;
    }

    /**
     * 获取对话发送内容
     *
     * @param key 缓存Key
     * @return 对话发送内容
     */
    public static ChatDTO getChatSend(String key) {
        if (!RedisClient.hasKey(TEMP_CHAT_SEND_CACHE_HEAD + key)) {
            return null;
        }
        ChatDTO chat = RedisClient.get(TEMP_CHAT_SEND_CACHE_HEAD + key, ChatDTO.class);
        RedisClient.removeKey(TEMP_CHAT_SEND_CACHE_HEAD + key);
        return chat;
    }

    /**
     * 设置 用户每日记录 待更新
     *
     * @param accountId 用户ID
     */
    public static void setUserDailyUpdate(int accountId) {
        RedisClient.leftPush(LIST_UPDATE_USER_DAILY_RECORD_ID, String.valueOf(accountId));
    }

    /**
     * 获取 待更新的 用户每日记录
     *
     * @return 用户每日记录
     */
    public static BaseUserRecord getUpdateUserDaily() {
        return getListTarget(LIST_UPDATE_USER_DAILY_RECORD_ID, CACHE_USER_DAILY_RECORD_HASH_KEY, BaseUserRecord.class);
    }

    /**
     * 获取 待更新的 用户每日记录 列表长度
     *
     * @return 列表长度
     */
    public static long getUpdateUserDailyLength() {
        if (!RedisClient.hasKey(LIST_UPDATE_USER_DAILY_RECORD_ID)) {
            return 0L;
        }
        return RedisClient.listLength(LIST_UPDATE_USER_DAILY_RECORD_ID);
    }

    /**
     * 设置 用户使用卡密 待更新
     *
     * @param accountId 账号ID
     */
    public static void setUserUsingUpdate(int accountId) {
        RedisClient.leftPush(LIST_UPDATE_USER_USING_ID, String.valueOf(accountId));
    }

    /**
     * 获取 待更新的 用户使用卡密
     *
     * @return 用户使用卡密
     */
    public static CardRecordDTO getUpdateUserUsing() {
        return getListTarget(LIST_UPDATE_USER_USING_ID, CACHE_CARD_USER_USING_HASH_KEY, CardRecordDTO.class);
    }

    /**
     * 获取 待更新的 用户使用卡密 列表长度
     *
     * @return 列表长度
     */
    public static long getUpdateUserUsingLength() {
        if (!RedisClient.hasKey(LIST_UPDATE_USER_USING_ID)) {
            return 0L;
        }
        return RedisClient.listLength(LIST_UPDATE_USER_USING_ID);
    }


    /**
     * 获取 用户使用卡密
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
     * 保存 用户使用卡密
     *
     * @param accountId 用户ID
     * @param card      卡密记录
     */
    public static void saveUsingCard(int accountId, CardRecordDTO card) {
        RedisClient.hSetPersist(CACHE_CARD_USER_USING_HASH_KEY, String.valueOf(accountId), card);
    }

    /**
     * 用户使用卡密缓存
     *
     * @param accountId 账号ID
     */
    public static void removeUsingCard(int accountId) {
        RedisClient.hRemoveField(CACHE_CARD_USER_USING_HASH_KEY, String.valueOf(accountId));
    }

    /**
     * 获取所有缓存中的 用户使用卡密
     *
     * @return 用户使用卡密集合
     */
    public static Collection<CardRecordDTO> getUsingList() {
        Map<String, CardRecordDTO> cardRecordMap = RedisClient.hGetAll(CACHE_CARD_USER_USING_HASH_KEY, CardRecordDTO.class);
        return cardRecordMap.values();
    }

    /**
     * 获取 用户每日记录
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

    /**
     * 获取所有缓存中的 用户每日记录
     *
     * @return 用户每日记录集合
     */
    public static Collection<BaseUserRecord> getUserRecordList() {
        Map<String, BaseUserRecord> userRecordMap = RedisClient.hGetAll(CACHE_USER_DAILY_RECORD_HASH_KEY, BaseUserRecord.class);
        return userRecordMap.values();
    }

    /**
     * 保存用户每日记录
     *
     * @param userRecord 用户记录
     */
    public static void saveUserDaily(BaseUserRecord userRecord) {
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

    public static BaseKey selectByWeight(List<BaseKey> keyList) {
        if (keyList.size() == 1) {
            return keyList.get(0);
        }
        int totalWeight = 0;
        for (BaseKey key : keyList) {
            totalWeight += key.getWeight();
        }
        int target = RandomUtil.getRandNum(0, totalWeight);
        int index = 0;
        for (BaseKey key : keyList) {
            index += key.getWeight();
            if (target <= index) {
                return key;
            }
        }
        return keyList.get(0);
    }

    /**
     * 保存卡密列表缓存
     *
     * @param keyList 卡密列表
     */
    public static void saveCacheKeyList(List<BaseKey> keyList) {
        RedisClient.set(TEMP_CARD_KEY_LIST, keyList, RandomUtil.getRandNum(TEMP_OUT_TIME[0], TEMP_OUT_TIME[1]));
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

}
