package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.ChatSceneRelate;
import org.dominate.achp.entity.req.PageReq;

import java.util.List;

/**
 * <p>
 * 对话场景关联 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-06
 */
public interface IChatSceneRelateService extends IService<ChatSceneRelate> {

    List<Integer> getSceneIdList(int categoryId, PageReq page);

}
