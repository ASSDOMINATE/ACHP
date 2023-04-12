package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.constant.SqlConstants;
import org.dominate.achp.common.enums.UserBindType;
import org.dominate.achp.entity.UserBind;
import org.dominate.achp.entity.dto.UserBindDTO;
import org.dominate.achp.entity.wrapper.UserWrapper;
import org.dominate.achp.mapper.UserBindMapper;
import org.dominate.achp.service.IUserBindInfoService;
import org.dominate.achp.service.IUserBindService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 用户三方绑定 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements IUserBindService {

    @Resource
    private IUserBindInfoService userBindInfoService;

    @Override
    public List<UserBind> getList(int accountId) {
        QueryWrapper<UserBind> query = new QueryWrapper<>();
        query.lambda().eq(UserBind::getAccountId, accountId);
        return list(query);
    }

    @Override
    public List<UserBindDTO> getDTOList(int accountId) {
        QueryWrapper<UserBind> query = new QueryWrapper<>();
        query.lambda().eq(UserBind::getAccountId, accountId).eq(UserBind::getIsDel, false);
        List<UserBind> bindList = list(query);
        if (CollectionUtils.isEmpty(bindList)) {
            return Collections.emptyList();
        }
        List<Integer> bindIdList = new ArrayList<>(bindList.size());
        for (UserBind bind : bindList) {
            bindIdList.add(bind.getId());
        }
        Map<Integer, String> infoMap = userBindInfoService.getInfoMap(bindIdList);
        List<UserBindDTO> bindDtoList = new ArrayList<>(bindList.size());
        for (UserBind bind : bindList) {
            bindDtoList.add(UserWrapper.build().entityDTO(bind, infoMap.getOrDefault(bind.getId(), StringUtils.EMPTY)));
        }
        return bindDtoList;
    }

    @Override
    public boolean addBind(int accountId, UserBindType bindTypeEnum, String bindCode, String info) {
        UserBind bind = findBind(accountId, bindTypeEnum);
        if (null != bind) {
            // 绑定码相同仅更新 info
            if (bind.getBindCode().equals(bindCode)) {
                return userBindInfoService.saveInfo(bind.getId(), info);
            }
            // 删除旧绑定后保存新绑定
            if (!delBind(bind.getId())) {
                return false;
            }
        }
        UserBind insert = new UserBind();
        insert.setAccountId(accountId);
        insert.setBindCode(bindCode);
        insert.setBindType(bindTypeEnum.getCode());
        if (save(insert)) {
            return userBindInfoService.saveInfo(insert.getId(), info);
        }
        return false;
    }

    @Override
    public boolean removeBind(int accountId, UserBindType bindTypeEnum) {
        UserBind bind = findBind(accountId, bindTypeEnum);
        if (null == bind) {
            return true;
        }
        return delBind(bind.getId());
    }

    @Override
    public Map<String, UserBind> findBindMap(List<String> codeList, UserBindType bindTypeEnum) {
        if (CollectionUtils.isEmpty(codeList)) {
            return Collections.emptyMap();
        }
        if (codeList.size() <= SqlConstants.DB_FIND_SIZE) {
            return parseBindMap(batchQuery(bindTypeEnum.getCode(), codeList));
        }
        // 分批查询数据库
        List<UserBind> bindList = new ArrayList<>();
        int index = 1;
        while (index * SqlConstants.DB_FIND_SIZE < codeList.size()) {
            int startIndex = SqlConstants.DB_FIND_SIZE * (index - 1);
            int endIndex = (SqlConstants.DB_FIND_SIZE * index);
            bindList.addAll(batchQuery(bindTypeEnum.getCode(), codeList.subList(startIndex, endIndex)));
            index++;
        }
        if (SqlConstants.DB_FIND_SIZE * (index - 1) < codeList.size()) {
            bindList.addAll(batchQuery(bindTypeEnum.getCode(), codeList.subList(SqlConstants.DB_FIND_SIZE * (index - 1),
                    codeList.size())));
        }
        return parseBindMap(bindList);
    }

    @Override
    public int getBindAccountId(String code, UserBindType bindTypeEnum) {
        QueryWrapper<UserBind> query = new QueryWrapper<>();
        query.lambda().eq(UserBind::getBindCode, code)
                .eq(UserBind::getBindType, bindTypeEnum.getCode())
                .select(UserBind::getAccountId)
                .last(SqlConstants.SQL_LIMIT_ONE);
        UserBind bind = getOne(query, false);
        if (null == bind) {
            return 0;
        }
        return bind.getAccountId();
    }

    private boolean delBind(int id) {
        UserBind update = new UserBind();
        update.setId(id);
        update.setIsDel(true);
        return updateById(update);
    }

    private UserBind findBind(int accountId, UserBindType bindTypeEnum) {
        QueryWrapper<UserBind> query = new QueryWrapper<>();
        query.lambda().eq(UserBind::getAccountId, accountId)
                .eq(UserBind::getBindType, bindTypeEnum.getCode())
                .eq(UserBind::getIsDel, false)
                .last(SqlConstants.SQL_LIMIT_ONE);
        return getOne(query);
    }

    private static Map<String, UserBind> parseBindMap(List<UserBind> bindList) {
        Map<String, UserBind> bindMap = new HashMap<>(bindList.size());
        for (UserBind bind : bindList) {
            bindMap.put(bind.getBindCode(), bind);
        }
        return bindMap;
    }

    private List<UserBind> batchQuery(Integer bindType, List<String> codeList) {
        if(CollectionUtils.isEmpty(codeList)){
            return Collections.emptyList();
        }
        QueryWrapper<UserBind> query = new QueryWrapper<>();
        query.lambda().eq(UserBind::getBindType, bindType)
                .eq(UserBind::getIsDel, false)
                .in(UserBind::getBindCode, codeList)
                .select(UserBind::getAccountId, UserBind::getBindCode, UserBind::getId);
        return list(query);
    }
}
