# Spring Boot HttpExchange Demo

基于 Spring Boot 4.0 和 Spring Framework 7.0 的 HttpExchange 微服务远程调用示例项目。

本项目演示了如何使用 Spring 官方推荐的 `@HttpExchange` 注解实现类型安全的 HTTP 客户端，并通过 OpenAPI Generator 自动生成客户端 SDK，支持 API 接口的自动注入和懒加载。

## 特性

- **Spring Boot 4.0 + Spring Framework 7.0** - 使用最新的 Spring 技术栈
- **HttpExchange 声明式客户端** - 使用 `@GetExchange`、`@PostExchange` 等注解定义 HTTP 接口
- **OpenAPI Generator 自动生成** - 从 OpenAPI 规范自动生成客户端代码
- **API 自动注入** - 基于 Programmatic Registration 实现 API 接口的自动注入
- **懒加载支持** - API 客户端在首次使用时才创建，优化启动性能
- **请求头传递** - 自动传递 Authorization、Trace ID 等请求头
- **负载均衡支持** - 可选集成 Spring Cloud LoadBalancer

## 项目结构

```
springboot-httpexchange-demo/
├── pom.xml                                    # 父 POM
├── httpexchange-dependencies/                 # 依赖版本管理
├── httpexchange-server/                       # 服务端 (端口 8181)
│   └── src/main/java/.../server/
│       ├── controller/UserController.java
│       ├── model/                             # User, CreateUserRequest, UpdateUserRequest
│       └── service/UserService.java
├── openapi-httpexchange-sdk-generator/        # OpenAPI HttpExchange SDK 生成器
│   ├── openapi/                               # OpenAPI 规范文件
│   ├── openapi-templates/                     # 自定义 Mustache 模板
│   ├── templates/                             # 配置类模板
│   └── scripts/                               # Groovy 构建脚本
└── httpexchange-call/                         # 调用端 (端口 8282)
    └── src/main/java/.../call/
        └── controller/UserAggregateController.java
```

## 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.1 | 应用框架 |
| Spring Framework | 7.0.2 | 核心框架 |
| Java | 21 | 运行时 |
| OpenAPI Generator | 7.10.0 | 代码生成 |
| springdoc-openapi | 2.8.15 | API 文档 |

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.9+

### 1. 克隆项目

```bash
git clone https://github.com/cnhnwt/springboot-httpexchange-demo.git
cd springboot-httpexchange-demo
```

### 2. 构建项目

```bash
./mvnw clean install -DskipTests
```

> **说明**：从根目录构建时，`openapi-httpexchange-sdk-generator` 模块会自动跳过 SDK 生成（因为没有提供 `openapi.file.path` 或 `openapi.url` 参数）。如需生成 SDK，请参考下方"生成客户端 SDK"章节。

### 3. 启动服务端

```bash
cd httpexchange-server
../mvnw spring-boot:run
```

服务启动后访问：
- Swagger UI: http://localhost:8181/swagger-ui.html
- OpenAPI JSON: http://localhost:8181/v3/api-docs

### 4. 启动调用端

```bash
cd httpexchange-call
../mvnw spring-boot:run
```

服务启动后访问：
- Swagger UI: http://localhost:8282/swagger-ui.html

### 5. 测试 API

```bash
# 创建用户
curl -X POST http://localhost:8181/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","fullName":"John Doe"}'

# 通过聚合层获取用户
curl http://localhost:8282/aggregate/users
```

## 生成客户端 SDK

> **重要提示**：首次生成客户端 SDK 前，需要先从项目根目录构建整个项目，以安装 `httpexchange-dependencies` 模块到本地 Maven 仓库：
> ```bash
> # 在项目根目录执行
> ./mvnw clean install -DskipTests
> ```
> 从根目录构建时，SDK 生成器会自动跳过（因为没有提供参数），无需额外配置。

### OpenAPI 文件命名规范

本地 OpenAPI 文件必须遵循以下命名格式：

```
{server-artifactId}@{version}.json
```

**示例：**
- `httpexchange-server@0.0.1-SNAPSHOT.json` - httpexchange-server 服务的 0.0.1-SNAPSHOT 版本
- `user-service@1.0.0.json` - user-service 服务的 1.0.0 版本
- `order-api@2.1.0-SNAPSHOT.json` - order-api 服务的 2.1.0-SNAPSHOT 版本

