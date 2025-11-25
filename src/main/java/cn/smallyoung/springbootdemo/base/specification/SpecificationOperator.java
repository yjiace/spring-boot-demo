package cn.smallyoung.springbootdemo.base.specification;

import cn.hutool.core.convert.Convert;
import cn.smallyoung.springbootdemo.base.funcion.Function3Parameter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**。
 * 动态查询条件操作符类
 * 构建动态查询条件，统一管理查询操作符，封装了常见的查询操作
 *
 * @author smallyoung
 */
@Getter
public class SpecificationOperator {

    /**
     * 区间查询分隔符
     */
    private final static String BETWEEN_SEPARATOR = "~";

    /**
     * 分割多个值
     */
    private final static String IN_SEPARATOR = ":";

    /**
     * 查询字段名，如查询时的name,id之类
     */
    private final String key;

    /**
     * 操作符的value，具体要查询的值
     */
    private final Object value;

    /**
     * 查询操作符，自己定义的一组操作符，用来方便查询
     */
    private final Operator operator;

    /**
     * 条件连接方式：and或者or
     */
    public String join;


    /**
     * 构造方法，创建一个查询条件操作符实例
     *
     * @param key      查询字段名
     * @param value    查询值
     * @param operator 操作符
     * @param join     连接方式（and/or）
     */
    public SpecificationOperator(String key, Object value, Operator operator, String join) {
        this.key = key;
        this.value = value;
        this.operator = operator;
        this.join = join;
    }


    /**
     * 将字符串转换为对应的Operator枚举（默认返回EQ）
     *
     * @param data 字符串形式的操作符（如"EQ"、"LIKE"）
     * @return 对应的Operator枚举，若未匹配则返回EQ
     */
    public static Operator stringToOperator(String data) {
        return Stream.of(Operator.values()).filter(operator -> operator.name().equals(data)).findFirst().orElse(Operator.EQ);
    }


    /**
     * 将字符串转换为对应的Operator枚举（可指定默认值）
     *
     * @param data           字符串形式的操作符
     * @param defaultOperator 匹配失败时的默认操作符
     * @return 对应的Operator枚举，若未匹配则返回默认值
     */
    public static Operator stringToOperator(String data, Operator defaultOperator) {
        return Stream.of(Operator.values()).filter(operator -> operator.name().equals(data)).findFirst().orElse(defaultOperator);
    }


