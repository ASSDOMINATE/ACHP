package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.ChatContent;
import org.dominate.achp.entity.dto.ContentDTO;

import java.util.List;

/**
 * <p>
 * 聊天内容 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IChatContentService extends IService<ChatContent> {

    /**
     * 获取聊天内容列表
     *
     * @param contentIdList ID列表
     * @return 聊天内容列表
     */
    List<ContentDTO> list(List<Integer> contentIdList);
}
