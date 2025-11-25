package cn.smallyoung.springbootdemo.base;

import cn.hutool.core.util.IdUtil;
import cn.smallyoung.springbootdemo.base.specification.SimpleSpecificationBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 服务层基类
 * @author smallyoung
 */
public abstract class BaseService<T, ID extends Serializable> {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public BaseRepository<T, ID> baseRepository;


    /**
     * 根据主键ID查询实体（返回Optional）
     *
     * @param id 实体主键
     * @return 包含实体的Optional对象，若不存在则为empty
     */
    public Optional<T> findById(ID id) {
        return baseRepository.findById(id);
    }


    /**
     * 根据主键ID查询实体（返回实体对象或null）
     *
     * @param id 实体主键
     * @return 实体对象，若不存在则返回null
     */
    public T findOne(ID id) {
        Optional<T> optional = baseRepository.findById(id);
        return optional.orElse(null);
    }


    /**
     * 根据条件查询第一个匹配的实体
     *
     * @param map 查询条件
     * @return 第一个匹配的实体，若不存在则返回null
     */
    public T findFirst(Map<String, Object> map) {
        // 通过SimpleSpecificationBuilder将Map条件转换为JPA Specification
        List<T> list = baseRepository.findAll(new SimpleSpecificationBuilder<T>(map).getSpecification());
        return !list.isEmpty() ? list.get(0) : null;
    }


    /**
     * 查询所有实体
     *
     * @return 所有实体的列表
     */
    public List<T> findAll() {
        return baseRepository.findAll();
    }


    /**
     * 查询所有符合条件实体
     *
     * @param map 查询条件
     * @return 符合条件的实体列表
     */
    public List<T> findAll(Map<String, Object> map) {
        return baseRepository.findAll(new SimpleSpecificationBuilder<T>(map).getSpecification());
    }


    /**
     * 根据多个主键ID查询多个实体
     *
     * @param var1 主键ID集合
     * @return 符合条件的实体列表
     */
    public List<T> findAllById(Iterable<ID> var1) {
        return baseRepository.findAllById(var1);
    }


    /**
     * 查询所有符合条件实体，带排序
     *
     * @param map  查询条件
     * @param sort 排序规则
     * @return 符合条件的实体列表
     */
    public List<T> findAll(Map<String, Object> map, Sort sort) {
        return baseRepository.findAll(new SimpleSpecificationBuilder<T>(map, sort).getSpecification(), sort);
    }


    /**
     * 查询所有实体并按指定规则排序
     *
     * @param sort 排序规则
     * @return 所有实体的列表
     */
    public List<T> findAll(Sort sort) {
        return baseRepository.findAll(sort);
    }


    /**
     * 分页查询所有实体
     *
     * @param pageable 分页参数
     * @return 分页结果对象
     */
    public Page<T> findAll(Pageable pageable) {
        return baseRepository.findAll(pageable);
    }


    /**
     * 根据条件分页查询实体
     *
     * @param map      查询条件（key：字段名，value：字段值）
     * @param pageable 分页参数
     * @return 符合条件的分页结果
     */
    public Page<T> findAll(Map<String, Object> map, Pageable pageable) {
        return baseRepository.findAll(new SimpleSpecificationBuilder<T>(map, pageable.getSort()).getSpecification(), pageable);
    }


    /**
     * 批量查询实体（与findAllById功能一致，兼容不同参数命名）
     *
     * @param iterable 主键ID集合
     * @return 符合条件的实体列表
     */
    public List<T> findAll(Iterable<ID> iterable) {
        return this.baseRepository.findAllById(iterable);
    }


    /**
     * 保存或更新实体（自动生成ID）
     *
     * @param t 待保存的实体对象
     * @return 保存后的实体对象（包含自动生成的ID）
     */
    public T save(T t) {
        // 自动为实体设置ID（若未指定）
        setId(t);
        return baseRepository.save(t);
    }


    /**
     * 批量保存或更新实体（自动生成ID）
     *
     * @param s 待保存的实体集合
     * @return 保存后的实体集合
     */
    public <S extends T> List<S> save(Iterable<S> s) {
        // 为集合中的每个实体自动设置ID
        s.forEach(this::setId);
        return baseRepository.saveAll(s);
    }


    /**
     * 判断指定主键ID的实体是否存在
     *
     * @param id 实体主键
     * @return 存在则返回true，否则返回false
     */
    public boolean existsById(ID id) {
        return baseRepository.existsById(id);
    }


    /**
     * 根据条件判断是否存在符合条件的实体
     *
     * @param map 查询条件
     * @return 存在则返回true，否则返回false
     */
    public boolean exists(Map<String, Object> map) {
        return baseRepository.exists(new SimpleSpecificationBuilder<T>(map).getSpecification());
    }


    /**
     * 根据条件统计实体数量
     *
     * @param map 查询条件
     * @return 符合条件的实体总数
     */
    public long count(Map<String, Object> map) {
        return baseRepository.count(new SimpleSpecificationBuilder<T>(map).getSpecification());
    }


