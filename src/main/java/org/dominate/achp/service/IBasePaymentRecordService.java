package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BasePaymentRecord;
import org.dominate.achp.entity.dto.PayOrderDTO;

/**
 * <p>
 * 支付记录 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IBasePaymentRecordService extends IService<BasePaymentRecord> {

    int save(PayOrderDTO payOrder, BaseCard card);
}
