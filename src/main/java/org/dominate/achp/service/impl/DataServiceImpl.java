package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.service.DataService;
import org.dominate.achp.service.IUserRoleBindService;
import org.dominate.achp.service.IUserRolePermissionService;
import org.dominate.achp.service.IUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 需要跨 Service 调用的数据 服务实现类
 *
 * @author dominate
 * @since 2022/02/25
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class DataServiceImpl implements DataService {

    private IUserRolePermissionService userRolePermissionService;
    private IUserRoleBindService userRoleBindService;
    private IUserRoleService userRoleService;

    @Override
    public List<Integer> getUserPermissionIdList(int accountId) {
        return getUserPermissionIdList(accountId, 0);
    }

    @Override
    public List<Integer> getUserPermissionIdList(int accountId, int platformId) {
        List<Integer> roleIdList = userRoleBindService.userBindRoleIdList(accountId);
        if (CollectionUtils.isEmpty(roleIdList)) {
            return Collections.emptyList();
        }
        if (0 != platformId) {
            roleIdList = userRoleService.filterPlatform(roleIdList, platformId);
        }
        return userRolePermissionService.getRolesPermissionIdList(roleIdList);
    }



}
