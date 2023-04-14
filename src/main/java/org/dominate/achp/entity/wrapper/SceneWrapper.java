package org.dominate.achp.entity.wrapper;

import org.dominate.achp.common.enums.SceneCategoryType;
import org.dominate.achp.entity.ChatScene;
import org.dominate.achp.entity.ChatSceneCategory;
import org.dominate.achp.entity.dto.SceneCategoryDTO;
import org.dominate.achp.entity.dto.SceneDTO;
import org.dominate.achp.entity.dto.SceneDetailDTO;
import org.dominate.achp.entity.dto.SceneInfoDTO;

import java.util.ArrayList;
import java.util.List;

public class SceneWrapper {

    public static SceneWrapper build() {
        return new SceneWrapper();
    }

    public SceneInfoDTO entityInfoDTO(ChatScene entity){
        SceneInfoDTO dto = new SceneInfoDTO();
        setScene(dto, entity);
        dto.setNotice(entity.getNotice());
        return dto;
    }

    public SceneDTO entityDTO(ChatScene entity) {
        SceneDTO dto = new SceneDTO();
        setScene(dto, entity);
        return dto;
    }

    private static void setScene(SceneDTO dto, ChatScene entity) {
        dto.setId(entity.getId());
        dto.setDesr(entity.getDesr());
        dto.setTitle(entity.getTitle());
        dto.setChatCount(entity.getChatCount());
        dto.setReadCount(entity.getReadCount());
        dto.setSendCount(entity.getSendCount());
    }

    public SceneDetailDTO entityDetailDTO(ChatScene entity) {
        SceneDetailDTO dto = new SceneDetailDTO();
        setScene(dto, entity);
        dto.setNotice(entity.getNotice());
        return dto;
    }

    public List<SceneDTO> entitySceneDTO(List<ChatScene> entityList) {
        List<SceneDTO> dtoList = new ArrayList<>(entityList.size());
        for (ChatScene entity : entityList) {
            dtoList.add(entityDTO(entity));
        }
        return dtoList;
    }

    public SceneCategoryDTO entityDTO(ChatSceneCategory entity) {
        SceneCategoryDTO dto = new SceneCategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDesr(entity.getDesr());
        SceneCategoryType type = SceneCategoryType.getValueByCode(entity.getType());
        dto.setTypeCode(type.getCode());
        dto.setTypeName(type.getName());
        return dto;
    }

    public List<SceneCategoryDTO> entityCategoryDTO(List<ChatSceneCategory> entityList) {
        List<SceneCategoryDTO> dtoList = new ArrayList<>(entityList.size());
        for (ChatSceneCategory entity : entityList) {
            dtoList.add(entityDTO(entity));
        }
        return dtoList;
    }

    }
