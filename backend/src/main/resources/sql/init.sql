-- 创建数据库
CREATE DATABASE IF NOT EXISTS exam_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE exam_db;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role INT NOT NULL DEFAULT 2 COMMENT '角色: 1-教师 2-学生',
    status INT NOT NULL DEFAULT 1 COMMENT '状态: 1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 题目表
CREATE TABLE IF NOT EXISTS t_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL COMMENT '题目内容',
    type INT NOT NULL COMMENT '类型: 1-选择题 2-判断题',
    option_a VARCHAR(200) COMMENT '选项A',
    option_b VARCHAR(200) COMMENT '选项B',
    option_c VARCHAR(200) COMMENT '选项C',
    option_d VARCHAR(200) COMMENT '选项D',
    answer VARCHAR(10) NOT NULL COMMENT '正确答案',
    score INT NOT NULL DEFAULT 5 COMMENT '分值',
    analysis TEXT COMMENT '解析',
    create_by BIGINT COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 试卷表
CREATE TABLE IF NOT EXISTS t_paper (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL COMMENT '试卷标题',
    question_ids VARCHAR(500) COMMENT '题目ID列表，逗号分隔',
    total_score INT DEFAULT 0 COMMENT '总分',
    duration INT NOT NULL DEFAULT 60 COMMENT '考试时长(分钟)',
    create_by BIGINT COMMENT '创建人ID',
    status INT DEFAULT 1 COMMENT '状态: 1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 考试记录表
CREATE TABLE IF NOT EXISTS t_exam_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    answers TEXT COMMENT '用户答案，JSON格式',
    score INT DEFAULT 0 COMMENT '得分',
    status INT DEFAULT 0 COMMENT '状态: 1-已完成 0-进行中',
    start_time DATETIME COMMENT '开始时间',
    submit_time DATETIME COMMENT '提交时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES t_user(id),
    FOREIGN KEY (paper_id) REFERENCES t_paper(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化默认教师账号: admin/admin123
INSERT INTO t_user (username, password, real_name, role, status)
VALUES ('admin', '$2b$10$nm9vHOx5Qu2dyCP5eCMzc.heB65vmSvwW/SPbJVWKES3.NiFReQ6q', '管理员', 1, 1);

-- 初始化默认学生账号: student/student123
INSERT INTO t_user (username, password, real_name, role, status)
VALUES ('student', '$2b$12$cc3t2dtktaqU/n0rt5nUseXtsd5gcqpoovOgbvzTApR1a7l3H2CP2', '学生', 2, 1);

-- 初始化示例题目
INSERT INTO t_question (content, type, option_a, option_b, option_c, option_d, answer, score, analysis, create_by) VALUES
('下列不是Java关键字的是？', 1, 'class', 'interface', 'extends', 'implement', 'D', 5, 'interface和class都是Java的关键字，extends和implements也是，而D选项不是', 1),
('Python中用于定义函数的关键字是？', 1, 'function', 'def', 'func', 'define', 'B', 5, 'Python使用def关键字定义函数', 1),
('下列哪个是HTML5新增的语义化标签？', 1, '<div>', '<span>', '<article>', '<font>', 'C', 5, '<article>是HTML5新增的语义化标签', 1),
('在JavaScript中，以下哪个方法可以向数组末尾添加元素？', 1, 'push()', 'pop()', 'shift()', 'unshift()', 'A', 5, 'push()方法向数组末尾添加元素', 1),
('关系型数据库不包括以下哪个？', 1, 'MySQL', 'Oracle', 'MongoDB', 'SQL Server', 'C', 5, 'MongoDB是非关系型数据库', 1),
('下列运算符中，优先级最高的是？', 1, '+', '*', '>', '=', 'B', 5, '算术运算符优先级高于关系运算符', 1),
('CSS中用于设置元素_display_属性的值不包括？', 1, 'block', 'inline', 'none', 'show', 'D', 5, 'show不是display的有效值', 1),
('Linux中查看当前目录的命令是？', 1, 'ls', 'pwd', 'cd', 'dir', 'B', 5, 'pwd命令用于显示当前目录', 1),
('下列哪种排序算法的时间复杂度是O(n log n)？', 1, '冒泡排序', '选择排序', '快速排序', '插入排序', 'C', 5, '快速排序平均时间复杂度为O(n log n)', 1),
('HTTP状态码404表示？', 1, '服务器错误', '页面未找到', '权限不足', '请求超时', 'B', 5, '404表示请求的资源不存在', 1),
('判断题：Java是一种编译型语言。', 2, '正确', '错误', NULL, NULL, 'A', 5, 'Java源代码先编译成字节码，再由JVM解释执行', 1),
('判断题：Python中缩进是有语法意义的。', 2, '正确', '错误', NULL, NULL, 'A', 5, 'Python使用缩进来表示代码块', 1),
('判断题：SQL语言中INSERT用于查询数据。', 2, '正确', '错误', NULL, NULL, 'B', 5, 'INSERT用于插入数据，SELECT用于查询', 1),
('判断题：HTML是一种编程语言。', 2, '正确', '错误', NULL, NULL, 'B', 5, 'HTML是标记语言，不是编程语言', 1),
('判断题：Git是一种版本控制系统。', 2, '正确', '错误', NULL, NULL, 'A', 5, 'Git是分布式版本控制系统', 1);

-- 初始化示例试卷
INSERT INTO t_paper (title, question_ids, total_score, duration, create_by, status) 
VALUES ('计算机基础测试', '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15', 75, 60, 1, 1);
