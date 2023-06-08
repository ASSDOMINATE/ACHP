package org.dominate.achp.entity.req;


import com.hwja.tool.utils.RandomUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.enums.SexType;
import org.dominate.achp.common.enums.UserState;

import java.io.Serializable;

/**
 * 用户信息请求类
 *
 * @author dominate
 * @since 2022/02/25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class InfoReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号ID
     */
    private Integer accountId;

    /**
     * 别名 最大长度45
     */
    private String alias;

    /**
     * 名称 最大长度45
     */
    private String name;

    /**
     * 描述 最大长度45
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 头像地址 最大长度1024
     */
    private String avatar;

    /**
     * 用户唯一编码
     */
    private String uniqueCode;

    /**
     * 身份证号
     */
    private String identity;

    /**
     * 状态
     */
    private Integer state;

    public static InfoReq registerInfo(int accountId, String sign) {
        InfoReq infoReq = new InfoReq();
        infoReq.setAccountId(accountId);
        infoReq.setName(sign);
        infoReq.setAlias(infoReq.getName());
        infoReq.setPhone(StringUtils.EMPTY);
        infoReq.setAvatar(StringUtils.EMPTY);
        infoReq.setEmail(StringUtils.EMPTY);
        infoReq.setIdentity(StringUtils.EMPTY);
        infoReq.setSex(SexType.UNKNOWN.getCode());
        infoReq.setUniqueCode(RandomUtil.create32RandOrder(accountId));
        infoReq.setState(UserState.NORMAL.getCode());
        return infoReq;
    }


    public static InfoReq initInfo(int accountId, String sign) {
        InfoReq infoReq = new InfoReq();
        infoReq.setAccountId(accountId);
        infoReq.setName(String.valueOf(accountId));
        infoReq.setAlias(infoReq.getName());
        infoReq.setPhone(StringUtils.EMPTY);
        infoReq.setAvatar(StringUtils.EMPTY);
        infoReq.setEmail(StringUtils.EMPTY);
        infoReq.setIdentity(StringUtils.EMPTY);
        infoReq.setSex(SexType.UNKNOWN.getCode());
        infoReq.setUniqueCode(accountId + "-" + sign);
        infoReq.setState(UserState.NORMAL.getCode());
        return infoReq;
    }
}