### 版本号说明

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `client.version` | 生成的客户端 SDK 版本号 | 从 OpenAPI 文件名解析，如 `0.0.1-SNAPSHOT` |
| `server.version` | 服务端版本号（从 URL 生成时使用） | `0.0.1-SNAPSHOT` |

### 从本地 OpenAPI 文件生成

```bash
cd openapi-httpexchange-sdk-generator
../mvnw clean install -Dopenapi.file.path=openapi/httpexchange-server@0.0.1-SNAPSHOT.json
```

**参数说明：**
- `openapi.file.path` - OpenAPI 文件的相对路径（相对于 openapi-httpexchange-sdk-generator 目录）

### 从运行中的服务生成

```bash
cd openapi-httpexchange-sdk-generator
../mvnw clean install \
  -Dopenapi.url=http://localhost:8181/v3/api-docs \
  -Dserver.artifactId=httpexchange-server \
  -Dserver.version=0.0.1-SNAPSHOT
```

**参数说明：**
- `openapi.url` - 服务的 OpenAPI 端点 URL
- `server.artifactId` - 服务的 artifactId（必填）
- `server.version` - 服务版本号（可选，默认 `0.0.1-SNAPSHOT`）

### 自定义 Maven Settings

如果需要使用自定义的 Maven settings 文件（如私有仓库配置或自定义本地仓库路径），只需使用 `-s` 参数：

```bash
cd openapi-httpexchange-sdk-generator
../mvnw clean install \
  -Dopenapi.file.path=openapi/httpexchange-server@0.0.1-SNAPSHOT.json \
  -s /path/to/your/settings.xml
```

**说明：**
- `-s` 参数用于当前构建过程（解析父 POM 依赖）
- 构建脚本会自动检测 `-s` 参数并将其传递给生成的客户端 SDK 的子构建过程
- 生成的 SDK 会安装到 settings 文件中配置的本地仓库路径

**使用场景：**
- 使用私有 Maven 仓库（如 Nexus、Artifactory）
- 使用自定义本地仓库路径（如 `/users/data/maven_jar/`）
- 使用镜像仓库加速依赖下载

**示例输出：**
```
[INFO] Installing .../httpexchange-server-client-0.0.1-SNAPSHOT.jar to /users/data/maven_jar/org/cnhnwt/httpexchange-server-client/0.0.1-SNAPSHOT/httpexchange-server-client-0.0.1-SNAPSHOT.jar
```

**实现原理：**
1. Groovy 脚本从 Maven session 中获取 `-s` 参数指定的 settings 文件路径
2. 将路径写入临时文件 `target/maven-settings-path.txt`
3. 子构建脚本从临时文件读取路径并传递 `-s` 参数

**高级用法：** 如果需要为子构建指定不同的 settings 文件，可以额外使用 `-Dmaven.settings.path` 参数：

```bash
../mvnw clean install \
  -Dopenapi.file.path=openapi/httpexchange-server@0.0.1-SNAPSHOT.json \
  -s /path/to/parent-settings.xml \
  -Dmaven.settings.path=/path/to/child-settings.xml
```

### 部署到远程 Maven 仓库

默认情况下，生成的客户端 SDK 会安装到本地 Maven 仓库（`install`）。如果需要部署到远程仓库，可以使用 `-Dclient.build.goal=deploy` 参数：

#### 部署到远程仓库

```bash
cd openapi-httpexchange-sdk-generator
../mvnw clean install \
  -Dopenapi.file.path=openapi/httpexchange-server@0.0.1-SNAPSHOT.json \
  -Dclient.build.goal=deploy \
  -s /path/to/your/settings.xml
```

#### 配置远程仓库

在生成的客户端 SDK 的 pom.xml 中，需要配置 `distributionManagement`。可以通过修改 `openapi-httpexchange-sdk-generator/templates/client-pom-template.xml` 模板来添加：

