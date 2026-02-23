# 基于 Sa-Token 的登录注册功能设计说明书

## 为什么需要 (Why)
当前系统主要依赖第三方 OAuth（如支付宝）进行认证，缺乏原生的用户名/密码认证机制。为了支持用户直接注册和登录，并建立健壮的会话管理体系，我们需要集成 Sa-Token 框架。

## 变更内容 (What Changes)
- **依赖变更**: 在 `container-common-core` 中添加 `sa-token-spring-boot-starter`。
- **实体变更**: 在 `CustomerEntity` 中增加 `password` 字段。
- **配置变更**:
  - 在 `container-common-redis` 中配置 Sa-Token (集成 Redis)。
  - 在 `application.yml` (或 Nacos 配置) 中添加 Sa-Token 相关属性。
- **新模块逻辑 (在 `container-account` 中实现)**:
  - **Controller**: `AuthController`，提供 `/auth/login` (登录), `/auth/register` (注册), `/auth/logout` (登出) 接口。
  - **Service**: `AuthService`，处理核心业务逻辑。
  - **Mapper**: `CustomerMapper` (在 `container-account` 中创建或复用)，支持 `insert` 和 `selectByUsername` 操作。
- **全局异常处理**: 在 `GlobalExceptionHandler` 中处理 `NotLoginException` 等认证异常。

## 影响范围 (Impact)
- **受影响的规范**: 无。
- **受影响的代码**:
  - `container-common` 模块下的 `pom.xml`。
  - `CustomerEntity.java`。
  - `container-account` 模块下的新增文件。

## 新增需求 (ADDED Requirements)
### 需求：用户注册
系统必须允许用户通过用户名和密码进行注册。

#### 场景：注册成功
- **当 (WHEN)** 用户发送 `POST /auth/register` 请求，包含有效的 `username`, `password`, 和 `mobile`。
- **那么 (THEN)** 系统创建一个新的 `CustomerEntity`，对密码进行加密处理，保存到数据库，并返回成功信息。

### 需求：用户登录
系统必须允许用户通过用户名和密码登录。

#### 场景：登录成功
- **当 (WHEN)** 用户发送 `POST /auth/login` 请求，包含有效的 `username` 和 `password`。
- **那么 (THEN)** 系统验证凭据，生成 Sa-Token，通过 `StpUtil.login(id)` 登录用户，并返回 Token。

### 需求：全局认证保护
系统必须具备保护敏感接口的能力（具体接口待定，目前先启用此能力）。

## 修改的需求 (MODIFIED Requirements)
### 需求：客户实体 (Customer Entity)
- 在 `CustomerEntity` 中添加 `password` 字段以存储加密后的密码。
