package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.common.enums.UserState;
import org.dominate.achp.entity.UserAccount;
import org.dominate.achp.sys.exception.BusinessException;

/**
 * <p>
 * 用户账号 服务类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
public interface IUserAccountService extends IService<UserAccount> {

    /**
     * 添加账号 返回账号ID
     * 添加失败返回 0
     *
     * @param password 账号密码
     * @return 账号ID
     */
    int addAccount(String password);


    /**
     * 验证密码
     *
     * @param id       账号ID
     * @param password 密码
     * @return 是否验证通过
     */
    boolean validPassword(int id, String password);

    /**
     * 修改密码
     * <p>
     * 修改失败会抛出失败原因异常
     *
     * @param id             账号ID
     * @param oldPassword    旧密码
     * @param updatePassword 新密码
     * @return 是否修改成功
     */
    boolean modifyPassword(int id, String oldPassword, String updatePassword) throws BusinessException;

    /**
     * 重置密码
     *
     * @param id
     * @return 重置后的随机密码
     */
    String resetPassword(int id);

    /**
     * 设置密码
     *
     * @param id       账号ID
     * @param password 新密码
     * @return 是否设置成功
     */
    boolean setPassword(int id, String password);

    /**
     * 获取账号状态
     *
     * @param id 账号ID
     * @return 状态枚举
     */
    UserState getState(int id);

    /**
     * 更新状态
     *
     * @param id        账号ID
     * @param stateEnum 状态枚举
     * @return 是否修改成功
     */
    boolean updateState(int id, UserState stateEnum);

}
