package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.ChatSceneRelate;
import org.dominate.achp.entity.req.PageReq;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 对话场景关联 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-06
 */
public interface IChatSceneRelateService extends IService<ChatSceneRelate> {

    /**
     * 获取分类下的场景ID列表
     *
     * @param categoryId 分类ID
     * @param page       分页参数
     * @return 场景ID列表
     */
    List<Integer> getSceneIdList(int categoryId, PageReq page);

    /**
     * 获取场景的分类ID列表
     *
     * @param sceneId 场景ID
     * @return 分类ID列表
     */
    List<Integer> getCategoryIdList(int sceneId);

    /**
     * 获取场景的分类ID列表
     *
     * @param sceneIdList 场景ID列表
     * @return 分类ID列表
     */
    Map<Integer, List<Integer>> getCategoryIdMap(List<Integer> sceneIdList);

    /**
     * 获取关联列表
     *
     * @param targetId 关联目标ID
     * @param forScene true 目标为场景 / false目标为分类
     * @return 关联列表
     */
    List<ChatSceneRelate> getRelateList(int targetId, boolean forScene);

}
