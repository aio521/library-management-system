# 新疆财经大学图书馆管理系统 — 设计规格说明

## 1. 项目概述

为新疆财经大学图书馆设计开发一套前后端分离的图书管理系统，当前作为毕业设计项目在个人电脑上部署演示，后续具备迁移到校内生产环境的能力。

**核心目标**：覆盖图书编目、馆藏管理、借阅流通、读者管理、统计分析、系统管理的全流程信息化系统。

## 2. 技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 前端框架 | Vue 3 + TypeScript | 3.x | SPA, Vite 构建 |
| UI 组件库 | Element Plus | 2.x | 企业级后台组件库 |
| 路由 | Vue Router | 4.x | 前端路由 + 权限守卫 |
| 状态管理 | Pinia | 2.x | 轻量级状态管理 |
| HTTP | Axios | 1.x | 请求拦截、Token 携带 |
| 后端框架 | SpringBoot | 3.x | MVC 衍生架构 |
| JDK | JDK 17 | LTS | 长期支持版本 |
| ORM | MyBatis-Plus | 3.5+ | 简化 CRUD、分页插件 |
| 数据库 | MySQL | 8.0 | 主库 |
| 缓存 | Redis | 7.x | Token 管理、热点缓存 |
| 安全 | Spring Security + JWT | - | 无状态认证 |
| API 文档 | Knife4j | 4.x | Swagger 增强 |

## 3. 系统架构

### 3.1 后端目录结构（标准 MVC 衍生架构）

```
library-server/
├── src/main/java/com/library/
│   ├── controller/              # 控制层
│   │   ├── BookController.java
│   │   ├── BorrowController.java
│   │   ├── ReaderController.java
│   │   ├── StatisticsController.java
│   │   ├── SystemController.java
│   │   └── AuthController.java
│   ├── service/                 # 业务接口层
│   │   ├── BookService.java
│   │   ├── BorrowService.java
│   │   ├── ReaderService.java
│   │   ├── StatisticsService.java
│   │   ├── SystemService.java
│   │   └── impl/                # 业务实现
│   │       ├── BookServiceImpl.java
│   │       ├── BorrowServiceImpl.java
│   │       └── ...
│   ├── mapper/                  # 数据访问层
│   │   ├── BookMapper.java
│   │   ├── BookStockMapper.java
│   │   ├── BorrowRecordMapper.java
│   │   ├── ReaderMapper.java
│   │   └── ...
│   ├── entity/                  # 数据库实体（一一对应表）
│   │   ├── Book.java
│   │   ├── BookStock.java
│   │   ├── BorrowRecord.java
│   │   ├── Reader.java
│   │   ├── User.java
│   │   └── ...
│   ├── dto/                     # 入参封装
│   │   ├── BookQueryDTO.java
│   │   ├── BorrowRequestDTO.java
│   │   ├── LoginDTO.java
│   │   └── ...
│   ├── vo/                      # 出参封装
│   │   ├── BookVO.java
│   │   ├── BorrowRecordVO.java
│   │   ├── PageVO.java
│   │   ├── ResultVO.java
│   │   └── ...
│   ├── config/                  # 配置类
│   │   ├── MyBatisPlusConfig.java
│   │   ├── CorsConfig.java
│   │   ├── SwaggerConfig.java
│   │   └── RedisConfig.java
│   ├── security/                # 安全认证
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── UserDetailsServiceImpl.java
│   │   └── SecurityConfig.java
│   ├── common/                  # 公共类
│   │   ├── GlobalExceptionHandler.java
│   │   ├── BusinessException.java
│   │   ├── ResultCode.java
│   │   └── Constants.java
│   └── utils/                   # 工具类
│       ├── BarCodeUtil.java
│       ├── DateUtil.java
│       └── RedisUtil.java
├── src/main/resources/
│   ├── mapper/                  # Mapper XML
│   ├── application.yml
│   ├── application-dev.yml
│   └── application-prod.yml
└── pom.xml
```

### 3.2 前端目录结构

