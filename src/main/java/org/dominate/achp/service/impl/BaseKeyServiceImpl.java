package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.StringUtil;
import org.dominate.achp.common.cache.ChatCache;
import org.dominate.achp.common.enums.State;
import org.dominate.achp.common.utils.FreqUtil;
import org.dominate.achp.entity.BaseKey;
import org.dominate.achp.mapper.BaseKeyMapper;
import org.dominate.achp.service.IBaseKeyService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * API-Key配置 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
public class BaseKeyServiceImpl extends ServiceImpl<BaseKeyMapper, BaseKey> implements IBaseKeyService {

    @Override
    public List<BaseKey> enableList() {
        QueryWrapper<BaseKey> query = new QueryWrapper<>();
        query.lambda().eq(BaseKey::getState, State.ENABLE.getCode()).orderByDesc(BaseKey::getWeight);
        return list(query);
    }

    @Override
    public String getBestApiKey() {
        List<BaseKey> keyList = ChatCache.getCacheKeyList();
        if (CollectionUtils.isEmpty(keyList)) {
            keyList = enableList();
            ChatCache.saveCacheKeyList(keyList);
        }
        if (CollectionUtils.isEmpty(keyList)) {
            return StringUtil.EMPTY;
        }
        BaseKey key = ChatCache.selectByWeight(keyList);
        return key.getApiKey();
    }


}
