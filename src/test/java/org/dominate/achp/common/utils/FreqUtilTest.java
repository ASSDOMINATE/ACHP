package org.dominate.achp.common.utils;

import org.junit.Test;

public class FreqUtilTest {

    @Test
    public void testApiKeyFreq() throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            System.out.println(i);
            boolean canUse = FreqUtil.waitFreqForApiKey("1");
            System.out.println("请求次数 " + i + " 是否可用 " + canUse);
            if (i > 300) {
                FreqUtil.releaseApiKey("1");
            }
        }
    }
}
