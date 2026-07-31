-- ============================================================
-- TJNovel 数据库表结构（根据后端各服务 @Entity 实体还原）
-- 说明：
--   1. 项目实际由 JPA/Hibernate(ddl-auto) 自动建表；本文件用于
--      ddl-auto=none 时手动初始化，或给面试官/DBA 看整体结构。
--   2. 共 4 个库，分别对应 4 个微服务，字符集统一 utf8mb4。
--   3. 微服务之间是逻辑关联（应用层维护），未在 MySQL 建物理外键，
--      跨库的关联用 COMMENT 标注。
-- ============================================================

-- ============ 用户库 userdb（user-service） ============
CREATE DATABASE IF NOT EXISTS userdb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE userdb;

-- 读者（普通用户）
CREATE TABLE READER (
    READER_ID            BIGINT       NOT NULL AUTO_INCREMENT,
    READER_NAME          VARCHAR(20)  NOT NULL UNIQUE,
    PASSWORD             VARCHAR(255) NOT NULL,
    PHONE                VARCHAR(11),
    GENDER               VARCHAR(2),
    BALANCE              DECIMAL(10,2) DEFAULT 0.00,            -- 书币余额
    AVATAR_URL           VARCHAR(255),
    BACKGROUND_URL       VARCHAR(255),
    IS_COLLECT_VISIBLE   VARCHAR(2)   DEFAULT '是',            -- 收藏是否公开
    IS_RECOMMEND_VISIBLE VARCHAR(2)   DEFAULT '是',            -- 推荐是否公开
    CREATE_TIME          DATETIME,
    PRIMARY KEY (READER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 作者
CREATE TABLE AUTHOR (
    AUTHOR_ID            BIGINT       NOT NULL AUTO_INCREMENT,
    AUTHOR_NAME          VARCHAR(20)  NOT NULL UNIQUE,
    PASSWORD             VARCHAR(255) NOT NULL,
    PHONE                VARCHAR(11),
    AVATAR_URL           VARCHAR(255),
    INTRODUCTION         VARCHAR(500),
    EARNING              DECIMAL(18,2) DEFAULT 0.00,            -- 累计收益
    REGISTER_TIME        DATETIME,
    PRIMARY KEY (AUTHOR_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 管理员
CREATE TABLE MANAGER (
    MANAGER_ID           BIGINT       NOT NULL AUTO_INCREMENT,
    MANAGER_NAME         VARCHAR(20)  NOT NULL UNIQUE,
    PASSWORD             VARCHAR(255) NOT NULL,
    PRIMARY KEY (MANAGER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 最近阅读（复合主键：读者+小说）
CREATE TABLE RECENT_READINGS (
    READER_ID            BIGINT       NOT NULL,
    NOVEL_ID             BIGINT       NOT NULL,                -- 逻辑FK -> contentdb.NOVEL.NOVEL_ID
    CHAPTER_ID           BIGINT,
    RECENT_READING_TIME  DATETIME     NOT NULL,
    PRIMARY KEY (READER_ID, NOVEL_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============ 内容库 contentdb（content-service） ============
CREATE DATABASE IF NOT EXISTS contentdb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE contentdb;

-- 小说
CREATE TABLE NOVEL (
    NOVEL_ID             BIGINT       NOT NULL AUTO_INCREMENT,
    AUTHOR_ID            BIGINT       NOT NULL,                -- 逻辑FK -> userdb.AUTHOR.AUTHOR_ID
    NOVEL_NAME           VARCHAR(40)  NOT NULL,
    INTRODUCTION         TEXT,                                -- 简介（TEXT，非分词检索走ES）
    CREATE_TIME          DATETIME,
    COVER_URL            VARCHAR(255),
    SCORE                DECIMAL(3,1) DEFAULT 0.0,             -- 评分
    TOTAL_WORD_COUNT     BIGINT       DEFAULT 0,              -- 总字数（冗余）
    RECOMMEND_COUNT      INT          DEFAULT 0,              -- 推荐数（冗余）
    COLLECTED_COUNT      INT          DEFAULT 0,              -- 收藏数（冗余）
    STATUS               VARCHAR(10)  DEFAULT '待审核',        -- 待审核/连载/完结/封禁
    ORIGINAL_NOVEL_ID    BIGINT       DEFAULT -1,             -- 被编辑版本的原始小说
    TOTAL_PRICE          DECIMAL(10,2) DEFAULT 0.00,          -- 整本购买总价（冗余）
    PRIMARY KEY (NOVEL_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 章节（复合主键：小说ID+章节ID；正文用 LONGTEXT）
CREATE TABLE CHAPTER (
    NOVEL_ID             BIGINT       NOT NULL,               -- 逻辑FK -> NOVEL.NOVEL_ID
    CHAPTER_ID           BIGINT       NOT NULL,
    TITLE                VARCHAR(40)  NOT NULL,
    CONTENT              LONGTEXT,                            -- 正文（大文本，行溢出存储）
    WORD_COUNT           BIGINT       NOT NULL,               -- 字数（冗余，避免扫正文）
    PRICE_PER_KILO       DECIMAL(10,2) DEFAULT 0.50,          -- 每千字单价
    IS_CHARGED           VARCHAR(2)   DEFAULT '否',           -- 是否收费章节
    PUBLISH_TIME         DATETIME,
    STATUS               VARCHAR(10)  DEFAULT '草稿',         -- 草稿/已发布
    PRIMARY KEY (NOVEL_ID, CHAPTER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 分类（字典表，主键值即分类名）
CREATE TABLE CATEGORY (
    CATEGORY_NAME        VARCHAR(20)  NOT NULL,
    PRIMARY KEY (CATEGORY_NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 小说-分类关联（多对多中间表，复合主键）
CREATE TABLE NOVEL_CATEGORY (
    NOVEL_ID             BIGINT       NOT NULL,               -- 逻辑FK -> NOVEL.NOVEL_ID
    CATEGORY_NAME        VARCHAR(20)  NOT NULL,               -- 逻辑FK -> CATEGORY.CATEGORY_NAME
    PRIMARY KEY (NOVEL_ID, CATEGORY_NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评论
CREATE TABLE COMMENTS (
    COMMENT_ID           BIGINT       NOT NULL AUTO_INCREMENT,
    READER_ID            BIGINT       NOT NULL,               -- 逻辑FK -> userdb.READER.READER_ID
    NOVEL_ID             BIGINT       NOT NULL,               -- 逻辑FK -> NOVEL.NOVEL_ID
    CHAPTER_ID           BIGINT       NOT NULL,               -- 逻辑FK -> CHAPTER.CHAPTER_ID
    TITLE                VARCHAR(40)  NOT NULL,
    CONTENT              TEXT,
    LIKES                INT          DEFAULT 0,              -- 点赞数（冗余）
    STATUS               VARCHAR(10)  DEFAULT '通过',
    CREATE_TIME          DATETIME,
    PRIMARY KEY (COMMENT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评论回复层级（COMMENT_ID为评论主键，PRE_COM_ID为父评论）
CREATE TABLE COMMENT_REPLY (
    COMMENT_ID           BIGINT       NOT NULL,               -- 逻辑FK -> COMMENTS.COMMENT_ID
    PRE_COM_ID           BIGINT,                              -- 父评论ID
    COMMENT_LEVEL        INT,                                -- 评论层级
    PRIMARY KEY (COMMENT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收藏（复合主键：小说+读者）
CREATE TABLE COLLECT (
    NOVEL_ID             BIGINT       NOT NULL,               -- 逻辑FK -> NOVEL.NOVEL_ID
    READER_ID            BIGINT       NOT NULL,               -- 逻辑FK -> userdb.READER.READER_ID
    IS_PUBLIC            VARCHAR(10)  DEFAULT '是',
    PRIMARY KEY (NOVEL_ID, READER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评论点赞（复合主键：评论+读者）
CREATE TABLE LIKES (
    COMMENT_ID           BIGINT       NOT NULL,               -- 逻辑FK -> COMMENTS.COMMENT_ID
    READER_ID            BIGINT       NOT NULL,               -- 逻辑FK -> userdb.READER.READER_ID
    PRIMARY KEY (COMMENT_ID, READER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 推荐（复合主键：小说+读者）
CREATE TABLE RECOMMEND (
    NOVEL_ID             BIGINT       NOT NULL,               -- 逻辑FK -> NOVEL.NOVEL_ID
    READER_ID            BIGINT       NOT NULL,               -- 逻辑FK -> userdb.READER.READER_ID
    REASON               VARCHAR(200),
    PRIMARY KEY (NOVEL_ID, READER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评分（复合主键：小说+读者）
CREATE TABLE rate (
    novel_id             BIGINT       NOT NULL,               -- 逻辑FK -> NOVEL.NOVEL_ID
    reader_id            BIGINT       NOT NULL,               -- 逻辑FK -> userdb.READER.READER_ID
    score                DECIMAL(3,2) NOT NULL,               -- 评分值
    rating_time          DATETIME,
    PRIMARY KEY (novel_id, reader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 举报
CREATE TABLE REPORT (
    REPORT_ID            BIGINT       NOT NULL AUTO_INCREMENT,
    REASON               VARCHAR(200),
    REPORT_TIME          DATETIME,
    PROGRESS             VARCHAR(10)  DEFAULT '未处理',        -- 未处理/已处理
    COMMENT_ID           BIGINT,                              -- 逻辑FK -> COMMENTS.COMMENT_ID
    READER_ID            BIGINT       NOT NULL,               -- 逻辑FK -> userdb.READER.READER_ID
    PRIMARY KEY (REPORT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 整本购买记录（复合主键：读者+小说）
CREATE TABLE WHOLEPURCHASE (
    READER_ID            BIGINT       NOT NULL,               -- 逻辑FK -> userdb.READER.READER_ID
    NOVEL_ID             BIGINT       NOT NULL,               -- 逻辑FK -> NOVEL.NOVEL_ID
    IS_BOUGHT            VARCHAR(2)   DEFAULT '否',
    PRIMARY KEY (READER_ID, NOVEL_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============ 交易库 transactiondb（transaction-service） ============
CREATE DATABASE IF NOT EXISTS transactiondb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE transactiondb;

-- 交易流水（充值/打赏/购买均落此表）
CREATE TABLE TRANSACTION (
    TRANSACTION_ID       BIGINT       NOT NULL AUTO_INCREMENT,
    READER_ID            BIGINT       NOT NULL,               -- 逻辑FK -> userdb.READER.READER_ID
    TRANS_TYPE           VARCHAR(10)  NOT NULL,               -- 充值/打赏/购买
    AMOUNT               DECIMAL(10,2) NOT NULL,              -- 金额（书币）
    TIME                 DATETIME,
    PRIMARY KEY (TRANSACTION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 打赏明细（与主表一对一，TRANSACTION_ID 即主键）
CREATE TABLE REWARD (
    TRANSACTION_ID       BIGINT       NOT NULL,               -- 逻辑FK(一对一) -> TRANSACTION.TRANSACTION_ID
    NOVEL_ID             BIGINT       NOT NULL,               -- 逻辑FK -> contentdb.NOVEL.NOVEL_ID
    PRIMARY KEY (TRANSACTION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 章节购买明细（与主表一对一）
CREATE TABLE PURCHASE (
    TRANSACTION_ID       BIGINT       NOT NULL,               -- 逻辑FK(一对一) -> TRANSACTION.TRANSACTION_ID
    CHAPTER_ID           BIGINT       NOT NULL,               -- 逻辑FK -> contentdb.CHAPTER.CHAPTER_ID
    NOVEL_ID             BIGINT       NOT NULL,               -- 逻辑FK -> contentdb.NOVEL.NOVEL_ID
    PRIMARY KEY (TRANSACTION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 作者收入明细
CREATE TABLE AUTHOR_INCOME (
    ID                   BIGINT       NOT NULL AUTO_INCREMENT,
    AUTHOR_ID            BIGINT       NOT NULL,               -- 逻辑FK -> userdb.AUTHOR.AUTHOR_ID
    TYPE                 VARCHAR(20)  NOT NULL,               -- 打赏/订阅等
    AMOUNT               DECIMAL(10,2) NOT NULL,
    CREATE_TIME          DATETIME,
    NOVEL_ID             BIGINT,                              -- 逻辑FK -> contentdb.NOVEL.NOVEL_ID
    PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============ 管理库 admindb（admin-service） ============
CREATE DATABASE IF NOT EXISTS admindb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE admindb;

-- 管理操作主记录（审核/封禁等动作）
CREATE TABLE MANAGEMENT (
    MANAGEMENT_ID        BIGINT       NOT NULL AUTO_INCREMENT,
    MANAGER_ID           BIGINT       NOT NULL,               -- 逻辑FK -> userdb.MANAGER.MANAGER_ID
    RESULT               VARCHAR(200),
    TIME                 DATETIME,
    PRIMARY KEY (MANAGEMENT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 小说管理关联
CREATE TABLE NOVEL_MANAGEMENT (
    MANAGEMENT_ID        BIGINT       NOT NULL,               -- 逻辑FK(一对一) -> MANAGEMENT.MANAGEMENT_ID
    NOVEL_ID             BIGINT       NOT NULL,               -- 逻辑FK -> contentdb.NOVEL.NOVEL_ID
    PRIMARY KEY (MANAGEMENT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 章节管理关联
CREATE TABLE CHAPTER_MANAGEMENT (
    MANAGEMENT_ID        BIGINT       NOT NULL,               -- 逻辑FK(一对一) -> MANAGEMENT.MANAGEMENT_ID
    NOVEL_ID             BIGINT       NOT NULL,
    CHAPTER_ID           BIGINT       NOT NULL,
    PRIMARY KEY (MANAGEMENT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评论管理关联
CREATE TABLE COMMENT_MANAGEMENT (
    MANAGEMENT_ID        BIGINT       NOT NULL,               -- 逻辑FK(一对一) -> MANAGEMENT.MANAGEMENT_ID
    COMMENT_ID           BIGINT       NOT NULL,               -- 逻辑FK -> contentdb.COMMENTS.COMMENT_ID
    PRIMARY KEY (MANAGEMENT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 举报管理关联
CREATE TABLE REPORT_MANAGEMENT (
    MANAGEMENT_ID        BIGINT       NOT NULL,               -- 逻辑FK(一对一) -> MANAGEMENT.MANAGEMENT_ID
    REPORT_ID            BIGINT       NOT NULL,               -- 逻辑FK -> contentdb.REPORT.REPORT_ID
    PRIMARY KEY (MANAGEMENT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
