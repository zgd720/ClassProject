# 数据库设计文档

## ER图

```
┌──────────┐     ┌──────────────┐     ┌──────────────┐
│   user   │     │   student    │     │ course_score │
├──────────┤     ├──────────────┤     ├──────────────┤
│ id (PK)  │     │ id (PK)      │     │ id (PK)      │
│ username │────>│ student_id   │<────│ student_id   │
│ password │     │ name         │     │ semester     │
│ role     │     │ gender       │     │ course_name  │
│ name     │     │ major        │     │ score        │
│student_id│     │ class_name   │     │ gpa          │
│class_name│     │enrollment_yr │     └──────────────┘
└──────────┘     └──────────────┘
                       │
          ┌────────────┼────────────┐
          │            │            │
    ┌─────┴─────┐ ┌────┴─────┐ ┌───┴──────────┐
    │library_   │ │consumpt- │ │  risk_log    │
    │borrow     │ │ion       │ ├──────────────┤
    ├───────────┤ ├──────────┤ │ id (PK)      │
    │ id (PK)   │ │ id (PK)  │ │ student_id   │
    │ student_id│ │student_id│ │ risk_level   │
    │ borrow_dt │ │consume_tm│ │ reason       │
    │ book_name │ │ amount   │ │ update_time  │
    │ category  │ │ type     │ └──────────────┘
    └───────────┘ └──────────┘
```

## 表结构说明

### user (用户表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| password | VARCHAR(255) | BCrypt加密密码 |
| role | VARCHAR(20) | 角色：COUNSELOR/STUDENT/ADMIN |
| name | VARCHAR(50) | 真实姓名 |
| student_id | VARCHAR(50) | 关联学号 |
| class_name | VARCHAR(50) | 班级 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除标记 |

### student (学生信息表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| student_id | VARCHAR(50) | 学号，唯一 |
| name | VARCHAR(50) | 姓名 |
| gender | VARCHAR(10) | 性别 |
| major | VARCHAR(100) | 专业 |
| class_name | VARCHAR(50) | 班级 |
| enrollment_year | INT | 入学年份 |

### course_score (成绩表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| student_id | VARCHAR(50) | 学号 |
| semester | VARCHAR(20) | 学期 |
| course_name | VARCHAR(100) | 课程名称 |
| score | DECIMAL(5,2) | 分数 |
| gpa | DECIMAL(3,2) | 绩点 |

### library_borrow (借阅记录表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| student_id | VARCHAR(50) | 学号 |
| borrow_date | DATE | 借阅日期 |
| book_name | VARCHAR(200) | 书名 |
| category | VARCHAR(50) | 类别 |

### consumption (消费记录表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| student_id | VARCHAR(50) | 学号 |
| consume_time | DATETIME | 消费时间 |
| amount | DECIMAL(8,2) | 金额 |
| type | VARCHAR(50) | 类型(餐饮/超市/其他) |

### risk_log (风险记录表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| student_id | VARCHAR(50) | 学号 |
| risk_level | VARCHAR(20) | HIGH/MEDIUM/LOW |
| reason | VARCHAR(500) | 判定理由 |
| update_time | DATETIME | 更新时间 |

## 索引设计
- 所有表的主键为自增ID
- student_id 建立索引（高频关联查询）
- 班级、学期、日期、类别字段建立索引（统计查询）
