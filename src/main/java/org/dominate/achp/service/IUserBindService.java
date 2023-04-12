package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.common.enums.UserBindType;
import org.dominate.achp.entity.UserBind;
import org.dominate.achp.entity.dto.UserBindDTO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户三方绑定 服务类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
public interface IUserBindService extends IService<UserBind> {

    /**
     * 获取用户三方绑定列表
     *
     * @param accountId 账户ID
     * @return 绑定列表
     */
    List<UserBind> getList(int accountId);

    /**
     * 获取绑定详细数据
     *
     * @param accountId 账户ID
     * @return 绑定详细数据列表
     */
    List<UserBindDTO> getDTOList(int accountId);

    /**
     * 新增/更新绑定
     *
     * @param accountId    账户ID
     * @param bindTypeEnum 三方绑定类型
     * @param bindCode     三方绑定码
     * @param info         绑定数据
     * @return 是否绑定成功
     */
    boolean addBind(int accountId, UserBindType bindTypeEnum, String bindCode, String info);

    /**
     * 解除绑定
     *
     * @param accountId    账户ID
     * @param bindTypeEnum 绑定类型
     * @return 是否解除成功
     */
    boolean removeBind(int accountId, UserBindType bindTypeEnum);

    /**
     * 查找账号 ID Map
     *
     * @param codeList     三方绑定Code列表
     * @param bindTypeEnum 绑定类型
     * @return key 绑定Code value 账号ID
     */
    Map<String, UserBind> findBindMap(List<String> codeList, UserBindType bindTypeEnum);

    /**
     * 获取三方绑定的账户ID
     *
     * @param code         三方绑定Code
     * @param bindTypeEnum 绑定类型
     * @return 账户ID
     */
    int getBindAccountId(String code, UserBindType bindTypeEnum);

}
