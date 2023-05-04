package org.dominate.achp.common.cache;

import com.hwja.tool.clients.redis.RedisClient;
import org.dominate.achp.entity.dto.WallpaperDTO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 壁纸缓存
 *
 * @author dominate
 * @since 2023-04-27
 */
public final class WallpaperCache {

    /**
     * 哈希缓存
     * 壁纸数据缓存
     */
    private static final String CACHE_WALL_PAPER_DATA_HASH_KEY = "cache:wallpaper:data:hash";

    /**
     * 保存壁纸信息
     *
     * @param wallpaper 壁纸信息
     */
    public static void save(WallpaperDTO wallpaper) {
        RedisClient.hSetPersist(CACHE_WALL_PAPER_DATA_HASH_KEY, wallpaper.getCode(), wallpaper);
    }

    /**
     * 获取所有壁纸信息
     * 按 WallpaperDTO.seq 排序
     *
     * @return 壁纸信息列表
     */
    public static List<WallpaperDTO> getAll() {
        if (!RedisClient.hasKey(CACHE_WALL_PAPER_DATA_HASH_KEY)) {
            return Collections.emptyList();
        }
        Map<String, WallpaperDTO> wallpaperMap = RedisClient.hGetAll(CACHE_WALL_PAPER_DATA_HASH_KEY, WallpaperDTO.class);
        return wallpaperMap.values().stream().sorted(Comparator.comparingInt(WallpaperDTO::getSeq)).collect(Collectors.toList());
    }

}
