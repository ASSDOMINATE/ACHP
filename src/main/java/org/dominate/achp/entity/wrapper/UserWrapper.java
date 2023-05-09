package org.dominate.achp.entity.wrapper;

import org.dominate.achp.entity.UserBind;
import org.dominate.achp.entity.UserInfo;
import org.dominate.achp.entity.dto.UserBindDTO;
import org.dominate.achp.entity.dto.UserDTO;
import org.dominate.achp.entity.dto.UserInfoDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户 包装类
 *
 * @author dominate
 * @since 2022/01/26
 */
public class UserWrapper {

    public static UserWrapper build() {
        return new UserWrapper();
    }

    private void setDTO(UserDTO dto, UserInfo entity) {
        dto.setAccountId(entity.getAccountId());
        dto.setUniqueCode(entity.getUniqueCode());
        dto.setAlias(entity.getAlias());
        dto.setAvatar(entity.getAvatar());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setSex(entity.getSex());
        dto.setName(entity.getName());
        dto.setIdentity(entity.getIdentity());
        dto.setState(entity.getState());
    }

    public UserInfoDTO entityInfoDTO(UserInfo entity) {
        UserInfoDTO dto = new UserInfoDTO();
        setDTO(dto, entity);
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    public List<UserInfoDTO> entityInfoDTO(List<UserInfo> entityList) {
        List<UserInfoDTO> list = new ArrayList<>(entityList.size());
        for (UserInfo entity : entityList) {
            list.add(entityInfoDTO(entity));
        }
        return list;
    }

    public UserDTO entityDTO(UserInfo entity) {
        UserDTO dto = new UserDTO();
        setDTO(dto, entity);
        return dto;
    }

    public List<UserDTO> entityDTO(List<UserInfo> entityList) {
        List<UserDTO> list = new ArrayList<>(entityList.size());
        for (UserInfo entity : entityList) {
            list.add(entityDTO(entity));
        }
        return list;
    }

    public UserBindDTO entityDTO(UserBind entity, String data) {
        UserBindDTO dto = new UserBindDTO();
        dto.setId(entity.getId());
        dto.setAccountId(entity.getAccountId());
        dto.setBindCode(entity.getBindCode());
        dto.setBindType(entity.getBindType());
        dto.setOriginData(data);
        return dto;
    }

}
