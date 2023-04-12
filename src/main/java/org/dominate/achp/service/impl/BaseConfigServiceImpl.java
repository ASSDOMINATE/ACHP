package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.SqlUtil;
import org.dominate.achp.entity.BaseConfig;
import org.dominate.achp.mapper.BaseConfigMapper;
import org.dominate.achp.service.IBaseConfigService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 基础配置 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
public class BaseConfigServiceImpl extends ServiceImpl<BaseConfigMapper, BaseConfig> implements IBaseConfigService {

    @Override
    public BaseConfig current() {
        QueryWrapper<BaseConfig> query = new QueryWrapper<>();
        query.lambda().eq(BaseConfig::getDel, false)
                .orderByDesc(BaseConfig::getId)
                .last(SqlUtil.limitOne());
        return getOne(query);
    }
}
