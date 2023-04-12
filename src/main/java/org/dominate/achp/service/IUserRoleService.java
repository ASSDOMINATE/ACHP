package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.UserRole;
import org.dominate.achp.entity.dto.RoleDTO;
import org.dominate.achp.entity.req.RoleReq;

import java.util.List;

/**
 * <p>
 * 用户角色 服务类
 * </p>
 *
 * @author dominate
 * @since 2022-01-25
 */
public interface IUserRoleService extends IService<UserRole> {

    /**
     * 获取角色列表 查看用
     *
     * @param platformId 平台ID
     * @return 角色列表
     */
    List<RoleDTO> getDTOList(Integer platformId);

    /**
     * 角色ID获取角色
     *
     * @param roleIdList 角色ID列表
     * @return 角色列表
     */
    List<RoleDTO> getDTOList(List<Integer> roleIdList);

    /**
     * 获取角色列表 管理用
     *
     * @param platformId 平台ID
     * @param desrZT     角色描述
     * @param nameZT     角色名称
     * @return 角色列表
     */
    List<UserRole> getList(Integer platformId, String nameZT, String desrZT);

    /**
     * 获取角色列表 管理用
     *
     * @param platformId 平台ID
     * @return 角色列表
     */
    List<UserRole> getList(Integer platformId);

    /**
     * 获取所有子角色
     *
     * @param parentId 父角色ID
     * @return 子角色列表
     */
    List<UserRole> getChildList(int parentId);

    /**
     * 保存/新增角色
     *
     * @param req 角色数据
     * @return 是否保存/新增成功
     */
    boolean save(RoleReq req);

    /**
     * 按平台过滤角色ID
     *
     * @param roleIdList 角色IDList
     * @param platformId 平台ID
     * @return 角色IDList
     */
    List<Integer> filterPlatform(List<Integer> roleIdList, int platformId);


}