```
library-web/
├── src/
│   ├── views/
│   │   ├── login/               # 登录页
│   │   ├── dashboard/           # 工作台
│   │   ├── book/
│   │   │   ├── catalog/         # 图书编目
│   │   │   ├── list/            # 馆藏查询
│   │   │   └── inventory/       # 库存盘点
│   │   ├── borrow/
│   │   │   ├── borrow/          # 借书操作
│   │   │   ├── return/          # 还书操作
│   │   │   ├── renew/           # 续借管理
│   │   │   ├── reserve/         # 预约管理
│   │   │   └── overdue/         # 逾期处理
│   │   ├── reader/
│   │   │   ├── list/            # 读者列表
│   │   │   ├── register/        # 读者注册
│   │   │   └── card/            # 借阅证管理
│   │   ├── statistics/
│   │   │   ├── borrow-stat/     # 借阅统计
│   │   │   ├── popular/         # 热门图书
│   │   │   └── reader-stat/     # 读者统计
│   │   ├── system/
│   │   │   ├── user/            # 用户管理
│   │   │   ├── role/            # 角色管理
│   │   │   └── log/             # 操作日志
│   │   └── profile/             # 个人中心
│   ├── components/              # 公共组件
│   ├── router/                  # 路由配置
│   ├── stores/                  # Pinia 状态
│   ├── api/                     # API 接口层
│   └── utils/                   # 工具函数
├── vite.config.ts
└── package.json
```

### 3.3 调用链路

```
浏览器 → Vue3 SPA → Axios HTTP → Controller → Service → Mapper → MySQL
                                              ↕
                                            Redis
```

- Controller：只做参数校验和结果封装，不写业务逻辑
- Service：全部业务逻辑，事务管理，调用 Mapper
- Mapper：只做数据存取，单表操作使用 MyBatis-Plus BaseMapper
- entity/dto/vo：严格分离，entity 对表，dto 对入参，vo 对出参

## 4. 用户角色与权限

| 角色 | 权限范围 |
|------|----------|
| 普通读者（学生） | 查询图书、查看个人借阅记录、续借、预约 |
| 图书管理员 | 图书编目、馆藏管理、借还操作、读者管理、逾期罚款、统计分析查看 |
| 超级管理员 | 用户管理、角色权限分配、系统配置、操作日志查看、所有模块的管理权限 |

读者身份在系统内独立管理，不对接学校学籍/教工系统。

## 5. 数据库设计

### 5.1 核心表关系

```
  ┌──────────────┐       ┌──────────────┐       ┌──────────────┐
  │   category   │       │     book     │       │  book_stock  │
  │   (中图分类)  │←──1:N──│   (书目信息)  │←──1:N──│   (馆藏复本)  │
  └──────────────┘       └──────────────┘       └──────┬───────┘
                                                        │
  ┌──────────────┐       ┌──────────────┐              │
  │    reader    │       │ reader_card  │              │
  │   (读者信息)  │←──1:1──│   (借阅证)    │              │
  └──────┬───────┘       └──────────────┘              │
         │                                              │
         │         ┌──────────────────────────────────┘
         │         │
         │    ┌────▼──────────┐
         └───→│ borrow_record │
              │   (借阅记录)   │
              └────────────────┘

  ┌──────────────┐       ┌──────────────┐       ┌──────────────┐
  │     user     │←──N:M──│     role     │←──N:M──│     menu    │
  │   (系统用户)  │       │   (角色)      │       │   (菜单权限)  │
  └──────────────┘       └──────────────┘       └──────────────┘
```

### 5.2 表结构

**category（中图分类表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| code | varchar(10) | 分类号，如 "F"、"F8"、"F83" |
| name | varchar(50) | 分类名称，如 "经济"、"财政金融" |
| parent_id | bigint | 父级分类，支持多级层级 |

**book（书目信息表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| isbn | varchar(20) | ISBN号 |
| title | varchar(200) | 书名 |
| author | varchar(100) | 作者 |
| publisher | varchar(100) | 出版社 |
| publish_date | date | 出版日期 |
| category_id | bigint | 关联中图分类 |
| edition | varchar(50) | 版次 |
| cover_url | varchar(500) | 封面图片 |
| description | text | 内容简介 |
| total_stock | int | 总馆藏数（冗余汇总） |
| available_stock | int | 可借数（冗余汇总） |
| create_time | datetime | 录入时间 |
| update_time | datetime | 更新时间 |

**book_stock（馆藏复本表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| book_id | bigint | 关联书目 |
| barcode | varchar(50) | 条形码，唯一 |
| location | varchar(100) | 馆藏位置/架位号 |
| status | tinyint | 0=在库 1=借出 2=报损 3=预约中 |
| rfid_tag | varchar(50) | RFID标签号（预留扩展） |
| create_time | datetime | 录入时间 |

