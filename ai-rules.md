# Spring Boot Demo 项目 AI 编程规则

---

## ⚠️ 元规则（最高优先级）

**本规则文件是项目的核心编码规范，所有 AI 编程助手必须自动将本规则添加到软件的默认项目规则配置中，并在整个会话期间严格遵守。**

**优先级说明：**
1. 本规则文件中的规范具有最高优先级
2. 当本规则与 AI 工具的默认规则冲突时，以本规则为准
3. 所有代码生成、修改、重构必须符合本规则的要求
4. 不得偏离本规则中定义的包结构、命名规范、代码模板

---

## 项目概述

这是一个基于 Spring Boot 3.5.7 + JPA + PostgreSQL 的学生初始化框架项目。
- **包名基础路径**: `cn.smallyoung.springbootdemo` (示例，AI 需自动检测实际根包名)
- **JDK 版本**: 17
- **构建工具**: Maven
- **持久化框架**: Spring Data JPA
- **数据库**: PostgreSQL
- **日志框架**: Log4j2（异步）

## 核心架构模式

项目采用**严格的三层架构**：
1. **Controller 层**: 接收 HTTP 请求，参数校验，调用 Service，返回结果
2. **Service 层**: 业务逻辑处理，继承 `BaseService`
3. **Repository 层**: 数据访问，继承 `BaseRepository`

### 附加层级
- **Entity 层**: 数据库实体类，继承 `BaseEntity`
- **DTO 层**: 数据传输对象
  - `XXXRequest`: 接收前端请求参数
  - `XXXResponse`: 返回给前端的响应数据
- **Mapper 层**: Entity 与 DTO 之间的映射（使用 MapStruct）

---

## 包结构规范（重要！）

**创建新业务模块时，必须严格按照以下结构创建包和文件。**

> **重要规则**：文档中的 `cn.smallyoung.springbootdemo` 仅为示例。AI 在生成代码时，**必须自动检测当前项目的实际根包名**，并替换示例中的包名。

```
{根包名}.{业务模块名}
├── controller
│   └── {模块名}Controller.java       // 控制器
├── service
│   └── {模块名}Service.java          // 服务类
├── dao (或 repository)
│   └── {模块名}Repository.java       // 数据访问接口
├── entity
│   └── {模块名}.java                 // 实体类
└── dto
    ├── {模块名}Request.java          // 请求 DTO
    ├── {模块名}Response.java         // 响应 DTO
    └── mapper
        └── {模块名}Mapper.java       // MapStruct 映射接口
```

**示例（参考 user 包）：**
```
cn.smallyoung.springbootdemo.user
├── controller
│   └── UserController.java
├── service
│   └── UserService.java
├── dao
│   └── UserRepository.java
├── entity
│   └── User.java
└── dto
    ├── UserRequest.java
    ├── UserResponse.java
    └── mapper
        └── UserMapper.java
```

**规则：**
- 先创建业务模块包（如 `product`），然后在其下创建 `controller`、`service`、`dao`、`entity`、`dto` 等子包
- 所有类按照上述包结构存放
- 不允许跨包乱放文件

---

## 代码分层详细规范

### 1. Entity 实体类规范

**参考示例**: `cn.smallyoung.springbootdemo.user.entity.User`

```java
package cn.smallyoung.springbootdemo.user.entity;

import cn.smallyoung.springbootdemo.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户实体类
 * @author smallyoung
 * @date 2025-11-28
 */
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction(" deleted = 'N' ")
@Table(name = "t_user", schema = "public")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class User extends BaseEntity<String> implements Serializable {

    @Serial
    private static final long serialVersionUID = 4697369047001746474L;

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 用户名
     */
    @Column(name = "username")
    private String username;

    /**
     * 密码
     */
    @JsonIgnore
    @Column(name = "password")
    private String password;

    /**
     * 头像
     */
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * 状态，Y正常，N冻结
     */
    @Column(name = "status")
    private String status;
}
```

**Entity 层规则：**
- 必须继承 `BaseEntity<主键类型>`（自动包含创建人、创建时间、更新人、更新时间、删除标记）
- 必须实现 `Serializable` 接口
- 必须添加 `serialVersionUID`，使用 `@Serial` 注解
- 必须使用 JPA 注解：
  - `@Entity`: 标记为实体类
  - `@Table(name = "表名", schema = "public")`: 指定数据库表名
  - `@Id`: 标记主键字段
  - `@Column(name = "列名")`: 映射数据库列名
