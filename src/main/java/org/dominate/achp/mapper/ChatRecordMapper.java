package org.dominate.achp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.dominate.achp.entity.ChatRecord;

import java.util.List;

/**
 * <p>
 * 对话记录，关联用户-会话组-会话内容-会话场景 Mapper 接口
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface ChatRecordMapper extends BaseMapper<ChatRecord> {

    List<Integer> getUserLatestSceneIdList(@Param("accountId") Integer accountId, @Param("limit") Integer limit, @Param("size") Integer size);
}
