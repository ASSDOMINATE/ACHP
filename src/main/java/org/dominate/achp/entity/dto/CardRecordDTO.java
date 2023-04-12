package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 付费卡密记录
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
public class CardRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 卡密ID
     */
    private Integer cardId;

    private String cardName;

    /**
     * 卡密兑换Key
     */
    private String exchangeKey;

    private Integer cardTypeCode;

    private String cardTypeName;

    private Integer remainCount;

    private Integer requestCount;

    private Long startTime;

    private Long expireTime;

    private Integer stateCode;

    private String stateName;

    private String info;


}
