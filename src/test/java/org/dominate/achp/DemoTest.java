package org.dominate.achp;

import com.alibaba.fastjson.JSON;
import com.hwja.tool.utils.DateUtil;
import com.hwja.tool.utils.RandomUtil;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.dominate.achp.common.cache.PayOrderCache;
import org.dominate.achp.common.enums.ChatRoleType;
import org.dominate.achp.common.enums.GptModelType;
import org.dominate.achp.common.enums.SceneItemType;
import org.dominate.achp.common.helper.ApplePayHelper;
import org.dominate.achp.common.helper.ChatGptHelper;
import org.dominate.achp.common.helper.WeChatPayHelper;
import org.dominate.achp.common.utils.ChatTokenUtil;
import org.dominate.achp.common.utils.UniqueCodeUtil;
import org.dominate.achp.entity.dto.*;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

public class DemoTest {


    @Test
    public void testCache(){
        Collection<PayOrderDTO> payOrders = PayOrderCache.getList();
        for (PayOrderDTO payOrder : payOrders) {
            System.out.println(payOrder.getSysOrderCode());
        }
    }


    @Test
    public void models(){
        ChatGptHelper.modelList();
    }

    @Test
    public void testWechatPay(){
        String orderCode = UniqueCodeUtil.createPayOrder(3);
        String wxCode = WeChatPayHelper.createAppPayOrder(orderCode, BigDecimal.ONE,"测试订单");
        System.out.println("app "+wxCode);
        PayResultDTO result = WeChatPayHelper.createNativePayOrder(orderCode, BigDecimal.ONE,"测试订单");

        System.out.println("native " + result.getPartyOrderCode());
        System.out.println("链接" + result.getCodeUrl());
        WeChatPayHelper.verifyPayOrder(result.getSysOrderCode(), BigDecimal.ONE);
    }

    @Test
    public void testOrder(){
        try {
            WeChatPayHelper.verifyPayOrder("2168208273588298465833413", new BigDecimal(18));
            System.out.println("支付完成");
        }catch (Exception e){
            System.out.println("支付未完成");
        }
    }

    @Test
    public void forBigNum() {
        System.out.println(DateUtil.dateToStr(new Date(), "yyyy年M月d日"));
    }

