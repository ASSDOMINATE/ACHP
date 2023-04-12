package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.UserPermission;
import org.dominate.achp.entity.UserRolePermission;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 用户角色权限 服务类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
public interface IUserRolePermissionService extends IService<UserRolePermission> {

    /**
     * 添加角色下的权限
     * 会添加所有子权限
     *
     * @param roleId       角色ID
     * @param permissionId 权限ID
     * @return 添加成功
     */
    boolean add(int roleId, int permissionId);

    /**
     * 删除角色下的权限
     * 会删除所有子权限
     *
     * @param roleId       角色ID
     * @param permissionId 权限ID
     * @return
     */
    boolean delete(int roleId, int permissionId);

    /**
     * 删除权限后
     *
     * @param permissionId
     * @return
     */
    boolean deletePermission(int permissionId);

    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Integer> getRolePermissionIdList(int roleId);

    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<UserPermission> getRolePermissionList(int roleId);

    /**
     * 获取角色列表的权限ID列表
     *
     * @param roleIdList 角色ID列表
     * @return 权限ID列表
     */
    List<Integer> getRolesPermissionIdList(List<Integer> roleIdList);

    /**
     * 获取拥有该权限的角色
     *
     * @param permissionId 权限ID
     * @return 角色ID列表
     */
    List<Integer> getHasPermissionRoleIdList(int permissionId);

    /**
     * 角色是否有权限
     *
     * @param roleIds       角色ID
     * @param permissionId 权限ID
     * @return 是否有权限
     */
    boolean hasRolePermission(int permissionId, Collection<Integer> roleIds);



}
