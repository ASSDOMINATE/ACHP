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

    List<SceneCategoryDTO> enabledList();

    List<ChatSceneCategory> list(Collection<Integer> ids);

}
