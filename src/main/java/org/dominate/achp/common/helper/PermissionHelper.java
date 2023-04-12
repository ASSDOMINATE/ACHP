package org.dominate.achp.common.helper;

import com.hwja.tool.clients.redis.RedisClient;
import org.dominate.achp.entity.UserPermission;

import java.util.*;

/**
 * 权限验证
 *
 * @author dominate
 * @since 2022/02/22
 */
public final class PermissionHelper {

    private static final String CACHE_SYS_PERMISSION = "cache:sys:permission";

    /**
     * 获取缓存里的所有权限路径
     *
     * @return 权限路径 Set
     */
    public static Set<String> getAllPathSet() {
        Map<String, Object> cachePermissionMap = getAllPermCache();
        return cachePermissionMap.keySet();
    }

    /**
     * 更新权限
     *
     * @param allPermissionList 所有权限列表
     */
    public static void refreshPerm(List<UserPermission> allPermissionList) {
        List<String> setPathList = new ArrayList<>(allPermissionList.size());
        for (UserPermission permission : allPermissionList) {
            setPermCache(permission.getPath(), permission.getId());
            setPathList.add(permission.getPath());
        }
        Map<String, Object> cachePermissionMap = getAllPermCache();
        for (String path : cachePermissionMap.keySet()) {
            if (!setPathList.contains(path)) {
                removePermCache(path);
            }
        }
    }

    /**
     * 判断是否有权限
     *
     * @param path             请求地址
     * @param permissionIdList 权限ID列表
     * @return 是否有权限
     */
    public static boolean hasPerm(String path, List<Integer> permissionIdList) {
        if (!needCheckPerm(path)) {
            return true;
        }
        Integer permissionId = getExistedPermId(path);
        return permissionIdList.contains(permissionId);
    }

    /**
     * 更新权限缓存
     *
     * @param path         权限路径
     * @param permissionId 权限ID
     */
    public static void setPermCache(String path, int permissionId) {
        RedisClient.hSetPersist(CACHE_SYS_PERMISSION, path, permissionId);
    }

    /**
     * 删除权限缓存
     *
     * @param path 权限路径
     */
    public static void removePermCache(String path) {
        RedisClient.hRemoveField(CACHE_SYS_PERMISSION, path);
    }

    /**
     * 获取所有权限缓存
     *
     * @return 权限缓存 key path , value id
     */
    private static Map<String, Object> getAllPermCache() {
        if (!RedisClient.hasKey(CACHE_SYS_PERMISSION)) {
            return Collections.emptyMap();
        }
        return RedisClient.hGetAll(CACHE_SYS_PERMISSION);
    }

    /**
     * 获取存在的权限ID
     * 需要确保 path 的存在的
     *
     * @param path 权限路径
     * @return 权限ID
     */
    private static Integer getExistedPermId(String path) {
        return RedisClient.hGet(CACHE_SYS_PERMISSION, path, Integer.class);
    }

    /**
     * 是否需要检查权限
     *
     * @param path 权限路径
     * @return 是否需要检查权限
     */
    private static boolean needCheckPerm(String path) {
        return RedisClient.hHasKey(CACHE_SYS_PERMISSION, path);
    }

}
