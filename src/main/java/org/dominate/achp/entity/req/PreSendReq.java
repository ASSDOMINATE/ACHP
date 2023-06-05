package org.dominate.achp.entity.req;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 预发送请求
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
public class PreSendReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String chatId;

    private Integer sceneId;

    private String sentence;

}
