# -*- coding: utf-8 -*-
"""
TJNovel 种子数据生成脚本
- 生成 4 个数据库（userdb/contentdb/admindb/transactiondb）的全量测试数据
- 保证跨表数据一致性（余额/收益/统计数/评分等）
- 密码使用与后端一致的 PBKDF2WithHmacSHA256(FixedSalt123456, 10000, 120bit) -> Base64 前 20 字符
"""
import hashlib
import base64
import random
from datetime import datetime, timedelta

random.seed(20260814)

# ---------------- 工具 ----------------
def hash_password(pwd: str) -> str:
    dk = hashlib.pbkdf2_hmac('sha256', pwd.encode('utf-8'), b'FixedSalt123456', 10000, dklen=15)
    return base64.b64encode(dk).decode('ascii')[:20]

def esc(s) -> str:
    if s is None:
        return 'NULL'
    return "'" + str(s).replace('\\', '\\\\').replace("'", "''") + "'"

def now_offset(days=0, hours=0):
    return (datetime(2026, 8, 10, 10, 0, 0) + timedelta(days=days, hours=hours)).strftime('%Y-%m-%d %H:%M:%S')

# ---------------- 章节内容生成 ----------------
PARA_TEMPLATES = [
    "夜色如墨，风从旷野尽头席卷而来。他立在城头，望着远处若隐若现的灯火，心中翻涌着难以言说的情绪。",
    "这一战，无人知晓会以怎样的方式落幕。但他握紧手中的剑，目光如炬，没有半分退缩的意思。",
    "她轻轻推开窗，月光便洒了进来。那些深埋在记忆里的旧事，像潮水一般涌上心头，挥之不去。",
    "所谓命运，不过是一次次选择叠加而成的轨迹。而此刻，站在岔路口的他们，都将走向截然不同的未来。",
    "消息传开的时候，整个客栈都安静了一瞬。随后是窃窃私语，再然后，便是一阵压抑的骚动。",
    "他深吸一口气，将那些纷乱的念头压下去。眼下要做的事只有一件，而他已经没有退路。",
    "雨下了整夜，直到天明才停。泥泞的小路上留下两行脚印，一深一浅，通向未知的远方。",
    "古籍上记载的传说，竟在今日得到了印证。他看着眼前缓缓开启的石门，心跳骤然加快。",
    "有些真相，掩埋得越久，揭开时便越惊心动魄。他不知道自己即将触碰的，是怎样的过往。",
    "天空渐亮，晨雾散去，露珠在草叶上闪烁。新的一天开始了，而属于他们的故事，才刚刚翻开第一页。",
]

def gen_content(word_count: int) -> str:
    paras = []
    total = 0
    while total < word_count:
        p = random.choice(PARA_TEMPLATES)
        paras.append(p)
        total += len(p)
    return "\n\n".join(paras)[:word_count]

# ---------------- 基础数据 ----------------
MANAGERS = ['admin', 'manager1', 'manager2']
AUTHORS = [
    ('尘缘未了', '123456', '玄幻小说作家，代表作《星辰之巅》。', '13800000001'),
    ('南城旧梦', '123456', '都市情感类作家，擅长细腻叙事。', '13800000002'),
    ('星海拾光', '123456', '科幻作家，专注硬核设定与宏大世界观。', '13800000003'),
    ('笔墨春秋', '123456', '历史题材作家，笔锋严谨。', '13800000004'),
    ('夜半风声', '123456', '悬疑推理作家，擅长反转剧情。', '13800000005'),
    ('花间辞', '123456', '言情作家，文字温暖治愈。', '13800000006'),
]
READERS = [
    ('reader001', '123456', '13800000101', '男'),
    ('reader002', '123456', '13800000102', '女'),
    ('reader003', '123456', '13800000103', '男'),
    ('reader004', '123456', '13800000104', '女'),
    ('reader005', '123456', '13800000105', '男'),
    ('reader006', '123456', '13800000106', '女'),
    ('reader007', '123456', '13800000107', '男'),
    ('reader008', '123456', '13800000108', '女'),
]
CATEGORIES = ['玄幻', '都市', '科幻', '历史', '悬疑', '言情']

