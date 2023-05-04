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

    /**
     * 用户 卡密记录列表
     *
     * @param accountId 账号ID
     * @return 卡密列表
     */
    List<CardRecordDTO> userRecordList(int accountId);

    /**
     * 付费卡密 卡密记录列表
     *
     * @param cardId 付费卡密ID
     * @param page   分页参数
     * @return 卡密列表
     */
    List<CardRecordDTO> cardRecordList(int cardId, PageReq page);


    /**
     * 获取用户待使用卡密信息
     *
     * @param accountId 账号ID
     * @return 待使用卡密信息
     */
    String getRecordWaitInfo(int accountId);

    /**
     * 是否有可用的绑定记录
     *
     * @param accountId 账号ID
     * @return 是否有可用卡密
     */
    boolean hasUsingBinding(int accountId);

    /**
     * 绑定卡密
     *
     * @param accountId 账号ID
     * @param id        卡密ID
     * @param card      付费卡密
     * @return 是否绑定成功
     */
    boolean bindRecord(int accountId, int id, BaseCard card);

    /**
     * 绑定卡密
     *
     * @param accountId 账哈ID
     * @param card      付费卡密
     * @return 是否绑定成功
     */
    boolean bindRecord(int accountId, BaseCard card);

    /**
     * 创建卡密记录
     * 生成可供绑定的卡密记录
     *
     * @param card 付费卡密
     * @return 卡密记录ID
     */
    int createRecord(BaseCard card);

    /**
     * 查找可绑定的卡密记录
     *
     * @param code 兑换码
     * @return 卡密记录
     */
    BaseCardRecord findActiveRecord(String code);

    /**
     * 保存卡密记录为已使用状态
     *
     * @param accountId    账号ID
     * @param id           卡密记录ID
     * @param requestCount 请求次数
     * @param remainCount  剩余次数
     * @return 是否保存成功
     */
    boolean saveRecordUsed(int accountId, int id, int requestCount, int remainCount);

    /**
     * 保存卡密记录为使用状态
     *
     * @param accountId 账号ID
     * @param id        卡密记录ID
     * @param card      付费卡密
     * @return 是否保存成功
     */
    boolean saveRecordUsing(int accountId, int id, BaseCard card);

}
