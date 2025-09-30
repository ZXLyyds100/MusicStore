-- 1. 创建数据库（若不存在）
CREATE DATABASE IF NOT EXISTS music_store_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE music_store_db;

-- 2. 角色表（sys_role）
CREATE TABLE IF NOT EXISTS sys_role (
                                        id INT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
                                        role_name VARCHAR(20) NOT NULL COMMENT '角色名称（ROLE_ADMIN/ROLE_USER/ROLE_GUEST）',
    role_desc VARCHAR(50) DEFAULT '' COMMENT '角色描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_role_name (role_name) COMMENT '角色名称唯一'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '系统角色表';

-- 插入默认角色
INSERT INTO sys_role (role_name, role_desc) VALUES
                                                ('ROLE_ADMIN', '管理员角色，拥有所有权限'),
                                                ('ROLE_USER', '普通用户角色，拥有购物、收藏等权限'),
                                                ('ROLE_GUEST', '游客角色，仅拥有查看、搜索权限')
    ON DUPLICATE KEY UPDATE role_desc = VALUES(role_desc);

-- 3. 用户表（sys_user）
CREATE TABLE IF NOT EXISTS sys_user (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                                        username VARCHAR(50) NOT NULL COMMENT '用户名（登录用）',
    password VARCHAR(100) NOT NULL COMMENT '加密后的密码（BCrypt）',
    nickname VARCHAR(50) DEFAULT '' COMMENT '用户昵称',
    phone VARCHAR(20) DEFAULT '' COMMENT '手机号',
    email VARCHAR(100) DEFAULT '' COMMENT '邮箱',
    role_id INT COMMENT '角色ID（关联sys_role.id）',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：正常）',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0：未删，1：已删）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username) COMMENT '用户名唯一',
    INDEX idx_status (status) COMMENT '状态索引',
    INDEX idx_role_id (role_id) COMMENT '角色ID索引'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '系统用户表';

-- 插入默认管理员（密码：123，加密后的值），并关联ADMIN角色
INSERT INTO sys_user (username, password, nickname, status, role_id)
SELECT 'admin', '$2a$10$7T4Y6Z7X8W9V0U1S2D3F4G5H6J7K8L9M0N1O2P3Q4R5S6T7U8V9W0', '系统管理员', 1, r.id
FROM sys_role r WHERE r.role_name = 'ROLE_ADMIN'
    ON DUPLICATE KEY UPDATE password = VALUES(password), role_id = VALUES(role_id);

-- 4. 用户-角色中间表（sys_user_role） - 已废弃
/*
CREATE TABLE IF NOT EXISTS sys_user_role (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '中间表ID',
                                             user_id BIGINT NOT NULL COMMENT '用户ID（关联sys_user.id）',
                                             role_id INT NOT NULL COMMENT '角色ID（关联sys_role.id）',
                                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             UNIQUE KEY uk_user_role (user_id, role_id) COMMENT '用户-角色组合唯一',
    INDEX idx_user_id (user_id) COMMENT '用户ID索引',
    INDEX idx_role_id (role_id) COMMENT '角色ID索引'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '用户-角色关联表';

-- 给默认管理员分配ADMIN角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT (SELECT id FROM sys_user WHERE username = 'admin'), (SELECT id FROM sys_role WHERE role_name = 'ROLE_ADMIN')
    ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);
*/

-- 5. 音乐分类表（music_category）
CREATE TABLE IF NOT EXISTS music_category (
                                              id INT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
                                              category_name VARCHAR(50) NOT NULL COMMENT '分类名称（如流行、摇滚、古典）',
    category_desc VARCHAR(200) DEFAULT '' COMMENT '分类描述',
    sort INT DEFAULT 0 COMMENT '排序（数字越大越靠前）',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0：未删，1：已删）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_category_name (category_name) COMMENT '分类名称唯一'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '音乐分类表';

-- 6. 音乐信息表（music_info）
CREATE TABLE IF NOT EXISTS music_info (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '音乐ID',
                                          music_name VARCHAR(100) NOT NULL COMMENT '歌名',
    singer VARCHAR(50) NOT NULL COMMENT '歌手',
    category_id INT NOT NULL COMMENT '分类ID（关联music_category.id）',
    album_name VARCHAR(100) DEFAULT '' COMMENT '专辑名称',
    duration INT DEFAULT 0 COMMENT '时长（秒）',
    play_count INT DEFAULT 0 COMMENT '播放量',
    price DECIMAL(10,2) NOT NULL COMMENT '价格（元）',
    cover_url VARCHAR(255) DEFAULT '' COMMENT '封面图片URL',
    music_url VARCHAR(255) DEFAULT '' COMMENT '音乐文件URL',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0：未删，1：已删）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_id (category_id) COMMENT '分类ID索引',
    INDEX idx_singer (singer) COMMENT '歌手索引',
    INDEX idx_music_name (music_name) COMMENT '歌名索引'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '音乐信息表';

-- 7. 用户收藏表（music_collection）
CREATE TABLE IF NOT EXISTS music_collection (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
                                                user_id BIGINT NOT NULL COMMENT '用户ID（关联sys_user.id）',
                                                music_id BIGINT NOT NULL COMMENT '音乐ID（关联music_info.id）',
                                                create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                                                UNIQUE KEY uk_user_music (user_id, music_id) COMMENT '用户-音乐组合唯一（避免重复收藏）',
    INDEX idx_user_id (user_id) COMMENT '用户ID索引',
    INDEX idx_music_id (music_id) COMMENT '音乐ID索引'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '用户音乐收藏表';

-- 8. 购物车表（shopping_cart）
CREATE TABLE IF NOT EXISTS shopping_cart (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID',
                                             user_id BIGINT NOT NULL COMMENT '用户ID（关联sys_user.id）',
                                             music_id BIGINT NOT NULL COMMENT '音乐ID（关联music_info.id）',
                                             count INT DEFAULT 1 COMMENT '数量（默认1，音乐通常单首购买，可扩展）',
                                             is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0：未删，1：已删）',
                                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                                             update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             UNIQUE KEY uk_user_music (user_id, music_id) COMMENT '用户-音乐组合唯一（避免重复添加）',
    INDEX idx_user_id (user_id) COMMENT '用户ID索引'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '用户购物车表';

-- 9. 订单主表（order_main）
CREATE TABLE IF NOT EXISTS order_main (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
                                          order_no VARCHAR(32) NOT NULL COMMENT '订单编号（唯一，如20251001123456789）',
    user_id BIGINT NOT NULL COMMENT '用户ID（关联sys_user.id）',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额（元）',
    order_status TINYINT DEFAULT 0 COMMENT '订单状态（0：待支付，1：已支付，2：已取消，3：已完成）',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
    finish_time DATETIME DEFAULT NULL COMMENT '完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_order_no (order_no) COMMENT '订单编号唯一',
    INDEX idx_user_id (user_id) COMMENT '用户ID索引',
    INDEX idx_order_status (order_status) COMMENT '订单状态索引'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '订单主表';

-- 10. 订单子表（order_item）
CREATE TABLE IF NOT EXISTS order_item (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
                                          order_id BIGINT NOT NULL COMMENT '订单ID（关联order_main.id）',
                                          music_id BIGINT NOT NULL COMMENT '音乐ID（关联music_info.id）',
                                          music_name VARCHAR(100) NOT NULL COMMENT '歌名（冗余存储，避免音乐信息修改影响订单）',
    singer VARCHAR(50) NOT NULL COMMENT '歌手（冗余存储）',
    price DECIMAL(10,2) NOT NULL COMMENT '购买时的价格（元，冗余存储）',
    count INT DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id) COMMENT '订单ID索引',
    INDEX idx_music_id (music_id) COMMENT '音乐ID索引'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '订单子表（订单中的音乐明细）';