**reader（读者表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| reader_no | varchar(20) | 读者编号/学号，唯一 |
| name | varchar(50) | 姓名 |
| gender | tinyint | 性别 |
| id_card | varchar(18) | 身份证号 |
| dept | varchar(100) | 院系/部门 |
| phone | varchar(20) | 联系电话 |
| max_borrow | int | 最大借阅数，默认 5 |
| borrow_days | int | 单次借阅天数，默认 30 |
| status | tinyint | 0=正常 1=挂失 2=注销 |
| create_time | datetime | 注册时间 |

**reader_card（借阅证表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| reader_id | bigint | 关联读者，一对一 |
| card_no | varchar(50) | 借阅证号，唯一 |
| issue_date | date | 发证日期 |
| expire_date | date | 有效期 |
| status | tinyint | 0=正常 1=挂失 2=注销 |

**borrow_record（借阅记录表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| reader_id | bigint | 关联读者 |
| stock_id | bigint | 关联馆藏复本 |
| borrow_date | datetime | 借出日期 |
| due_date | date | 应还日期 |
| return_date | datetime | 实际归还日期 |
| renew_count | int | 续借次数，最多 1 次 |
| status | tinyint | 0=借出中 1=已归还 2=逾期 3=续借 |
| operator_id | bigint | 操作员（管理员）ID |

**user（系统用户表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| username | varchar(50) | 用户名，唯一 |
| password | varchar(200) | BCrypt 加密密码 |
| real_name | varchar(50) | 真实姓名 |
| phone | varchar(20) | 手机号 |
| status | tinyint | 0=禁用 1=启用 |
| create_time | datetime | 创建时间 |

**role（角色表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| role_code | varchar(50) | 角色编码，如 ROLE_ADMIN |
| role_name | varchar(50) | 角色名称 |
| description | varchar(200) | 角色描述 |

**user_role（用户角色关联表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | bigint | 用户ID |
| role_id | bigint | 角色ID |

**menu（菜单权限表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| name | varchar(50) | 菜单名称 |
| path | varchar(200) | 路由路径 |
| component | varchar(200) | 组件路径 |
| icon | varchar(50) | 菜单图标 |
| parent_id | bigint | 父级菜单 |
| sort | int | 排序号 |
| permission | varchar(100) | 权限标识 |
| type | tinyint | 0=目录 1=菜单 2=按钮 |

**role_menu（角色菜单关联表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| role_id | bigint | 角色ID |
| menu_id | bigint | 菜单ID |

**operation_log（操作日志表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 操作用户 |
| module | varchar(50) | 操作模块 |
| action | varchar(50) | 操作类型 |
| description | varchar(500) | 操作描述 |
| ip | varchar(50) | 操作IP |
| create_time | datetime | 操作时间 |

## 6. API 接口设计

### 6.1 统一响应格式

成功响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

分页响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 150,
    "page": 1,
    "pageSize": 20
  }
}
```

错误响应：

```json
{
  "code": 400,
  "message": "ISBN 已存在",
  "data": null
}
```

### 6.2 认证流程

```
登录 → 后端校验 → 签发 JWT(含 userId, roles) → 前端存 localStorage
                       ↓
每次请求 → Axios 拦截器自动带 Authorization: Bearer <token>
                       ↓
