package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.entity.UserPermission;
import org.dominate.achp.entity.UserRolePermission;
import org.dominate.achp.mapper.UserRolePermissionMapper;
import org.dominate.achp.service.IUserPermissionService;
import org.dominate.achp.service.IUserRolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 用户角色权限 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserRolePermissionServiceImpl extends ServiceImpl<UserRolePermissionMapper, UserRolePermission> implements IUserRolePermissionService {

    @Resource
    private IUserPermissionService userPermissionService;

    @Override
    public boolean add(int roleId, int permissionId) {
        // 获取父权限下所有权限
        List<Integer> addPermissionIdList = userPermissionService.getChildIdList(permissionId);
        addPermissionIdList.add(permissionId);
        // 是否已存在
        List<UserRolePermission> existedList = find(roleId, addPermissionIdList);
        List<Integer> existedIdList = new ArrayList<>(existedList.size());
        for (UserRolePermission permission : existedList) {
            existedIdList.add(permission.getPermissionId());
        }
        int insertCount = 0;
        int successCount = 0;
        for (Integer addPermissionId : addPermissionIdList) {
            // 数据库存在就不新增
            if (existedIdList.contains(addPermissionId)) {
                continue;
            }
            insertCount++;
            UserRolePermission insert = new UserRolePermission();
            insert.setRoleId(roleId);
            insert.setPermissionId(addPermissionId);
            successCount += save(insert) ? 1 : 0;
        }
        return insertCount == successCount;
    }

    @Override
    public boolean delete(int roleId, int permissionId) {
        // 获取父权限下所有权限
        List<Integer> addPermissionIdList = userPermissionService.getChildIdList(permissionId);
        addPermissionIdList.add(permissionId);
        List<UserRolePermission> existedList = find(roleId, addPermissionIdList);
        List<Integer> idList = new ArrayList<>(existedList.size());
        for (UserRolePermission permission : existedList) {
            idList.add(permission.getId());
        }
        return removeByIds(idList);
    }

    @Override
    public boolean deletePermission(int permissionId) {
        QueryWrapper<UserRolePermission> query = new QueryWrapper<>();
        query.lambda().eq(UserRolePermission::getPermissionId, permissionId);
        return remove(query);
    }

    @Override
    public List<Integer> getRolePermissionIdList(int roleId) {
        QueryWrapper<UserRolePermission> query = new QueryWrapper<>();
        query.lambda().eq(UserRolePermission::getRoleId, roleId).select(UserRolePermission::getPermissionId);
        return getTargetIdList(query, false);
    }

    @Override
    public List<UserPermission> getRolePermissionList(int roleId) {
        List<Integer> permissionIdList = getRolePermissionIdList(roleId);
        return userPermissionService.getListByIdList(permissionIdList);
    }

    @Override
    public List<Integer> getRolesPermissionIdList(List<Integer> roleIdList) {
        if (CollectionUtils.isEmpty(roleIdList)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserRolePermission> query = new QueryWrapper<>();
        query.lambda().in(UserRolePermission::getRoleId, roleIdList).select(UserRolePermission::getPermissionId);
        return getTargetIdList(query, false);
    }

    @Override
    public List<Integer> getHasPermissionRoleIdList(int permissionId) {
        if(0 == permissionId){
            return Collections.emptyList();
        }
        QueryWrapper<UserRolePermission> query = new QueryWrapper<>();
        query.lambda().eq(UserRolePermission::getPermissionId, permissionId).select(UserRolePermission::getRoleId);
        return getTargetIdList(query, true);
    }

    @Override
    public boolean hasRolePermission(int permissionId, Collection<Integer> roleIds) {
        if(CollectionUtils.isEmpty(roleIds)){
            return false;
        }
        QueryWrapper<UserRolePermission> query = new QueryWrapper<>();
        query.lambda().in(UserRolePermission::getRoleId, roleIds)
                .eq(UserRolePermission::getPermissionId, permissionId);
        return count(query) > 0;
    }


    private List<Integer> getTargetIdList(QueryWrapper<UserRolePermission> query, boolean forRole) {
        List<UserRolePermission> rolePermissionList = list(query);
        List<Integer> targetIdList = new ArrayList<>(rolePermissionList.size());
        for (UserRolePermission rolePermission : rolePermissionList) {
            targetIdList.add(forRole ? rolePermission.getRoleId() : rolePermission.getPermissionId());
        }
        return targetIdList;
    }

    private List<UserRolePermission> find(int roleId, List<Integer> permissionId) {
        if (CollectionUtils.isEmpty(permissionId)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserRolePermission> query = new QueryWrapper<>();
        query.lambda().eq(UserRolePermission::getRoleId, roleId).in(UserRolePermission::getPermissionId, permissionId);
        return list(query);
    }
}
