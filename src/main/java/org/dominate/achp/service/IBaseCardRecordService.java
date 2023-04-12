package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BaseCardRecord;
import org.dominate.achp.entity.dto.CardRecordDTO;
import org.dominate.achp.entity.req.PageReq;

import java.util.List;

/**
 * <p>
 * 付费卡密记录 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IBaseCardRecordService extends IService<BaseCardRecord> {

    List<CardRecordDTO> userRecordList(int accountId);

    List<CardRecordDTO> cardRecordList(int cardId, PageReq page);

    CardRecordDTO checkUserRecord(int accountId);

    boolean bindRecord(int accountId, int id, BaseCard card);

    boolean bindRecord(int accountId, BaseCard card);

    int createRecord(BaseCard card);

    BaseCardRecord findActiveRecord(String code);
}
