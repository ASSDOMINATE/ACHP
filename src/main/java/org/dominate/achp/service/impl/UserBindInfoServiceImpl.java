package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.constant.SqlConstants;
import org.dominate.achp.entity.UserBindInfo;
import org.dominate.achp.mapper.UserBindInfoMapper;
import org.dominate.achp.service.IUserBindInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>
 * 用户三方绑定信息 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserBindInfoServiceImpl extends ServiceImpl<UserBindInfoMapper, UserBindInfo> implements IUserBindInfoService {

    @Override
    public boolean saveInfo(int bindId, String info) {
        UserBindInfo bindInfo = findInfo(bindId);
        if (null != bindInfo) {
            UserBindInfo update = new UserBindInfo();
            update.setData(info);
            update.setId(bindInfo.getId());
            return updateById(update);
        }
        UserBindInfo insert = new UserBindInfo();
        insert.setBindId(bindId);
        insert.setData(info);
        return save(insert);
    }

    @Override
    public String getInfo(int bindId) {
        UserBindInfo bindInfo = findInfo(bindId);
        return null != bindInfo ? bindInfo.getData() : null;
    }

    @Override
    public boolean coverInfos(Map<Integer, String> infoMap) {
        if (CollectionUtils.isEmpty(infoMap)) {
            return false;
        }
        QueryWrapper<UserBindInfo> query = new QueryWrapper<>();
        query.lambda().in(UserBindInfo::getBindId, infoMap.keySet()).select(UserBindInfo::getId, UserBindInfo::getBindId);
        List<UserBindInfo> infoList = list(query);
        List<UserBindInfo> updateList = new ArrayList<>(infoList.size());
        for (UserBindInfo info : infoList) {
            String data = infoMap.get(info.getBindId());
            UserBindInfo update = new UserBindInfo();
            update.setId(info.getId());
            update.setData(data);
            updateList.add(update);
        }
        return updateBatchById(updateList);
    }

    @Override
    public Map<Integer, String> getInfoMap(List<Integer> bindIdList) {
        if (CollectionUtils.isEmpty(bindIdList)) {
            return Collections.emptyMap();
        }
        QueryWrapper<UserBindInfo> query = new QueryWrapper<>();
        query.lambda().eq(UserBindInfo::getBindId, bindIdList).select(UserBindInfo::getBindId, UserBindInfo::getData);
        List<UserBindInfo> infoList = list(query);
        Map<Integer, String> infoMap = new HashMap<>(infoList.size());
        for (UserBindInfo info : infoList) {
            infoMap.put(info.getBindId(), info.getData());
        }
        return infoMap;
    }

    private UserBindInfo findInfo(int bindId) {
        QueryWrapper<UserBindInfo> query = new QueryWrapper<>();
        query.lambda().eq(UserBindInfo::getBindId, bindId).last(SqlConstants.SQL_LIMIT_ONE);
        return getOne(query);
    }
}
