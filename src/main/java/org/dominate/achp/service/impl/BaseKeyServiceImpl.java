package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.RandomUtil;
import com.hwja.tool.utils.StringUtil;
import org.dominate.achp.common.cache.CardCache;
import org.dominate.achp.common.enums.State;
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
        List<BaseKey> keyList = CardCache.getCacheKeyList();
        if (CollectionUtils.isEmpty(keyList)) {
            keyList = enableList();
            CardCache.saveCacheKeyList(keyList);
        }
        if (CollectionUtils.isEmpty(keyList)) {
            return StringUtil.EMPTY;
        }
        BaseKey key = getByWeight(keyList);
        // 进行频率检查，如果过高进行自旋
        return key.getApiKey();
    }

    private static BaseKey getByWeight(List<BaseKey> keyList) {
        if (keyList.size() == 1) {
            return keyList.get(0);
        }
        int totalWeight = 0;
        for (BaseKey key : keyList) {
            totalWeight += key.getWeight();
        }
        int target = RandomUtil.getRandNum(0, totalWeight);
        int index = 0;
        for (BaseKey key : keyList) {
            index += key.getWeight();
            if (target <= index) {
                return key;
            }
        }
        return keyList.get(0);
    }
}