    /**
     * 定义所有支持的查询操作符，每个操作符包含参数校验规则和条件构建逻辑
     */
    @Getter
    public enum Operator {
        /**
         * 相等,字段值与查询值完全匹配
         */
        EQ(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.equal(expression, so.getValue())),
        /**
         * 不相等
         */
        NEQ(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.notEqual(expression, so.getValue())),
        /**
         * 空
         */
        NULL(o -> true, (expression, criteriaBuilder, so) -> criteriaBuilder.isNull(expression)),
        /**
         * 非空
         */
        NOTNULL(o -> true, (expression, criteriaBuilder, so) -> criteriaBuilder.isNotNull(expression)),
        /**
         * 模糊匹配，字段值包含查询值
         **/
        LIKE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.like(expression, "%" + so.getValue() + "%")),
        /**
         * 左模糊匹配，字段值以查询值结尾
         **/
        LEFTLIKE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.like(expression, "%" + so.getValue())),
        /**
         * 右模糊匹配，字段值以查询值开头
         **/
        RIGHTLIKE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.like(expression, so.getValue() + "%")),
        /**
         * 模糊查询 非，字段值不包含查询值
         */
        NOTLIKE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.notLike(expression, "%" + so.getValue() + "%")),
        /**
         * 大于
         **/
        GT(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.greaterThan(expression, (Comparable) Convert.convert(expression.getJavaType(), so.getValue()))),
        /**
         * 大于等于
         **/
        GE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.greaterThanOrEqualTo(expression, (Comparable) Convert.convert(expression.getJavaType(), so.getValue()))),
        /**
         * 小于
         **/
        LT(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.lessThan(expression, (Comparable) Convert.convert(expression.getJavaType(), so.getValue()))),
        /**
         * 小于等于
         **/
        LE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.lessThanOrEqualTo(expression, (Comparable) Convert.convert(expression.getJavaType(), so.getValue()))),
        /**
         * 区间
         **/
        BETWEEN(
                o -> !"".equals(o),
                (expression, criteriaBuilder, so) -> {
                    if (so.getValue() instanceof Collection) {
                        // 处理Collection类型的参数（如List）
                        List<?> list = Convert.convert(List.class, so.getValue());
                        return criteriaBuilder.between(expression, (Comparable) list.get(0), (Comparable) list.get(1));
                    } else if (String.valueOf(so.getValue()).split(BETWEEN_SEPARATOR).length == 2) {
                        // 处理字符串类型的参数（用~分隔）
                        String values = so.getValue().toString();
                        Class<?> clazz = expression.getJavaType();  // 获取字段的Java类型
                        // 分割并转换起始值和结束值
                        Object start = Convert.convert(clazz, values.split(BETWEEN_SEPARATOR)[0].trim());
                        Object end = Convert.convert(clazz, values.split(BETWEEN_SEPARATOR)[1].trim());
                        return criteriaBuilder.between(expression, (Comparable) start, (Comparable) end);
                    }
                    return null;  // 参数格式错误时返回null（不添加该条件）
                }
        ),
        /**
         * 包含
         */
        IN(o -> !"".equals(o),
                (expression, criteriaBuilder, so) -> {
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(expression);
                    if (so.getValue() instanceof Collection) {
                        // 处理Collection类型的参数
                        Iterator<?> iterator = ((Collection<?>) so.getValue()).iterator();
                        while (iterator.hasNext()) {
                            in.value(iterator.next());
                        }
                    } else if (so.getValue() instanceof String) {
                        // 处理字符串类型的参数（用:分隔）
                        String inValues = so.getValue().toString();
                        Class<?> clazz = expression.getJavaType();  // 获取字段的Java类型
                        for (String str : inValues.split(IN_SEPARATOR)) {
                            in.value(Convert.convert(clazz, str));  // 转换每个值的类型
                        }
                    }
                    return in;
                }
        ),

        /**
         * 不包含
         */
        NOTIN(o -> !"".equals(o), (expression, criteriaBuilder, so) -> {
            CriteriaBuilder.In in = criteriaBuilder.in(expression);
            if (so.getValue() instanceof Collection) {
                Iterator iterator = ((Collection) so.getValue()).iterator();
                while (iterator.hasNext()) {
                    in.value(iterator.next());
                }
            } else if (so.getValue() instanceof String) {
                String inValues = so.getValue().toString();
                Class<?> clazz = expression.getJavaType();
                for (String str : inValues.split(IN_SEPARATOR)) {
                    in.value(Convert.convert(clazz, str));
                }
            }
            return in.not();
        }),

        /**
         * JSON字段包含指定值（对JSON数组或对象进行搜索）
         * 例如：WHERE JSON_CONTAINS(participants, '1001') 或 WHERE JSON_CONTAINS(tags, '"spring"')
         */
        JSONCONTAIN(o -> !"".equals(o), (expression, criteriaBuilder, so) -> {
            Object value = so.getValue();
            if (value == null || "".equals(value.toString().trim())) {
                return null; // 如果没有有效值，不生成条件
            }

            // 将值转换为字符串并按逗号分割
            String[] valuesToSearch = String.valueOf(value).split(",");

            List<Predicate> predicates = new ArrayList<>();

            for (String singleValueStr : valuesToSearch) {
                singleValueStr = singleValueStr.trim(); // 去除前后空格
                if (singleValueStr.isEmpty()) {
                    continue; // 跳过空字符串
                }

                String jsonLiteralToSearch;

                // 尝试将字符串转换为数字，如果成功，则按数字处理
                // 否则，按字符串处理（用双引号包裹）
                try {
                    // 尝试解析为Long或Double，如果成功，则视为数字
                    // 注意：这里需要根据实际JSON中可能存储的数字类型进行调整
                    // 例如，如果只存整数ID，可以只尝试Long
                    if (singleValueStr.contains(".") || singleValueStr.contains("e") || singleValueStr.contains("E")) {
                        Double.parseDouble(singleValueStr);
                    } else {
                        Long.parseLong(singleValueStr);
                    }
                    jsonLiteralToSearch = singleValueStr; // 这是一个数字，直接作为JSON字面量传入
                } catch (NumberFormatException e) {
                    // 如果不是有效数字，则按JSON字符串处理，需要双引号包裹并转义
                    jsonLiteralToSearch = "\"" + singleValueStr.replace("\"", "\\\"") + "\"";
                }

                // 调用MySQL的JSON_CONTAINS函数，它返回一个Expression<Boolean>
                Expression<Boolean> jsonContainsExpression = criteriaBuilder.function(
                        "JSON_CONTAINS",
                        Boolean.class, // JSON_CONTAINS在MySQL中返回值通常映射为布尔值（1或0）
                        expression, // 第一个参数是JSON字段的Path
                        criteriaBuilder.literal(jsonLiteralToSearch) // 第二个参数是作为JSON字面量传入的搜索值
                );

                // 将 Expression<Boolean> 转换为 Predicate
                predicates.add(criteriaBuilder.isTrue(jsonContainsExpression));
            }

            if (predicates.isEmpty()) {
                return null; // 如果经过处理后没有有效的子条件，则返回null
            }

            // 使用 OR 连接所有生成的 Predicate
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        });


        /**
         * 查询值是否有效
         */
        private final java.util.function.Predicate<Object> checkValue;

        /**
         * 条件构建函数
         * 泛型参数：Path（字段路径）、CriteriaBuilder（JPA条件构建器）、SpecificationOperator（当前操作符实例）
         * 返回值：构建好的Predicate条件
         */
        private final Function3Parameter<Path, CriteriaBuilder, SpecificationOperator, Predicate> fun;

        /**
         * 枚举构造方法
         *
         * @param checkValue 参数校验器
         * @param fun        条件构建函数
         */
        Operator(java.util.function.Predicate<Object> checkValue, Function3Parameter<Path, CriteriaBuilder, SpecificationOperator, Predicate> fun) {
            this.checkValue = checkValue;
            this.fun = fun;
        }
    }
}