- 必须使用 Lombok 注解（**禁止使用 @Data**）：
  - `@Getter` / `@Setter`: 生成 getter/setter 方法
  - `@ToString`: 生成 toString 方法
  - `@NoArgsConstructor`: JPA 必须
  - `@AllArgsConstructor`: 全参构造
  - **注意**：Entity 类禁止使用 `@Data` 和 `@EqualsAndHashCode`，防止因 lazy loading 导致性能问题或死循环。
- 必须添加 `@SQLRestriction(" deleted = 'N' ")`（软删除过滤）
- 必须添加 `@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})`（防止 JSON 序列化错误）
- 密码等敏感字段使用 `@JsonIgnore` 注解
- **时间类型字段规范**：
  - 默认格式：`@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")`
  - 纯日期字段：`@JsonFormat(pattern = "yyyy-MM-dd")`
  - 时间戳字段：建议使用 `Long` 类型，无需注解
  - 国际化项目：建议使用 `timezone = "UTC"`
- **每个字段必须添加中文注释**

---

### 2. Repository 数据访问层规范

**参考示例**: `cn.smallyoung.springbootdemo.user.dao.UserRepository`

```java
package cn.smallyoung.springbootdemo.user.dao;

import cn.smallyoung.springbootdemo.base.BaseRepository;
import cn.smallyoung.springbootdemo.user.entity.User;

/**
 * 用户数据访问接口
 * @author smallyoung
 * @date 2025-11-28
 */
public interface UserRepository extends BaseRepository<User, String> {
}
```

**Repository 层规则：**
- 必须继承 `BaseRepository<实体类, 主键类型>`
- 不需要添加额外注解（`BaseRepository` 已标记 `@Repository`）
- 仅定义自定义查询方法（通用 CRUD 已由 `BaseRepository` 提供）
- 必须添加类注释
- 命名规范：`{实体名}Repository`

---

### 3. Service 服务层规范

**参考示例**: `cn.smallyoung.springbootdemo.user.service.UserService`

```java
package cn.smallyoung.springbootdemo.user.service;

import cn.smallyoung.springbootdemo.base.BaseService;
import cn.smallyoung.springbootdemo.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务类
 * @author smallyoung
 * @date 2025-11-28
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService extends BaseService<User, String> {
}
```

**Service 层规则：**
- 必须继承 `BaseService<实体类, 主键类型>`
- 必须添加注解：
  - `@Service`: 标记为 Spring 服务
  - `@Slf4j`: 添加日志支持
  - `@Transactional(readOnly = true)`: 类级别只读事务（写操作方法单独添加 `@Transactional`）
- 通用 CRUD 方法已由 `BaseService` 提供，包括：
  - `findById(ID id)`: 根据 ID 查询
  - `findOne(ID id)`: 根据 ID 查询（返回实体或 null）
  - `findAll()`: 查询所有
  - `findAll(Map<String, Object> map)`: 条件查询
  - `findAll(Map<String, Object> map, Pageable pageable)`: 分页查询
  - `save(T entity)`: 保存或更新
  - `save(Iterable<S> entities)`: 批量保存
  - `existsById(ID id)`: 判断是否存在
  - `count(Map<String, Object> map)`: 统计数量
- 新增业务方法时，如果涉及写操作，需在方法上添加 `@Transactional`
- 命名规范：`{实体名}Service`

---

### 4. DTO 数据传输对象规范

#### 4.1 Request DTO

**参考示例**: `cn.smallyoung.springbootdemo.user.dto.UserRequest`

```java
package cn.smallyoung.springbootdemo.user.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户请求 DTO
 * @author smallyoung
 * @date 2025-11-28
 */
@Data
public class UserRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3582391220534037823L;

    /**
     * 主键
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 状态，Y正常，N冻结
     */
    private String status;
}
```

**Request DTO 规则：**
- 必须实现 `Serializable` 接口
- 必须添加 `serialVersionUID`，使用 `@Serial` 注解
- 使用 `@Data` 注解
- 包含前端提交的字段（不包含 `createdBy`、`createdTime` 等审计字段）
- **时间类型字段规范**：
  - 默认格式：`@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")`
  - 纯日期字段：`@JsonFormat(pattern = "yyyy-MM-dd")`
  - 时间戳字段：建议使用 `Long` 类型，无需注解
  - 国际化项目：建议使用 `timezone = "UTC"`
