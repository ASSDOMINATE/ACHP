package org.dominate.achp.entity.wrapper;

import com.hwja.tool.utils.DateUtil;
import com.hwja.tool.utils.StringUtil;
import org.dominate.achp.common.enums.CardRecordState;
import org.dominate.achp.common.enums.CardType;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BaseCardRecord;
import org.dominate.achp.entity.dto.CardDTO;
import org.dominate.achp.entity.dto.CardRecordDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 卡密相关包装工具
 *
 * @author dominate
 * @since 2023-04-24
 */
public class CardWrapper {

    public static CardWrapper build() {
        return new CardWrapper();
    }

    public CardDTO entityDTO(BaseCard entity) {
        CardDTO dto = new CardDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDesr(entity.getDesr());
        dto.setBalance(entity.getBalance());
        dto.setStock(entity.getStock());
        dto.setCountLimit(entity.getCountLimit());
        dto.setDayLimit(entity.getDayLimit());
        dto.setProductCode(entity.getProductCode());
        CardType type = CardType.getValueByCode(entity.getType());
        dto.setCardTypeCode(type.getCode());
        dto.setCardTypeName(type.getName());
        dto.setOrgBalance(entity.getOrgBalance());
        dto.setTag(entity.getTag());
        dto.setBuyType(entity.getBuyType());
        return dto;
    }

    public List<CardDTO> entityCardDTO(List<BaseCard> entityList) {
        List<CardDTO> dtoList = new ArrayList<>(entityList.size());
        for (BaseCard entity : entityList) {
            dtoList.add(entityDTO(entity));
        }
        return dtoList;
    }

    public CardRecordDTO entityDTO(BaseCardRecord entity) {
        CardRecordDTO dto = new CardRecordDTO();
        dto.setId(entity.getId());
        dto.setCardId(entity.getCardId());
        dto.setCardName(entity.getCardName());
        dto.setExchangeKey(entity.getExchangeKey());
        dto.setExpireTime(entity.getExpireTime().getTime());
        dto.setStartTime(entity.getStartTime().getTime());
        dto.setRemainCount(entity.getRemainCount());
        dto.setRequestCount(entity.getRequestCount());
        CardType cardType = CardType.getValueByCode(entity.getCardType());
        dto.setCardTypeCode(cardType.getCode());
        dto.setCardTypeName(cardType.getName());
        CardRecordState recordState = CardRecordState.getValueByCode(entity.getState());
        dto.setStateCode(recordState.getCode());
        dto.setStateName(recordState.getName());
        if (entity.getAccountId() != 0) {
            if (CardRecordState.USING == recordState) {
                switch (cardType) {
                    case COUNT:
                        dto.setInfo("会员剩余：" + dto.getRemainCount() + "次");
                        break;
                    case DAY:
                        dto.setInfo("当前会员：" + formatTime(entity.getExpireTime()) + "到期");
                        break;
                    case NO_LIMIT:
                        dto.setInfo("当前为永久会员");
                    default:
                        dto.setInfo(StringUtil.EMPTY);
                        break;
                }
            }
        }
        return dto;
    }

    private static String formatTime(Date date) {
        return DateUtil.dateToStr(date, "yyyy年M月d日");
    }


    public List<CardRecordDTO> entityCardRecordDTO(List<BaseCardRecord> entityList) {
        List<CardRecordDTO> dtoList = new ArrayList<>(entityList.size());
        for (BaseCardRecord entity : entityList) {
            dtoList.add(entityDTO(entity));
        }
        return dtoList;
    }
}
