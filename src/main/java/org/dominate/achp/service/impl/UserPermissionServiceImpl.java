package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.cache.PermissionCache;
import org.dominate.achp.common.constant.SqlConstants;
import org.dominate.achp.common.enums.PermissionType;
import org.dominate.achp.entity.UserPermission;
import org.dominate.achp.entity.dto.PermissionDTO;
import org.dominate.achp.entity.req.PermissionReq;
import org.dominate.achp.entity.wrapper.PermissionWrapper;
import org.dominate.achp.mapper.UserPermissionMapper;
import org.dominate.achp.service.IUserPermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 用户权限 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserPermissionServiceImpl extends ServiceImpl<UserPermissionMapper, UserPermission> implements IUserPermissionService {

    @Override
    public List<PermissionDTO> getDTOList(Integer platformId, PermissionType typeEnum) {
        QueryWrapper<UserPermission> query = createQuery(platformId, typeEnum.getCode());
        query.lambda().eq(UserPermission::getIsDel, false)
                .select(UserPermission::getId, UserPermission::getParentId, UserPermission::getPermissionType,
                        UserPermission::getPlatformId, UserPermission::getName, UserPermission::getDesr,
                        UserPermission::getCode, UserPermission::getPath);
        List<UserPermission> permissionList = list(query);
        return PermissionWrapper.build().entityVO(permissionList);
    }

    @Override
    public List<UserPermission> getList(Integer platformId, PermissionType typeEnum, String name) {
        QueryWrapper<UserPermission> query = createQuery(platformId, typeEnum.getCode());
        query.lambda().like(UserPermission::getName, name);
        return list(query);
    }

    @Override
    public List<UserPermission> getChildList(int parentId) {
        List<UserPermission> list = new ArrayList<>();
        findChild(parentId, list);
        return list;
    }

    @Override
    public List<Integer> getChildIdList(int parentId) {
        List<Integer> list = new ArrayList<>();
        findChildId(parentId, list);
        return list;
    }

    @Override
    public List<UserPermission> getListByIdList(List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserPermission> query = new QueryWrapper<>();
        query.lambda().in(UserPermission::getId, idList)
                .eq(UserPermission::getIsDel, false);
        return list(query);
    }

    @Override
    public boolean save(PermissionReq req) {
        if (null == req.getId()) {
            UserPermission insert = new UserPermission();
            insert.setPlatformId(req.getPlatformId());
            insert.setParentId(req.getParentId());
            insert.setPermissionType(req.getType());
            insert.setName(req.getName());
            insert.setDesr(req.getDesr());
            insert.setCode(createUniqueCode());
            insert.setPath(req.getPath());
            if (save(insert)) {
                PermissionCache.setPermCache(req.getPath(), insert.getId());
                req.setId(insert.getId());
                return true;
            }
            return false;
        }
        UserPermission permission = getById(req.getId());
        if (null == permission) {
            return false;
        }
        UserPermission update = new UserPermission();
        if (null != req.getPlatformId() && !req.getPlatformId().equals(permission.getPlatformId())) {
            update.setPlatformId(req.getPlatformId());
            update.setId(req.getId());
        }
        if (null != req.getParentId() && !req.getParentId().equals(permission.getParentId())) {
            update.setParentId(req.getParentId());
            update.setId(req.getId());
        }
        if (null != req.getType() && !req.getType().equals(permission.getPermissionType())) {
            update.setPermissionType(req.getType());
            update.setId(req.getId());
        }
        if (StringUtils.isNotEmpty(req.getName()) && !req.getName().equals(permission.getName())) {
            update.setName(req.getName());
            update.setId(req.getId());
        }
        if (StringUtils.isNotEmpty(req.getDesr()) && !req.getDesr().equals(permission.getDesr())) {
            update.setDesr(req.getDesr());
            update.setId(req.getId());
        }
        if (StringUtils.isNotEmpty(req.getPath()) && !req.getPath().equals(permission.getPath())) {
            update.setPath(req.getPath());
            update.setId(req.getId());
        }
        if (null == update.getId()) {
            return false;
        }
        if (updateById(update)) {
            if (StringUtils.isNotEmpty(update.getPath())) {
                PermissionCache.setPermCache(update.getPath(), update.getId());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean setDelete(int id, boolean del) {
        UserPermission permission = getById(id);
        if (null == permission) {
            return false;
        }
        if (permission.getIsDel().equals(del)) {
            return true;
        }
        UserPermission update = new UserPermission();
        update.setId(id);
        update.setIsDel(del);
        if (del) {
            PermissionCache.removePermCache(update.getPath());
        } else {
            PermissionCache.setPermCache(permission.getPath(), id);
        }
        return updateById(update);
    }

    @Override
    public List<String> parsePathList(List<Integer> permIdList, int platformId) {
        if (CollectionUtils.isEmpty(permIdList)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserPermission> query = new QueryWrapper<>();
        query.lambda().in(UserPermission::getId, permIdList)
                .eq(0 != platformId, UserPermission::getPlatformId, platformId)
                .eq(UserPermission::getPermissionType, PermissionType.MENU.getCode())
                .select(UserPermission::getPath);
        List<UserPermission> permList = list(query);
        List<String> pathList = new ArrayList<>(permList.size());
        for (UserPermission perm : permList) {
            pathList.add(perm.getPath());
        }
        return pathList;
    }

    @Override
    public int findPerm(String path) {
        QueryWrapper<UserPermission> query = new QueryWrapper<>();
        query.lambda().eq(UserPermission::getPath, path)
                .select(UserPermission::getId)
                .last(SqlConstants.SQL_LIMIT_ONE);
        UserPermission perm = getOne(query);
        return null == perm ? 0 : perm.getId();
    }

    private String createUniqueCode() {
        String permCode = RandomUtil.createRandomStrWords(8);
        if (!isExistedCode(permCode)) {
            return permCode;
        }
        return createUniqueCode();
    }

    private boolean isExistedCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return true;
        }
        QueryWrapper<UserPermission> query = new QueryWrapper<>();
        query.lambda().eq(UserPermission::getCode, code);
        return 0 < count(query);
    }

    private void findChildId(int parentId, List<Integer> list) {
        List<Integer> thisList = getNextIdList(parentId);
        if (CollectionUtils.isEmpty(thisList)) {
            return;
        }
        list.addAll(thisList);
        for (Integer id : thisList) {
            findChildId(id, list);
        }
    }

    private void findChild(int parentId, List<UserPermission> list) {
        List<UserPermission> thisList = getNextList(parentId);
        if (CollectionUtils.isEmpty(thisList)) {
            return;
        }
        list.addAll(thisList);
        for (UserPermission permission : thisList) {
            findChild(permission.getId(), list);
        }
    }

    private List<Integer> getNextIdList(int parentId) {
        List<UserPermission> list = getNextList(parentId, true);
        List<Integer> idList = new ArrayList<>(list.size());
        for (UserPermission permission : list) {
            idList.add(permission.getId());
        }
        return idList;
    }

    private List<UserPermission> getNextList(int parentId) {
        return getNextList(parentId, false);
    }

    private List<UserPermission> getNextList(int parentId, boolean onlyId) {
        QueryWrapper<UserPermission> query = new QueryWrapper<>();
        query.lambda().eq(UserPermission::getParentId, parentId).eq(UserPermission::getIsDel, false);
        if (onlyId) {
            query.lambda().select(UserPermission::getId);
        }
        return list(query);
    }

    private QueryWrapper<UserPermission> createQuery(Integer platformId, int typeCode) {
        QueryWrapper<UserPermission> query = new QueryWrapper<>();
        query.lambda().eq(null != platformId && 0 != platformId, UserPermission::getPlatformId, platformId)
                .eq(PermissionType.ALL.getCode() != typeCode, UserPermission::getPermissionType, typeCode);
        return query;
    }
}
