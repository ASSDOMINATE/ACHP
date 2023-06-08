package org.dominate.achp.entity.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 发送场景请求参数
 *
 * @author dominate
 * @since 2023-04-04
 */
@Data
public class SendSceneReq {

    @NotNull
    private Integer sceneId;

    @NotNull
    SendSceneItemReq[] items;
}
