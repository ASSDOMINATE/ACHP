package org.dominate.achp.entity.wrapper;


import org.dominate.achp.common.enums.PermissionType;
import org.dominate.achp.entity.UserPermission;
import org.dominate.achp.entity.dto.PermissionDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限 包装类
 *
 * @author dominate
 * @since 2022/01/26
 */
public class PermissionWrapper {

    public static PermissionWrapper build() {
        return new PermissionWrapper();
    }

    public PermissionDTO entityVO(UserPermission entity) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(entity.getId());
        dto.setParentId(entity.getParentId());
        PermissionType type = PermissionType.getValueByCode(entity.getPermissionType());
        dto.setTypeCode(type.getCode());
        dto.setTypeName(type.getName());
        dto.setPlatformId(entity.getPlatformId());
        dto.setName(entity.getName());
        dto.setDesr(entity.getDesr());
        dto.setCode(entity.getCode());
        dto.setPath(entity.getPath());
        return dto;
    }

    public List<PermissionDTO> entityVO(List<UserPermission> entityList) {
        List<PermissionDTO> list = new ArrayList<>(entityList.size());
        for (UserPermission entity : entityList) {
            list.add(entityVO(entity));
        }
        return list;
    }


}
