package org.dominate.achp.service;

import org.dominate.achp.entity.dto.SceneDetailDTO;
import org.dominate.achp.entity.req.SendSceneReq;

import java.util.List;

/**
 * 场景相关逻辑
 *
 * @author dominate
 * @since 2023-04-14
 */
public interface SceneService {

    /**
     * 获取场景详情
     * 只有Scene表中数据，其他属性还需要组装
     *
     * @param sceneId 场景ID
     * @return 场景详情部分属性
     */
    SceneDetailDTO getSceneDetail(int sceneId);

    /**
     * 解析场景发送参数，生成场景对话内容
     *
     * @param sendScene 场景发送参数
     * @return 场景对话内容
     */
    String parseSceneContent(SendSceneReq sendScene);

    /**
     * 保存场景关联
     *
     * @param sceneId        场景ID
     * @param categoryIdList 分类ID列表
     * @param accountId      账号ID
     * @return 是否保存成功
     */
    boolean saveSceneRelate(int sceneId, List<Integer> categoryIdList, int accountId);

    boolean setSceneRelateFirst(int sceneId,int categoryId,int accountId);
}
