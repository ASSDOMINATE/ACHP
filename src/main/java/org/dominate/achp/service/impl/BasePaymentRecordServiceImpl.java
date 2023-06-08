package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.SqlUtil;
import com.hwja.tool.utils.StringUtil;
import org.dominate.achp.common.enums.PayType;
import org.dominate.achp.common.utils.UniqueCodeUtil;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BasePaymentRecord;
import org.dominate.achp.entity.dto.PayOrderDTO;
import org.dominate.achp.mapper.BasePaymentRecordMapper;
import org.dominate.achp.service.IBasePaymentRecordService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 支付记录 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
public class BasePaymentRecordServiceImpl extends ServiceImpl<BasePaymentRecordMapper, BasePaymentRecord> implements IBasePaymentRecordService {

    @Override
    public int save(PayOrderDTO payOrder, BaseCard card, int cardRecordId) {
        return save(payOrder.getAccountId(), payOrder.getSysOrderCode(),
                payOrder.getPartyOrderCode(), payOrder.getPayType(), card, cardRecordId);
    }

    @Override
    public int save(int accountId, String orderCode, String partyOrderCode, int payTypeCode, BaseCard card, int cardRecordId) {
        BasePaymentRecord record = new BasePaymentRecord();
        record.setAccountId(accountId);
        record.setType(payTypeCode);
        record.setPrice(card.getBalance());
        record.setOrderCode(orderCode);
        record.setPartyCode(partyOrderCode);
        String message = PayType.createPayMessage(payTypeCode, card.getBalance(), card.getName());
        record.setMessage(message);
        record.setComment(StringUtil.EMPTY);
        if (save(record)) {
            return record.getId();
        }
        return 0;
    }

    @Override
    public int save(int accountId, String partyOrderCode, PayType payType, BaseCard card, int cardRecordId) {
        return save(accountId, UniqueCodeUtil.createPayOrder(payType.getDbCode()), partyOrderCode, payType.getDbCode(),
                card, cardRecordId);
    }

    @Override
    public boolean isUniqueOrder(PayOrderDTO payOrder) {
        return isUniqueOrder(payOrder.getPartyOrderCode(), payOrder.getPayType());
    }

    @Override
    public boolean isUniqueOrder(String partyOrderCode, int payTypeCode) {
        QueryWrapper<BasePaymentRecord> query = new QueryWrapper<>();
        // 三方订单号 + 三方类型
        query.lambda().eq(BasePaymentRecord::getPartyCode, partyOrderCode)
                .eq(BasePaymentRecord::getType, payTypeCode);
        return count(query) == 0;
    }

    @Override
    public BasePaymentRecord find(PayType payType, String partyOrder) {
        QueryWrapper<BasePaymentRecord> query = new QueryWrapper<>();
        query.lambda().eq(BasePaymentRecord::getPartyCode, partyOrder)
                .eq(BasePaymentRecord::getType, payType.getDbCode())
                .last(SqlUtil.limitOne());
        return getOne(query);
    }
}
