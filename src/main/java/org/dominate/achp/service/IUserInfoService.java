package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.common.enums.ExistedType;
import org.dominate.achp.entity.UserInfo;
import org.dominate.achp.entity.dto.UserDTO;
import org.dominate.achp.entity.dto.UserInfoDTO;
import org.dominate.achp.entity.req.InfoReq;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
public interface IUserInfoService extends IService<UserInfo> {

    /**
     * 是否存在
     *
     * @param keyword         值
     * @param existedType 查询的类型
     * @return 是否存在
     */
    boolean existed(String keyword, ExistedType existedType);

    /**
     * 用户查找
     *
     * @param keyword 关键字
     * @return 用户列表
     */
    List<UserInfoDTO> search(String keyword);

    /**
     * 保存用户信息 根据AccountId判断是否为更新
     *
     * @param req 用户信息数据
     * @return 是否保存成功
     */
    boolean saveInfo(InfoReq req);

    /**
     * 查询用户信息
     * 查询 唯一编码/手机号/邮箱
     *
     * @param keyword     关键词
     * @param onlyAccount 是否只查询账户ID
     * @return 查询到的用户信息，没有则返回null
     */
    UserInfo find(String keyword, boolean onlyAccount);

    /**
     * 查询用户账户ID
     * 查询 手机号/邮箱
     *
     * @param keyword 关键词
     * @return 查询到的用户信息ID，没有则返回0
     */
    int find(String keyword);

    /**
     * 获取用户信息
     *
     * @param accountId 账号ID
     * @return 用户信息，没有则返回null
     */
    UserInfo getInfo(int accountId);

    /**
     * 获取 Info Map
     *
     * @param accountIdList 账号ID列表
     * @return key 账号ID value UserInfo
     */
    Map<Integer, UserInfo> getInfoMap(List<Integer> accountIdList);

    /**
     * 读取用户
     *
     * @param accountIdList 账户ID列表
     * @return 用户数据列表
     */
    List<UserDTO> getDTOList(Collection<Integer> accountIdList);

    /**
     * 读取用户
     *
     * @param accountIdList 账户ID列表
     * @param filterLeave   是否过滤离职
     * @return 用户数据列表
     */
    List<UserDTO> getDTOList(Collection<Integer> accountIdList, boolean filterLeave);

    /**
     * 分页查询用户
     *
     * @param index 开始位置
     * @param size  数量
     * @param filterLeave 是否过滤离职
     * @return 用户数据列表
     */
    List<UserDTO> getDTOList(int index, int size, boolean filterLeave);


    /**
     * 获取用户邮箱
     *
     * @param accountId 账号ID
     * @return 用户邮箱
     */
    String getEmail(int accountId);



}