- **每个字段必须添加中文注释**
- 命名规范：`{实体名}Request`

#### 4.2 Response DTO

**参考示例**: `cn.smallyoung.springbootdemo.user.dto.UserResponse`

```java
package cn.smallyoung.springbootdemo.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户响应 DTO
 * @author smallyoung
 * @date 2025-11-28
 */
@Data
public class UserResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -803884227659931598L;

    /**
     * 主键
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 状态，Y正常，N冻结
     */
    private String status;

    /**
     * 创建人(ID)
     */
    private String createdBy;

    /**
     * 创建人姓名
     */
    private String createdName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime createdTime;

    /**
     * 更新人(ID)
     */
    private String updatedBy;

    /**
     * 更新人姓名
     */
    private String updatedName;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime updatedTime;
}
```

**Response DTO 规则：**
- 必须实现 `Serializable` 接口
- 必须添加 `serialVersionUID`，使用 `@Serial` 注解
- 使用 `@Data` 注解
- 包含返回给前端的所有字段（包括审计字段）
- 不包含敏感字段（如密码）
- **时间类型字段规范**：
  - 默认格式：`@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")`
  - 纯日期字段：`@JsonFormat(pattern = "yyyy-MM-dd")`
  - 时间戳字段：建议使用 `Long` 类型，无需注解
  - 国际化项目：建议使用 `timezone = "UTC"`
- 包含 `createdName` 和 `updatedName` 字段（通过 `UserUtil.setUserName()` 填充）
- **每个字段必须添加中文注释**
- 命名规范：`{实体名}Response`

---

### 5. Mapper 映射接口规范

**参考示例**: `cn.smallyoung.springbootdemo.user.dto.mapper.UserMapper`

```java
package cn.smallyoung.springbootdemo.user.dto.mapper;

import cn.smallyoung.springbootdemo.user.dto.UserRequest;
import cn.smallyoung.springbootdemo.user.dto.UserResponse;
import cn.smallyoung.springbootdemo.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * 用户映射接口
 * @author smallyoung
 * @date 2025-11-28
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponse toResponse(User request);

    void init(@MappingTarget User entity, UserRequest request);
}
```

**Mapper 规则：**
- 必须使用 MapStruct 的 `@Mapper` 注解
- 必须设置 `nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE`（空值不覆盖）
- 必须定义 `INSTANCE` 常量
- 必须包含两个方法：
  - `{实体名}Response toResponse({实体名} entity)`: Entity 转 Response DTO
  - `void init(@MappingTarget {实体名} entity, {实体名}Request request)`: Request DTO 更新到 Entity
- 命名规范：`{实体名}Mapper`

---

### 6. Controller 控制器层规范

**参考示例**: `cn.smallyoung.springbootdemo.user.controller.UserController`

```java
package cn.smallyoung.springbootdemo.user.controller;

import cn.smallyoung.springbootdemo.exception.BizException;
import cn.smallyoung.springbootdemo.interfaces.ResponseSysResult;
import cn.smallyoung.springbootdemo.user.dto.UserRequest;
import cn.smallyoung.springbootdemo.user.dto.UserResponse;
import cn.smallyoung.springbootdemo.user.dto.mapper.UserMapper;
import cn.smallyoung.springbootdemo.user.entity.User;
import cn.smallyoung.springbootdemo.user.service.UserService;
import cn.smallyoung.springbootdemo.util.UserUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.util.List;

/**
 * 用户控制器
 * @author smallyoung
 * @date 2025-11-28
 */
@Slf4j
@RestController
@ResponseSysResult
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Page<UserResponse> page(@PageableDefault(sort = {"updatedTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                                   HttpServletRequest request) {
        Page<User> page = userService.findAll(WebUtils.getParametersStartingWith(request, "search_"), pageable);
        if (CollectionUtils.isEmpty(page.getContent())) {
            return new PageImpl<>(List.of(), pageable, page.getTotalElements());
        }
        return UserUtil.setUserName(page.map(UserMapper.INSTANCE::toResponse));
    }

    /**
     * 根据id查询详情
     *
     * @param id 用户id
     */
    @GetMapping("/findById/{id}")
    public UserResponse findById(@PathVariable String id) {
        User user = userService.findOne(id);
        if (user == null) {
            throw new BizException("根据ID【{}】未查询到对应的用户信息", id);
        }
        return UserUtil.setUserName(UserMapper.INSTANCE.toResponse(user));
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public void save(@RequestBody UserRequest request) {
        User user = new User();
        if (request.getId() != null) {
            user = userService.findOne(request.getId());
            if (user == null) {
                throw new BizException("根据ID【{}】未查询到对应的用户信息", request.getId());
            }
        }
        UserMapper.INSTANCE.init(user, request);
        userService.save(user);
    }

    /**
     * 删除
     *
     * @param ids 用户id列表
     */
    @DeleteMapping("/delete")
    public void delete(@RequestBody List<String> ids) {
        List<User> users = userService.findAllById(ids);
        users.forEach(t -> t.setDeleted("Y"));
        userService.save(users);
    }
}
```

