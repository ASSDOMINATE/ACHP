package org.dominate.achp;

import com.alibaba.fastjson.JSON;
import com.hwja.tool.utils.DateUtil;
import com.hwja.tool.utils.RandomUtil;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.dominate.achp.common.enums.ChatRoleType;
import org.dominate.achp.common.enums.GptModelType;
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
        System.out.println(DateUtil.dateToStr(new Date(), "yyyy年M月d日"));
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
        SceneItemBaseDTO parseBase = type.getItem().parseJson(json, type, null, null);
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
