package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.UserRoleBind;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户角色绑定 服务类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
public interface IUserRoleBindService extends IService<UserRoleBind> {

    /**
     * 设置用户角色
     *
     * @param accountId 账号ID
     * @param roleId    角色ID
     * @return 是否设置成功
     */
    boolean add(int accountId, int roleId);

    /**
     * 删除用户角色
     *
     * @param accountId 账号ID
     * @param roleId    角色ID
     * @return 是否设置成功
     */
    boolean delete(int accountId, int roleId);

    /**
     * 获取用户绑定角色ID列表
     *
     * @param accountId 账号ID
     * @return 角色ID列表
     */
    List<Integer> userBindRoleIdList(int accountId);

    /**
     * 获取用户绑定 角色ID Map
     *
     * @param accountIdList 账号ID
     * @return 角色ID Map
     */
    Map<Integer, List<Integer>> userBindRoleIdMap(List<Integer> accountIdList);

    /**
     * 角色下的用户ID列表
     *
     * @param roleId 角色ID
     * @param index  分页位置
     * @param size   分页数量
     * @return 用户ID列表
     */
    List<Integer> roleUserList(int roleId, int index, int size);

    /**
     * 有角色的用户ID列表
     *
     * @param roleId     角色ID
     * @param accountIds 用户ID
     * @return 用户ID列表
     */
    List<Integer> hasRoleUserList(int roleId, List<Integer> accountIds);

    /**
     * 添加用户
     *
     * @param roleId     角色ID
     * @param accountIds 用户ID列表
     * @return 是否添加成功
     */
    boolean add(int roleId, List<Integer> accountIds);

}
