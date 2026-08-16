-- TJNovel 追加数据脚本 v2（保持跨库一致性）
-- 真实上界：AUTHOR 16, READER 12, NOVEL 49, COMMENT 116
-- 复用现有作者(14-16)与读者(10-12)；新小说从50起，新评论从117起。
-- 所有外键均指向真实实体；交易保证 READER.BALANCE 与 AUTHOR.EARNING 与流水一致。

SET NAMES utf8mb4;
START TRANSACTION;

SET @n1 = 50;
SET @n2 = 51;
SET @n3 = 52;
SET @n4 = 53;
SET @n5 = 54;
SET @n6 = 55;
SET @n7 = 56;
SET @n8 = 57;
SET @c1 = 117;

-- ============ 1. 新增小说（关联真实作者 14/15/16） ============
INSERT INTO contentdb.novel (NOVEL_ID, AUTHOR_ID, NOVEL_NAME, INTRODUCTION, CREATE_TIME, COVER_URL, SCORE, TOTAL_WORD_COUNT, RECOMMEND_COUNT, COLLECTED_COUNT, STATUS, ORIGINAL_NOVEL_ID, TOTAL_PRICE) VALUES
(@n1, 14, '青云问心',   '剑修问心，一念成魔一念成佛，他在正邪之间寻自己的道。', '2025-01-05 10:00:00', 'https://picsum.photos/seed/novel50/300/400', 8.5, 0, 0, 0, '连载', -1, 0.00),
(@n2, 15, '汴京风云',   '一纸婚书牵动朝堂，小人物在汴京的棋局里落子无悔。', '2025-02-10 09:30:00', 'https://picsum.photos/seed/novel51/300/400', 9.0, 0, 0, 0, '连载', -1, 0.00),
(@n3, 16, '银河拓荒',   '人类第一支深空舰队启航，拓荒者的墓碑写满星辰的名字。', '2025-03-01 15:20:00', 'https://picsum.photos/seed/novel52/300/400', 8.8, 0, 0, 0, '连载', -1, 0.00),
(@n4, 14, '余生请多指教', '失忆的她敲开他的门，两个破碎的人拼成完整的余生。', '2025-01-20 20:00:00', 'https://picsum.photos/seed/novel53/300/400', 9.2, 0, 0, 0, '完结', -1, 0.00),
(@n5, 15, '商海浮沉',   '白手起家的青年在资本洪流中沉浮，终成一代商业传奇。', '2025-02-25 11:00:00', 'https://picsum.photos/seed/novel54/300/400', 8.3, 0, 0, 0, '完结', -1, 0.00),
(@n6, 16, '无声证词',   '法医手中的每一份报告，都是死者最后的证词。', '2025-03-15 19:00:00', 'https://picsum.photos/seed/novel55/300/400', 9.1, 0, 0, 0, '连载', -1, 0.00),
(@n7, 14, '江湖夜雨',   '十年一觉扬州梦，醒来看见满身风雨与未凉的热血。', '2025-01-12 13:00:00', 'https://picsum.photos/seed/novel56/300/400', 8.9, 0, 0, 0, '完结', -1, 0.00),
(@n8, 15, '时间裂缝',   '她在旧钟楼里拧动了时间，裂缝另一端站着童年的自己。', '2025-02-18 22:00:00', 'https://picsum.photos/seed/novel57/300/400', 8.7, 0, 0, 0, '连载', -1, 0.00);

