package org.dominate.achp.common.utils;

import com.hwja.tool.utils.RandomUtil;

/**
 * 随机编码生成工具
 *
 * @author ASSDOMINATE
 * @since 2022/05/13
 */
public final class UniqueCodeUtil {

    private static final int SSE_CODE_LENGTH = 18;

    private static final int PAY_ORDER_CODE_LENGTH = 24;
    private static final int CARD_EXCHANGE_KEY_LENGTH = 64;

    public static String createChatId() {
        return RandomUtil.createUniqueCode(SSE_CODE_LENGTH);
    }

    public static String createPayOrder(int payTypeCode) {
        return payTypeCode + RandomUtil.createUniqueCode(PAY_ORDER_CODE_LENGTH);
    }

    public static String createExchangeKey() {
        return RandomUtil.createRandomStrWords(CARD_EXCHANGE_KEY_LENGTH);
    }

}
