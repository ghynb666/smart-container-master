# 任务列表

- [x] 任务 1: 添加依赖与配置
  - [x] 子任务 1.1: 在 `container-common/container-common-core/pom.xml` 中添加 `sa-token-spring-boot-starter` 依赖。
  - [x] 子任务 1.2: 在 `container-common/container-common-redis/pom.xml` 中添加 `sa-token-dao-redis-jackson` 依赖。
  - [x] 子任务 1.3: 在 `container-account/src/main/resources/application.yml` 中添加 Sa-Token 配置（如果文件不存在则创建）。

- [x] 任务 2: 更新实体与 Mapper
  - [x] 子任务 2.1: 在 `container-common-core` 的 `CustomerEntity.java` 中添加 `password` 字段。
  - [x] 子任务 2.2: 在 `container-account` 中创建 `CustomerMapper.java` 和 `CustomerMapper.xml`，支持 `insert` 和 `selectByUsername` 方法。

- [x] 任务 3: 实现认证逻辑
  - [x] 子任务 3.1: 在 `container-api` 或 `container-account` 中创建 `AuthRequest` 和 `AuthResponse` DTO 对象。
  - [x] 子任务 3.2: 在 `container-account` 中创建 `AuthService`，实现 `login` 和 `register` 方法。
  - [x] 子任务 3.3: 在 `container-account` 中创建 `AuthController`，暴露 `/auth/login` 和 `/auth/register` 接口。
  - [x] 子任务 3.4: 实现 `GlobalExceptionHandler` 以捕获并处理 `NotLoginException`。

# 任务依赖
- 任务 3 依赖于 任务 2 和 任务 1。
