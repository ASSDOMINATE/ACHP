package org.dominate.achp.service;

import org.dominate.achp.entity.dto.SceneDetailDTO;
import org.dominate.achp.entity.req.SendSceneReq;

public interface SceneService {

    SceneDetailDTO getSceneDetail(int sceneId);

    String parseSceneContent(SendSceneReq sendScene);
}
