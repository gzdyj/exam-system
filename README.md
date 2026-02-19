# 计算机题目考试系统

一个基于 Spring Boot + Vue.js 的在线考试系统，支持教师管理题库、试卷，学生在线考试、自动评分等功能。

## 功能特性

### 教师端
- 📊 **仪表盘** - 数据统计概览
- 📝 **题库管理** - 添加、编辑、删除选择题和判断题
- 📋 **试卷管理** - 组卷、设置考试时长和分值
- 👥 **学生管理** - 管理学生账号
- 📈 **考试记录** - 查看学生考试成绩

### 学生端
- 🎯 **在线考试** - 参加考试、实时倒计时
- 📝 **答题界面** - 友好的答题体验
- 📊 **成绩查询** - 查看历史考试成绩

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- MyBatis-Plus
- MariaDB/MySQL

### 前端
- Vue 3 + TypeScript
- Vite
- Element Plus
- Tailwind CSS
- Pinia (状态管理)
- Vue Router

## 项目结构

```
exam-system/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/exam/
│   │   │   │   ├── config/      # 配置类
│   │   │   │   ├── controller/  # 控制器
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── mapper/      # MyBatis Mapper
│   │   │   │   ├── service/     # 服务层
│   │   │   │   └── util/        # 工具类
│   │   │   └── resources/
│   │   │       ├── sql/         # SQL脚本
│   │   │       └── application.yml
│   │   └── test/
│   └── pom.xml
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/            # API请求
│   │   ├── components/     # 组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理
│   │   └── views/          # 页面
│   ├── index.html
│   ├── vite.config.ts
│   └── package.json
├── nginx.conf              # Nginx配置
├── docker-compose.yml      # Docker编排
└── README.md
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+ / MariaDB 10.6+
- Maven 3.8+

### 后端启动

1. 创建数据库并导入初始数据：
```bash
mysql -u root -p < backend/src/main/resources/sql/init.sql
```

2. 修改数据库配置（`backend/src/main/resources/application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/exam_db
    username: root
    password: your_password
```

3. 启动后端服务：
```bash
cd backend
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173

### Docker 部署

```bash
# 构建前端
cd frontend
npm install
npm run build

# 启动服务
docker-compose up -d
```

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 教师 | admin | admin123 |
| 学生 | student | student123 |

## API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 题目接口
- `GET /api/questions` - 获取题目列表
- `POST /api/questions` - 添加题目
- `PUT /api/questions/{id}` - 更新题目
- `DELETE /api/questions/{id}` - 删除题目

### 试卷接口
- `GET /api/papers` - 获取试卷列表
- `GET /api/papers/{id}` - 获取试卷详情
- `POST /api/papers` - 创建试卷

### 考试记录接口
- `GET /api/exam-records` - 获取考试记录
- `POST /api/exam-records/start` - 开始考试
- `POST /api/exam-records/{id}/submit` - 提交试卷

## 在线演示

- 演示地址：http://111.229.95.128

## 许可证

MIT License
