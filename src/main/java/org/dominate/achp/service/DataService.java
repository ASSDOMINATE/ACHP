package org.dominate.achp.service;

import java.util.List;

/**
 * 需要跨 Service 调用的数据处理服务类
 *
 * @author dominate
 * @since 2022/02/25
 */
public interface DataService {

    /**
     * 获取用户权限ID列表
     *
     * @param accountId 账户ID
     * @return 权限ID列表
     */
    List<Integer> getUserPermissionIdList(int accountId);

    /**
     * 获取用户指定平台权限ID列表
     *
     * @param accountId  账户ID
     * @param platformId 平台ID
     * @return 权限ID列表
     */
    List<Integer> getUserPermissionIdList(int accountId, int platformId);


}
