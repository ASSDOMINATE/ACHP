package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.ChatScene;
import org.dominate.achp.entity.dto.SceneDTO;
import org.dominate.achp.entity.req.PageReq;

import java.util.List;

/**
 * <p>
 * 对话场景 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IChatSceneService extends IService<ChatScene> {

    /**
     * 分页获取场景
     *
     * @param page 分页参数
     * @return 场景列表
     */
    List<SceneDTO> list(PageReq page);

    /**
     * ID 列表获取场景
     *
     * @param sceneIdList 场景ID列表
     * @return 场景列表
     */
    List<SceneDTO> list(List<Integer> sceneIdList);

    /**
     * 获取场景的系统描述
     *
     * @param id 场景ID
     * @return 系统描述
     */
    String getSystem(int id);
}
