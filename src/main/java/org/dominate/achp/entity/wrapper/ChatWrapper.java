package org.dominate.achp.entity.wrapper;

import org.dominate.achp.entity.ChatContent;
import org.dominate.achp.entity.ChatGroup;
import org.dominate.achp.entity.dto.ContentDTO;
import org.dominate.achp.entity.dto.GroupDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 对话相关包装类型
 *
 * @author dominate
 * @since 2023-04-14
 */
public class ChatWrapper {

    public static ChatWrapper build() {
        return new ChatWrapper();
    }

    public GroupDTO entityDTO(ChatGroup entity) {
        GroupDTO dto = new GroupDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        return dto;
    }

    public List<GroupDTO> entityGroupDTO(List<ChatGroup> entityList) {
        List<GroupDTO> dtoList = new ArrayList<>(entityList.size());
        for (ChatGroup entity : entityList) {
            dtoList.add(entityDTO(entity));
        }
        return dtoList;
    }

    public ContentDTO entityDTO(ChatContent entity) {
        ContentDTO dto = new ContentDTO();
        dto.setId(entity.getId());
        dto.setLastId(entity.getLastId());
        dto.setSentence(entity.getSentence());
        dto.setReply(entity.getReply());
        dto.setCreateTime(entity.getCreateTime().getTime());
        return dto;
    }

    public List<ContentDTO> entityContentDTO(List<ChatContent> entityList) {
        List<ContentDTO> dtoList = new ArrayList<>(entityList.size());
        for (ChatContent entity : entityList) {
            dtoList.add(entityDTO(entity));
        }
        return dtoList;
    }

}