-- ============ 2. 新增章节（每本3章：ch1免费，ch2/ch3收费） ============
INSERT INTO contentdb.chapter (NOVEL_ID, CHAPTER_ID, TITLE, CONTENT, WORD_COUNT, PRICE_PER_KILO, IS_CHARGED, PUBLISH_TIME, STATUS) VALUES
(@n1,1,'入魔',     '心魔初现，他在镜中看见另一个自己。', 2980, 0.50, '否', '2025-01-05 10:05:00', '已发布'),
(@n1,2,'问心',     '一念之间，剑锋偏了半寸，却问出了真心。', 3120, 0.50, '是', '2025-01-08 10:05:00', '已发布'),
(@n1,3,'证道',     '风雪夜，他于悬崖之巅证得属于自己的道。', 3045, 0.50, '是', '2025-01-12 10:05:00', '已发布'),
(@n2,1,'婚书',     '红纸婚书落下，汴京的棋盘悄然转动。', 3050, 0.50, '否', '2025-02-10 09:35:00', '已发布'),
(@n2,2,'棋局',     '朝堂之上的每一步，都是生死博弈。', 3200, 0.50, '是', '2025-02-14 09:35:00', '已发布'),
(@n2,3,'落子',     '他落子无悔，也赌上了整座汴京的安稳。', 3150, 0.50, '是', '2025-02-18 09:35:00', '已发布'),
(@n3,1,'启航',     '引擎轰鸣，深空舰队驶入人类从未触及的黑暗。', 2900, 0.50, '否', '2025-03-01 15:25:00', '已发布'),
(@n3,2,'拓荒',     '第一面旗帜插上无名星，血与尘俱是勋章。', 3300, 0.50, '是', '2025-03-05 15:25:00', '已发布'),
(@n3,3,'墓碑',     '每一座墓碑都刻着星辰，写给回不去的地球。', 3180, 0.50, '是', '2025-03-10 15:25:00', '已发布'),
(@n4,1,'敲门',     '雨夜，她忘了自己是谁，只记得要敲这扇门。', 2800, 0.50, '否', '2025-01-20 20:05:00', '已发布'),
(@n4,2,'拼图',     '两个破碎的灵魂，一点点拼回完整的形状。', 3050, 0.50, '是', '2025-01-24 20:05:00', '已发布'),
(@n4,3,'余生',     '她说，余生请多指教，他红了眼眶。', 3000, 0.50, '是', '2025-01-28 20:05:00', '已发布'),
(@n5,1,'起点',     '一间租来的门面，是他商业帝国的第一块砖。', 2950, 0.50, '否', '2025-02-25 11:05:00', '已发布'),
(@n5,2,'风浪',     '资本围剿之下，他第一次尝到失败的苦。', 3100, 0.50, '是', '2025-03-01 11:05:00', '已发布'),
(@n5,3,'传奇',     '潮水退去，他站在岸上，成了别人眼中的传奇。', 3250, 0.50, '是', '2025-03-06 11:05:00', '已发布'),
(@n6,1,'尸检',     '第一份报告指出，死因并不简单。', 2850, 0.50, '否', '2025-03-15 19:05:00', '已发布'),
(@n6,2,'线索',     '显微镜下的纤维，指向一个被忽略的人。', 3050, 0.50, '是', '2025-03-19 19:05:00', '已发布'),
(@n6,3,'证词',     '无声的证词，终于让真相浮出水面。', 3120, 0.50, '是', '2025-03-24 19:05:00', '已发布'),
(@n7,1,'扬州',     '十年扬州梦，醒来已是满身风雨。', 3000, 0.50, '否', '2025-01-12 13:05:00', '已发布'),
(@n7,2,'夜雨',     '江湖夜雨十年灯，故人已在灯火阑珊处。', 3300, 0.50, '是', '2025-01-17 13:05:00', '已发布'),
(@n7,3,'热血',     '未曾凉的热血，仍在胸中奔腾如初。', 3180, 0.50, '是', '2025-01-22 13:05:00', '已发布'),
(@n8,1,'钟楼',     '旧钟楼的指针停摆，时间在此处裂开缝隙。', 2900, 0.50, '否', '2025-02-18 22:05:00', '已发布'),
(@n8,2,'裂缝',     '裂缝那端，童年自己正怯生生地回望。', 3200, 0.50, '是', '2025-02-22 22:05:00', '已发布'),
(@n8,3,'重逢',     '她牵起童年的手，把遗憾轻轻放下。', 3100, 0.50, '是', '2025-02-27 22:05:00', '已发布');

