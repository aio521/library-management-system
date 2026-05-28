# 新疆财经大学图书馆管理系统

基于 SpringBoot 3 + Vue 3 + Element Plus 的前后端分离图书管理系统。

> **声明**
>
> 本项目由 **Claude Code**（Anthropic 推出的 AI 编程助手）辅助完成开发。
>
> 项目仅用于**个人学习、毕业设计及技术交流**，不涉及任何商业用途。项目中的业务数据（如读者信息、图书信息）均为虚构的测试数据，不包含任何真实个人信息。
>
> 如需在此基础上进行二次开发或用于实际部署，请遵守相关法律法规及学校规定。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | SpringBoot 3.2 + JDK 17 |
| ORM | MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7 |
| 安全 | Spring Security + JWT |
| 前端 | Vue 3 + TypeScript + Element Plus |
| 构建 | Maven + Vite |

## 环境要求

- JDK 17+
- MySQL 8.0+
- Redis（可选，不装也能运行，仅登出黑名单功能受影响）
- Node.js 18+
- Maven 3.6+

## 快速开始

### 1. 初始化数据库

```sql
-- 用 utf8mb4 字符集执行建表和种子数据
mysql -u root -p --default-character-set=utf8mb4 < library-server/src/main/resources/db/schema.sql
mysql -u root -p --default-character-set=utf8mb4 < library-server/src/main/resources/db/data.sql
```

### 2. 修改数据库密码

编辑 `library-server/src/main/resources/application-dev.yml`，将 `username` 和 `password` 改为本机 MySQL 的账号密码。

### 3. 启动 Redis（可选）

```bash
# Windows
E:\Develop\Path\Redis\redis-server.exe
```

### 4. 启动后端

```bash
cd library-server
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

后端启动后访问：http://localhost:8080  
API 文档：http://localhost:8080/doc.html

### 5. 启动前端

```bash
cd library-web
npm install        # 首次运行需要
npm run dev
```

前端启动后访问：http://localhost:5173

## 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| librarian | admin123 | 图书管理员 |

## Docker 一键部署（可选）

```bash
docker-compose up
```

自动启动 MySQL + Redis + 后端 + 前端，浏览器访问 http://localhost。

## 项目结构

```
├── library-server/                # 后端 SpringBoot 项目
│   └── src/main/java/com/library/
│       ├── controller/            # 控制层
│       ├── service/               # 业务逻辑层
│       │   └── impl/
│       ├── mapper/                # 数据访问层
│       ├── entity/                # 数据库实体
│       ├── dto/                   # 入参封装
│       ├── vo/                    # 出参封装
│       ├── config/                # 配置类
│       ├── security/              # JWT + Spring Security
│       ├── common/                # 公共类
│       └── utils/                 # 工具类
├── library-web/                   # 前端 Vue3 项目
│   └── src/
│       ├── views/                 # 页面
│       │   ├── login/             #   登录
│       │   ├── dashboard/         #   工作台
│       │   ├── book/              #   图书管理
│       │   ├── borrow/            #   借阅管理
│       │   ├── reader/            #   读者管理
│       │   ├── statistics/        #   统计分析
│       │   ├── system/            #   系统管理
│       │   └── profile/           #   个人中心
│       ├── layout/                # 布局组件
│       ├── router/                # 路由配置
│       ├── stores/                # Pinia 状态
│       ├── api/                   # Axios 封装
│       └── components/            # 公共组件
├── docs/superpowers/
│   ├── specs/                     # 设计规格说明
│   └── plans/                     # 实现计划
└── docker-compose.yml             # Docker 编排
```

## 功能模块

| 模块 | 功能 |
|------|------|
| 图书管理 | 书目编目、馆藏查询、复本管理、库存盘点 |
| 借阅管理 | 借书、还书、续借、预约、逾期处理 |
| 读者管理 | 读者注册、信息编辑、借阅证办理/挂失 |
| 统计分析 | 借阅概览、热门排行、分类统计、借阅趋势 |
| 系统管理 | 用户管理、角色管理、菜单权限、操作日志 |

## 业务规则

- 读者默认最多同时借阅 5 本，单次借期 30 天
- 续借仅限 1 次，续借后从续借日起延长 30 天
- 有逾期未还记录的读者不可再借阅
- 条形码自动生成，格式：`BK` + 年月日 + 4 位流水号
- 借阅证号自动生成，格式：`RD` + 年月日 + 4 位流水号
- 预约保留 3 天，超时自动取消
