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

    List<ChatSceneItem> list(int sceneId);
}
