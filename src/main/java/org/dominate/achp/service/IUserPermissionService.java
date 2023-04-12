package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.common.enums.PermissionType;
import org.dominate.achp.entity.UserPermission;
import org.dominate.achp.entity.dto.PermissionDTO;
import org.dominate.achp.entity.req.PermissionReq;

import java.util.List;

/**
 * <p>
 * 用户权限 服务类
 * </p>
 *
 * @author dominate
 * @since 2022-01-25
 */
public interface IUserPermissionService extends IService<UserPermission> {

    /**
     * 查看权限列表 显示用
     *
     * @param platformId 平台ID
     * @param typeEnum   权限类型
     * @return 权限列表
     */
    List<PermissionDTO> getDTOList(Integer platformId, PermissionType typeEnum);

    /**
     * 查看权限列表 管理用
     *
     * @param platformId 平台ID
     * @param typeEnum   权限类型
     * @param name 权限名称过滤
     * @return 权限列表
     */
    List<UserPermission> getList(Integer platformId, PermissionType typeEnum, String name);

    /**
     * 获取所有的子权限
     *
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    List<UserPermission> getChildList(int parentId);

    /**
     * 获取所有的子权限ID列表
     *
     * @param parentId 父权限ID
     * @return 子权限ID列表
     */
    List<Integer> getChildIdList(int parentId);

    /**
     * ID列表获取
     * @param idList id列表
     * @return 权限列表
     */
    List<UserPermission> getListByIdList(List<Integer> idList);

    /**
     * 保存/新增 权限
     *
     * @param req 权限数据
     * @return 是否保存/新增成功
     */
    boolean save(PermissionReq req);

    /**
     * 设置 删除状态
     *
     * @param id  权限ID
     * @param del 是否删除
     * @return 是否设置成功
     */
    boolean setDelete(int id, boolean del);

    /**
     * 解析菜单路径列表
     * @param permIdList 权限ID列表
     * @param platformId 平台ID
     * @return 菜单路径列表
     */
    List<String> parsePathList(List<Integer> permIdList,int platformId);

    /**
     * 查找权限ID
     * @param path 权限路径
     * @return 权限ID
     */
    int findPerm(String path);

}