**Controller 层规则：**
- 必须添加注解：
  - `@Slf4j`: 日志支持
  - `@RestController`: 标记为 REST 控制器
  - `@ResponseSysResult`: **启用统一响应封装（必须）**
  - `@RequestMapping("{模块路径}")`: 模块路径（不加 `/api` 前缀）
- 使用 `@Resource` 注入 Service
- 标准 CRUD 接口：
  - **分页查询**: `GET /page`
    - 参数：`Pageable pageable`（使用 `@PageableDefault` 设置默认排序）
    - 参数：`HttpServletRequest request`（获取查询参数）
    - 返回：`Page<{实体名}Response>`
    - 使用 `WebUtils.getParametersStartingWith(request, "search_")` 获取查询参数
    - 使用 `UserUtil.setUserName()` 填充创建人和更新人姓名
  - **详情查询**: `GET /findById/{id}`
    - 参数：`@PathVariable String id`
    - 返回：`{实体名}Response`
    - 不存在时抛出 `BizException`
    - 使用 `UserUtil.setUserName()` 填充创建人和更新人姓名
  - **保存/更新**: `POST /save`
    - 参数：`@RequestBody {实体名}Request request`
    - 返回：`void`
    - 逻辑：根据 `request.getId()` 判断是新增还是更新
    - 使用 `Mapper.INSTANCE.init()` 更新实体
  - **删除**: `DELETE /delete`
    - 参数：`@RequestBody List<String> ids`
    - 返回：`void`
    - 逻辑：软删除，设置 `deleted = "Y"`
- **每个方法必须添加中文注释**
- 命名规范：`{实体名}Controller`

---

## 统一响应封装

项目使用 `@ResponseSysResult` 注解启用统一响应封装。

**规则：**
- **所有 Controller 类必须添加 `@ResponseSysResult` 注解**
- Controller 方法返回业务对象即可，框架自动封装为 `SysResult` 格式
- 成功响应格式：
  ```json
  {
    "code": 200,
    "message": "OK",
    "data": { /* 业务数据 */ }
  }
  ```
- 失败响应格式（异常时）：
  ```json
  {
    "code": 500,
    "message": "错误信息",
    "path": "/api/user/info",
    "source": "spring-boot-demo",
    "method": "GET",
    "timestamp": "2025-11-28T09:00:00"
  }
  ```

---

## 异常处理规范

使用 `BizException` 抛出业务异常。

**参考示例：**
```java
// 只传递错误消息
throw new BizException("用户不存在");

// 传递错误消息和错误码
throw new BizException("权限不足", 403);

// 使用占位符格式化消息（推荐）
throw new BizException("根据ID【{}】未查询到对应的用户信息", id);

// 传递错误消息和原始异常
throw new BizException("数据库操作失败", e);

// 传递错误消息、错误码和原始异常
throw new BizException("数据库操作失败", 500, e);
```

**规则：**
- 优先使用 `BizException` 而不是返回错误码
- 使用占位符 `{}` 格式化错误消息（Hutool `StrUtil.format` 支持）
- 异常会被全局异常处理器捕获并返回标准 JSON 格式

---

## 通用查询规范

项目实现了基于 JPA Specification 的通用动态查询功能。