```xml
<distributionManagement>
    <repository>
        <id>releases</id>
        <name>Release Repository</name>
        <url>https://your-nexus-server/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>snapshots</id>
        <name>Snapshot Repository</name>
        <url>https://your-nexus-server/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

#### Maven Settings 配置示例

在您的 `settings.xml` 中配置仓库认证信息：

```xml
<settings>
    <servers>
        <server>
            <id>releases</id>
            <username>your-username</username>
            <password>your-password</password>
        </server>
        <server>
            <id>snapshots</id>
            <username>your-username</username>
            <password>your-password</password>
        </server>
    </servers>
    
    <!-- 可选：配置镜像仓库 -->
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <mirrorOf>central</mirrorOf>
            <name>Aliyun Maven Mirror</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
</settings>
```

#### 构建参数汇总

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `openapi.file.path` | 本地 OpenAPI 文件路径 | - |
| `openapi.url` | 远程 OpenAPI URL | - |
| `server.artifactId` | 服务 artifactId | 从文件名解析 |
| `server.version` | 服务版本号 | `0.0.1-SNAPSHOT` |
| `client.version` | 客户端 SDK 版本号 | 从文件名解析 |
| `client.build.goal` | 构建目标 | `install` |
| `-s` | 自定义 Maven settings 文件路径（自动传递给子构建） | - |
| `maven.settings.path` | 为子构建指定不同的 settings 路径（高级用法） | 自动从 `-s` 获取 |

## 使用生成的客户端

### 添加依赖

```xml
<dependency>
    <groupId>org.cnhnwt</groupId>
    <artifactId>httpexchange-server-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 配置

```yaml
# application.yml
clients:
  httpexchange-server:
    enabled: true
    base-url: http://localhost:8181
    connect-timeout: 5s
    read-timeout: 30s
    auto-register-apis: true  # 启用 API 自动注入
    headers:
      propagation-enabled: true
      propagate-headers:
        - Authorization
        - X-Trace-Id
```

### 不同部署环境配置

#### 本地开发环境

```yaml
# application-local.yml
clients:
  httpexchange-server:
    enabled: true
    base-url: http://localhost:8181
    connect-timeout: 5s
    read-timeout: 30s
```

#### Docker Compose 环境

```yaml
# application-docker.yml
clients:
  httpexchange-server:
    enabled: true
    # 使用 Docker 服务名作为主机名
    base-url: http://httpexchange-server:8181
    connect-timeout: 10s
    read-timeout: 60s
```

#### Kubernetes 环境

```yaml
# application-k8s.yml
clients:
  httpexchange-server:
    enabled: true
    # 使用 K8s Service 名称，格式：http://{service-name}.{namespace}.svc.cluster.local:{port}
    base-url: http://httpexchange-server.default.svc.cluster.local:8181
    connect-timeout: 10s
    read-timeout: 60s
```

#### Nacos 服务发现环境

使用 Nacos 作为服务注册中心时，需要额外配置：

**1. 添加依赖：**

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

**2. 配置 Nacos：**

```yaml
# application-nacos.yml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        namespace: dev
        group: DEFAULT_GROUP

clients:
  httpexchange-server:
    enabled: true
    # 使用服务名（Nacos 注册的服务名），不需要指定端口
    base-url: http://httpexchange-server
    # 启用负载均衡
    load-balanced: true
    connect-timeout: 10s
    read-timeout: 60s
```

**3. 启用负载均衡：**

生成的客户端自动配置类会根据 `load-balanced: true` 配置使用 `@LoadBalanced` 的 `RestClient`。

#### 多环境配置示例

```yaml
# application.yml - 通用配置
clients:
  httpexchange-server:
    enabled: true
    auto-register-apis: true
    headers:
      propagation-enabled: true
      propagate-headers:
        - Authorization
        - X-Trace-Id
        - X-Request-Id

---
# 本地环境
spring:
  config:
    activate:
      on-profile: local
clients:
  httpexchange-server:
    base-url: http://localhost:8181

---
# 开发环境
spring:
  config:
    activate:
      on-profile: dev
clients:
  httpexchange-server:
    base-url: http://dev-server:8181

---
# 生产环境（使用 Nacos）
spring:
  config:
    activate:
      on-profile: prod
clients:
  httpexchange-server:
    base-url: http://httpexchange-server
    load-balanced: true
```

### 使用方式

