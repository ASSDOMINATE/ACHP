package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.StringUtil;
import org.dominate.achp.common.enums.PayType;
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
    public int save(PayOrderDTO payOrder, BaseCard card) {
        BasePaymentRecord record = new BasePaymentRecord();
        record.setAccountId(payOrder.getAccountId());
        record.setType(payOrder.getPayType());
        record.setPrice(card.getBalance());
        record.setOrderCode(payOrder.getSysOrderCode());
        record.setPartyCode(payOrder.getPartyOrderCode());
        String message = PayType.createPayMessage(payOrder.getPayType(), card.getBalance(), card.getName());
        record.setMessage(message);
        record.setComment(StringUtil.EMPTY);
        save(record);
        return record.getId();
    }
}