# (名称, 作者下标, 分类列表, 状态, 简介, 章节数, 每章基础字数, 千字价格[分], 付费起始章)
NOVELS = [
    ('星辰之巅', 0, ['玄幻'], '连载', '少年林夜自小城走出，觉醒星辰血脉，踏上问鼎苍穹的修行之路。', 16, 3000, 150, 4),
    ('都市医仙', 1, ['都市'], '连载', '隐世神医叶尘回归都市，一手银针济世救人，从此风云再起。', 14, 2600, 120, 4),
    ('星际远征', 2, ['科幻'], '完结', '人类文明踏上星辰大海，与未知文明相遇，一场跨越星系的远征就此展开。', 18, 3200, 160, 3),
    ('大秦风云', 3, ['历史'], '连载', '从边关小吏到帝国权臣，看他如何在大秦乱世中步步为营。', 12, 2400, 100, 3),
    ('迷雾追凶', 4, ['悬疑'], '完结', '连环失踪案背后，隐藏着一个尘封二十年的秘密。刑警队长抽丝剥茧。', 13, 2800, 130, 3),
    ('锦瑟年华', 5, ['言情'], '连载', '一场意外让两个陌生人相遇，从此琴瑟和鸣，岁月静好。', 15, 2200, 110, 4),
    ('剑道独尊', 0, ['玄幻'], '连载', '一柄锈剑，一个落魄少年，走出了一条震撼九州的剑道。', 17, 3100, 150, 4),
    ('重生之商界传奇', 1, ['都市'], '连载', '重回一九九八，他凭借记忆中的大势，一步步缔造商业帝国。', 16, 2700, 120, 4),
    ('量子文明', 2, ['科幻'], '连载', '量子计算机觉醒之日，人类文明的命运被彻底改写。', 14, 3000, 150, 3),
    ('盛唐风华', 3, ['历史'], '连载', '长安城中，少年诗人与江湖剑客，共同见证盛唐的辉煌与暗流。', 11, 2300, 100, 3),
    ('午夜谜案', 4, ['悬疑'], '连载', '每晚零点，准时响起的钟声带走一条性命。谁在午夜敲钟？', 12, 2500, 120, 3),
    ('春风十里', 5, ['言情'], '连载', '校园里的青涩暗恋，毕业后的久别重逢，春风十里不如你。', 15, 2100, 100, 4),
]

# 章节标题用词
CH_TITLE = ['风起', '暗流', '抉择', '破晓', '重逢', '迷局', '征途', '归途', '惊变', '真相',
            '远行', '初见', '约定', '风波', '执念', '新生', '孤注', '对峙']

# ---------------- 生成 SQL ----------------
sql_lines = []

def add(db, table, cols, rows):
    if not rows:
        return
    col_str = ", ".join("`%s`" % c for c in cols)
    sql_lines.append(f"INSERT INTO `{db}`.`{table}` ({col_str}) VALUES")
    vals = []
    for r in rows:
        vals.append("(" + ", ".join(esc(v) for v in r) + ")")
    sql_lines.append(",\n".join(vals) + ";\n")

# ---------- 1. userdb：管理员 / 作者 / 读者 ----------
add('userdb', 'manager', ['manager_id', 'manager_name', 'password'],
    [(i + 1, name, hash_password('123456')) for i, name in enumerate(MANAGERS)])

add('userdb', 'author', ['author_id', 'author_name', 'avatar_url', 'earning', 'introduction',
                         'password', 'phone', 'register_time'],
    [(i + 1, name, f'https://picsum.photos/seed/author{i+1}/200/200', 0, intro,
      hash_password(pwd), phone, now_offset(days=-i * 10 - 5))
     for i, (name, pwd, intro, phone) in enumerate(AUTHORS)])

add('userdb', 'reader', ['reader_id', 'avatar_url', 'background_url', 'balance', 'create_time',
                         'gender', 'is_collect_visible', 'is_recommend_visible',
                         'password', 'phone', 'reader_name'],
    [(i + 1, f'https://picsum.photos/seed/reader{i+1}/200/200',
      f'https://picsum.photos/seed/bg{i+1}/1200/600', 0,
      now_offset(days=-i * 3 - 2), gender, '是', '是',
      hash_password(pwd), phone, name)
     for i, (name, pwd, phone, gender) in enumerate(READERS)])

