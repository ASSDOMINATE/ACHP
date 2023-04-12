package org.dominate.achp.service;

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

}
