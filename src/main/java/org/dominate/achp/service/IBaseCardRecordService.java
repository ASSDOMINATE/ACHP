package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BaseCardRecord;
import org.dominate.achp.entity.dto.CardRecordDTO;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.sys.exception.BusinessException;

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

    /**
     * 检查获取当前可以用记录
     *
     * @param accountId 用户ID
     * @return 卡密记录
     */
    CardRecordDTO checkUserRecord(int accountId) throws BusinessException;

    boolean hasBinding(int accountId);

    boolean bindRecord(int accountId, int id, BaseCard card);

    boolean bindRecord(int accountId, BaseCard card);

    int createRecord(BaseCard card);

    BaseCardRecord findActiveRecord(String code);

    boolean setRecordUsed(int accountId, int id, int requestCount, int remainCount);

}
