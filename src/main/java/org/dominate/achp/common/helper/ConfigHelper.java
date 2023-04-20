package org.dominate.achp.common.helper;

import com.hwja.tool.clients.redis.RedisClient;
import org.dominate.achp.entity.dto.AppConfigDTO;

import java.util.Map;

public class ConfigHelper {

    private static final String CACHE_APP_CONFIG_HASH_KEY = "cache.app.config:hash";

    /**
     * 获取所有APP配置
     *
     * @return APP配置 Map
     */
    public static Map<String, AppConfigDTO> getAllAppConfig() {
        return RedisClient.hGetAll(CACHE_APP_CONFIG_HASH_KEY, AppConfigDTO.class);
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
}
