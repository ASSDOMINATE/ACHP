package org.dominate.achp.entity.req;

import lombok.Data;

/**
 * 发送场景项参数
 *
 * @author dominate
 * @since 2023-04-04
 */
@Data
public class SendSceneItemReq {

    private Integer itemId;

    private String value;

}