-- ============ 3. 小说分类 ============
INSERT INTO contentdb.novel_category (NOVEL_ID, CATEGORY_NAME) VALUES
(@n1,'玄幻'),(@n1,'都市'),
(@n2,'历史'),
(@n3,'科幻'),
(@n4,'言情'),(@n4,'都市'),
(@n5,'都市'),
(@n6,'悬疑'),
(@n7,'玄幻'),
(@n8,'科幻'),(@n8,'言情');

-- ============ 4. 评论（引用真实读者/小说/章节） ============
INSERT INTO contentdb.comments (COMMENT_ID, READER_ID, NOVEL_ID, CHAPTER_ID, TITLE, CONTENT, LIKES, STATUS, CREATE_TIME) VALUES
(@c1,   10, @n1, 2, '问心封神', '剑锋偏半寸那段写得太绝了！', 14, '通过', '2025-01-09 11:00:00'),
(@c1+1, 11, @n1, 3, '证道泪目', '悬崖证道，我跟着哭了。', 9,  '通过', '2025-01-13 21:00:00'),
(@c1+2, 12, @n2, 2, '朝堂戏精', '汴京的博弈写得人心悬一线。', 16, '通过', '2025-02-15 10:00:00'),
(@c1+3, 10, @n3, 2, '硬核科幻', '拓荒的描写有《三体》那味儿了。', 11, '通过', '2025-03-06 16:00:00'),
(@c1+4, 11, @n4, 2, '甜哭我了', '拼图那章太治愈，二刷落泪。', 23, '通过', '2025-01-25 23:00:00'),
(@c1+5, 12, @n5, 2, '商战带感', '失败那章很真实，不悬浮。', 12, '通过', '2025-03-02 12:00:00'),
(@c1+6, 10, @n6, 2, '细节控', '纤维线索伏笔收得漂亮。', 8,  '通过', '2025-03-20 20:00:00'),
(@c1+7, 11, @n7, 2, '江湖味浓', '夜雨十年灯，年度最佳。', 19, '通过', '2025-01-18 14:00:00'),
(@c1+8, 12, @n8, 2, '时间浪漫', '裂缝设定温柔又心碎。', 15, '通过', '2025-02-23 23:00:00'),
(@c1+9, 10, @n2, 3, '落子无悔', '赌上汴京，男人浪漫至极。', 24, '通过', '2025-02-19 09:00:00');

-- ============ 5. 评论点赞 ============
INSERT INTO contentdb.likes (COMMENT_ID, READER_ID) VALUES
(@c1,   11),(@c1,   12),
(@c1+1, 10),
(@c1+2, 10),(@c1+2, 11),
(@c1+4, 10),(@c1+4, 12),
(@c1+7, 10),(@c1+7, 12),
(@c1+9, 11),(@c1+9, 12);

-- ============ 6. 评论回复（楼中楼） ============
INSERT INTO contentdb.comment_reply (COMMENT_ID, PRE_COM_ID, COMMENT_LEVEL) VALUES
(@c1+1, @c1,   2),
(@c1+3, @c1+2, 2),
(@c1+6, @c1+5, 2);

-- ============ 7. 收藏 ============
INSERT INTO contentdb.collect (NOVEL_ID, READER_ID, IS_PUBLIC) VALUES
(@n1, 10, '是'),(@n1, 11, '是'),
(@n2, 12, '是'),
(@n3, 10, '否'),
(@n4, 11, '是'),(@n4, 12, '是'),
(@n5, 10, '是'),
(@n7, 11, '是'),
(@n8, 12, '否');

-- ============ 8. 评分 ============
INSERT INTO contentdb.rate (novel_id, reader_id, score, rating_time) VALUES
(@n1, 10, 8.5, '2025-01-13 10:00:00'),
(@n1, 11, 8.7, '2025-01-14 10:00:00'),
(@n2, 12, 9.0, '2025-02-19 10:00:00'),
(@n3, 10, 8.8, '2025-03-11 10:00:00'),
(@n4, 11, 9.2, '2025-01-29 10:00:00'),
(@n5, 12, 8.3, '2025-03-07 10:00:00'),
(@n6, 10, 9.1, '2025-03-25 10:00:00'),
(@n7, 11, 8.9, '2025-01-23 10:00:00'),
(@n8, 12, 8.7, '2025-03-01 10:00:00');

