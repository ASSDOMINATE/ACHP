package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.UserBindInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户三方绑定信息 服务类
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
public interface IUserBindInfoService extends IService<UserBindInfo> {

    /**
     * 保存/更新信息
     *
     * @param bindId 绑定ID
     * @param info   绑定信息
     * @return 是否保存成功
     */
    boolean saveInfo(int bindId, String info);

    /**
     * 获取绑定信息
     *
     * @param bindId 绑定ID
     * @return 绑定信息
     */
    String getInfo(int bindId);

    /**
     * 覆盖Info数据
     *
     * @param infoMap key bindId value info Json
     * @return 是否覆盖完成
     */
    boolean coverInfos(Map<Integer, String> infoMap);

    /**
     * 获取Info数据Map
     * @param bindIdList 绑定ID列表
     * @return Info Map
     */
    Map<Integer,String> getInfoMap(List<Integer> bindIdList);
}
