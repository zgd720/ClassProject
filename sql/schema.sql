-- 基于多源数据的大学生学业行为分析助手 - 数据库DDL

CREATE DATABASE IF NOT EXISTS academic_analysis DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE academic_analysis;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
    `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色: COUNSELOR/STUDENT/ADMIN',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `student_id` VARCHAR(50) DEFAULT NULL COMMENT '关联学号(学生角色时必填)',
    `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级(辅导员角色时填写所带班级)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 学生基本信息表
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `student_id` VARCHAR(50) NOT NULL COMMENT '学号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
    `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
    `class_name` VARCHAR(50) NOT NULL COMMENT '班级',
    `enrollment_year` INT DEFAULT NULL COMMENT '入学年份',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    KEY `idx_class_name` (`class_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生基本信息表';

-- 成绩表
DROP TABLE IF EXISTS `course_score`;
CREATE TABLE `course_score` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `student_id` VARCHAR(50) NOT NULL COMMENT '学号',
    `semester` VARCHAR(20) NOT NULL COMMENT '学期(如2025-2026-1)',
    `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `score` DECIMAL(5,2) DEFAULT NULL COMMENT '成绩',
    `gpa` DECIMAL(3,2) DEFAULT NULL COMMENT '绩点',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_semester` (`semester`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 借阅记录表
DROP TABLE IF EXISTS `library_borrow`;
CREATE TABLE `library_borrow` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `student_id` VARCHAR(50) NOT NULL COMMENT '学号',
    `borrow_date` DATE NOT NULL COMMENT '借阅日期',
    `book_name` VARCHAR(200) NOT NULL COMMENT '书名',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '图书类别',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_borrow_date` (`borrow_date`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 消费记录表
DROP TABLE IF EXISTS `consumption`;
CREATE TABLE `consumption` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `student_id` VARCHAR(50) NOT NULL COMMENT '学号',
    `consume_time` DATETIME NOT NULL COMMENT '消费时间',
    `amount` DECIMAL(8,2) NOT NULL COMMENT '消费金额',
    `type` VARCHAR(50) NOT NULL COMMENT '消费类型(餐饮/超市/其他)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_consume_time` (`consume_time`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费记录表';

-- 风险记录表
DROP TABLE IF EXISTS `risk_log`;
CREATE TABLE `risk_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `student_id` VARCHAR(50) NOT NULL COMMENT '学号',
    `risk_level` VARCHAR(20) NOT NULL COMMENT '风险等级: HIGH/MEDIUM/LOW',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '判定理由',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险记录表';

-- 初始用户由后端DataInitializer自动创建（BCrypt密码加密）
-- 默认账号：admin/admin123（管理员）、counselor/123456（辅导员）、student01/123456（学生）