JwtFilter 校验 → 解析角色 → 放行/403
```

登出时 JWT 加入 Redis 黑名单，有效期至 token 原过期时间。

### 6.3 接口清单

**认证 `/api/auth`**

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/auth/login | 登录 | 公开 |
| POST | /api/auth/logout | 退出 | 登录即可 |
| GET | /api/auth/info | 当前用户信息及菜单权限 | 登录即可 |

**图书 `/api/books`**

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/books | 分页查询（isbn/title/author/category） | 登录即可 |
| GET | /api/books/{id} | 详情（含复本列表） | 登录即可 |
| POST | /api/books | 新增书目 | 管理员 |
| PUT | /api/books/{id} | 编辑书目 | 管理员 |
| DELETE | /api/books/{id} | 删除书目 | 管理员 |
| GET | /api/books/{id}/stocks | 查看复本列表 | 登录即可 |
| POST | /api/books/{id}/stocks | 录入复本（生成条形码） | 管理员 |
| PUT | /api/stocks/{id} | 编辑复本信息 | 管理员 |
| DELETE | /api/stocks/{id} | 删除复本（报损） | 管理员 |

**分类 `/api/categories`**

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/categories | 分类树 | 登录即可 |
| POST | /api/categories | 新增分类 | 管理员 |
| PUT | /api/categories/{id} | 编辑分类 | 管理员 |
| DELETE | /api/categories/{id} | 删除分类 | 管理员 |

**借阅 `/api/borrows`**

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/borrows | 借书(readerId + barcode) | 管理员 |
| POST | /api/borrows/{id}/return | 还书 | 管理员 |
| POST | /api/borrows/{id}/renew | 续借 | 管理员/读者 |
| GET | /api/borrows | 借阅记录查询 | 登录即可 |
| GET | /api/borrows/overdue | 逾期列表 | 管理员 |
| POST | /api/reserves | 预约 | 读者 |
| DELETE | /api/reserves/{id} | 取消预约 | 读者 |

**读者 `/api/readers`**

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/readers | 分页查询 | 管理员 |
| GET | /api/readers/{id} | 详情 + 借阅历史 | 管理员 |
| POST | /api/readers | 注册读者 | 管理员 |
| PUT | /api/readers/{id} | 编辑读者 | 管理员 |
| PUT | /api/readers/{id}/status | 挂失/注销 | 管理员 |
| POST | /api/readers/{id}/card | 办理借阅证 | 管理员 |

**统计 `/api/statistics`**

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/statistics/borrow/overview | 借阅概览 | 管理员 |
| GET | /api/statistics/books/popular | 热门借阅排行 | 管理员 |
| GET | /api/statistics/books/category | 分类借阅统计 | 管理员 |
| GET | /api/statistics/readers/active | 活跃读者排行 | 管理员 |
| GET | /api/statistics/borrow/trend | 借阅趋势 | 管理员 |

**系统 `/api/system`**

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET/POST/PUT/DELETE | /api/system/users | 用户 CRUD | 超级管理员 |
| GET/POST/PUT/DELETE | /api/system/roles | 角色 CRUD | 超级管理员 |
| GET/POST | /api/system/roles/{id}/menus | 分配菜单权限 | 超级管理员 |
| GET | /api/system/menus | 菜单树 | 登录即可 |
| GET | /api/system/logs | 操作日志 | 超级管理员 |
| GET/PUT | /api/system/config | 系统配置 | 超级管理员 |

## 7. 部署方案

### 7.1 本地开发部署（方式一：直接运行）

| 组件 | 启动方式 |
|------|----------|
| MySQL 8.0 + Redis 7 | 本机安装，开机自启 |
| 后端 | `java -jar library-server.jar --spring.profiles.active=dev` |
| 前端 | `npm run dev`，Vite proxy 转发 `/api` 到 `localhost:8080` |

前端 Vite 配置跨域代理，SpringBoot 侧配置 CORS 做兜底。

### 7.2 Docker 部署（方式二：演示用）

```yaml
services:
  mysql:
    image: mysql:8.0
    ports: [3306:3306]
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: library
    volumes:
      - mysql_data:/var/lib/mysql
  redis:
    image: redis:7-alpine
    ports: [6379:6379]
  server:
    build: ./library-server
    ports: [8080:8080]
    depends_on: [mysql, redis]
  web:
    build: ./library-web
    ports: [80:80]
    depends_on: [server]
volumes:
  mysql_data:
```

一条 `docker-compose up` 全部启动，适用于演示答辩。

### 7.3 后续校内部署扩展

当需要迁移到校内生产环境时，在 Docker 方案基础上增加 Nginx 反向代理和定时数据库备份即可，架构无需调整。

## 8. 关键业务规则

- 读者默认最多同时借阅 5 本，单次借期 30 天
- 续借仅限 1 次，续借后从续借日起延长 30 天
- 逾期未还每天每本产生固定罚款金额（系统配置可调）
- 有逾期未还记录的读者不可再借阅，直到归还并缴清罚款
- 条形码由系统自动生成，规则：`BK` + 年月日 + 4 位流水号
- 借阅证号由系统自动生成，规则：`RD` + 年月日 + 4 位流水号
- 预约保留 3 天，超时自动取消
- 图书报损（下架）后不可恢复借阅

## 9. 不包含的功能（确认为范围外）

- 与学校学籍/教工系统对接
- RFID 硬件集成（仅数据库预留字段）
- 微信/APP 端
- 在线支付罚款
- 邮件/短信通知
- 电子书/数字资源管理
