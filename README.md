# Spring Boot Demo 学生初始化框架

这是一个 Spring Boot 初始化项目框架，集成了常用的工具和规范，旨在帮助学生快速上手 Spring Boot 开发。本项目提供了统一响应封装、全局异常处理等开箱即用的功能，让开发者专注于业务逻辑的实现。

## 🤖 致 AI 编程助手

[![AI Rules](https://img.shields.io/badge/AI-Read%20Rules%20First-orange?style=for-the-badge)](ai-rules.md)


**重要指令：**
本项目根目录下包含核心规则文件 `ai-rules.md`。
无论你当前运行在何种 IDE 或 Agent 环境中（如 Cursor, Trae, Windsurf, Antigravity 等），**请务必优先读取并遵循 `ai-rules.md` 文件中的所有规范和指令**。
该文件定义了本项目的代码风格、架构规范和最佳实践，是本项目开发的最高准则。

**确认要求：**
如果你已成功读取 `ai-rules.md` 文件，请在首次交互时明确回复：**"已读取项目规则"**。

## 🛠 项目环境

- **JDK**: 17
- **Spring Boot**: 3.5.7
- **构建工具**: Maven
- **包名**: `cn.smallyoung.springbootdemo`

## 🌿 分支管理

本项目采用分支管理策略，不同分支提供不同级别的功能支持。**请根据您的项目需求选择合适的分支进行开发。**

| 分支名称              | 功能特性                              | 适用场景                       | 基于分支 |
|-------------------|-----------------------------------|----------------------------|----------|
| `main`            | 基础框架 + 通用工具方法                     | 学习 Spring Boot 基础、快速搭建简单应用 | - |
| `main-postgresql` | `main` + PostgreSQL 数据库 + 通用查询逻辑  | 需要数据库持久化的应用开发              | `main` |
| `main-security`   | `main-postgresql` + JWT 鉴权 + 权限管理 | 需要用户认证和权限控制的企业级应用          | `main-postgresql` |
| `main-wechat`     | `main` + 微信小程序登录 + 微信小程序获取手机号     | 需要微信登录或获取手机号的应用场景          | `main` |

### 分支切换

```bash
# 切换到 PostgreSQL 数据库分支
git checkout main-postgresql

# 切换到权限管理分支
git checkout main-security

# 返回主分支
git checkout main
```

### 功能对比

| 功能模块 | main | main-postgresql | main-security | main-wechat |
|---------|:----:|:---------------:|:-------------:|:-----------:|
| 统一响应封装 | ✅ | ✅ | ✅ | ✅ |
| 全局异常处理 | ✅ | ✅ | ✅ | ✅ |
| 异步日志配置 | ✅ | ✅ | ✅ | ✅ |
| PostgreSQL 集成 | ❌ | ✅ | ✅ | ❌ |
| JPA 通用查询 | ❌ | ✅ | ✅ | ❌ |
| JPA 审计功能 | ❌ | ✅ | ✅ | ❌ |
| JWT 认证 | ❌ | ❌ | ✅ | ❌ |
| 权限管理 | ❌ | ❌ | ✅ | ❌ |
| 单点登录 (SSO) | ❌ | ❌ | ✅ | ❌ |
| 微信小程序登录 | ❌ | ❌ | ❌ | ✅ |
| 微信小程序手机号 | ❌ | ❌ | ❌ | ✅ |


## 📦 主要依赖

项目已经集成了以下常用依赖，无需重复添加:

| 依赖 | 版本 | 说明 |
|------|------|------|
| `spring-boot-starter-web` | 3.5.7 | 提供 Web 开发支持 |
| `spring-boot-starter-data-jpa` | 3.5.7 | JPA 数据访问支持 |
| `postgresql` | - | PostgreSQL 数据库驱动 |
| `lombok` | - | 简化 Java 代码，减少样板代码 |
| `hutool-all` | 5.8.41 | 强大的 Java 工具包 |
| `fastjson2` | 2.0.52 | 高性能 JSON 处理库 |
| `mapstruct` | 1.6.2 | Java Bean 映射工具 |
| `spring-boot-starter-log4j2` | 3.5.7 | 日志框架（已排除默认的 Logback） |
| `log4jdbc` | 1.2 | SQL 日志打印工具 |

## 📂 项目结构

```
cn.smallyoung.springbootdemo
├── base/                              // 基础模块
│   ├── BaseEntity.java                // 基础实体类（包含 id、创建时间、更新时间等）
│   ├── BaseRepository.java            // 基础 Repository 接口
│   ├── BaseService.java               // 基础 Service 类（包含通用 CRUD 和分页查询）
│   ├── funcion/
│   │   └── Function3Parameter.java    // 三参数函数式接口
│   └── specification/                 // JPA 动态查询规范
│       ├── SimpleSpecification.java
│       ├── SimpleSpecificationBuilder.java
│       └── SpecificationOperator.java
├── component/
│   └── ResponseSysResultAdvice.java   // 全局统一响应处理和异常拦截
├── exception/
│   └── BizException.java              // 自定义业务异常
├── interfaces/
│   └── ResponseSysResult.java         // 统一响应注解
├── user/                              // 用户模块（示例业务模块）
│   ├── controller/                    // 控制器层
│   ├── dao/                           // 数据访问层
│   ├── dto/                           // 数据传输对象
│   ├── entity/                        // 实体类
│   └── service/                       // 服务层
├── util/
│   ├── SpringBeanUtils.java           // Spring Bean 工具类
│   ├── SysResult.java                 // 统一响应结果实体类
│   └── UserUtil.java                  // 用户工具类
└── SpringBootDemoApplication.java     // 启动类
```

**配置文件：**
- `application.yaml` - 主配置文件
- `log4j2.xml` - Log4j2 日志配置文件
- `sql/` - 数据库初始化脚本目录

## ✨ 核心特性

### 1. 统一响应封装

项目实现了基于注解的自动统一响应封装机制。只需在 Controller 类或方法上添加 `@ResponseSysResult` 注解，框架就会自动将返回值包装为标准的 `SysResult` 格式。

#### `@ResponseSysResult` 注解说明

`@ResponseSysResult` 是一个自定义注解，用于标记需要进行统一响应封装的 Controller 类或方法。

**注解定义：**
```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ResponseBody
public @interface ResponseSysResult {
}
```

**使用方式：**

1. **类级别使用**：在 Controller 类上添加 `@ResponseSysResult`，该类中的所有方法都会自动进行响应封装。

```java
@RestController
@RequestMapping("/api/user")
@ResponseSysResult  // 类级别注解，所有方法都会被封装
public class UserController {
    
    @GetMapping("/info")
    public User getUserInfo() {
        // 直接返回 User 对象，框架会自动封装为 SysResult
        return new User("张三", 20);
    }
}
```

2. **方法级别使用**：在特定方法上添加 `@ResponseSysResult`，只有该方法会进行响应封装。

```java
@RestController
@RequestMapping("/api/product")
public class ProductController {
    
    @GetMapping("/list")
    @ResponseSysResult  // 方法级别注解，只有该方法会被封装
    public List<Product> getProductList() {
        return productService.findAll();
    }
    
    @GetMapping("/raw")
    public String getRawData() {
        // 没有 @ResponseSysResult 注解，不会被封装
        return "原始数据";
    }
}
```

**工作原理：**

`ResponseSysResultAdvice` 类实现了 `ResponseBodyAdvice` 接口，通过 `supports()` 方法判断是否需要进行响应封装：

```java
@Override
public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    // 检查类或方法上是否有 @ResponseSysResult 注解
    return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ANNOTATION_TYPE) 
        || returnType.hasMethodAnnotation(ANNOTATION_TYPE);
}
```

只有标记了 `@ResponseSysResult` 注解的 Controller 类或方法，才会触发 `beforeBodyWrite()` 方法进行响应封装。

#### 响应格式

**成功响应示例：**
```json
{
  "code": 200,
  "message": "OK",
  "data": {
    "name": "张三",
    "age": 20
  }
}
```

**失败响应示例（异常时）：**
```json
{
  "code": 500,
  "message": "业务异常信息",
  "path": "/api/user/info",
  "source": "spring-boot-demo",
  "method": "GET",
  "timestamp": "2025-11-27T10:30:00"
}
```

#### `SysResult` 类说明

`SysResult` 是统一响应结果的封装类，提供了以下静态方法：

- `SysResult.success(T data)`: 返回成功响应（code=200）
- `SysResult.result(HttpStatus status, T data)`: 自定义状态码返回
- `SysResult.failure(String msg)`: 返回失败响应（code=500）

**字段说明：**
- `code`: HTTP 状态码
- `message`: 响应消息
- `data`: 响应数据（泛型，可为任意类型）

**特殊处理：**
- 如果 Controller 方法返回 `String` 类型，会自动转换为 JSON 字符串
- 如果返回值已经是 `SysResult` 类型，则不会重复封装

### 2. 全局异常处理

项目集成了全局异常处理机制，可以优雅地处理业务异常和系统异常。

#### `BizException` 业务异常类

自定义业务异常类，支持多种构造方式：

```java
// 只传递错误消息
throw new BizException("用户不存在");

// 传递错误消息和错误码
throw new BizException("权限不足", 403);

// 使用占位符格式化消息
throw new BizException("用户 {} 不存在", userId);

// 传递错误消息和原始异常
throw new BizException("数据库操作失败", e);

// 传递错误消息、错误码和原始异常
throw new BizException("数据库操作失败", 500, e);
```

#### 异常拦截机制

`ResponseSysResultAdvice` 中的 `@ExceptionHandler` 会统一捕获所有异常，并返回标准 JSON 格式：

```java
@ExceptionHandler(value = Exception.class)
public Map<String, Object> handler(HttpServletRequest request, Exception e) {
    int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
    if (e instanceof BizException bizException && bizException.getCode() != null) {
        code = bizException.getCode();
    }
    return Dict.create()
        .set("code", code)
        .set("message", e.getMessage())
        .set("path", request.getRequestURI())
        .set("source", applicationName)
        .set("method", request.getMethod())
        .set("timestamp", LocalDateTime.now());
}
```

**异常响应包含以下信息：**
- `code`: 错误码（BizException 可自定义，默认 500）
- `message`: 错误消息
- `path`: 请求路径
- `source`: 应用名称（来自 `spring.application.name` 配置）
- `method`: 请求方法（GET/POST/PUT/DELETE 等）
- `timestamp`: 异常发生时间

### 3. 异步日志配置

启动类中配置了 Log4j2 异步日志：

```java
System.setProperty("Log4j2.contextSelector", 
    "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
```

这可以显著提升日志性能，避免日志输出阻塞业务线程。项目已包含完整的 `log4j2.xml` 配置文件。

### 4. 应用配置

项目在 `application.yaml` 中预配置了以下内容：

```yaml
server:
  port: 8080                          # 服务端口
  servlet:
    encoding:
      charset: UTF-8                  # 字符编码
      force: true
  compression:
    enabled: true                     # 启用响应压缩
    mime-types: application/json,application/xml,text/html,text/xml,text/plain

spring:
  application:
    name: spring-boot-demo            # 应用名称
  servlet:
    multipart:
      max-file-size: 10MB             # 单个文件最大大小
      max-request-size: 10MB          # 请求最大大小
  main:
    allow-bean-definition-overriding: true
```

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd spring-boot-demo
```

### 2. 导入 IDE

使用 **IntelliJ IDEA** 或 **Eclipse** 打开项目，等待 Maven 依赖下载完成。

### 3. 启动项目

运行 `SpringBootDemoApplication` 类的 `main` 方法启动项目。

### 4. 创建测试接口

在项目中创建 `controller` 包和测试 Controller：

```java
package cn.smallyoung.springbootdemo.controller;

import cn.smallyoung.springbootdemo.exception.BizException;
import cn.smallyoung.springbootdemo.interfaces.ResponseSysResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
@ResponseSysResult  // 启用统一响应封装
public class DemoController {

    @GetMapping("/hello")
    public String hello() {
        // 直接返回字符串，框架会自动封装为 SysResult
        return "Hello, Spring Boot!";
    }

    @GetMapping("/user")
    public User getUser() {
        // 返回对象，框架会自动封装
        return new User("张三", 20);
    }

    @GetMapping("/error")
    public void testError() {
        // 抛出业务异常，会被全局异常处理器捕获
        throw new BizException("这是一个业务异常", 400);
    }
    
    // 内部类，仅用于演示
    record User(String name, Integer age) {}
}
```

访问测试：
- `http://localhost:8080/api/demo/hello` - 返回封装后的字符串
- `http://localhost:8080/api/demo/user` - 返回封装后的用户对象
- `http://localhost:8080/api/demo/error` - 触发异常处理

## 📝 开发建议

### 1. 日志使用

项目使用 Log4j2，通过 Lombok 的 `@Slf4j` 注解使用日志：

```java
@Slf4j
@Service
public class UserService {
    public void doSomething() {
        log.info("执行业务逻辑");
        log.error("发生错误", exception);
    }
}
```

### 2. 使用 Hutool 工具

项目已集成 Hutool，可以直接使用其丰富的工具方法：

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
```

### 3. 使用 Fastjson2

项目已集成 Fastjson2，可用于 JSON 处理：

```java
// 对象转 JSON
String json = JSON.toJSONString(user);

// JSON 转对象
User user = JSON.parseObject(json, User.class);
```

## 🔧 常用工具说明

- **Lombok**: 通过注解自动生成 Getter/Setter、构造方法、toString 等方法
- **Hutool**: 提供了丰富的工具方法，如字符串处理、日期处理、加密解密、集合操作等
- **Fastjson2**: 用于高性能的 JSON 序列化和反序列化
- **MapStruct**: 用于 DTO 和 Entity 之间的对象转换，编译期生成映射代码，性能优异

## 🔍 通用查询规范

项目实现了基于 JPA Specification 的通用动态查询功能,支持通过 URL 参数或 Map 参数构建复杂的查询条件,无需编写重复的查询代码。

### 参数格式

查询参数格式为:`search_${join}_${operator}_${fields}=value`

> **注意**: 变量中禁止使用 `-` 连接符,必须使用 `_` 下划线分隔

**参数组成部分:**

1. **`${join}`** - 连接关键词
   - 必须以 `AND` 或 `OR` 开头(不区分大小写)
   - 相同的连接关键词表示同一个括号内的查询条件
   - 例如:`AND1`、`OR1`、`AND2` 等

2. **`${operator}`** - 操作符
   - 指定查询的比较方式
   - 支持的操作符见下方列表

3. **`${fields}`** - 字段名
   - 实体对象中的属性名
   - 支持关联查询,使用 `.` 进行分隔
   - 例如:`user.department.name`

### 支持的操作符

| 操作符 | 说明 | 示例 |
|--------|------|------|
| `EQ` | 相等 | `search_AND1_EQ_name=张三` |
| `NEQ` | 不相等 | `search_AND1_NEQ_status=0` |
| `NULL` | 为空 | `search_AND1_NULL_deletedAt=` |
| `NOTNULL` | 不为空 | `search_AND1_NOTNULL_email=` |
| `LIKE` | 前后模糊匹配 | `search_AND1_LIKE_name=张` |
| `LEFTLIKE` | 前模糊匹配(以...结尾) | `search_AND1_LEFTLIKE_name=三` |
| `RIGHTLIKE` | 后模糊匹配(以...开头) | `search_AND1_RIGHTLIKE_name=张` |
| `NOTLIKE` | 不包含 | `search_AND1_NOTLIKE_name=李` |
| `GT` | 大于 | `search_AND1_GT_age=18` |
| `GE` | 大于等于 | `search_AND1_GE_age=18` |
| `LT` | 小于 | `search_AND1_LT_age=60` |
| `LE` | 小于等于 | `search_AND1_LE_age=60` |
| `BETWEEN` | 区间查询 | `search_AND1_BETWEEN_age=18~60` |
| `IN` | 包含(多值) | `search_AND1_IN_status=1:2:3` |
| `NOTIN` | 不包含(多值) | `search_AND1_NOTIN_status=0:9` |
| `JSONCONTAIN` | JSON字段包含 | `search_AND1_JSONCONTAIN_tags=spring` |

**特殊操作符说明:**

- **BETWEEN**: 两个值之间用 `~` 分隔,例如:`18~60`
- **IN/NOTIN**: 多个值用 `:` 分隔,例如:`1:2:3`
- **NULL/NOTNULL**: 判空/非空查询 (需传递任意非空值，例如 `1`，否则会被过滤)

### 空值处理

框架会自动过滤**空字符串**（`""`）的查询条件。

这意味着前端可以统一传递所有查询参数，无需在前端判断值是否为空。如果参数值为空字符串，该条件将被自动忽略，不会生成 SQL 语句。

> **注意**: 对于 `NULL` 和 `NOTNULL` 操作符，也必须传递一个非空值（如 `1`），否则该条件也会被过滤。

**示例：**

```
GET /api/users?search_AND_EQ_name=&search_AND_GT_age=18&search_AND_NULL_email=1
```

- `name` 为空，条件被过滤
- `age` 有值，生成 `age > 18`
- `email` 有值（`1`），生成 `email IS NULL`

生成的 SQL：

```sql
WHERE age > 18 AND email IS NULL
```

### 使用示例

#### 示例 1: 简单查询

查询名称为"张三"的用户:

```
GET /api/users?search_AND1_EQ_name=张三
```

生成的 SQL 条件:
```sql
WHERE name = '张三'
```

#### 示例 2: 多条件 AND 查询

查询年龄大于18且状态为1的用户:

```
GET /api/users?search_AND1_GT_age=18&search_AND1_EQ_status=1
```

生成的 SQL 条件:
```sql
WHERE age > 18 AND status = 1
```

#### 示例 3: 多条件 OR 查询

查询名称包含"张"或"李"的用户:

```
GET /api/users?search_OR1_LIKE_name=张&search_OR1_LIKE_name=李
```

生成的 SQL 条件:
```sql
WHERE name LIKE '%张%' OR name LIKE '%李%'
```

#### 示例 4: 复杂组合查询

查询(名称为"张三"或"李四")且(年龄在18-60之间)的用户:

```
GET /api/users?search_OR1_EQ_name=张三&search_OR1_EQ_name=李四&search_AND2_BETWEEN_age=18~60
```

生成的 SQL 条件:
```sql
WHERE (name = '张三' OR name = '李四') AND (age BETWEEN 18 AND 60)
```

#### 示例 5: 关联查询

支持多级关联查询，例如查询角色名包含"管理员"的用户：

```
GET /api/users?search_AND_EQ_roles.name=管理员
```

> **重要提示**: 使用关联查询时，**必须**在 Entity 实体类中正确配置 JPA 关联关系（如 `@OneToMany`, `@ManyToOne`, `@ManyToMany`），否则无法生成 JOIN 语句。

生成的 SQL 条件（自动处理 JOIN）：
```sql
SELECT DISTINCT u.* FROM user u 
LEFT JOIN user_role ur ON u.id = ur.user_id 
LEFT JOIN role r ON ur.role_id = r.id 
WHERE r.name = '管理员'
```

#### 示例 6: IN 查询

查询状态为1、2或3的用户:

```
GET /api/users?search_AND1_IN_status=1:2:3
```

生成的 SQL 条件:
```sql
WHERE status IN (1, 2, 3)
```

### 注意事项

1. **参数命名规范**
   - 必须使用下划线 `_` 分隔,不能使用 `-`
   - 字段名区分大小写,需与实体类属性名完全一致

2. **关联查询**
   - 使用 `.` 分隔关联属性,如 `department.name`
   - 确保实体类中已正确配置关联关系(`@ManyToOne`、`@OneToMany` 等)

3. **特殊字符处理**
   - URL 参数中的特殊字符需要进行 URL 编码
   - 例如:空格编码为 `%20`,中文需要 UTF-8 编码

4. **性能优化**
   - 避免过度使用 LIKE 查询,建议使用全文索引
   - 关联查询时注意 N+1 问题,可使用 `@EntityGraph` 优化
   - 对于复杂查询,建议添加合适的数据库索引

---

祝学习愉快!如有问题,欢迎交流。
