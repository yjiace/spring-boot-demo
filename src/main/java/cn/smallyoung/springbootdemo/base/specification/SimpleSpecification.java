package cn.smallyoung.springbootdemo.base.specification;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.PluralAttribute;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JPA动态查询条件的核心实现类
 * 负责将查询条件列表（SpecificationOperator）转换为JPA可执行的Predicate对象
 * <p>
 * 描述:
 * 创建SimpleSpecification来实现Specification接口，
 * 并且根据条件生成Specification对象，因为在最后查询的时候需要这个对象
 * SimpleSpecification是核心类型，
 * 用来根据条件生成Specification对象，这个SimpleSpecification直接存储了具体的查询条件。
 * @author smallyoung
 */
public class SimpleSpecification<T> implements Specification<T> {

    @Serial
    private static final long serialVersionUID = 9101383631123058909L;

    /**
     * 查询的条件列表，是一组列表
     */
    private final List<SpecificationOperator> operators;

    /**
     * 排序规则
     */
    private final Sort sort;


    /**
     * 构造方法：初始化查询条件和排序规则
     *
     * @param operators 查询条件列表（由SimpleSpecificationBuilder收集）
     * @param sort      排序规则
     */
    SimpleSpecification(List<SpecificationOperator> operators, Sort sort) {
        this.operators = operators;
        this.sort = sort;
    }


    /**
     * 将查询条件转换为JPA的Predicate对象，即SQL条件表达式
     *
     * @param root            实体的根路径（对应SQL中的FROM子句）
     * @param criteriaQuery   查询对象（可用于设置排序、去重等）
     * @param criteriaBuilder 条件构建器（用于创建等于、大于等条件）
     * @return 拼接后的Predicate对象（SQL条件），可为null（无查询条件）
     */
    @Override
    public Predicate toPredicate(
            @Nullable Root<T> root,
            @Nullable CriteriaQuery<?> criteriaQuery,
            @Nullable CriteriaBuilder criteriaBuilder
    ) {
        Predicate resultPre = null;

        if (operators != null) {
            // 按连接方式（AND/OR）分组条件，便于批量处理同类型连接的条件
            // 例如：将所有AND连接的条件归为一组，OR连接的条件归为另一组
            Map<String, List<SpecificationOperator>> map = operators.stream()
                    .filter(s -> s != null && s.getOperator() != null)
                    // 按join字段分组
                    .collect(Collectors.groupingBy(SpecificationOperator::getJoin));

            // 遍历每组条件，构建组内的Predicate
            for (Map.Entry<String, List<SpecificationOperator>> entry : map.entrySet()) {
                Predicate predicate = null;

                // 处理组内每个条件
                for (SpecificationOperator so : entry.getValue()) {
                    Predicate p = so.getOperator().getCheckValue().test(so.getValue()) ?
                            so.getOperator().getFun().apply(expression(root, criteriaQuery, so.getKey()), criteriaBuilder, so) : null;

                    if (p == null) {
                        continue;
                    }

                    // 按连接方式（AND/OR）拼接组内条件
                    if (entry.getKey().startsWith("AND")) {
                        predicate = (predicate != null && criteriaBuilder != null) ?
                                criteriaBuilder.and(predicate, p) : p;
                    } else if (entry.getKey().startsWith("OR")) {
                        predicate = (predicate != null && criteriaBuilder != null) ?
                                criteriaBuilder.or(predicate, p) : p;
                    }
                }

                // 组内条件拼接
                if (predicate != null) {
                    resultPre = ((resultPre != null && criteriaBuilder != null) ?
                            criteriaBuilder.and(resultPre, predicate) : predicate);
                }
            }
        }
        return resultPre;
    }


    /**
     * 获取实体字段的路径
     * 自动处理集合属性（@OneToMany）的JOIN操作，并根据排序规则决定是否去重
     *
     * @param root            实体的根路径
     * @param criteriaQuery   查询对象
     * @param key             字段名
     * @return 字段的Path对象
     */
    private Path<?> expression(
            @Nullable Root<T> root,
            @Nullable CriteriaQuery<?> criteriaQuery,
            String key
    ) {

        if (root == null) {
            return null;
        }

        // 字段路径
        Path<?> expression;
        // 是否需要去重
        boolean distinct = false;
        // 拆分字段名
        String[] names = key.split("\\.");
        // 决定是否需要去重
        boolean hasMany = (sort == null || sort.stream().anyMatch(s -> s.getProperty().contains(".")));

        // 处理第一个字段
        if (root.getModel().getAttribute(names[0]) instanceof PluralAttribute) {
            expression = root.join(names[0], JoinType.LEFT);
            if (!hasMany) {
                distinct = true;
            }
        } else {
            expression = root.get(names[0]);
        }

        // 处理剩余的关联字段
        for (int i = 1; i < names.length; i++) {
            if (expression.get(names[i]).getModel() instanceof PluralAttribute) {
                Join<?, ?> join = root.join(names[0], JoinType.LEFT);
                for (int j = 1; j < i; j++) {
                    join = join.join(names[j], JoinType.LEFT);
                }
                expression = join.join(names[i], JoinType.LEFT);
                if (!hasMany) {
                    distinct = true;
                }
            } else {
                expression = expression.get(names[i]);
            }
        }

        if (criteriaQuery != null) {
            criteriaQuery.distinct(distinct);
        }

        return expression;
    }
}
