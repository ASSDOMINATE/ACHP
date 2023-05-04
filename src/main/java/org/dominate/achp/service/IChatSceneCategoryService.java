package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.ChatSceneCategory;
import org.dominate.achp.entity.dto.SceneCategoryDTO;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 对话场景分类 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-06
 */
public interface IChatSceneCategoryService extends IService<ChatSceneCategory> {

    /**
     * 获取启用的分类列表
     *
     * @return 分类列表
     */
    List<SceneCategoryDTO> enabledList();

    /**
     * 获取分类列表
     * @param ids 分类ID列表
     * @return 分类列表
     */
    List<ChatSceneCategory> list(Collection<Integer> ids);

}
