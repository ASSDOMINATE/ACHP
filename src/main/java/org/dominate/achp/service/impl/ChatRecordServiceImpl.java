package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.SqlUtil;
import lombok.AllArgsConstructor;
import org.dominate.achp.entity.ChatRecord;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.mapper.ChatRecordMapper;
import org.dominate.achp.service.IChatRecordService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 对话记录，关联用户-会话组-会话内容-会话场景 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
@AllArgsConstructor
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecord> implements IChatRecordService {

    private final ChatRecordMapper chatRecordMapper;


    @Override
    public int getLastContentId(String groupId) {
        QueryWrapper<ChatRecord> query = new QueryWrapper<>();
        query.lambda().eq(ChatRecord::getGroupId, groupId)
                .select(ChatRecord::getContentId)
                .orderByDesc(ChatRecord::getContentId)
                .last(SqlUtil.limitOne());
        ChatRecord record = getOne(query);
        return Optional.ofNullable(record).isEmpty() ? 0 : record.getContentId();
    }

    @Override
    public List<String> getUserGroupIdList(int accountId, PageReq page) {
        QueryWrapper<ChatRecord> query = createListQuery(page.getSize(), page.getPage());
        query.lambda().eq(ChatRecord::getAccountId, accountId)
                .select(ChatRecord::getGroupId);
        List<ChatRecord> recordList = list(query);
        List<String> groupIdList = new ArrayList<>(recordList.size());
        for (ChatRecord record : recordList) {
            groupIdList.add(record.getGroupId());
        }
        return groupIdList;
    }

    @Override
    public List<Integer> getUserSceneIdList(int accountId, PageReq page) {
        return chatRecordMapper.getUserLatestSceneIdList(accountId,page.getIndex(),page.getSize());
    }

    @Override
    public List<Integer> getGroupContentIdList(String groupId, PageReq page) {
        QueryWrapper<ChatRecord> query = createListQuery(page.getSize(), page.getPage());
        query.lambda().eq(ChatRecord::getGroupId, groupId)
                .select(ChatRecord::getContentId)
                .orderByDesc(ChatRecord::getId);
        List<ChatRecord> recordList = list(query);
        List<Integer> contentIdList = new ArrayList<>(recordList.size());
        for (ChatRecord record : recordList) {
            contentIdList.add(record.getContentId());
        }
        return contentIdList;
    }

    private static QueryWrapper<ChatRecord> createListQuery(int size, int page) {
        QueryWrapper<ChatRecord> query = new QueryWrapper<>();
        query.lambda().orderByDesc(ChatRecord::getId).last(SqlUtil.pageLimit(size, page));
        return query;

    }
}
