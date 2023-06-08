package org.dominate.achp.entity.wrapper;

import org.dominate.achp.entity.UserRole;
import org.dominate.achp.entity.dto.RoleDTO;
import org.dominate.achp.entity.dto.RolePermCheckDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色 包装类
 *
 * @author dominate
 * @since 2022/01/26
 */
public class RoleWrapper {

    public static RoleWrapper build() {
        return new RoleWrapper();
    }

    public RoleDTO entityVO(UserRole entity) {
        RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setParentId(entity.getParentId());
        dto.setPlatformId(entity.getPlatformId());
        dto.setName(entity.getName());
        dto.setDesr(entity.getDesr());
        return dto;
    }

    public List<RoleDTO> entityVO(List<UserRole> entityList) {
        List<RoleDTO> list = new ArrayList<>(entityList.size());
        for (UserRole entity : entityList) {
            list.add(entityVO(entity));
        }
        return list;
    }


    public RolePermCheckDTO entityVO(UserRole entity, int permId, boolean hasPerm) {
        RolePermCheckDTO dto = new RolePermCheckDTO();
        dto.setPermId(permId);
        dto.setId(entity.getId());
        dto.setPlatformId(entity.getPlatformId());
        dto.setName(entity.getName());
        dto.setHasPerm(hasPerm);
        return dto;
    }

    public List<RolePermCheckDTO> entityVO(List<UserRole> entityList, List<Integer> hasPermRoleIdList, int permId) {
        List<RolePermCheckDTO> list = new ArrayList<>(entityList.size());
        for (UserRole entity : entityList) {
            list.add(entityVO(entity, permId, hasPermRoleIdList.contains(entity.getId())));
        }
        return list;
    }


}
