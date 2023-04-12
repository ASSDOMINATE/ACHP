package org.dominate.achp;

import com.alibaba.fastjson.JSON;
import com.hwja.tool.utils.DateUtil;
import com.hwja.tool.utils.RandomUtil;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.dominate.achp.common.enums.ChatRoleType;
import org.dominate.achp.common.enums.SceneItemType;
import org.dominate.achp.common.helper.ChatGptHelper;
import org.dominate.achp.common.utils.ApplePayUtil;
import org.dominate.achp.common.utils.ChatTokenUtil;
import org.dominate.achp.entity.dto.*;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class DemoTest {


    @Test
    public void forBigNum() {
        System.out.println(DateUtil.dateToStr(new Date(),"yyyy年M月d日"));
    }

    @Test
    public void testApplePay() {
        String payData = "MIITtgYJKoZIhvcNAQcCoIITpzCCE6MCAQExCzAJBgUrDgMCGgUAMIIDVwYJKoZIhvcNAQcBoIIDSASCA0QxggNAMAoCAQgCAQEEAhYAMAoCARQCAQEEAgwAMAsCAQECAQEEAwIBADALAgELAgEBBAMCAQAwCwIBDwIBAQQDAgEAMAsCARACAQEEAwIBADALAgEZAgEBBAMCAQMwDAIBCgIBAQQEFgI0KzAMAgEOAgEBBAQCAgCJMA0CAQMCAQEEBQwDMzUwMA0CAQ0CAQEEBQIDAf1hMA0CARMCAQEEBQwDMS4wMA4CAQkCAQEEBgIEUDI1NjAYAgECAgEBBBAMDmNvbS53dTd6aGkud3d3MBgCAQQCAQIEELtWLp66knHbAO1nbteXjNswGwIBAAIBAQQTDBFQcm9kdWN0aW9uU2FuZGJveDAcAgEFAgEBBBQAi3gu4MiFgquqiKWGztIbutJByTAeAgEMAgEBBBYWFDIwMjItMDQtMTlUMDA6NDE6NTZaMB4CARICAQEEFhYUMjAxMy0wOC0wMVQwNzowMDowMFowNQIBBwIBAQQtH3lCWGt";
        ApplePayUtil.verifyPay(payData);
    }


    @Test
    public void testStream() {
        List<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(1);
        a.add(1);
        a.add(2);
        a.add(1);
        a.add(1);
        a.add(1);
        a.add(1);
        a.stream().forEach((b) -> {
            if (b == 2) {
                return;
            }
            System.out.println(b);
        });
    }

    @Test
    public void testExtend() {
        SceneItemInputDTO input = new SceneItemInputDTO();
        input.setType(SceneItemType.INPUT);
        input.setValue("asdasda");
        tesItemType(input);

        SceneItemSelectDTO multipleSelect = new SceneItemSelectDTO();
        multipleSelect.setSelectWords(new String[]{"1", "2", "3"});
        multipleSelect.setMaxSelected(2);
        multipleSelect.setType(SceneItemType.MULTIPLE_SELECT);
        tesItemType(multipleSelect);

        SceneItemSelectDTO singleSelect = new SceneItemSelectDTO();
        singleSelect.setSelectWords(new String[]{"1", "2", "3"});
        singleSelect.setMaxSelected(1);
        singleSelect.setType(SceneItemType.SINGLE_SELECT);
        tesItemType(singleSelect);

        SceneItemLimitDTO limit = new SceneItemLimitDTO();
        limit.setMax(100);
        limit.setType(SceneItemType.WORDS_LIMIT);
        tesItemType(limit);

        SceneDetailDTO detail = new SceneDetailDTO();

        List<SceneItemBaseDTO> itemList = new ArrayList<>();
        itemList.add(input);
        itemList.add(limit);
        itemList.add(singleSelect);
        itemList.add(multipleSelect);
        detail.setItems(itemList.toArray(new SceneItemBaseDTO[0]));
        System.out.println(JSON.toJSONString(detail));
    }

    private static void tesItemType(SceneItemBaseDTO base) {
        String json = base.toJson();
        SceneItemType type = SceneItemType.getValueByCode(base.getTypeCode());
        SceneItemBaseDTO parseBase = type.getItem().parseJson(json, type, null,null);
        String parseJson = parseBase.toJson();
        assert json.equals(parseJson);
        System.out.println(base.getTypeCode());
        System.out.println(base.toDBJson());
    }

    private static ChatMessage createMessage(String content, boolean forUser) {
        return createMessage(content, forUser ? ChatRoleType.USER : ChatRoleType.AI);
    }

    public static ChatMessage createMessage(String content, ChatRoleType roleEnum) {
        ChatMessage message = new ChatMessage();
        message.setContent(content);
        message.setRole(roleEnum.getRole());
        return message;
    }

    @Test
    public void testToken() {
        List<ContentDTO> list = new ArrayList<>();
        for (int i = 0; i < 260; i++) {
            ContentDTO content = new ContentDTO();
            content.setReply(RandomUtil.createRandomStrWords(RandomUtil.getRandNum(1, 30)));
            content.setSentence(RandomUtil.createRandomStrWords(RandomUtil.getRandNum(1, 30)));
            list.add(content);
        }
        List<ChatMessage> messageList = parseMessages(list, ChatGptHelper.DEFAULT_MODEL_ID);
        System.out.println(messageList.size());
    }

    private static List<ChatMessage> parseMessages(List<ContentDTO> contentList, String modelId) {
        if (CollectionUtils.isEmpty(contentList)) {
            return Collections.emptyList();
        }
        List<ChatMessage> messageList = new ArrayList<>(contentList.size() * 2);
        for (ContentDTO content : contentList) {
            messageList.add(createMessage(content.getSentence(), true));
            messageList.add(createMessage(content.getReply(), false));
        }
        int tokens = ChatTokenUtil.tokens(modelId, messageList);
        int limitTokens = 4096;
        if (limitTokens >= tokens) {
            return messageList;
        }
        return filter(messageList, modelId, tokens - limitTokens);
    }

    private static List<ChatMessage> filter(List<ChatMessage> messageList, String modelId, int deleteTokens) {
        Iterator<ChatMessage> iterator = messageList.listIterator();
        int deleteTotal = 0;
        while (iterator.hasNext()) {
            ChatMessage message = iterator.next();
            int tokens = ChatTokenUtil.tokens(modelId, message.getContent());
            deleteTotal += tokens;
            iterator.remove();
            if (deleteTotal >= deleteTokens) {
                return messageList;
            }
        }
        return messageList;
    }
}