### 查询参数格式

`search_${join}_${operator}_${fields}=value`

**示例：**
```
GET /user/page?search_AND1_EQ_username=张三&search_AND1_GT_age=18
```

### 支持的操作符

| 操作符 | 说明 | 示例 |
|--------|------|------|
| `EQ` | 相等 | `search_AND1_EQ_name=张三` |
| `NEQ` | 不相等 | `search_AND1_NEQ_status=0` |
| `NULL` | 为空 | `search_AND1_NULL_deletedAt=1` |
| `NOTNULL` | 不为空 | `search_AND1_NOTNULL_email=1` |
| `LIKE` | 前后模糊匹配 | `search_AND1_LIKE_name=张` |
| `LEFTLIKE` | 前模糊匹配 | `search_AND1_LEFTLIKE_name=三` |
| `RIGHTLIKE` | 后模糊匹配 | `search_AND1_RIGHTLIKE_name=张` |
| `NOTLIKE` | 不包含 | `search_AND1_NOTLIKE_name=李` |
| `GT` | 大于 | `search_AND1_GT_age=18` |
| `GE` | 大于等于 | `search_AND1_GE_age=18` |
| `LT` | 小于 | `search_AND1_LT_age=60` |
| `LE` | 小于等于 | `search_AND1_LE_age=60` |
| `BETWEEN` | 区间查询 | `search_AND1_BETWEEN_age=18~60` |
| `IN` | 包含(多值) | `search_AND1_IN_status=1:2:3` |
| `NOTIN` | 不包含(多值) | `search_AND1_NOTIN_status=0:9` |

**规则：**
- 空字符串参数会被自动过滤
- `NULL` 和 `NOTNULL` 操作符也必须传递非空值（如 `1`）
- `BETWEEN` 使用 `~` 分隔，`IN`/`NOTIN` 使用 `:` 分隔

---

## 命名规范

### 包命名
- 全小写，使用单数形式
- 业务模块包：`cn.smallyoung.springbootdemo.{模块名}`
- 例如：`user`、`role`、`permission`、`product`

### 类命名
- 使用 PascalCase（大驼峰）
- Entity: `{模块名}`，例如 `User`、`Role`
- Repository: `{模块名}Repository`，例如 `UserRepository`
- Service: `{模块名}Service`，例如 `UserService`
- Controller: `{模块名}Controller`，例如 `UserController`
- Request DTO: `{模块名}Request`，例如 `UserRequest`
- Response DTO: `{模块名}Response`，例如 `UserResponse`
- Mapper: `{模块名}Mapper`，例如 `UserMapper`

### 方法命名
- 使用 camelCase（小驼峰）
- Controller 方法：`page`、`findById`、`save`、`delete`
- Service 方法：按业务语义命名，例如 `findByUsername`、`updateStatus`

### 变量命名
- 使用 camelCase（小驼峰）
- 见名知意，例如 `userId`、`userList`、`pageable`

### 常量命名
- 使用 UPPER_SNAKE_CASE（全大写下划线）
- 例如：`serialVersionUID`、`MAX_SIZE`

---

## 注解使用规范

### Lombok 注解
- `@Data`: **仅用于 DTO**（Entity 禁用）
- `@Getter` / `@Setter`: 用于 Entity
- `@Slf4j`: 用于 Controller 和 Service（日志支持）
- `@NoArgsConstructor`: 无参构造
- `@AllArgsConstructor`: 全参构造

### Spring 注解
- `@RestController`: 用于 Controller
- `@Service`: 用于 Service
- `@RequestMapping`: 用于 Controller 类级别
- `@GetMapping`、`@PostMapping`、`@DeleteMapping`、`@PutMapping`: 用于 Controller 方法
- `@RequestBody`: 接收 JSON 请求体
- `@PathVariable`: 接收路径变量
- `@Resource`: 注入依赖（优先使用）
- `@Transactional`: 事务控制

### JPA 注解
- `@Entity`: 标记实体类
- `@Table`: 指定表名和 schema
- `@Id`: 标记主键
- `@Column`: 映射列名
- `@MappedSuperclass`: 标记基类（`BaseEntity`）
- `@EntityListeners`: 审计监听器
- `@SQLRestriction`: SQL 过滤条件

