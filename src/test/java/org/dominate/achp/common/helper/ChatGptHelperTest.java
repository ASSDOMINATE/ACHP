package org.dominate.achp.common.helper;

import org.junit.Test;

public class ChatGptHelperTest {



    @Test
    public void testGPT() {
        String result = ChatGptHelper.send("简单解释下超弦定理").getReply();
        System.out.println(result);
    }

}
