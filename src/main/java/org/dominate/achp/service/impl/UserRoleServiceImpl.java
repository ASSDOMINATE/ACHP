package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.enums.State;
import org.dominate.achp.entity.UserRole;
import org.dominate.achp.entity.dto.RoleDTO;
import org.dominate.achp.entity.req.RoleReq;
import org.dominate.achp.entity.wrapper.RoleWrapper;
import org.dominate.achp.mapper.UserRoleMapper;
import org.dominate.achp.service.IUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 用户角色 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {


    @Override
    public List<RoleDTO> getDTOList(Integer platformId) {
        QueryWrapper<UserRole> query = createQuery(platformId);
        query.lambda().eq(UserRole::getState, State.ENABLE.getCode())
                .select(UserRole::getId, UserRole::getParentId, UserRole::getPlatformId, UserRole::getName,
                        UserRole::getDesr);
        return RoleWrapper.build().entityVO(list(query));
    }

    @Override
    public List<RoleDTO> getDTOList(List<Integer> roleIdList) {
        if (CollectionUtils.isEmpty(roleIdList)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserRole> query = new QueryWrapper<>();
        query.lambda().in(UserRole::getId, roleIdList)
                .select(UserRole::getId, UserRole::getParentId, UserRole::getPlatformId, UserRole::getName,
                        UserRole::getDesr);
        return RoleWrapper.build().entityVO(list(query));
    }

    @Override
    public List<UserRole> getList(Integer platformId) {
        return getList(platformId, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    @Override
    public List<UserRole> getList(Integer platformId, String nameZT, String desrZT) {
        QueryWrapper<UserRole> query = createQuery(platformId);
        query.lambda().like(StringUtils.isNotEmpty(nameZT), UserRole::getName, nameZT).
                like(StringUtils.isNotEmpty(desrZT), UserRole::getDesr, desrZT);
        return list(query);
    }

    @Override
    public List<UserRole> getChildList(int parentId) {
        List<UserRole> list = new ArrayList<>();
        findChild(parentId, list);
        return list;
    }

    @Override
    public boolean save(RoleReq req) {
        if (null == req.getId()) {
            if (StringUtils.isEmpty(req.getName()) || null == req.getParentId() || null == req.getPlatformId()) {
                return false;
            }
            UserRole insert = new UserRole();
            insert.setName(req.getName());
            insert.setParentId(req.getParentId());
            insert.setPlatformId(req.getPlatformId());
            insert.setDesr(StringUtils.isNotEmpty(req.getDesr()) ? req.getDesr() : StringUtils.EMPTY);
            insert.setState(null == req.getState() ? State.ENABLE.getCode() : State.getValueByCode(
                    req.getState()).getCode());
            return save(insert);
        }
        UserRole role = getById(req.getId());
        if (null == role) {
            return false;
        }
        UserRole update = new UserRole();
        if (StringUtils.isNotEmpty(req.getName()) && !req.getName().equals(role.getName())) {
            update.setName(req.getName());
            update.setId(role.getId());
        }
        if (StringUtils.isNotEmpty(req.getDesr()) && !req.getDesr().equals(role.getDesr())) {
            update.setDesr(req.getDesr());
            update.setId(role.getId());
        }
        if (null != req.getParentId() && !req.getParentId().equals(role.getParentId())) {
            update.setParentId(req.getParentId());
            update.setId(req.getId());
        }
        if (null != req.getPlatformId() && !req.getPlatformId().equals(role.getPlatformId())) {
            update.setPlatformId(req.getPlatformId());
            update.setId(req.getId());
        }
        if (null != req.getState() && !req.getState().equals(role.getState())) {
            update.setState(State.getValueByCode(req.getState()).getCode());
            update.setId(req.getId());
        }
        if (null == req.getId()) {
            return false;
        }
        return updateById(update);
    }

    @Override
    public List<Integer> filterPlatform(List<Integer> roleIdList, int platformId) {
        if(CollectionUtils.isEmpty(roleIdList)){
            return Collections.emptyList();
        }
        QueryWrapper<UserRole> query = createQuery(platformId);
        query.lambda().in(UserRole::getId, roleIdList).select(UserRole::getId);
        List<UserRole> roleList = list(query);
        List<Integer> idList = new ArrayList<>(roleIdList.size());
        for (UserRole role : roleList) {
            idList.add(role.getId());
        }
        return idList;
    }


    private void findChild(int parentId, List<UserRole> list) {
        List<UserRole> thisList = getNextList(parentId);
        list.addAll(thisList);
        for (UserRole role : thisList) {
            findChild(role.getId(), list);
        }
    }

    private List<UserRole> getNextList(int parentId) {
        QueryWrapper<UserRole> query = new QueryWrapper<>();
        query.lambda().eq(UserRole::getParentId, parentId).eq(UserRole::getState, State.ENABLE.getCode());
        return list(query);
    }

    private static QueryWrapper<UserRole> createQuery(Integer platformId) {
        QueryWrapper<UserRole> query = new QueryWrapper<>();
        query.lambda().eq(null != platformId && 0 != platformId, UserRole::getPlatformId, platformId);
        return query;
    }
}
