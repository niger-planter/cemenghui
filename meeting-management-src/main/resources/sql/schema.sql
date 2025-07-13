CREATE DATABASE IF NOT EXISTS meeting_db CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
USE meeting_db;

DROP TABLE IF EXISTS t_meeting;
CREATE TABLE t_meeting (
    id SERIAL PRIMARY KEY COMMENT '会议ID',
    meeting_name VARCHAR(255) NOT NULL COMMENT '会议名称',
    creator VARCHAR(100) NOT NULL COMMENT '创建人',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-进行中，1-已结束',
    content TEXT COMMENT '会议内容',
    cover_image VARCHAR(255) COMMENT '会议封面图片路径',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) DEFAULT CHARSET=utf8mb4 COMMENT='会议表';

-- 插入测试数据
INSERT INTO t_meeting (meeting_name, creator, start_time, end_time, status, content, cover_image)
VALUES ('项目启动会议', '张三', '2025-06-01 09:00:00', '2025-06-01 11:00:00', 1, '讨论项目启动计划和任务分配', '/uploads/covers/meeting1.jpg'),
       ('技术研讨会', '李四', '2025-06-10 14:00:00', '2025-06-10 16:30:00', 1, '讨论系统架构设计和技术选型', '/uploads/covers/meeting2.jpg'),
       ('周进度会议', '王五', '2025-06-20 10:00:00', '2025-06-20 11:00:00', 0, '每周项目进度汇报和问题讨论', '/uploads/covers/meeting3.jpg');