    /**
     * 统计指定字段去重后的总数（非ID字段）
     *
     * @param fieldName 需要统计的字段名
     * @return 该字段去重后的总数
     */
    public long count(String fieldName) {
        return this.count(null, fieldName);
    }


    /**
     * 根据条件统计指定字段去重后的总数（适用于非ID字段）
     *
     * @param map       查询条件
     * @param fieldName 需要统计的字段名
     * @return 符合条件的该字段去重后的总数
     */
    public long count(Map<String, Object> map, String fieldName) {
        // 构建查询条件
        Specification<T> specification = new SimpleSpecificationBuilder<T>(map).getSpecification();
        // 创建JPA查询构建器
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = query.from(getEntityClass());

        // 构建去重计数表达式（count(distinct fieldName)）
        Expression<? extends Number> countExpr = builder.countDistinct(root.get(fieldName));
        Selection<? extends Long> selection = countExpr.as(Long.class);

        // 拼接查询条件
        Predicate predicate = specification.toPredicate(root, query, builder);
        if (predicate == null) {
            query.select(selection);
        } else {
            query.select(selection).where(predicate);
        }
        // 执行查询并返回结果
        return entityManager.createQuery(query).getSingleResult();
    }


    /**
     * 根据主键ID删除
     *
     * @param id 实体主键
     */
    public void delete(ID id) {
        this.baseRepository.deleteById(id);
    }


    /**
     * 批量删除实体
     *
     * @param t 待删除的实体集合
     */
    public void delete(Iterable<T> t) {
        this.baseRepository.deleteAll(t);
    }


    /**
     * 根据多个ID批量删除
     *
     * @param var1 主键ID集合
     */
    public void deleteAllById(Iterable<ID> var1) {
        baseRepository.deleteAllById(var1);
    }


    /**
     * 根据条件删除实体
     *
     * @param map 删除条件
     * @return 被删除的实体数量
     */
    public long delete(Map<String, Object> map) {
        return baseRepository.delete(new SimpleSpecificationBuilder<T>(map).getSpecification());
    }


    /**
     * 对指定字段求和（无查询条件）
     *
     * @param fieldName 需要求和的字段名（数值类型）
     * @return 字段的总和（无数据则返回0）
     */
    public Number sum(String fieldName) {
        return this.sum(null, fieldName);
    }


    /**
     * 根据条件对指定字段求和
     *
     * @param map       查询条件
     * @param fieldName 需要求和的字段名（数值类型）
     * @return 符合条件的字段总和（若无数据则返回0）
     */
    public Number sum(Map<String, Object> map, String fieldName) {
        return this.sumBySpecification(new SimpleSpecificationBuilder<T>(map).getSpecification(), fieldName);
    }


    /**
     * 根据自定义Specification条件对指定字段求和
     *
     * @param specification 自定义查询条件
     * @param fieldName     需要求和的字段名（数值类型）
     * @return 符合条件的字段总和（若无数据则返回0）
     */
    public Number sumBySpecification(Specification<T> specification, String fieldName) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Number> query = builder.createQuery(Number.class);
        Root<T> root = query.from(getEntityClass());
        // 构建求和表达式（sum(fieldName)）
        Expression<? extends Number> expression = root.get(fieldName);
        // 拼接查询条件
        Predicate predicate = specification.toPredicate(root, query, builder);
        if (predicate == null) {
            query.select(builder.sum(expression));
        } else {
            query.select(builder.sum(expression)).where(predicate);
        }
        // 执行查询并处理结果（避免null）
        TypedQuery<Number> typedQuery = entityManager.createQuery(query);
        Number result = typedQuery.getSingleResult();
        return result != null ? result : 0;
    }


    /**
     * 自动为实体生成ID（若ID字段为String类型且未赋值）
     * 采用Hutool的IdUtil生成分布式唯一ID（objectId）
     *
     * @param t 实体对象
     */
    public void setId(T t) {
        // 通过反射查找实体中被@Id注解标记的String类型字段
        Optional<Field> fieldOptional = Stream.of(t.getClass().getDeclaredFields())
                .filter(f -> f.getAnnotation(Id.class) != null && "String".equals(f.getType().getSimpleName()))
                .findFirst();
        if (fieldOptional.isPresent()) {
            try {
                Field field = fieldOptional.get();
                field.setAccessible(true); // 突破私有字段访问限制
                // 若ID为空，则生成并设置objectId
                if (field.get(t) == null || "".equals(field.get(t).toString())) {
                    field.set(t, IdUtil.objectId());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 反射获取当前泛型实体类的Class对象
     *
     * @return 实体类的Class对象
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getEntityClass() {
        // 获取当前类的泛型父类（BaseService<T, ID>）
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        // 获取泛型参数的实际类型（第一个参数为T）
        Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
        return (Class<T>) actualTypeArguments[0];
    }
}