package cn.smallyoung.springbootdemo.base.specification;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 动态查询条件构建器，用于将查询参数转换为JPA Specification对象
 * 支持通过Map参数自动解析查询条件，并生成可执行的动态查询规则
 *
 * @author smallyoung
 */
public class SimpleSpecificationBuilder<T> {

    /**
     * 条件列表
     */
    private final List<SpecificationOperator> operators;

    /**
     * 排序规则
     */
    private Sort sort;

    /**
     * 构造方法，根据Map参数和排序规则初始化查询条件
     * 根据Map封装Specification
     * Map键的约定格式：`join_operator_key`（用下划线分隔）
     * - join：连接方式（AND/OR，不区分大小写）
     * - operator：操作符（如EQ、LIKE、IN等，对应SpecificationOperator.Operator枚举）
     * - key：查询字段名（对应实体类的属性）
     *
     * @param map  包含查询条件的Map，键需遵循约定格式
     * @param sort 排序规则
     */
    public SimpleSpecificationBuilder(Map<String, Object> map, Sort sort) {
        // 调用另一个构造方法解析Map参数
        this(map);
        this.sort = sort;
    }

    /**
     * 构造方法：仅根据Map参数初始化查询条件
     * 自动解析Map中的键值对，转换为SpecificationOperator条件列表
     *
     * @param map 包含查询条件的Map，键需遵循上述格式
     */
    public SimpleSpecificationBuilder(Map<String, Object> map) {
        operators = new ArrayList<>();
        if (map == null) {
            return;
        }
        // 解析Map中的每个键值对
        // 键的格式需拆分为3部分：join、operator、key
        int keyLength = 3;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // 获取Map的键，并按下划线分割
            String[] keys = entry.getKey().split("_", 3);
            if (keys.length == keyLength) {
                this.add(keys[2], map.get(entry.getKey()), SpecificationOperator.stringToOperator(keys[1]), keys[0]);
            }
        }
    }


    /**
     * 手动往list中填加条件
     *
     * @param key      键
     * @param operator 查询条件，如EQ
     * @param value    查询的值
     * @param join     查询方式 ，包括AND、OR
     * @return SimpleSpecificationBuilder实例
     */
    public SimpleSpecificationBuilder<T> add(String key, Object value, SpecificationOperator.Operator operator, String join) {
        SpecificationOperator so = new SpecificationOperator(key, value, operator, join);
        operators.add(so);
        return this;
    }


    /**
     * 生成最终的JPA Specification对象
     * 将条件列表和排序规则封装为可执行的动态查询规则
     *
     * @return 用于JPA查询的Specification对象
     */
    public Specification<T> getSpecification() {
        // 委托给SimpleSpecification类处理条件拼接和查询生成
        return new SimpleSpecification<>(operators, sort);
    }
}
