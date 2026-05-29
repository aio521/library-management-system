CREATE DATABASE IF NOT EXISTS library DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library;

DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS role_menu;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS borrow_record;
DROP TABLE IF EXISTS reserve;
DROP TABLE IF EXISTS reader_card;
DROP TABLE IF EXISTS reader;
DROP TABLE IF EXISTS book_stock;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS menu;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS user;

CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0
) ENGINE=InnoDB COMMENT='中图分类表';

CREATE TABLE book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20),
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100),
    publisher VARCHAR(100),
    publish_date DATE,
    category_id BIGINT,
    edition VARCHAR(50),
    cover_url VARCHAR(500),
    description TEXT,
    total_stock INT DEFAULT 0,
    available_stock INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='书目信息表';

CREATE TABLE book_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    barcode VARCHAR(50) NOT NULL UNIQUE,
    location VARCHAR(100),
    status TINYINT DEFAULT 0 COMMENT '0=在库 1=借出 2=报损 3=预约中',
    rfid_tag VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='馆藏复本表';

CREATE TABLE reader (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '关联user表ID（自助注册时创建）',
    reader_no VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    gender TINYINT DEFAULT 1 COMMENT '0=女 1=男',
    id_card VARCHAR(18),
    dept VARCHAR(100),
    phone VARCHAR(20),
    max_borrow INT DEFAULT 5,
    borrow_days INT DEFAULT 30,
    status TINYINT DEFAULT 0 COMMENT '0=正常 1=挂失 2=注销',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='读者表';

CREATE TABLE reader_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reader_id BIGINT NOT NULL UNIQUE,
    card_no VARCHAR(50) NOT NULL UNIQUE,
    issue_date DATE,
    expire_date DATE,
    status TINYINT DEFAULT 0 COMMENT '0=正常 1=挂失 2=注销'
) ENGINE=InnoDB COMMENT='借阅证表';

CREATE TABLE borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reader_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    borrow_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,
    return_date DATETIME,
    renew_count INT DEFAULT 0,
    status TINYINT DEFAULT 0 COMMENT '0=借出中 1=已归还 2=逾期 3=续借',
    operator_id BIGINT
) ENGINE=InnoDB COMMENT='借阅记录表';

CREATE TABLE reserve (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reader_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    reserve_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    expire_date DATE,
    status TINYINT DEFAULT 0 COMMENT '0=预约中 1=已到馆 2=已取消 3=已过期'
) ENGINE=InnoDB COMMENT='预约表';

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    status TINYINT DEFAULT 1 COMMENT '0=禁用 1=启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='系统用户表';

CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(200)
) ENGINE=InnoDB COMMENT='角色表';

CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB COMMENT='用户角色关联表';

CREATE TABLE menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    path VARCHAR(200),
    component VARCHAR(200),
    icon VARCHAR(50),
    parent_id BIGINT DEFAULT 0,
    sort INT DEFAULT 0,
    permission VARCHAR(100),
    type TINYINT DEFAULT 1 COMMENT '0=目录 1=菜单 2=按钮'
) ENGINE=InnoDB COMMENT='菜单权限表';

CREATE TABLE role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB COMMENT='角色菜单关联表';

CREATE TABLE operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    module VARCHAR(50),
    action VARCHAR(50),
    description VARCHAR(500),
    ip VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='操作日志表';

-- 短信验证码表
DROP TABLE IF EXISTS sms_code;
CREATE TABLE sms_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    code VARCHAR(6) NOT NULL,
    expire_time DATETIME NOT NULL,
    used TINYINT DEFAULT 0 COMMENT '0=未使用 1=已使用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='短信验证码表';
