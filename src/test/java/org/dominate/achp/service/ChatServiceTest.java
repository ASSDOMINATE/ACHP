package org.dominate.achp.service;

import com.theokanning.openai.completion.chat.ChatMessage;
import org.dominate.achp.common.helper.ChatGptHelper;
import org.dominate.achp.entity.dto.ContentDTO;
import org.dominate.achp.entity.dto.ReplyDTO;
import org.dominate.achp.entity.req.PageReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ChatServiceTest {

    @Resource
    private IChatRecordService chatRecordService;
    @Resource
    private IChatContentService chatContentService;
    @Resource
    private ChatService chatService;

    @Test
    public void testChat(){
        List<Integer> contentIdList = chatRecordService.getGroupContentIdList("168535751414443037", PageReq.chatPage());
        List<ContentDTO> contentList = chatContentService.list(contentIdList);
        ReplyDTO reply = ChatGptHelper.send("你再想想有给我一个类似的更完整的总结",contentList);
        System.out.println(reply.getReply());
//        List<ChatMessage> messageList = ChatGptHelper.parseMessages(contentList,"你再想想有给我一个类似的更完整的总结","",ChatGptHelper.DEFAULT_MODEL_ID);
//        for (ChatMessage message : messageList) {
//            System.out.println(message);
//        }
    }
}
