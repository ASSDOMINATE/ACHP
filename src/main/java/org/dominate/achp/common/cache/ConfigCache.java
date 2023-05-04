package org.dominate.achp.common.cache;

import com.hwja.tool.clients.redis.RedisClient;
import com.hwja.tool.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.entity.BaseConfig;
import org.dominate.achp.entity.dto.AppConfigDTO;
import org.dominate.achp.entity.dto.AppNoticeDTO;

import java.util.Map;

/**
 * 系统配置缓存
 *
 * @author domiante
 * @since 2023-04-27
 */
public final class ConfigCache {

    /**
     * 哈希缓存
     * APP配置
     */
    private static final String CACHE_APP_CONFIG_HASH_KEY = "cache.app.config:hash";
    /**
     * 键值缓存
     * APP提醒
     */
    private static final String CACHE_APP_NOTICE_INFO_KEY = "cache:app:notice:info";

    private static final String DEFAULT_VERSION = "default";
    private static final String VERSION_PLATFORM_SPLIT = "&";


    /**
     * 系统配置常量缓存
     */
    private static BaseConfig SYS_CONFIG_CACHE = null;


    /**
     * 获取基础配置
     *
     * @return 基础配置
     */
    public static BaseConfig getConfig() {
        return SYS_CONFIG_CACHE;
    }

    /**
     * 清除配置，记得下次使用从数据库更新
     */
    public static void clearConfig() {
        SYS_CONFIG_CACHE = null;
    }

    /**
     * 更新基础配置
     *
     * @param config 基础配置
     */
    public static void updateConfig(BaseConfig config) {
        SYS_CONFIG_CACHE = config;
    }

    /**
     * 获取所有APP配置
     *
     * @return APP配置 Map
     */
    public static Map<String, AppConfigDTO> getAllAppConfig() {
        return RedisClient.hGetAll(CACHE_APP_CONFIG_HASH_KEY, AppConfigDTO.class);
    }

    /**
     * 获取指定版本的APP配置
     * <p>
     * 如果没有该版本的配置，将生成默认配置
     *
     * @param version  版本号
     * @param platform 平台
     * @return APP配置
     */
    public static AppConfigDTO getAppConfig(String version, String platform) {
        if (StringUtils.isEmpty(platform)) {
            return getAppConfig(version);
        }
        String versionWithPlatform = platform + VERSION_PLATFORM_SPLIT + version;
        return getAppConfig(versionWithPlatform);
    }

    /**
     * 获取APP配置
     * <p>
     * 如果没有该版本的配置，将生成默认配置
     *
     * @param version 版本号
     * @return APP配置
     */
    public static AppConfigDTO getAppConfig(String version) {
        if (StringUtil.isEmpty(version)) {
            version = DEFAULT_VERSION;
        }
        if (!RedisClient.hHasKey(CACHE_APP_CONFIG_HASH_KEY, version)) {
            AppConfigDTO config = new AppConfigDTO(version);
            RedisClient.hSetPersist(CACHE_APP_CONFIG_HASH_KEY, version, config);
            return config;
        }
        return RedisClient.hGet(CACHE_APP_CONFIG_HASH_KEY, version, AppConfigDTO.class);
    }

    /**
     * 设置APP配置
     *
     * @param config 配置信息
     */
    public static void setAppConfig(AppConfigDTO config) {
        RedisClient.hSetPersist(CACHE_APP_CONFIG_HASH_KEY, config.getVersion(), config);
    }

    /**
     * 获取APP提醒
     *
     * @return APP提醒
     */
    public static AppNoticeDTO getAppNotice() {
        if (!RedisClient.hasKey(CACHE_APP_NOTICE_INFO_KEY)) {
            AppNoticeDTO notice = new AppNoticeDTO();
            RedisClient.setPersist(CACHE_APP_NOTICE_INFO_KEY, notice);
            return notice;
        }
        return RedisClient.get(CACHE_APP_NOTICE_INFO_KEY, AppNoticeDTO.class);
    }

    /**
     * 设置APP提醒
     *
     * @param notice APP提醒数据
     */
    public static void setAppNotice(AppNoticeDTO notice) {
        RedisClient.setPersist(CACHE_APP_NOTICE_INFO_KEY, notice);
    }
}
