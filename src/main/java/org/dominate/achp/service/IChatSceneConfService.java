package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.ChatSceneConf;

import java.util.List;

/**
 * <p>
 * 对话场景配置，把场景项拼接为文字的配置 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IChatSceneConfService extends IService<ChatSceneConf> {

    /**
     * 获取指定场景的配置列表
     *
     * @param sceneId 场景ID
     * @return 配置列表
     */
    List<ChatSceneConf> list(int sceneId);

}
