package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.BaseKey;

import java.util.List;

/**
 * <p>
 * API-Key配置 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IBaseKeyService extends IService<BaseKey> {

    /**
     * 获取启用的Key列表 按权重排序
     *
     * @return ApiKey列表
     */
    List<BaseKey> enableList();

    /**
     * 获取最好的Key
     *
     * @return ApiKey
     */
    String getBestApiKey();
}
