package org.dominate.achp.common.helper;

import com.hwja.tool.clients.redis.RedisClient;
import com.hwja.tool.utils.BaseUtil;
import com.hwja.tool.utils.LoadUtil;
import com.hwja.tool.utils.RandomUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.enums.ExceptionType;
import org.dominate.achp.entity.UserInfo;
import org.dominate.achp.entity.dto.UserAuthDTO;
import org.dominate.achp.sys.exception.BusinessException;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.List;
import java.util.Map;

/**
 * 授权工具
 *
 * @author dominate
 * @since 2022/02/22
 */
public final class AuthHelper {

    private static final String JWT_SECRET = LoadUtil.getProperty("jwt.secret");

    private static final String CACHE_USER_ID_TOKEN_FIELD_KEY = "cache:user:id:token";
    private static final String CACHE_VALID_MOBILE_SIGN = "cache:valid:mobile";

    private static final String APPEND_PLATFORM_ID = "&";

    private static final Key JWT_SECRET_KEY;

    static {
        JWT_SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    }

    /**
     * 设置授权
     *
     * @param info           用户信息
     * @param platformId     平台ID
     * @param permissionList 权限列表
     * @return JWT-Token
     */
    public static String setAuth(UserInfo info, int platformId, List<Integer> permissionList) {
        // 用户数据
        UserAuthDTO userAuth = new UserAuthDTO(info, platformId, permissionList);
        // 设置随机 登录标识 Token
        userAuth.setToken(RandomUtil.create32RandOrder(info.getId()));
        // 设置验证
        setLogin(userAuth);
        // 生成 JwtToken
        Map<String, Object> claims = BaseUtil.beanToMap(userAuth);
        return createJwtToken(claims);
    }

    /**
     * 解析 JWT-Token 获得用户信息并确保是有效用户
     *
     * @param token JWT-Token
     * @return 账号ID 失败抛出异常
     */
    public static int parseWithValidForId(String token) {
        return parseWithValid(token).getAccountId();
    }

    /**
     * 解析 JWT-Token 获得用户信息并确保是有效用户
     *
     * @param token JWT-Token
     * @return 有效的用户信息 失败抛出异常
     */
    public static UserAuthDTO parseWithValid(String token) {
        UserAuthDTO userAuth;
        try {
            userAuth = parse(token);
        } catch (Exception e) {
            // 解析报错 没有必要记录错误的Token值
            throw BusinessException.create(ExceptionType.INVALID_TOKEN);
        }
        if (!isValid(userAuth)) {
            throw BusinessException.create(ExceptionType.LOGIN_STATE_ERROR);
        }
        return userAuth;
    }

    public static UserAuthDTO parseWithValidAdmin(String token) {
        UserAuthDTO userAuth = parseWithValid(token);
        if (CollectionUtils.isEmpty(userAuth.getPermissions())) {
            throw BusinessException.create(ExceptionType.NO_PERMISSION);
        }
        return userAuth;
    }

    public static int parseWithValidAdminForId(String token) {
        return parseWithValidAdmin(token).getAccountId();
    }

    public static boolean checkAdminUser(String token) {
        try {
            parseWithValidAdmin(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 解析 JWT-Token 获得用户信息
     *
     * @param token JWT-Token
     * @return 用户信息
     */
    public static UserAuthDTO parse(String token) {
        Map<String, Object> claims = parseJwtToken(token);
        return BaseUtil.mapToBean(claims, UserAuthDTO.class);
    }

    /**
     * 设置Token无效
     *
     * @param token JWT-Token
     */
    public static void setInvalid(String token) {
        UserAuthDTO userAuth = parseWithValid(token);
        if (null == userAuth) {
            return;
        }
        setLogout(userAuth.getAccountId(), userAuth.getPlatformId());
    }

    /**
     * 是否为有效的用户
     *
     * @param user 用户数据
     * @return 是否有效
     */
    public static boolean isValid(UserAuthDTO user) {
        if (null == user) {
            return false;
        }
        String token = getLoginToken(user.getAccountId(), user.getPlatformId());
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        return user.getToken().equals(token);
    }

    private static final int[] VALID_CACHE_OUT_TIME = {60 * 5, 60 * 6};

    public static String setMobileValid(String mobile) {
        String validCode = RandomUtil.createRandomStrNums(6);
        String key = createValidCacheKey(mobile);
        try {
            if (!ALiYunHelper.sendSMSForValid(mobile, validCode, ALiYunHelper.SMS_TEMPLATE_COMMON)) {
                throw BusinessException.create(ExceptionType.SEND_SMS_ERROR);
            }
            RedisClient.set(key, validCode, RandomUtil.getRandNum(VALID_CACHE_OUT_TIME[0], VALID_CACHE_OUT_TIME[1]));
        } catch (Exception e) {
            throw BusinessException.create(ExceptionType.SEND_SMS_ERROR);
        }
        return validCode;
    }

    public static boolean checkMobileValid(String mobile, String validCode) {
        String key = createValidCacheKey(mobile);
        if (!RedisClient.hasKey(key)) {
            return false;
        }
        String targetCode = RedisClient.get(key).toString();
        if (!targetCode.equals(validCode)) {
            return false;
        }
        RedisClient.removeKey(key);
        return true;
    }

    private static String createValidCacheKey(String mobile) {
        return CACHE_VALID_MOBILE_SIGN + mobile;
    }


    /**
     * 创建 JWT-Token
     *
     * @param claims 加密数据
     * @return JWT-Token
     */
    private static String createJwtToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).signWith(JWT_SECRET_KEY).compact();
    }

    /**
     * 解析 JWT-Token
     *
     * @param token JWT-Token
     * @return 解析结果
     */
    private static Map<String, Object> parseJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(JWT_SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    /**
     * 设置账户退出登录
     *
     * @param accountId  退出登录的账号ID
     * @param platformId 退出登录的平台
     */
    private static void setLogout(int accountId, int platformId) {
        RedisClient.hRemoveField(CACHE_USER_ID_TOKEN_FIELD_KEY, parseUserKey(accountId, platformId));
    }

    public static void setLogout(int accountId) {
        setLogout(accountId, 0);
    }

    private static void setLogout(int platformId, int... accountIds) {
        for (int accountId : accountIds) {
            RedisClient.hRemoveField(CACHE_USER_ID_TOKEN_FIELD_KEY, parseUserKey(accountId, platformId));
        }
    }

    /**
     * 设置用户登录
     *
     * @param user 登录用户信息
     */
    private static void setLogin(UserAuthDTO user) {
        RedisClient.hSetPersist(CACHE_USER_ID_TOKEN_FIELD_KEY, parseUserKey(user), user.getToken());
    }

    /**
     * 获取用户登录标识
     *
     * @param accountId 账户ID
     * @return 登录标识
     */
    private static String getLoginToken(int accountId, int platformId) {
        String key = parseUserKey(accountId, platformId);
        if (!RedisClient.hHasKey(CACHE_USER_ID_TOKEN_FIELD_KEY, key)) {
            return StringUtils.EMPTY;
        }
        return RedisClient.hGet(CACHE_USER_ID_TOKEN_FIELD_KEY, key, String.class);
    }

    private static String parseUserKey(UserAuthDTO user) {
        return parseUserKey(user.getAccountId(), user.getPlatformId());
    }

    private static String parseUserKey(int accountId, int platformId) {
        return accountId + APPEND_PLATFORM_ID + platformId;
    }

}
