package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.BaseConfig;

/**
 * <p>
 * 基础配置 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IBaseConfigService extends IService<BaseConfig> {

    /**
     * 获取当前配置
     *
     * 使用静态缓存
     * @return 配置
     */
    BaseConfig current();
}
