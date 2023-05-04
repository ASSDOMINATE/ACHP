package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.ChatSceneItem;

import java.util.List;

/**
 * <p>
 * 对话场景项 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IChatSceneItemService extends IService<ChatSceneItem> {

    /**
     * 获取指定场景的场景项列表
     *
     * @param sceneId 场景ID
     * @return 场景项列表
     */
    List<ChatSceneItem> list(int sceneId);
}