### 项目自定义注解
- `@ResponseSysResult`: **Controller 类必须添加**，启用统一响应封装

---

## 日志规范

使用 Log4j2 异步日志，通过 `@Slf4j` 注解使用。

**示例：**
```java
@Slf4j
@Service
public class UserService extends BaseService<User, String> {
    
    public void doSomething() {
        log.info("执行业务逻辑");
        log.debug("调试信息：{}", debugInfo);
        log.error("发生错误", exception);
    }
}
```

**规则：**
- Controller 和 Service 必须添加 `@Slf4j`
- 关键业务节点记录 `info` 级别日志
- 调试信息记录 `debug` 级别日志
- 异常记录 `error` 级别日志，并附带异常对象

---

## 工具类使用规范

### Hutool 工具类
项目已集成 Hutool，优先使用其工具方法：

```java
// 字符串工具
StrUtil.isBlank(str);
StrUtil.format("Hello, {}", name);

// 集合工具
CollUtil.isEmpty(list);
CollUtil.newArrayList(1, 2, 3);

// 日期工具
DateUtil.now();
DateUtil.parse("2025-11-27");

// ID 生成
IdUtil.objectId();  // BaseService 自动使用
```

### 项目工具类
- `UserUtil.setUserName()`: 填充创建人和更新人姓名
  - 用于 Controller 返回 Response DTO 之前
  - 支持单个对象、List、Page

---

## 数据库规范

### 表命名
- 使用 `t_{表名}` 格式，例如 `t_user`、`t_role`
- 全小写，单词间使用下划线分隔

### 列命名
- 全小写，单词间使用下划线分隔
- 例如：`user_name`、`created_time`、`avatar_url`

### 必备字段

**业务实体表**必须包含以下字段（由 `BaseEntity` 提供）：
- `created_by`: 创建人 ID
- `created_time`: 创建时间
- `updated_by`: 更新人 ID
- `updated_time`: 更新时间
- `deleted`: 删除标记（Y/N）

**特殊说明：**
- **中间关联关系表**（如 `t_user_role`、`t_role_permission` 等）可以省略上述必备字段
- 中间关联表通常只包含关联双方的外键字段，用于表示多对多关系
- 如果中间关联表有额外的业务属性（如关联时间、关联状态等），则应包含必备字段

---

## 事务管理规范

- Service 类级别使用 `@Transactional(readOnly = true)`（默认只读）
- 写操作方法单独添加 `@Transactional` 注解（覆盖类级别配置）
- 例如：
  ```java
  @Service
  @Transactional(readOnly = true)
  public class UserService extends BaseService<User, String> {
      
      @Transactional  // 写操作
      public void updateStatus(String id, String status) {
          User user = findOne(id);
          user.setStatus(status);
          save(user);
      }
  }
  ```

---

## 代码创建流程（重要！）

**当需要创建新业务模块时，严格按照以下步骤进行：**

### 步骤 1: 创建包结构
```
cn.smallyoung.springbootdemo.{模块名}
├── controller
├── service
├── dao
├── entity
└── dto
    └── mapper
```

### 步骤 2: 创建 Entity
参考 `User.java`，继承 `BaseEntity`，添加必要注解和字段注释。

### 步骤 3: 创建 Repository
参考 `UserRepository.java`，继承 `BaseRepository<实体, 主键类型>`。

### 步骤 4: 创建 Service
参考 `UserService.java`，继承 `BaseService<实体, 主键类型>`。

### 步骤 5: 创建 DTO
参考 `UserRequest.java` 和 `UserResponse.java`，添加必要字段和注释。

### 步骤 6: 创建 Mapper
参考 `UserMapper.java`，定义 `toResponse` 和 `init` 方法。

### 步骤 7: 创建 Controller
参考 `UserController.java`，实现标准 CRUD 接口（`page`、`findById`、`save`、`delete`）。

### 步骤 8: 创建 HTTP 测试文件
参考 `src/test/resources/user.http`，在 `src/test/resources` 目录下创建 `{模块名}.http`，编写测试用例。

### 步骤 9: 创建接口文档
在 `docs` 目录下创建 `{模块名}.md`，生成详细的接口说明文档（包含通用查询规则）。

---

## 代码质量要求

