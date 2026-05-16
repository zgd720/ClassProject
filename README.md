# 基于多源数据的大学生学业行为分析助手

基于多源数据（成绩、消费、借阅）的大学生学业行为分析系统，帮助辅导员快速了解学生学业状态，提供个性化AI学习建议。

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端 | Java 17, Spring Boot 3.2, Mybatis-Plus, Maven |
| 数据库 | MySQL 8.0 |
| 前端 | Vue 3 (Vite), Tailwind CSS, Axios, ECharts |
| API文档 | SpringDoc OpenAPI (Swagger) |
| AI调用 | 智谱 GLM-4-Flash API |
| 容器化 | Docker + Docker Compose |

## 快速启动

### 前置条件
- Docker & Docker Compose
- 智谱AI API Key（可选，用于AI建议功能）

### 配置AI API Key（可选）
编辑 `backend/src/main/resources/application.yml`，将 `ai.api.key` 替换为你的智谱AI API Key。

### 一键启动
```bash
docker compose up --build
```

启动后访问：
- **前端页面**：http://localhost
- **Swagger API文档**：http://localhost:8080/swagger-ui/index.html

### 测试账号
| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 辅导员 | counselor | 123456 |
| 学生 | student01 | 123456 |

## 功能模块

1. **数据管理**：支持导入学生信息、成绩、借阅、消费四类CSV文件
2. **学生画像**：学生列表（搜索/分页/风险筛选）、详细信息（学业/生活趋势图表）
3. **风险提示**：基于挂科数和绩点的自动风险分级（高/中/低）
4. **AI建议**：调用大模型生成个性化学习建议（超时降级兜底）
5. **可视化看板**：风险分布、绩点趋势、借阅类别、消费时段图表

## 项目结构

```
├── docker-compose.yml
├── README.md
├── sql/schema.sql              # 数据库DDL
├── docs/
│   ├── database_design.md      # 数据库设计文档
│   └── ai_module.md            # AI模块说明
├── backend/                    # Spring Boot后端
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/example/academic/
└── frontend/                   # Vue 3前端
    ├── Dockerfile
    ├── nginx.conf
    └── src/
```

## CSV数据格式

### 学生信息 (student.csv)
```csv
student_id,name,gender,major,class_name,enrollment_year
20210101001,张三,男,软件工程,软件工程2101,2021
```

### 成绩记录 (course_score.csv)
```csv
student_id,semester,course_name,score,gpa
20210101001,2021-2022-1,高等数学,85,3.0
```

### 借阅记录 (library_borrow.csv)
```csv
student_id,borrow_date,book_name,category
20210101001,2024-03-15,算法导论,科技
```

### 消费记录 (consumption.csv)
```csv
student_id,consume_time,amount,type
20210101001,2024-03-15 12:00:00,15.00,餐饮
```