    @Test
    public void testApplePay() {
        String payData = "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";
//        ApplePayHelper.verifyPay(payData,"2000000333697868","vip_card_week");
        payData = "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";
        ApplePayHelper.verifyPay(payData,"2000000333697868","vip_card_week");

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
        SceneItemBaseDTO parseBase = type.getItem().parseJson(json, type, null, null);
        String parseJson = parseBase.toJson();
        assert json.equals(parseJson);
        System.out.println(base.getTypeCode());
        System.out.println(base.toDbJson());
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
    public void token() {
        // 4179
        String a = "你可以读取Github上的开源代码，然后给我解答疑惑吗\n" +
                "当然可以，请问您需要解答哪些疑惑呢？我会尽力帮助您。\n" +
                "https://github.com/ASSDOMINATE/2048 这个是干嘛的\n" +
                "这是一个开源的2048游戏，参考了经典的2048游戏规则和界面设计。用户可以通过操作数字格子，使它们合并到一起，得到更大的数字。游戏的终点是得到一个2048的数字格子，但如果数字格子填满了整个游戏区域，游戏也会结束。这个开源项目提供了一些自定义参数，用户可以调整游戏难度和界面设置。\n" +
                "这个代码中有什么特别的吗\n" +
                "在这个代码中，有一些特别的地方：  1. 使用了 MVC 框架：这个代码使用了 Model-View-Controller （MVC）的设计模式，将游戏分为三个部分：模型层、视图层和控制层。这样可以使得代码更加规范和易于扩展。  2. 采用了面向对象的编程思想：整个游戏的实现都是基于 JavaScript 的面向对象编程思想实现的。  3. 充分利用了 HTML5 和 CSS3 技术：游戏的界面使用了 CSS3 动画效果，同时使用 Canvas 和 SVG 技术实现了游戏的绘制和动画效果。在 HTML5 的 localStorage 中存储了游戏的状态，以实现游戏的数据持久化功能。  4. 支持移动设备操作：游戏支持使用键盘和移动设备上的手势操作，适配了不同的设备。  5. 支持自定义游戏设置：游戏提供了一些自定义参数，用户可以根据自己的需求来调整游戏难度和界面设置。\n" +
                "不对吧，应该没有canvas和svg使用吧\n" +
                "非常抱歉，我犯了一个错误。查看了代码之后发现，这个2048游戏并没有使用 Canvas 和 SVG 技术来实现游戏的绘制和动画效果。  相反，游戏的界面使用了纯 CSS 实现的动画效果，通过对格子元素的样式设置和更新，来实现数字块的移动和合并。\n" +
                "这两种方案更好呢，在这个场景下\n" +
                "Canvas 和 SVG 技术是适用于绘图场景的技术，其中 Canvas 更适合于像素级别的图形处理，SVG 更适合于矢量图形处理。对于本身就是一个好玩的小游戏的2048来说，使用 CSS 实现游戏界面比使用 Canvas 和 SVG 来实现游戏界面更加合适。  使用 CSS 实现游戏界面的优点是代码量小、运行效率高、兼容性更好、易于维护和优化等，适合于制作简单的小游戏。  而使用 Canvas 和 SVG 技术来实现游戏界面的优点是图形的渲染效果更加详细精美，图片处理方便，并且支持更加复杂的交互操作，但需要更多的代码量和技术成本，并且相对 CSS 更加复杂，适合于需要制作高质量图形的游戏场景。\n" +
                "你评价下这个项目的代码水平\n" +
                "从我对这个项目的代码中的初步观察，我认为这个项目的代码水平还是比较优秀的。  以下是我认为这个项目代码水平较好的优点：  1. 代码结构清晰：整个项目的代码结构相对清晰明了，MVC 架构的概念被很好地实现，使得代码逻辑更加清晰明了。  2. 面向对象编程理念贯穿整个项目：代码中充分使用了 JavaScript 的面向对象编程思想，利用了对象、继承等概念，将游戏不同的组成部分分离处理，使得代码更加分层、可维护性更高。  3. 使用 ES6 的语法进行开发：在代码的实现中使用了 ES6 的语法，如箭头函数、let/const块级作用域声明、模板字面量、解构赋值等，这些新的语法特性让代码更加简洁明了。  4. 注释写的比较详细：多数部分都在代码中写了注释，并且注释较为详细，传达了作者的想法和思路，让代码更容易理解和维护。  当然，还有一些需要提高的地方，比如：适应不同屏幕尺寸的问题、兼容性问题等，但整体上项目的代码水平还是比较不错的。\n" +
                "但是我没在代码里看到有太多注释呢\n" +
                "非常抱歉，由于我看到的是比较早期版本的代码，可能与现在的代码不同，确实没有多少注释。但是从最近的提交记录来看，开发者已经增加了一些注释，以便更好地理解代码。   总的来说，注释是良好编码实践的一部分，它们可以帮助代码阅读者更好地理解代码，尤其是当代码存在一些不同寻常、复杂的实现时，有注释将是非常有用的。虽然某些代码可能容易理解，但是维护者和其他人也需要了解代码背后的思想和目的。因此，注释可以作为一种工具，帮助开发者更好地维护和修改代码。\n" +
                "在编程中，如果不使用else进行开发你觉得这样好吗\n" +
                "在编程中不使用 `else` 语句进行开发是可行的，但是并不总是最佳实践。  以下是不使用 `else` 的一些优点：  1. 代码更加简洁易懂：避免了多层嵌套的语法结构，可读性更强，也更容易理解代码的实现过程。  2. 减少了出错的可能性：减小了代码的复杂度，避免了代码中出现不必要的逻辑错误。  以下是不使用 `else` 的一些缺点：  1. 可读性有时会降低：有时候使用 `else` 语句，可以使得代码更加清晰易懂，特别是在需要复杂的条件操作时。  2. 可维护性变差：如果多个条件没有通过 `else` 聚合在一起，可能导致代码更难以维护。当然，可以使用一些其他的代码书写规范来避免这种情况的发生。  总之，使用或不使用 `else` 语句主要看不同的情况。如果 `else` 的存在增加了代码的易读性和易维护性，则应该使用，否则可以考虑避免使用 `else` 语句。\n" +
                "你赞同 else 的前后条件必须对立互斥吗\n" +
                "在一些情况下，使用 `else` 语句时，前后条件必须对立互斥，以保证程序获得正确的执行结果。  对立性是指在两个分支语句之间互斥，且只有一个分支能够被执行。如果前后条件不对立，则会存在多种情况，可能会导致程序逻辑错误。因此，当使用 `else` 语句时，应该确保前后条件对立互斥。  例如，考虑以下代码：  ```javascript if (score >= 80) {   console.log('优秀'); } else if (score >= 60) {   console.log('良好'); } else {   console.log('不及格'); } ```  在这个例子中，第二个条件是第一个条件的子集。如果将两个条件的顺序调换，则会出现错误的结果。因此，为了保证程序的正确性，在使用 `else` 语句时，应该确保前后条件对立互斥。  但是，在某些情况下，使用 `else` 语句时，前后的条件可能不需要对立互斥。例如，当前后条件都是逻辑上互相独立的时，使用 `else if` 可以使得代码更加清晰可读。  总的来说，在使用 `else` 语句时，必须根据具体的情况来判断条件的对立性，从而保证代码的正确性和可读性。\n" +
                "这个例子如果只用if的话可读性明显更好，我一直认为绝大多数else应该是尽量不用，只在前后对立互斥的情况下使用\n" +
                "您的观点是正确的，事实上，在某些情况下使用过多的 `else` 语句可能会降低代码的可读性。  在判断语句的时候，使用多个 `if` 语句（而非 `else-if`）可以让代码更易于理解和调试，减少逻辑错误的发生。多个互相独立的 `if` 语句可以让读者更容易跟踪程序的逻辑流程，而且在添加或移除条件时也更加容易。  然而，在某些情况下，使用 `else-if` 比多个独立的 `if` 语句更好。例如，在上面设定分数等级的代码中，改为多个独立的 `if` 语句可能会让代码变得比较冗长，然后使用 `else-if` 语句可以节省代码量，提高可读性。  综上所述，`if` 和 `else-if` 都有各自适用的情况。在编写代码的时候，我们需要根据具体情况选择适当的判断语句，来保证代码的可读性和可维护性。另外，为了保证代码的可读性和简洁性，需要权衡使用 `if` 和 `else` 所带来的优缺点。\n" +
                "从java上来说，这个场景switch好更多吧\n" +
                "在 Java 语言中，如果进行多个条件判断，可以采用多种方式实现，例如`if-else`语句和`switch-case`语句等。相对于多层嵌套的`if-else`语句，`switch-case`语句可以更加简洁流畅地处理多个条件的情况。  在上面的Java代码中，使用`switch-case`语句可以让代码更加清晰易懂。例如：  ``` switch (score / 10) {     case 10:     case 9:         System.out.println(\"优秀\");         break;     case 8:     case 7:     case 6:         System.out.println(\"良好\");         break;     default:         System.out.println(\"不及格\");         break; } ```  `switch`语句根据分数值的范围选择不同结果，代码清晰易懂，并且容易扩展和维护。当然，使用`switch-case`语句也存在一些缺点，例如只能使用常量、不支持浮点型数值等，需要根据具体场景和需求来选择合适的方法。\n" +
                "在java中你认为代码质量高能体现在哪些地方\n" +
                "在 Java 中，代码质量好的代码通常应该具备以下特点：  1. 符合编程范式：遵循Java编程的相关规范和范式，如面向对象编程、SOLID原则等。代码要易于扩展、维护、重构，同时要遵守一些良好的编码习惯、命名规范等，以便其他人能够轻松地理解代码。  2. 易读性好：代码应该具备良好的可读性，让阅读代码的人快速理解代码的意图，避免一些不必要的错误。好的 Java 代码通常注重代码的结构、格式化和命名方式等。  3. 高效性和可靠性：Java 代码要具备高效性和可靠性，效率高、代码安全等问题都应该考虑到。合适的算法和数据结构可以在保证算法正确性的基础上提高代码的效率。  4. 易测性：代码中应该具备良好的可测性，应该认真考虑测试用例，尽可能覆盖各种场景，以便能够发现潜在的错误和问题。  5. 可维护性：Java 代码应该具备良好的可维护性，代码结构应该合适，根据代码的逻辑进行分层，错误处理要求得当，以便其他人可以轻松维护和修改代码。  总的来说，Java代码质量高的代码应该尽可能简洁、易读、高效、可靠、易测和易于维护。这些特点可以帮助开发人员编写出更加优质的Java代码。\n" +
                "在现代的架构中，数据的连表查询行为被弱化了，我认为所有的连表其实都可以去掉，这样程序的扩展性更强，耦合性也相应的降低了\n" +
                "数据的关联查询确实是现代架构中的一个拓展性和可维护性问题，但是去掉所有的连表并不是一个可行的方法，这是因为关联查询是必要的，而且查询连接表的性能可以通过索引进行优化。  去掉所有的关联查询在一些场景下是可行的，例如非关系型数据库或者基于 NoSQL 的数据存储等，但是在传统的关系型数据库中，如果完全去掉关联查询操作，则会导致查询性能下降、复杂性增加等问题。  在传统关系型数据库中使用关联查询，可以让我们避免数据冗余和数据异常问题。同时在一些情况下，使用关联查询会使得程序的逻辑更加清晰，降低代码重复和复杂性。可以使用一些合理的拆分表（如水平拆分、垂直拆分）的方式来减少关联查询的需求，同时通过索引等方式对关联查询进行优化，以提升查询的效率。  综上所述，关联查询的需求取决于具体的架构和业务\n" +
                "在mysql，我有个优化方案，假如一张表有20个字段，其中只有4个字段是会出现在查询条件了，这样我把它拆成4 16字段的两张表，你觉得这样有什么优点及缺点";
        String[] s = a.split("\n");
        List<ChatMessage> messageList = new ArrayList<>();
        String modelId = GptModelType.GPT_3_5_TURBO.getId();
        int countToken = 0;
        for (int i = 0; i < s.length; i++) {
            boolean isUser = i % 2 == 0;
            countToken += ChatTokenUtil.tokens(modelId, isUser ? "user" : "assistant");
            countToken += ChatTokenUtil.tokens(modelId, s[i]);
            messageList.add(ChatGptHelper.createMessage(s[i], isUser));
            if (!isUser){
                countToken += 3;
            }
        }
        for (ChatMessage message : messageList) {
            System.out.println(message.getRole() + ":" + message.getContent());
        }
        int tokens = ChatTokenUtil.tokens(modelId, messageList);
        System.out.println("count token " + countToken);
        System.out.println(tokens);
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