-- ============ 9. 推荐 ============
INSERT INTO contentdb.recommend (NOVEL_ID, READER_ID, REASON) VALUES
(@n1, 10, '问心写得极好，仙侠爱好者必看。'),
(@n2, 12, '汴京风云权谋在线，历史迷狂喜。'),
(@n4, 11, '治愈系言情，睡前读物首选。'),
(@n7, 11, '江湖夜雨，武侠老味道。'),
(@n8, 12, '时间裂缝温柔又烧脑。');

-- ============ 10. 最近阅读 ============
INSERT INTO userdb.recent_readings (READER_ID, NOVEL_ID, CHAPTER_ID, RECENT_READING_TIME) VALUES
(10, @n1, 3, '2025-01-13 21:30:00'),
(10, @n3, 2, '2025-03-06 16:30:00'),
(11, @n4, 2, '2025-01-25 23:10:00'),
(11, @n7, 2, '2025-01-18 14:30:00'),
(12, @n2, 3, '2025-02-19 09:10:00'),
(12, @n8, 2, '2025-02-23 23:10:00');

-- ============ 11. 举报（引用真实评论） ============
INSERT INTO contentdb.report (REASON, COMMENT_ID, READER_ID, PROGRESS, REPORT_TIME) VALUES
('疑似剧透', @c1+2, 11, '未处理', '2025-02-16 11:00:00'),
('引战',     @c1+5, 10, '已处理', '2025-03-03 13:00:00');

