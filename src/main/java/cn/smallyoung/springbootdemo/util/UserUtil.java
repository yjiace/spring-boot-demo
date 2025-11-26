package cn.smallyoung.springbootdemo.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.smallyoung.springbootdemo.config.JwtConfig;
import cn.smallyoung.springbootdemo.user.entity.User;
import cn.smallyoung.springbootdemo.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 获取当前的登录用户id
 *
 * @author SmallYoung
 */
@Slf4j
@Component
public class UserUtil {


    private static JwtConfig jwtConfig;


    @Autowired
    public void setTemplate(JwtConfig jwtConfig) {
        UserUtil.jwtConfig = jwtConfig;
    }


    /**
     * 获取当前登录用户id
     *
     * @return 登录用户id
     */
    public static String getCurrentAuditor() {
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (servletRequestAttributes != null) {
                HttpServletRequest request = servletRequestAttributes.getRequest();
                Object uId = request.getAttribute("userId");
                return uId == null ? "-1" : uId.toString();
            }
            return "-1";
        } catch (Exception e) {
            return "-1";
        }
    }

    /**
     * 获取当前登录用户Token
     *
     * @return 当前登录用户Token
     */
    public static String getCurrentUserToken() {
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (servletRequestAttributes != null) {
                HttpServletRequest request = servletRequestAttributes.getRequest();
                return request.getHeader(jwtConfig.getTokenHead());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 获取登录时的redis key
     *
     * @param id    ID
     * @param token token
     * @return redis key
     */
    public static String getLoginRedisKey(String id, String token) {
        if (jwtConfig.getSso()) {
            return jwtConfig.getRedisKey() + id;
        } else {
            return jwtConfig.getRedisKey() + DigestUtil.md5Hex(id + token);
        }
    }

    /**
     * 从指定属性路径收集用户ID
     */
    private static void collectUserIdsFromProperty(Object obj, String propertyPath, Set<String> userIds) {
        if (obj == null || StrUtil.isBlank(propertyPath)) {
            return;
        }

        try {
            String[] pathParts = propertyPath.split("\\.");
            Object currentObj = obj;

            for (int i = 0; i < pathParts.length; i++) {
                if (currentObj == null) {
                    break;
                }

                String propertyName = pathParts[i];
                BeanWrapper wrapper = new BeanWrapperImpl(currentObj);

                if (!wrapper.isReadableProperty(propertyName)) {
                    break;
                }

                Object propertyValue = wrapper.getPropertyValue(propertyName);

                if (i == pathParts.length - 1) {
                    // 最后一层，这里应该是用户ID
                    if (propertyValue instanceof Collection<?> collection) {
                        // 如果最后一层是集合，收集集合中的所有ID
                        collection.forEach(item -> {
                            String userId = parseUserId(item);
                            if (userId != null) {
                                userIds.add(userId);
                            }
                        });
                    } else {
                        // 单个值
                        String userId = parseUserId(propertyValue);
                        if (userId != null) {
                            userIds.add(userId);
                        }
                    }
                } else {
                    // 中间层，继续往下探索
                    if (propertyValue instanceof Collection<?> collection) {
                        // 如果是集合，需要对集合中的每个元素继续处理剩余的路径
                        String remainingPath = String.join(".", Arrays.copyOfRange(pathParts, i + 1, pathParts.length));
                        collection.forEach(item -> collectUserIdsFromProperty(item, remainingPath, userIds));
                        return; // 集合处理完毕，不需要继续循环
                    } else {
                        currentObj = propertyValue;
                    }
                }
            }
        } catch (Exception e) {
            log.debug("从属性路径 {} 收集用户ID时出错: {}", propertyPath, e.getMessage());
        }
    }

    /**
     * 为指定属性路径设置用户信息
     */
    private static void setUserNameForProperty(Object obj, UserProperty userProperty, Map<String, User> userMap) {
        if (obj == null || userProperty == null) {
            return;
        }

        String idProperty = userProperty.getIdProperty();
        String realNameProperty = userProperty.getRealNameProperty();
        String avatarProperty = userProperty.getAvatarProperty();

        if (StrUtil.isBlank(idProperty)) {
            return;
        }

        try {
            String[] idPathParts = idProperty.split("\\.");
            Object currentObj = obj;

            // 处理到倒数第二层
            for (int i = 0; i < idPathParts.length - 1; i++) {
                if (currentObj == null) {
                    return;
                }

                String propertyName = idPathParts[i];
                BeanWrapper wrapper = new BeanWrapperImpl(currentObj);

                if (!wrapper.isReadableProperty(propertyName)) {
                    return;
                }

                Object propertyValue = wrapper.getPropertyValue(propertyName);

                if (propertyValue instanceof Collection<?> collection) {
                    // 如果遇到集合，递归处理集合中的每个元素
                    String remainingIdPath = String.join(".", Arrays.copyOfRange(idPathParts, i + 1, idPathParts.length));
                    String remainingNamePath = StrUtil.isNotBlank(realNameProperty) ?
                            getRemainingPath(realNameProperty, i + 1) : null;
                    String remainingAvatarPath = StrUtil.isNotBlank(avatarProperty) ?
                            getRemainingPath(avatarProperty, i + 1) : null;

                    UserProperty remainingProperty = UserProperty.builder()
                            .idProperty(remainingIdPath)
                            .realNameProperty(remainingNamePath)
                            .avatarProperty(remainingAvatarPath)
                            .build();

                    collection.forEach(item -> setUserNameForProperty(item, remainingProperty, userMap));
                    return;
                } else {
                    currentObj = propertyValue;
                }
            }

            // 处理最后一层
            if (currentObj != null) {
                BeanWrapper wrapper = new BeanWrapperImpl(currentObj);
                String finalPropertyName = idPathParts[idPathParts.length - 1];

                if (wrapper.isReadableProperty(finalPropertyName)) {
                    Object idValue = wrapper.getPropertyValue(finalPropertyName);
                    String userId = parseUserId(idValue);

                    if (userId != null && userMap.containsKey(userId)) {
                        User userInfo = userMap.get(userId);

                        // 设置用户名
                        if (StrUtil.isNotBlank(realNameProperty)) {
                            String finalNameProperty = getLastPathPart(realNameProperty);
                            if (finalNameProperty != null && wrapper.isWritableProperty(finalNameProperty)) {
                                wrapper.setPropertyValue(finalNameProperty, userInfo.getUsername());
                            }
                        }

                        // 设置头像
                        if (StrUtil.isNotBlank(avatarProperty)) {
                            String finalAvatarProperty = getLastPathPart(avatarProperty);
                            if (finalAvatarProperty != null && wrapper.isWritableProperty(finalAvatarProperty)) {
                                // 由于RemoteUserVo可能没有getAvatar方法，暂时使用默认头像
                                wrapper.setPropertyValue(finalAvatarProperty, "默认头像");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("为属性 {} 设置用户信息时出错: {}", idProperty, e.getMessage());
        }
    }

    /**
     * 获取剩余路径
     */
    private static String getRemainingPath(String fullPath, int startIndex) {
        if (StrUtil.isBlank(fullPath)) {
            return null;
        }
        String[] parts = fullPath.split("\\.");
        if (startIndex >= parts.length) {
            return null;
        }
        return String.join(".", Arrays.copyOfRange(parts, startIndex, parts.length));
    }

    /**
     * 获取路径的最后一部分
     */
    private static String getLastPathPart(String path) {
        if (StrUtil.isBlank(path)) {
            return null;
        }
        String[] parts = path.split("\\.");
        return parts[parts.length - 1];
    }

    /**
     * 判断是否为简单类型（基本类型、包装类型、String、Date等）
     */
    private static boolean isSimpleType(Class<?> clazz) {
        if (clazz == null) {
            return true;
        }

        return ClassUtil.isSimpleTypeOrArray(clazz) ||
                ClassUtil.isBasicType(clazz) ||
                clazz == String.class ||
                clazz == Date.class ||
                java.sql.Date.class.isAssignableFrom(clazz) ||
                java.time.temporal.Temporal.class.isAssignableFrom(clazz) ||
                Number.class.isAssignableFrom(clazz) ||
                CharSequence.class.isAssignableFrom(clazz) ||
                Boolean.class == clazz ||
                Character.class == clazz ||
                clazz.isEnum();
    }

    /**
     * 为分页数据中的每个对象设置用户名
     *
     * @param data 分页数据对象
     * @param <T>  数据项的类型
     * @return 设置用户名后的分页数据
     */
    public static <T> Page<T> setUserName(Page<T> data) {
        return UserUtil.setUserName(data, null);
    }

    /**
     * 为列表数据中的每个对象设置用户名
     *
     * @param data 列表数据（包含多个数据项）
     * @param <T>  数据项的类型（泛型）
     * @return 设置用户名后的列表数据
     */
    public static <T> List<T> setUserName(List<T> data) {
        return UserUtil.setUserName(data, null);
    }

    /**
     * 为单个对象设置用户名
     *
     * @param data 单个数据对象
     * @param <T>  对象的类型（泛型）
     * @return 设置用户名后的对象
     */
    public static <T> T setUserName(T data) {
        return UserUtil.setUserName(data, null);
    }

    /**
     * 为分页数据中的每个对象设置用户名
     *
     * @param data          分页数据对象
     * @param <T>           数据项的类型
     * @param dicProperties 自定义映射的用户名属性
     * @return 设置用户名后的分页数据
     */
    public static <T> Page<T> setUserName(Page<T> data, List<UserProperty> dicProperties) {
        if (data == null || data.getContent().isEmpty()) {
            return data;
        }
        UserUtil.setUserName(data.getContent(), dicProperties);
        return data;
    }

    /**
     * 为单个对象设置用户名
     *
     * @param data          单个数据对象
     * @param <T>           对象的类型（泛型）
     * @param dicProperties 自定义映射的用户名属性
     * @return 设置用户名后的对象
     */
    public static <T> T setUserName(T data, List<UserProperty> dicProperties) {
        if (data == null) {
            return data;
        }
        List<T> result = UserUtil.setUserName(List.of(data), dicProperties);
        return result.isEmpty() ? data : result.get(0);
    }

    /**
     * 为列表数据中的每个对象设置用户名（支持嵌套对象和列表）
     *
     * @param data          列表数据
     * @param <T>           数据项的类型
     * @param dicProperties 自定义映射的用户名属性
     * @return 设置用户名后的列表数据
     */
    public static <T> List<T> setUserName(List<T> data, List<UserProperty> dicProperties) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        List<UserProperty> userProperties = Objects.requireNonNullElse(dicProperties, defaultUserProperties);
        if (CollectionUtil.isEmpty(userProperties)) {
            return data;
        }

        // 收集所有需要查询的用户ID
        Set<String> userIds = new HashSet<>();
        data.forEach(item -> collectUserIds(item, userProperties, userIds));

        if (CollectionUtil.isEmpty(userIds)) {
            return data;
        }

        // 批量查询用户信息
        Map<String, User> userMap = getUserMap(userIds);
        if (CollectionUtil.isEmpty(userMap)) {
            return data;
        }

        // 为每个对象设置用户名
        data.forEach(item -> setUserNameForObject(item, userProperties, userMap));

        return data;
    }

    /**
     * 递归收集所有用户ID
     */
    private static void collectUserIds(Object obj, List<UserProperty> userProperties, Set<String> userIds) {
        if (obj == null) {
            return;
        }

        // 如果是Collection类型，递归处理每个元素
        if (obj instanceof Collection<?> collection) {
            collection.forEach(item -> collectUserIds(item, userProperties, userIds));
            return;
        }

        // 如果是数组，递归处理每个元素
        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            Arrays.stream(array).forEach(item -> collectUserIds(item, userProperties, userIds));
            return;
        }

        // 如果是基本类型或包装类型，直接返回
        if (isSimpleType(obj.getClass())) {
            return;
        }

        try {
            BeanWrapper beanWrapper = new BeanWrapperImpl(obj);

            for (UserProperty userProperty : userProperties) {
                String idProperty = userProperty.getIdProperty();
                if (StrUtil.isBlank(idProperty)) {
                    continue;
                }

                // 处理嵌套属性，包括集合中的属性
                collectUserIdsFromProperty(obj, idProperty, userIds);
            }

            // 递归处理嵌套对象和集合属性
            Arrays.stream(beanWrapper.getPropertyDescriptors())
                    .filter(pd -> pd.getReadMethod() != null)
                    .forEach(pd -> {
                        try {
                            Object nestedValue = beanWrapper.getPropertyValue(pd.getName());
                            if (nestedValue != null && !isSimpleType(nestedValue.getClass())) {
                                collectUserIds(nestedValue, userProperties, userIds);
                            }
                        } catch (Exception e) {
                            log.debug("递归处理嵌套属性 {} 时出错: {}", pd.getName(), e.getMessage());
                        }
                    });

        } catch (Exception e) {
            log.debug("处理对象 {} 时出错: {}", obj.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 为对象设置用户名
     */
    private static void setUserNameForObject(Object obj, List<UserProperty> userProperties, Map<String, User> userMap) {
        if (obj == null || CollectionUtil.isEmpty(userMap)) {
            return;
        }

        // 如果是Collection类型，递归处理每个元素
        if (obj instanceof Collection<?> collection) {
            collection.forEach(item -> setUserNameForObject(item, userProperties, userMap));
            return;
        }

        // 如果是数组，递归处理每个元素
        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            Arrays.stream(array).forEach(item -> setUserNameForObject(item, userProperties, userMap));
            return;
        }

        // 如果是基本类型或包装类型，直接返回
        if (isSimpleType(obj.getClass())) {
            return;
        }

        try {
            BeanWrapper beanWrapper = new BeanWrapperImpl(obj);

            for (UserProperty userProperty : userProperties) {
                String idProperty = userProperty.getIdProperty();
                if (StrUtil.isBlank(idProperty)) {
                    continue;
                }
                // 处理嵌套属性和集合属性的用户名设置
                setUserNameForProperty(obj, userProperty, userMap);
            }
            // 递归处理嵌套对象和集合属性
            Arrays.stream(beanWrapper.getPropertyDescriptors())
                    .filter(pd -> pd.getReadMethod() != null)
                    .forEach(pd -> {
                        try {
                            Object nestedValue = beanWrapper.getPropertyValue(pd.getName());
                            if (nestedValue != null && !isSimpleType(nestedValue.getClass())) {
                                setUserNameForObject(nestedValue, userProperties, userMap);
                            }
                        } catch (Exception e) {
                            log.debug("递归设置嵌套属性 {} 时出错: {}", pd.getName(), e.getMessage());
                        }
                    });

        } catch (Exception e) {
            log.debug("为对象 {} 设置用户名时出错: {}", obj.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 解析用户ID
     */
    private static String parseUserId(Object idValue) {
        if (idValue == null) {
            return null;
        }

        String idStr = idValue.toString();
        if (StrUtil.isBlank(idStr) || !StrUtil.isNumeric(idStr)) {
            return null;
        }
        return idStr;
    }

    /**
     * 批量获取用户信息
     */
    private static Map<String, User> getUserMap(Set<String> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }

        try {
            UserService userService = SpringBeanUtils.getBean(UserService.class);
            List<User> userList = userService.findAllById(userIds);

            if (CollectionUtil.isEmpty(userList)) {
                return Collections.emptyMap();
            }

            return userList.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            User::getId,
                            Function.identity(),
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            log.error("批量获取用户信息失败: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    /**
     * 默认的用户属性配置
     */
    private final static List<UserProperty> defaultUserProperties = List.of(
            UserProperty.builder()
                    .idProperty("createdBy")
                    .realNameProperty("createdName")
                    .avatarProperty("createdAvatar")
                    .build(),
            UserProperty.builder()
                    .idProperty("updatedBy")
                    .realNameProperty("updatedName")
                    .avatarProperty("updatedAvatar")
                    .build()
    );

    /**
     * 用户属性配置类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProperty {
        /**
         * 用户ID属性路径（支持嵌套，如：user.id, department.manager.id）
         */
        private String idProperty;

        /**
         * 用户真实姓名属性路径（支持嵌套，如：user.name, department.manager.name）
         */
        private String realNameProperty;

        /**
         * 用户头像属性路径（支持嵌套，如：user.avatar, department.manager.avatar）
         */
        private String avatarProperty;
    }

}
