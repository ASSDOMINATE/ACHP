package org.dominate.achp.common.helper;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.hwja.tool.utils.LoadUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ALiYunHelper {

    // 短信配置

    public static final String SMS_TEMPLATE_COMMON = LoadUtil.getProperty("aliyun.sms.temp.common");

    private static final String SMS_ACCESS_KEY_ID = LoadUtil.getProperty("aliyun.sms.key");
    private static final String SMS_SECRET = LoadUtil.getProperty("aliyun.sms.secret");
    private static final String SMS_SIGN_NAME = LoadUtil.getProperty("aliyun.sms.sign");

    // STS权鉴

    private static final String STS_ACCESS_KEY_ID = LoadUtil.getProperty("aliyun.sts.key");
    private static final String STS_SECRET = LoadUtil.getProperty("aliyun.sts.secret");
    private static final String STS_ROLE_ARN = LoadUtil.getProperty("aliyun.sts.role.arn");
    private static final String STS_ROLE_SESSION_NAME = LoadUtil.getProperty("aliyun.sts.role.session.name");

    // 常量配置

    private static final String OSS_UPLOAD_DEFAULT_BUCKET = "public-src";
    private static final String OSS_ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
    private static final String STS_ENDPOINT = "sts.aliyuncs.com";
    private static final String STS_PRODUCT = "Sts";
    private static final String REGION_ID = "cn-hangzhou";

    private static final String[] DEFAULT_CONNECT_TIMEOUT = {"sun.net.client.defaultConnectTimeout", "10000"};
    private static final String[] DEFAULT_READ_TIMEOUT = {"sun.net.client.defaultReadTimeout", "10000"};
    private static final String SEND_SUCCESS_SIGN = "OK";
    private static final String EMPTY_PARAM = "";

    // 开发者无需替换
    private static final String PRODUCT = "Dysmsapi";
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";


    public static AssumeRoleResponse getSTSKey() throws ClientException {
        ProtocolType protocolType = ProtocolType.HTTPS;
        try {
            DefaultProfile.addEndpoint(EMPTY_PARAM, EMPTY_PARAM, STS_PRODUCT, STS_ENDPOINT);
            IClientProfile profile = DefaultProfile.getProfile(EMPTY_PARAM, STS_ACCESS_KEY_ID, STS_SECRET);
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setMethod(MethodType.POST);
            request.setRoleArn(STS_ROLE_ARN);
            request.setRoleSessionName(STS_ROLE_SESSION_NAME);
            request.setPolicy(null);
            request.setProtocol(protocolType);
            request.setDurationSeconds(3600L);
            return client.getAcsResponse(request);
        } catch (com.aliyun.oss.ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送默认SMS短信
     *
     * @param mobile       目标手机号
     * @param templateCode 验证码
     * @return boolean 是否发送成功
     */
    public static boolean sendSMSForValid(String mobile, String validCode, String templateCode) throws ClientException {
        return sendSMSForValid(mobile, validCode, templateCode, SMS_SIGN_NAME);
    }


    /**
     * 发送SMS短信
     *
     * @param mobile       目标手机号
     * @param templateCode 验证码
     * @param sign         短信标签
     * @return boolean 是否发送成功
     */
    public static boolean sendSMSForValid(String mobile, String validCode, String templateCode, String sign) throws ClientException {
        // 可自助调整超时时间
        System.setProperty(DEFAULT_CONNECT_TIMEOUT[0], DEFAULT_CONNECT_TIMEOUT[1]);
        System.setProperty(DEFAULT_READ_TIMEOUT[0], DEFAULT_READ_TIMEOUT[1]);

        // 初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID, SMS_ACCESS_KEY_ID, SMS_SECRET);
        DefaultProfile.addEndpoint(REGION_ID, REGION_ID, PRODUCT, DOMAIN);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        // 必填:待发送手机号
        request.setPhoneNumbers(mobile);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName(sign);
        // 必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);

        // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(packCodeValue(validCode));

        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        if (sendSmsResponse.getCode().equals(SEND_SUCCESS_SIGN)) {
            return true;
        } else {
            log.error("发送短信出错，状态码为" + sendSmsResponse.getCode());
            log.error("发送短信出错，描述为" + sendSmsResponse.getMessage());
            return false;
        }
    }

    private static String packCodeValue(String value) {
        return "{\"code\":\"" + value + "\"}";
    }

}
