# Spring Boot Demo 学生初始化框架

这是一个Spring Boot 初始化项目框架，集成了常用的工具和规范，旨在帮助大家快速上手 Spring Boot 开发。

## 🛠 项目环境

- **JDK**: 17
- **Spring Boot**: 3.5.7
- **构建工具**: Maven

## 📦 主要依赖

项目已经集成了以下常用依赖，无需重复添加：

- **Web Starter**: 提供 Web 开发支持 (`spring-boot-starter-web`)
- **Lombok**: 简化 Java 代码 (`lombok`)
- **Hutool**: 强大的 Java 工具包 (`hutool-all`)
- **Fastjson2**: 高性能 JSON 处理库 (`fastjson2`)
- **MapStruct**: Java Bean 映射工具 (`mapstruct`)
- **Log4j2**: 日志框架 (`spring-boot-starter-log4j2`)

## 📂 项目结构

```
net.onest.springbootdemo
├── component
│   └── ResponseSysResultAdvice.java  // 全局统一响应处理
├── config                            // 配置类目录
├── exception
│   └── BizException.java             // 自定义业务异常
├── filter                            // 过滤器目录
├── interfaces
│   └── ResponseSysResult.java        // 响应包装注解（用于控制是否启用统一响应）
├── util
│   └── SysResult.java                // 统一响应结果实体类
└── SpringBootDemoApplication.java    // 启动类
```

## ✨ 核心特性

### 1. 统一响应封装 (`SysResult`)

项目实现了自动的统一响应封装。控制器（Controller）只需要返回具体的数据对象，框架会自动将其包装为 `SysResult` 格式返回给前端。

**响应格式示例：**
```json
{
  "code": 200,
  "message": "OK",
  "data": { ... }
}
```

- **实现原理**: 通过 `ResponseSysResultAdvice` 类实现 `ResponseBodyAdvice` 接口，拦截 Controller 的返回值进行封装。
- **SysResult 类**: 位于 `util` 包下，定义了标准的响应结构。

### 2. 全局异常处理 (`BizException`)

集成了全局异常处理机制，可以优雅地处理业务异常和系统异常。

- **BizException**: 自定义业务异常类，支持传入错误码和错误信息。
- **异常拦截**: `ResponseSysResultAdvice` 中包含 `@ExceptionHandler`，统一捕获异常并返回标准 JSON 格式。

### 3. 常用工具集成

- **Hutool**: 提供了丰富的工具方法，如字符串处理、日期处理、加密解密等。
- **Fastjson2**: 用于 JSON 序列化和反序列化。
- **MapStruct**: 用于 DTO 和 Entity 之间的对象转换。

## 🚀 快速开始

1. **克隆项目**到本地。
2. 使用 **IntelliJ IDEA** 打开项目。
3. 等待 **Maven** 依赖下载完成。
4. 运行 `SpringBootDemoApplication` 类的 `main` 方法启动项目。
5. 访问测试接口（需自行创建 Controller）。

## 📝 开发指南

### 创建接口

在 `controller` 包（需新建）下创建 Controller 类：

```java
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @GetMapping("/hello")
    public String hello() {
        // 直接返回字符串或对象，框架会自动封装
        return "Hello, Spring Boot!";
    }

    @GetMapping("/error")
    public void error() {
        // 抛出业务异常
        throw new BizException("演示业务异常", 500);
    }
}
```

祝学习愉快！
