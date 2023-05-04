package org.dominate.achp.service;

import org.dominate.achp.entity.dto.CardRecordDTO;
import org.dominate.achp.sys.exception.BusinessException;

/**
 * 卡密相关逻辑
 *
 * @author dominate
 * @since 2023-04-27
 */
public interface CardService {

    /**
     * 检查发送限制
     *
     * @param accountId 账号ID
     * @return 是否达到限制
     */
    void checkSendLimit(int accountId);

    /**
     * 增加发送记录
     *
     * @param accountId 账号ID
     */
    void addUserRequestRecord(int accountId);


    /**
     * 检查获取当前可以用记录
     *
     * @param accountId 用户ID
     * @return 卡密记录
     * @throws BusinessException 若检查不通过将抛出业务异常，描述不通过原因
     */
    CardRecordDTO checkUserRecord(int accountId) throws BusinessException;
}
