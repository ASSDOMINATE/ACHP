package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.constant.SqlConstants;
import org.dominate.achp.common.enums.UserState;
import org.dominate.achp.common.utils.PasswordUtil;
import org.dominate.achp.entity.UserAccount;
import org.dominate.achp.mapper.UserAccountMapper;
import org.dominate.achp.service.IUserAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户账号 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements IUserAccountService {


    @Override
    public int addAccount(String password) {
        UserAccount account = new UserAccount();
        // 生成盐值
        setPassword(account, password);
        account.setState(UserState.NOT_ACTIVE.getCode());
        if (save(account)) {
            return account.getId();
        }
        return SqlConstants.DB_EMPTY_ID_SIGN;
    }

    private static void setPassword(UserAccount account, String password) {
        account.setSalt(PasswordUtil.createSalt());
        account.setPassword(PasswordUtil.encryptPassword(password, account.getSalt()));
    }


    @Override
    public boolean validPassword(int id, String password) {
        UserAccount account = getById(id);
        if (!validPassword(password, account)) {
            return false;
        }
        return UserState.NORMAL == UserState.getValueByCode(account.getState());
    }

    @Override
    public boolean modifyPassword(int id, String oldPassword, String updatePassword) {
        UserAccount account = getById(id);
        if (!validPassword(oldPassword, account)) {
            return false;
        }
        // 修改密码需要重置盐值
        UserAccount update = new UserAccount();
        update.setId(account.getId());
        update.setSalt(PasswordUtil.createSalt());
        update.setPassword(PasswordUtil.encryptPassword(updatePassword, update.getSalt()));
        return updateById(update);
    }

    @Override
    public String resetPassword(int id) {
        UserAccount account = getById(id);
        if (null == account) {
            return null;
        }
        // 生成随机密码、随机盐值
        String password = PasswordUtil.createRandPassword();
        UserAccount update = new UserAccount();
        update.setId(account.getId());
        update.setSalt(PasswordUtil.createSalt());
        update.setPassword(PasswordUtil.encryptPassword(password, update.getSalt()));
        if (updateById(update)) {
            return password;
        }
        return null;
    }

    @Override
    public boolean setPassword(int id, String password) {
        UserAccount update = new UserAccount();
        update.setId(id);
        update.setSalt(PasswordUtil.createSalt());
        update.setPassword(PasswordUtil.encryptPassword(password, update.getSalt()));
        return updateById(update);
    }

    @Override
    public UserState getState(int id) {
        UserAccount account = getById(id);
        if (null == account) {
            return UserState.NOT_FIND;
        }
        return UserState.getValueByCode(account.getState());
    }

    @Override
    public boolean updateState(int id, UserState stateEnum) {
        UserAccount account = new UserAccount();
        account.setId(id);
        account.setState(stateEnum.getCode());
        return updateById(account);
    }

    /**
     * 验证密码
     *
     * @param password 密码
     * @param account  数据库账号
     * @return 是否验证通过
     */
    private static boolean validPassword(String password, UserAccount account) {
        if (null == account) {
            return false;
        }
        return PasswordUtil.validPassword(password, account.getSalt(), account.getPassword());
    }
}
