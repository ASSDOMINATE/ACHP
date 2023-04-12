package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.constant.SqlConstants;
import org.dominate.achp.common.enums.ExceptionType;
import org.dominate.achp.common.enums.ExistedType;
import org.dominate.achp.common.enums.SexType;
import org.dominate.achp.common.enums.UserState;
import org.dominate.achp.entity.UserInfo;
import org.dominate.achp.entity.dto.UserDTO;
import org.dominate.achp.entity.req.InfoReq;
import org.dominate.achp.entity.wrapper.UserWrapper;
import org.dominate.achp.mapper.UserInfoMapper;
import org.dominate.achp.service.IUserInfoService;
import org.dominate.achp.sys.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    private static final List<SFunction<UserInfo, ?>> SEARCH_FUNCTION = new ArrayList<>();

    static {
        // 唯一编码查询
        SEARCH_FUNCTION.add(UserInfo::getUniqueCode);
        // 身份证号查询
        SEARCH_FUNCTION.add(UserInfo::getIdentity);
        // 名字查询
        SEARCH_FUNCTION.add(UserInfo::getName);
        // 别名查询
        SEARCH_FUNCTION.add(UserInfo::getAlias);
        // 电话号查询
        SEARCH_FUNCTION.add(UserInfo::getPhone);
        // 邮箱查询
        SEARCH_FUNCTION.add(UserInfo::getEmail);
    }

    @Override
    public boolean existed(String keyword, ExistedType existedType) {
        switch (existedType) {
            case MAIL:
                return isExisted(UserInfo::getEmail, keyword);
            case PHONE:
                return isExisted(UserInfo::getPhone, keyword);
            case IDENTITY:
                return isExisted(UserInfo::getIdentity, keyword);
            default:
                return false;
        }
    }

    @Override
    public List<UserDTO> search(String keyword) {
        List<UserInfo> infoList = new ArrayList<>(SqlConstants.SEARCH_LIMIT);
        List<Integer> infoIdList = new ArrayList<>(SqlConstants.SEARCH_LIMIT);
        for (SFunction<UserInfo, ?> function : SEARCH_FUNCTION) {
            List<UserInfo> findList = list(createSearchQuery(function, keyword, SqlConstants.SEARCH_LIMIT - infoList.size()));
            for (UserInfo info : findList) {
                if (infoIdList.contains(info.getAccountId())) {
                    continue;
                }
                infoList.add(info);
                infoIdList.add(info.getAccountId());
            }
            if (SqlConstants.SEARCH_LIMIT <= infoList.size()) {
                return UserWrapper.build().entityDTO(infoList);
            }
        }
        return UserWrapper.build().entityDTO(infoList);
    }

    @Override
    public boolean saveInfo(InfoReq req) {
        // 无账号ID新增
        UserInfo dbInfo = getInfo(req.getAccountId());
        if (null == dbInfo) {
            return insert(req);
        }
        return update(req, dbInfo);
    }

    @Override
    public UserInfo find(String keyword, boolean onlyAccountId) {
        if (StringUtils.isEmpty(keyword)) {
            return null;
        }
        UserInfo user = findByUniqueCode(keyword, onlyAccountId);
        if (null != user) {
            return user;
        }
        user = findByName(keyword, onlyAccountId);
        if (null != user) {
            return user;
        }
        // 手机号查询
        user = findByPhone(keyword, onlyAccountId);
        if (null != user) {
            return user;
        }
        // 邮箱查询
        return findByEmail(keyword, onlyAccountId);
    }

    @Override
    public int find(String keyword) {
        UserInfo info = find(keyword, true);
        return null == info ? 0 : info.getAccountId();
    }

    @Override
    public UserInfo getInfo(int accountId) {
        return 0 < accountId ? getOne(createQueryOne(UserInfo::getAccountId, accountId)) : null;
    }

    @Override
    public Map<Integer, UserInfo> getInfoMap(List<Integer> accountIdList) {
        if (CollectionUtils.isEmpty(accountIdList)) {
            return Collections.emptyMap();
        }
        if (accountIdList.size() <= SqlConstants.DB_FIND_SIZE) {
            return parseUserMap(batchQuery(accountIdList));
        }
        // 分批查询数据库
        List<UserInfo> bindList = new ArrayList<>();
        int index = 1;
        while (index * SqlConstants.DB_FIND_SIZE < accountIdList.size()) {
            int startIndex = SqlConstants.DB_FIND_SIZE * (index - 1);
            int endIndex = (SqlConstants.DB_FIND_SIZE * index);
            bindList.addAll(batchQuery(accountIdList.subList(startIndex, endIndex)));
            index++;
        }
        if (SqlConstants.DB_FIND_SIZE * (index - 1) < accountIdList.size()) {
            bindList.addAll(batchQuery(accountIdList.subList(SqlConstants.DB_FIND_SIZE * (index - 1),
                    accountIdList.size())));
        }
        return parseUserMap(bindList);
    }

    @Override
    public List<UserDTO> getDTOList(Collection<Integer> accountIdList) {
        return getDTOList(accountIdList, false);
    }

    @Override
    public List<UserDTO> getDTOList(Collection<Integer> accountIdList, boolean filterLeave) {
        if (CollectionUtils.isEmpty(accountIdList)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.lambda().in(UserInfo::getAccountId, accountIdList).eq(filterLeave, UserInfo::getState, UserState.NORMAL.getCode());
        List<UserInfo> infoList = list(query);
        return UserWrapper.build().entityDTO(infoList);
    }

    @Override
    public List<UserDTO> getDTOList(int index, int size, boolean filterLeave) {
        int latestId = findLimitId(index, filterLeave);
        if (latestId < 0) {
            return Collections.emptyList();
        }
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.lambda().eq(filterLeave, UserInfo::getState, UserState.NORMAL.getCode()).ge(UserInfo::getId, latestId).last(SqlUtil.indexLimit(size, 0));
        return UserWrapper.build().entityDTO(list(query));
    }

    private int findLimitId(int index, boolean filterLeave) {
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.lambda().eq(filterLeave, UserInfo::getState, UserState.NORMAL.getCode()).last(SqlUtil.indexLimit(1, index)).select(UserInfo::getId);
        UserInfo userInfo = getOne(query);
        if (Objects.isNull(userInfo)) {
            return -1;
        }
        return userInfo.getId();
    }

    @Override
    public String getEmail(int accountId) {
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.lambda().eq(UserInfo::getAccountId, accountId).select(UserInfo::getEmail).last(SqlConstants.SQL_LIMIT_ONE);
        UserInfo info = getOne(query);
        if (null == info) {
            return StringUtils.EMPTY;
        }
        return info.getEmail();
    }

    private static Map<Integer, UserInfo> parseUserMap(List<UserInfo> infoList) {
        Map<Integer, UserInfo> infoMap = new HashMap<>(infoList.size());
        for (UserInfo info : infoList) {
            infoMap.put(info.getAccountId(), info);
        }
        return infoMap;
    }

    private List<UserInfo> batchQuery(List<Integer> accountIdList) {
        if (CollectionUtils.isEmpty(accountIdList)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.lambda().in(UserInfo::getAccountId, accountIdList);
        return list(query);
    }

    private boolean isExisted(SFunction<UserInfo, ?> column, Integer val) {
        if (null == val) {
            return false;
        }
        return isExistedObj(column, val);
    }

    private boolean isExisted(SFunction<UserInfo, ?> column, String val) {
        if (StringUtils.isEmpty(val)) {
            return false;
        }
        return isExistedObj(column, val);
    }

    private boolean isExistedObj(SFunction<UserInfo, ?> column, Object val) {
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.lambda().eq(column, val);
        return 0 < count(query);
    }


    private UserInfo findByUniqueCode(String uniqueCode, boolean onlyAccountId) {
        return StringUtils.isNotEmpty(uniqueCode) ? getOne(createQueryOne(UserInfo::getUniqueCode, uniqueCode, onlyAccountId)) : null;
    }

    private UserInfo findByName(String name, boolean onlyAccountId) {
        return StringUtils.isNotEmpty(name) ? getOne(createQueryOne(UserInfo::getName, name, onlyAccountId)) : null;
    }

    private UserInfo findByPhone(String phone, boolean onlyAccountId) {
        return StringUtils.isNotEmpty(phone) ? getOne(createQueryOne(UserInfo::getPhone, phone, onlyAccountId)) : null;
    }

    private UserInfo findByEmail(String email, boolean onlyAccountId) {
        return StringUtils.isNotEmpty(email) ? getOne(createQueryOne(UserInfo::getEmail, email, onlyAccountId)) : null;
    }

    private static QueryWrapper<UserInfo> createQueryOne(SFunction<UserInfo, ?> column, Object val) {
        return createQueryOne(column, val, false);
    }

    private static QueryWrapper<UserInfo> createQueryOne(SFunction<UserInfo, ?> column, Object val, boolean onlyAccountId) {
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.lambda().eq(column, val).last(SqlConstants.SQL_LIMIT_ONE);
        if (onlyAccountId) {
            query.lambda().select(UserInfo::getAccountId);
        }
        return query;
    }

    private static QueryWrapper<UserInfo> createSearchQuery(SFunction<UserInfo, ?> column, Object val, int limit) {
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.lambda().likeRight(column, val).last(SqlUtil.indexLimit(limit, 0));
        return query;
    }

    private boolean insert(InfoReq req) {
        // 名字 唯一编码 不能为空
        if (StringUtils.isAllEmpty(req.getName(), req.getUniqueCode())) {
            throw BusinessException.create(ExceptionType.PARAM_ERROR);
        }
        // 唯一编码 身份证号 手机号 已存在
        if (isExisted(UserInfo::getUniqueCode, req.getUniqueCode())
                || isExisted(UserInfo::getIdentity, req.getIdentity())
                || isExisted(UserInfo::getPhone, req.getPhone())
                || isExisted(UserInfo::getName, req.getName())) {
            throw BusinessException.create(ExceptionType.USER_INFO_EXISTED);
        }
        UserInfo insert = new UserInfo();
        // 必填字段
        insert.setAccountId(req.getAccountId());
        insert.setName(req.getName());
        insert.setAlias(StringUtils.isNotEmpty(req.getAlias()) ? req.getAlias() : req.getName());
        insert.setUniqueCode(req.getUniqueCode());
        insert.setSex(UserState.NORMAL.getCode());
        //可为空字段
        if (StringUtils.isNotEmpty(req.getIdentity())) {
            insert.setIdentity(req.getIdentity());
        }
        if (StringUtils.isNotEmpty(req.getPhone())) {
            insert.setPhone(req.getPhone());
        }
        if (StringUtils.isNotEmpty(req.getEmail())) {
            insert.setEmail(req.getEmail());
        }
        if (StringUtils.isNotEmpty(req.getAvatar())) {
            insert.setAvatar(req.getAvatar());
        }
        if (null != req.getSex()) {
            insert.setSex(SexType.checkSexCode(req.getSex()));
        }
        return save(insert);
    }

    private boolean update(InfoReq req, UserInfo dbInfo) {
        UserInfo update = new UserInfo();
        if (StringUtils.isNotEmpty(req.getName()) && !req.getName().equals(dbInfo.getName())) {
            if (isExisted(UserInfo::getName, req.getName())) {
                throw BusinessException.create(ExceptionType.USER_INFO_EXISTED);
            }
            update.setName(req.getName()).setId(dbInfo.getId());
        }
        if (StringUtils.isNotEmpty(req.getAlias()) && !req.getAlias().equals(dbInfo.getAlias())) {
            update.setAlias(req.getAlias()).setId(dbInfo.getId());
        }
        if (StringUtils.isNotEmpty(req.getPhone()) && !req.getPhone().equals(dbInfo.getPhone())) {
            if (isExisted(UserInfo::getPhone, req.getPhone())) {
                throw BusinessException.create(ExceptionType.USER_INFO_EXISTED);
            }
            update.setPhone(req.getPhone()).setId(dbInfo.getId());
        }
        if (StringUtils.isNotEmpty(req.getEmail()) && !req.getEmail().equals(dbInfo.getEmail())) {
            if (isExisted(UserInfo::getEmail, req.getEmail())) {
                throw BusinessException.create(ExceptionType.USER_INFO_EXISTED);
            }
            update.setEmail(req.getEmail()).setId(dbInfo.getId());
        }
        if (StringUtils.isNotEmpty(req.getAvatar()) && !req.getAvatar().equals(dbInfo.getAvatar())) {
            update.setAvatar(req.getAvatar()).setId(dbInfo.getId());
        }
        if (StringUtils.isNotEmpty(req.getIdentity()) && !req.getIdentity().equals(dbInfo.getIdentity())) {
            if (isExisted(UserInfo::getIdentity, req.getIdentity())) {
                throw BusinessException.create(ExceptionType.USER_INFO_EXISTED);
            }
            update.setIdentity(req.getIdentity()).setId(dbInfo.getId());
        }
        if (req.getState() != null && !dbInfo.getState().equals(req.getState())) {
            update.setState(req.getState()).setId(dbInfo.getId());
        }
        if (req.getSex() != null && !dbInfo.getSex().equals(req.getSex())) {
            update.setSex(req.getSex()).setId(dbInfo.getId());
        }
        if (null == update.getId()) {
            return true;
        }
        return updateById(update);
    }

}