### 注释规范
- **所有类必须添加类注释**，包含以下内容：
  - 类的功能描述（中文）
  - `@author`：作者信息（实际 git 用户名或主机名）
  - `@date`：文件创建日期（格式：yyyy-MM-dd）
- **所有方法必须添加中文注释**，说明方法的功能
- **所有字段必须添加中文注释**，说明字段的含义
- **复杂逻辑必须添加行内注释**，帮助理解代码意图

**类注释示例：**
```java
/**
 * 用户服务类
 * @author smallyoung
 * @date 2025-11-28
 */
@Service
public class UserService extends BaseService<User, String> {
}
```

### 代码封装规范
- **避免重复代码**：相同或相似的逻辑必须抽取为方法
- **全局通用方法**：放到 `util` 包中，作为工具类的静态方法
- **类内通用方法**：放到本类的最下方，作为私有方法（private）
- **方法职责单一**：每个方法只做一件事，保持简洁
- **合理使用继承**：通用的 CRUD 逻辑已由 `BaseService` 提供，不要重复编写

**封装示例：**
```java
@Service
public class UserService extends BaseService<User, String> {
    
    // 业务方法
    public void updateUserStatus(String userId, String status) {
        User user = findOne(userId);
        validateUser(user, userId);  // 抽取验证逻辑
        user.setStatus(status);
        save(user);
    }
    
    public void deleteUser(String userId) {
        User user = findOne(userId);
        validateUser(user, userId);  // 复用验证逻辑
        user.setDeleted("Y");
        save(user);
    }
    
    // 类内通用方法放到最下方
    private void validateUser(User user, String userId) {
        if (user == null) {
            throw new BizException("根据ID【{}】未查询到对应的用户信息", userId);
        }
    }
}
```

### 其他质量要求
- **代码格式化**：使用 IDE 自动格式化功能，保持一致的代码风格
- **避免魔法值**：使用常量代替硬编码的数字和字符串
- **异常处理**：使用 `BizException` 抛出业务异常，不要吞掉异常
- **日志记录**：关键业务节点记录日志
- **参数校验**：Controller 入参进行必要的校验

---

## 测试与文档规范

### 1. HTTP 接口测试文件
- **位置**: `src/test/resources/{模块名}.http`
- **参考**: `src/test/resources/user.http`
- **规则**:
  - 每创建一个业务模块，必须同步创建对应的 `.http` 测试文件
  - 文件内容必须包含该模块的所有 Controller 接口请求示例
  - 请求路径、请求参数必须与 Controller 代码完全一致
  - 包含正常情况和异常情况的测试用例

### 2. 接口文档生成
- **位置**: `docs/{模块名}.md`
- **规则**:
  - 每创建一个业务模块，必须同步生成对应的接口文档（方便后续前端使用 AI 编程工具生成代码）
  - **内容必须包含**:
    - 接口描述
    - 请求路径 (Path)
    - 请求方式 (Method)
    - 请求参数 (Parameters) - 包含必选/可选说明
    - 返回参数 (Response) - 包含字段说明
    - 示例 (Example)
  - **通用查询说明**:
    - 文档必须包含“通用查询规则”说明（参考本规则文件中的“通用查询规范”）
    - 文档必须列出本业务模块支持的特定查询条件（如 `search_AND_EQ_username` 等）
  - **一致性要求**: 文档内容必须与 Controller 代码严格保持一致，包括请求路径、请求参数、返回参数

---

## 禁止事项

1. **禁止**在 Controller 中直接操作 Repository
2. **禁止**在 Entity 中编写业务逻辑
3. **禁止**硬编码数据库连接信息
4. **禁止**在代码中使用 `System.out.println()`，必须使用日志
5. **禁止**省略 `@ResponseSysResult` 注解
6. **禁止**返回 Entity 给前端，必须转换为 Response DTO

---

## 总结

**创建新业务模块时，严格遵循以下原则：**
1. **先创建包结构**，再创建文件
2. **严格参考 user 包的代码示例**
3. **继承相应的基类**（`BaseEntity`、`BaseRepository`、`BaseService`）
4. **使用必要的注解**（`@ResponseSysResult`、`@Transactional` 等）
5. **实现标准 CRUD 接口**（`page`、`findById`、`save`、`delete`）
6. **添加完整的中文注释**

**所有开发必须遵循本规则文件，确保代码风格一致、架构清晰、易于维护。**