-- ============ 12. 交易：购买收费章节（读者10/11/12 消费，金额同步扣减并更新作者收益） ============
-- 购买1：读者10 买 n1 第2章
SET @r=10; SET @nid=@n1; SET @cid=2;
SET @amt=(SELECT ROUND(WORD_COUNT/1000*PRICE_PER_KILO,0) FROM contentdb.chapter WHERE NOVEL_ID=@nid AND CHAPTER_ID=@cid);
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '购买章节', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.purchase (TRANSACTION_ID, CHAPTER_ID, NOVEL_ID) VALUES (@tid, @cid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '章节分成', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 购买2：读者10 买 n1 第3章
SET @r=10; SET @nid=@n1; SET @cid=3;
SET @amt=(SELECT ROUND(WORD_COUNT/1000*PRICE_PER_KILO,0) FROM contentdb.chapter WHERE NOVEL_ID=@nid AND CHAPTER_ID=@cid);
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '购买章节', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.purchase (TRANSACTION_ID, CHAPTER_ID, NOVEL_ID) VALUES (@tid, @cid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '章节分成', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 购买3：读者10 买 n7 第2章
SET @r=10; SET @nid=@n7; SET @cid=2;
SET @amt=(SELECT ROUND(WORD_COUNT/1000*PRICE_PER_KILO,0) FROM contentdb.chapter WHERE NOVEL_ID=@nid AND CHAPTER_ID=@cid);
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '购买章节', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.purchase (TRANSACTION_ID, CHAPTER_ID, NOVEL_ID) VALUES (@tid, @cid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '章节分成', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 购买4：读者11 买 n2 第2章
SET @r=11; SET @nid=@n2; SET @cid=2;
SET @amt=(SELECT ROUND(WORD_COUNT/1000*PRICE_PER_KILO,0) FROM contentdb.chapter WHERE NOVEL_ID=@nid AND CHAPTER_ID=@cid);
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '购买章节', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.purchase (TRANSACTION_ID, CHAPTER_ID, NOVEL_ID) VALUES (@tid, @cid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '章节分成', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 购买5：读者11 买 n4 第2章
SET @r=11; SET @nid=@n4; SET @cid=2;
SET @amt=(SELECT ROUND(WORD_COUNT/1000*PRICE_PER_KILO,0) FROM contentdb.chapter WHERE NOVEL_ID=@nid AND CHAPTER_ID=@cid);
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '购买章节', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.purchase (TRANSACTION_ID, CHAPTER_ID, NOVEL_ID) VALUES (@tid, @cid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '章节分成', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 购买6：读者11 买 n7 第2章
SET @r=11; SET @nid=@n7; SET @cid=2;
SET @amt=(SELECT ROUND(WORD_COUNT/1000*PRICE_PER_KILO,0) FROM contentdb.chapter WHERE NOVEL_ID=@nid AND CHAPTER_ID=@cid);
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '购买章节', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.purchase (TRANSACTION_ID, CHAPTER_ID, NOVEL_ID) VALUES (@tid, @cid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '章节分成', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 购买7：读者12 买 n5 第2章
SET @r=12; SET @nid=@n5; SET @cid=2;
SET @amt=(SELECT ROUND(WORD_COUNT/1000*PRICE_PER_KILO,0) FROM contentdb.chapter WHERE NOVEL_ID=@nid AND CHAPTER_ID=@cid);
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '购买章节', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.purchase (TRANSACTION_ID, CHAPTER_ID, NOVEL_ID) VALUES (@tid, @cid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '章节分成', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 购买8：读者12 买 n6 第2章
SET @r=12; SET @nid=@n6; SET @cid=2;
SET @amt=(SELECT ROUND(WORD_COUNT/1000*PRICE_PER_KILO,0) FROM contentdb.chapter WHERE NOVEL_ID=@nid AND CHAPTER_ID=@cid);
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '购买章节', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.purchase (TRANSACTION_ID, CHAPTER_ID, NOVEL_ID) VALUES (@tid, @cid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '章节分成', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 购买9：读者12 买 n8 第2章
SET @r=12; SET @nid=@n8; SET @cid=2;
SET @amt=(SELECT ROUND(WORD_COUNT/1000*PRICE_PER_KILO,0) FROM contentdb.chapter WHERE NOVEL_ID=@nid AND CHAPTER_ID=@cid);
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '购买章节', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.purchase (TRANSACTION_ID, CHAPTER_ID, NOVEL_ID) VALUES (@tid, @cid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '章节分成', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- ============ 13. 交易：打赏（读者打赏小说，金额同步更新） ============
-- 打赏1：读者10 打赏 n7
SET @r=10; SET @nid=@n7; SET @amt=10.00;
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '打赏', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.reward (TRANSACTION_ID, NOVEL_ID) VALUES (@tid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '打赏收入', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 打赏2：读者11 打赏 n2
SET @r=11; SET @nid=@n2; SET @amt=20.00;
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '打赏', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.reward (TRANSACTION_ID, NOVEL_ID) VALUES (@tid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '打赏收入', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 打赏3：读者11 打赏 n4
SET @r=11; SET @nid=@n4; SET @amt=15.00;
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '打赏', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.reward (TRANSACTION_ID, NOVEL_ID) VALUES (@tid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '打赏收入', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 打赏4：读者12 打赏 n1
SET @r=12; SET @nid=@n1; SET @amt=12.00;
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '打赏', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.reward (TRANSACTION_ID, NOVEL_ID) VALUES (@tid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '打赏收入', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

-- 打赏5：读者12 打赏 n8
SET @r=12; SET @nid=@n8; SET @amt=18.00;
SET @aid=(SELECT AUTHOR_ID FROM contentdb.novel WHERE NOVEL_ID=@nid);
INSERT INTO transactiondb.transaction (READER_ID, TRANS_TYPE, AMOUNT) VALUES (@r, '打赏', @amt);
SET @tid=LAST_INSERT_ID();
INSERT INTO transactiondb.reward (TRANSACTION_ID, NOVEL_ID) VALUES (@tid, @nid);
INSERT INTO transactiondb.author_income (AUTHOR_ID, TYPE, AMOUNT, NOVEL_ID) VALUES (@aid, '打赏收入', @amt, @nid);
UPDATE userdb.reader SET BALANCE=BALANCE-@amt WHERE READER_ID=@r;
UPDATE userdb.author SET EARNING=EARNING+@amt WHERE AUTHOR_ID=@aid;

COMMIT;