# ---------- 2. contentdb：分类 / 小说 / 章节 ----------
add('contentdb', 'category', ['category_name'], [(c,) for c in CATEGORIES])

novel_meta = []  # (novel_id, author_id, status, name, intro, chapters[(title,wc,is_charged,ppk)], cats)
novel_id = 0
for idx, (name, author_idx, cats, status, intro, n_ch, base_wc, ppk, paid_start) in enumerate(NOVELS):
    novel_id += 1
    chapters = []
    for c in range(1, n_ch + 1):
        wc = base_wc + random.randint(-300, 400)
        is_charged = '是' if c >= paid_start else '否'
        chapters.append((f'第{c}章 {random.choice(CH_TITLE)}', wc, is_charged, ppk))
    novel_meta.append((novel_id, author_idx + 1, status, name, intro, chapters, cats))

# novel_id -> 章节列表
novel_ch_map = {m[0]: m[5] for m in novel_meta}

# 统计
novel_stats = {}
for nid, aid, status, name, intro, chapters, cats in novel_meta:
    total_wc = sum(c[1] for c in chapters)
    charged_price = sum(round(c[1] / 1000.0 * c[3]) for c in chapters if c[2] == '是')
    novel_stats[nid] = {'total_wc': total_wc, 'total_price': round(charged_price * 0.8),
                        'score': 0.0, 'collected': 0, 'recommended': 0}

collect_rows, rate_rows, recommend_rows = [], [], []
for nid in range(1, novel_id + 1):
    n_readers = random.sample(range(1, 9), random.randint(3, 7))
    for rid in n_readers:
        if random.random() < 0.75:
            collect_rows.append((nid, rid, random.choice(['是', '否'])))
            novel_stats[nid]['collected'] += 1
        if random.random() < 0.8:
            score = random.choice([3.0, 3.5, 4.0, 4.5, 4.5, 5.0, 5.0])
            rate_rows.append((nid, rid, score, now_offset(days=-random.randint(1, 60))))
        if random.random() < 0.5:
            recommend_rows.append((nid, rid, random.choice(['剧情精彩', '文笔细腻', '设定新颖', '强烈推荐', '值得一读'])))
            novel_stats[nid]['recommended'] += 1

for nid in range(1, novel_id + 1):
    scores = [r[2] for r in rate_rows if r[0] == nid]
    if scores:
        novel_stats[nid]['score'] = round(sum(scores) / len(scores), 1)

add('contentdb', 'novel', ['novel_id', 'author_id', 'collected_count', 'cover_url', 'create_time',
                           'introduction', 'novel_name', 'original_novel_id', 'recommend_count',
                           'score', 'status', 'total_price', 'total_word_count'],
    [(nid, aid, novel_stats[nid]['collected'], f'https://picsum.photos/seed/novel{nid}/300/400',
      now_offset(days=-nid * 4 - 1), intro, name, -1, novel_stats[nid]['recommended'],
      novel_stats[nid]['score'], status, novel_stats[nid]['total_price'], novel_stats[nid]['total_wc'])
     for nid, aid, status, name, intro, chapters, cats in novel_meta])

add('contentdb', 'novel_category', ['category_name', 'novel_id'],
    [(c, nid) for nid, aid, status, name, intro, chapters, cats in novel_meta for c in cats])

chapter_rows = []
for nid, aid, status, name, intro, chapters, cats in novel_meta:
    for cid, (title, wc, is_charged, ppk) in enumerate(chapters, start=1):
        content = gen_content(wc)
        chapter_rows.append((cid, nid, content, is_charged, ppk,
                             now_offset(days=-random.randint(1, 90)), '已发布', title, len(content)))
add('contentdb', 'chapter', ['chapter_id', 'novel_id', 'content', 'is_charged', 'price_per_kilo',
                             'publish_time', 'status', 'title', 'word_count'], chapter_rows)

add('contentdb', 'collect', ['novel_id', 'reader_id', 'is_public'], collect_rows)
add('contentdb', 'rate', ['novel_id', 'reader_id', 'score', 'rating_time'], rate_rows)
add('contentdb', 'recommend', ['novel_id', 'reader_id', 'reason'], recommend_rows)