#### 方式一：直接注入 API 接口（推荐）

```java
@RestController
public class UserController {
    
    @Autowired
    private UserControllerApi userClient;
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return userClient.getAllUsers();
    }
}
```

#### 方式二：使用 HttpServiceProxyFactory

```java
@Configuration
public class ClientConfig {
    
    @Autowired
    private HttpServiceProxyFactory proxyFactory;
    
    @Bean
    public UserControllerApi userClient() {
        return proxyFactory.createClient(UserControllerApi.class);
    }
}
```

## 生成的 API 接口示例

```java
public interface UserControllerApi {

    @GetExchange(url = "/api/users", accept = "application/json")
    ResponseEntity<List<User>> getAllUsers();

    @GetExchange(url = "/api/users/{id}", accept = "application/json")
    ResponseEntity<User> getUserById(@PathVariable("id") Long id);

    @PostExchange(url = "/api/users", accept = "application/json", contentType = "application/json")
    ResponseEntity<User> createUser(@RequestBody CreateUserRequest request);

    @PutExchange(url = "/api/users/{id}", accept = "application/json", contentType = "application/json")
    ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody UpdateUserRequest request);

    @DeleteExchange(url = "/api/users/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable("id") Long id);
}
```

## API 端点

### httpexchange-server (端口 8181)

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/users | 获取所有用户 |
| GET | /api/users/{id} | 获取用户详情 |
| POST | /api/users | 创建用户 |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |

### httpexchange-call (端口 8282)

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /aggregate/users | 聚合获取所有用户 |
| GET | /aggregate/users/{id} | 聚合获取用户详情 |
| POST | /aggregate/users | 聚合创建用户 |
| PUT | /aggregate/users/{id} | 聚合更新用户 |
| DELETE | /aggregate/users/{id} | 聚合删除用户 |

## 架构设计

```
┌─────────────────────────┐         HTTP          ┌─────────────────────────┐
│    httpexchange-call    │ ───────────────────── │   httpexchange-server   │
│       (端口 8282)        │                       │       (端口 8181)        │
│                         │                       │                         │
│  UserAggregateController│                       │     UserController      │
│           │             │                       │           │             │
│           ▼             │                       │           ▼             │
│   UserControllerApi     │                       │      UserService        │
│   (@GetExchange...)     │                       │                         │
└─────────────────────────┘                       └─────────────────────────┘
            │
            │ 依赖
            ▼
┌─────────────────────────┐
│ httpexchange-server-    │
│        client           │
│                         │
│  ┌───────────────────┐  │
│  │ UserControllerApi │  │  ← 自动生成的 HttpExchange 接口
│  └───────────────────┘  │
│  ┌───────────────────┐  │
│  │   Model Classes   │  │  ← 自动生成的数据模型
│  └───────────────────┘  │
│  ┌───────────────────┐  │
│  │ AutoConfiguration │  │  ← 自动配置 + API 自动注入
│  └───────────────────┘  │
└─────────────────────────┘
```

## 核心实现

### API 自动注入

项目使用 `BeanDefinitionRegistryPostProcessor` 实现 API 接口的自动注入：

1. **扫描阶段** - 扫描指定包下所有带有 `@HttpExchange` 注解的接口
2. **注册阶段** - 为每个接口注册一个懒加载的 Bean 定义
3. **创建阶段** - 首次使用时通过 `HttpServiceProxyFactory.createClient()` 创建实例

### 请求头传递

自动传递指定的请求头到下游服务：

```yaml
clients:
  httpexchange-server:
    headers:
      propagation-enabled: true
      propagate-headers:
        - Authorization
        - X-Trace-Id
        - X-User-Id
```

## 自定义模板

项目使用自定义的 Mustache 模板生成代码，位于 `openapi-httpexchange-sdk-generator/openapi-templates/` 目录：

- `api.mustache` - API 接口模板，使用 `@GetExchange`、`@PostExchange` 等注解

## 参考资料

- [Spring HTTP Interface](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-interface)
- [HTTP Service Client Enhancements](https://spring.io/blog/2025/09/23/http-service-client-enhancements)
- [OpenAPI Generator](https://openapi-generator.tech/)

## License

Apache 2.0