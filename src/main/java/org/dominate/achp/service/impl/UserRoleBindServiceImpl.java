package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.constant.SqlConstants;
import org.dominate.achp.entity.UserRoleBind;
import org.dominate.achp.mapper.UserRoleBindMapper;
import org.dominate.achp.service.IUserRoleBindService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 用户角色绑定 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserRoleBindServiceImpl extends ServiceImpl<UserRoleBindMapper, UserRoleBind> implements IUserRoleBindService {


    @Override
    public boolean add(int accountId, int roleId) {
        int bindId = findBindId(accountId, roleId);
        if (bindId > 0) {
            return true;
        }
        UserRoleBind insert = new UserRoleBind();
        insert.setAccountId(accountId);
        insert.setRoleId(roleId);
        return save(insert);
    }

    @Override
    public boolean delete(int accountId, int roleId) {
        int bindId = findBindId(accountId, roleId);
        if (bindId > 0) {
            return removeById(bindId);
        }
        return true;
    }

    @Override
    public List<Integer> userBindRoleIdList(int accountId) {
        QueryWrapper<UserRoleBind> query = new QueryWrapper<>();
        query.lambda().eq(UserRoleBind::getAccountId, accountId).select(UserRoleBind::getRoleId);
        List<UserRoleBind> bindList = list(query);
        List<Integer> roleIdList = new ArrayList<>(bindList.size());
        for (UserRoleBind bind : bindList) {
            roleIdList.add(bind.getRoleId());
        }
        return roleIdList;
    }

    @Override
    public Map<Integer, List<Integer>> userBindRoleIdMap(List<Integer> accountIdList) {
        if(CollectionUtils.isEmpty(accountIdList)){
            return Collections.emptyMap();
        }
        QueryWrapper<UserRoleBind> query = new QueryWrapper<>();
        query.lambda().in(UserRoleBind::getAccountId, accountIdList).select(UserRoleBind::getAccountId,
                UserRoleBind::getRoleId);
        List<UserRoleBind> bindList = list(query);
        Map<Integer, List<Integer>> roleIdMap = new HashMap<>(bindList.size());
        for (UserRoleBind bind : bindList) {
            if (!roleIdMap.containsKey(bind.getAccountId())) {
                roleIdMap.put(bind.getAccountId(), new ArrayList<>());
            }
            roleIdMap.get(bind.getAccountId()).add(bind.getRoleId());
        }
        return roleIdMap;
    }

    @Override
    public List<Integer> roleUserList(int roleId, int index, int size) {
        QueryWrapper<UserRoleBind> query = new QueryWrapper<>();
        query.lambda().eq(UserRoleBind::getRoleId, roleId)
                .select(UserRoleBind::getAccountId)
                .last(SqlUtil.indexLimit(size, index));
        List<UserRoleBind> bindList = list(query);
        List<Integer> userIdList = new ArrayList<>(bindList.size());
        for (UserRoleBind bind : bindList) {
            userIdList.add(bind.getAccountId());
        }
        return userIdList;
    }

    @Override
    public List<Integer> hasRoleUserList(int roleId, List<Integer> accountIds) {
        if (CollectionUtils.isEmpty(accountIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserRoleBind> query = new QueryWrapper<>();
        query.lambda().in(UserRoleBind::getAccountId, accountIds)
                .eq(UserRoleBind::getRoleId, roleId)
                .select(UserRoleBind::getAccountId);
        List<UserRoleBind> bindList = list(query);
        List<Integer> hasBindAccountList = new ArrayList<>(bindList.size());
        for (UserRoleBind bind : bindList) {
            hasBindAccountList.add(bind.getAccountId());
        }
        return hasBindAccountList;
    }

    @Override
    public boolean add(int roleId, List<Integer> accountIds) {
        List<Integer> hasRoleList = hasRoleUserList(roleId, accountIds);
        List<UserRoleBind> bindList = new ArrayList<>(accountIds.size());
        for (Integer accountId : accountIds) {
            if (hasRoleList.contains(accountId)) {
                continue;
            }
            UserRoleBind insert = new UserRoleBind();
            insert.setAccountId(accountId);
            insert.setRoleId(roleId);
            bindList.add(insert);
        }
        return saveBatch(bindList);
    }

    private int findBindId(int accountId, int roleId) {
        QueryWrapper<UserRoleBind> query = new QueryWrapper<>();
        query.lambda().eq(UserRoleBind::getAccountId, accountId).eq(UserRoleBind::getRoleId, roleId)
                .select(UserRoleBind::getId)
                .last(SqlConstants.SQL_LIMIT_ONE);
        UserRoleBind bind = getOne(query);
        if (null == bind) {
            return 0;
        }
        return bind.getId();
    }
}