# 评论 + 回复 + 点赞
comment_rows, comment_reply_rows, like_rows = [], [], []
cid = 0
comment_first = []  # 每条根评论 id
for nid in range(1, novel_id + 1):
    n_comments = random.randint(2, 4)
    for _ in range(n_comments):
        cid += 1
        chapter_id = random.randint(1, len(novel_ch_map[nid]))
        content = random.choice([
            '写得真好，一口气看完了！', '情节紧凑，期待后续更新。', '作者大大加油，太精彩了！',
            '人物塑造很立体，很喜欢主角。', '这章转折太意外了，完全没想到。', '文笔细腻，代入感很强。',
            '已经推荐给朋友了，大家都说好看。', '世界观设定很宏大，期待展开。'])
        likes = random.randint(0, 50)
        comment_rows.append((cid, chapter_id, content, now_offset(days=-random.randint(1, 50)),
                             likes, nid, random.randint(1, 8), '通过', ''))
        comment_first.append(cid)
        for rid in random.sample(range(1, 9), random.randint(0, 6)):
            like_rows.append((cid, rid))
        if random.random() < 0.5:
            cid += 1
            reply_content = random.choice(['同感！', '+1，我也这么觉得。', '有道理，分析得很到位。', '期待下一章！'])
            comment_rows.append((cid, chapter_id, reply_content, now_offset(days=-random.randint(1, 30)),
                                 0, nid, random.randint(1, 8), '通过', ''))
            comment_reply_rows.append((comment_first[-1], cid, 1))  # comment_id=被回复评论, pre_com_id=回复本身

add('contentdb', 'comments', ['comment_id', 'chapter_id', 'content', 'create_time', 'likes',
                              'novel_id', 'reader_id', 'status', 'title'], comment_rows)
add('contentdb', 'comment_reply', ['comment_id', 'pre_com_id', 'comment_level'], comment_reply_rows)
add('contentdb', 'likes', ['comment_id', 'reader_id'], like_rows)

# 举报（2 条）
add('contentdb', 'report', ['report_id', 'comment_id', 'reader_id', 'reason', 'report_time', 'progress'],
    [(1, comment_first[0], random.randint(1, 8), '涉及广告推广', now_offset(days=-2), '未处理'),
     (2, comment_first[1], random.randint(1, 8), '包含人身攻击', now_offset(days=-1), '未处理')])

# 全本购买：reader1 购买 novel3（《星际远征》）
add('contentdb', 'wholepurchase', ['novel_id', 'reader_id', 'is_bought'], [(3, 1, '是')])

# 最近阅读（按 novel_id+reader_id 去重，主键即此二元组）
recent_set = set()
while len(recent_set) < 24:
    rid = random.randint(1, 8)
    nid = random.randint(1, novel_id)
    recent_set.add((nid, rid))  # 每读者每本小说只保留一条
add('userdb', 'recent_readings', ['novel_id', 'reader_id', 'chapter_id', 'recent_reading_time'],
    [(nid, rid, random.randint(1, len(novel_ch_map[nid])),
      now_offset(days=-random.randint(0, 5), hours=-random.randint(1, 12)))
     for nid, rid in recent_set])

# ---------- 3. transactiondb：交易 / 购买 / 打赏 / 作者收入 ----------
# 先统计消费（解锁 + 打赏 + 全本购买），再确定充值额，保证每个读者余额充足
transactions, purchase_rows, reward_rows, author_income = [], [], [], []
tid = 0
author_earning = {aid: 0 for aid in range(1, 7)}
reader_consume = {rid: 0 for rid in range(1, 9)}

def record_consume(rid, amount):
    reader_consume[rid] += amount

