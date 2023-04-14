package org.dominate.achp.service;

import org.dominate.achp.entity.dto.SceneDetailDTO;
import org.dominate.achp.entity.req.SendSceneReq;

import java.util.List;

public interface SceneService {

    SceneDetailDTO getSceneDetail(int sceneId);

    String parseSceneContent(SendSceneReq sendScene);

    boolean saveSceneRelate(int sceneId, List<Integer> categoryIdList,int accountId);
}