for nid in range(1, novel_id + 1):
    paid_chapters = [c for c in range(1, len(novel_ch_map[nid]) + 1) if novel_ch_map[nid][c - 1][2] == '是']
    if not paid_chapters:
        continue
    for rid in random.sample(range(1, 9), random.randint(3, 6)):
        ch = random.choice(paid_chapters)
        wc = novel_ch_map[nid][ch - 1][1]
        ppk = novel_ch_map[nid][ch - 1][3]
        price = round(wc / 1000.0 * ppk)
        tid += 1
        t = now_offset(days=-random.randint(1, 40))
        transactions.append((tid, price, rid, t, '解锁章节'))
        purchase_rows.append((tid, ch, nid))
        record_consume(rid, price)
        aid = novel_meta[nid - 1][1]
        author_income.append((aid, nid, '章节购买', price, t))
        author_earning[aid] += price

for rid, nid, amt in [(1, 3, 500), (2, 1, 300), (3, 5, 200), (4, 7, 800), (5, 9, 500), (6, 11, 300)]:
    tid += 1
    t = now_offset(days=-random.randint(1, 20))
    transactions.append((tid, amt, rid, t, '打赏'))
    reward_rows.append((tid, nid))
    record_consume(rid, amt)
    aid = novel_meta[nid - 1][1]
    author_income.append((aid, nid, '打赏', amt, t))
    author_earning[aid] += amt

# 全本购买消费（reader1 买 novel3《星际远征》）
whole_purchase_price = novel_stats[3]['total_price']
record_consume(1, whole_purchase_price)

# 充值：消费额 + 随机余量（至少 2000 分），保证余额恒正
recharge_amounts = {}
for rid in range(1, 9):
    base = reader_consume[rid] + random.randint(2000, 15000)
    recharge_amounts[rid] = base
    tid += 1
    transactions.append((tid, base, rid, now_offset(days=-random.randint(10, 60)), '充值'))

add('transactiondb', 'transaction', ['transaction_id', 'amount', 'reader_id', 'time', 'trans_type'], transactions)
add('transactiondb', 'purchase', ['transaction_id', 'chapter_id', 'novel_id'], purchase_rows)
add('transactiondb', 'reward', ['transaction_id', 'novel_id'], reward_rows)
add('transactiondb', 'author_income', ['author_id', 'novel_id', 'type', 'amount', 'create_time'], author_income)

# ---------- 4. 一致性回填（UPDATE） ----------
sql_lines.append("UPDATE `userdb`.`reader` SET `balance` = CASE `reader_id`")
for rid in range(1, 9):
    sql_lines.append(f"  WHEN {rid} THEN {recharge_amounts[rid] - reader_consume[rid]}")
sql_lines.append("  ELSE `balance` END;\n")

sql_lines.append("UPDATE `userdb`.`author` SET `earning` = CASE `author_id`")
for aid in range(1, 7):
    sql_lines.append(f"  WHEN {aid} THEN {author_earning[aid]}")
sql_lines.append("  ELSE `earning` END;\n")

# ---------- 5. admindb：管理日志 ----------
add('admindb', 'management', ['management_id', 'manager_id', 'result', 'time'],
    [(1, 1, '审核通过小说《星辰之巅》', now_offset(days=-50)),
     (2, 2, '审核通过小说《星际远征》', now_offset(days=-40)),
     (3, 2, '审核通过小说《迷雾追凶》', now_offset(days=-35)),
     (4, 3, '处理举报：广告推广', now_offset(days=-2))])
add('admindb', 'novel_management', ['management_id', 'novel_id'], [(1, 1), (2, 3), (3, 5)])
add('admindb', 'report_management', ['management_id', 'report_id'], [(4, 1)])

# ---------------- 输出 ----------------
sql = "SET NAMES utf8mb4;\n" + "\n".join(sql_lines)
with open('/home/zhx/TJNovel/scripts/seed_data.sql', 'w', encoding='utf-8') as f:
    f.write(sql)

print(f"SQL 已生成: /home/zhx/TJNovel/scripts/seed_data.sql ({len(sql)/1024:.0f} KB)")
print(f"章节 {len(chapter_rows)} 章 | 评论 {len(comment_rows)} 条 | 交易 {len(transactions)} 条 | 作者收入 {len(author_income)} 条")
print(f"reader 余额(分): {[(rid, recharge_amounts[rid]-reader_consume[rid]) for rid in range(1,9)]}")
print(f"author 收益(分): {list(author_earning.items())}")
print(f"全本价格(分): {whole_purchase_price}")
