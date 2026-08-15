# -*- coding: utf-8 -*-
"""
TJNovel 追加数据脚本（增量，不清空现有数据）
- 新增作者 ID 11/12、小说 ID 32~41，与 frontend/src/stores/novelHomeData.js 完全对应
  - featuredNovelIds.male   = [35..41]
  - featuredNovelIds.female = [32..38]
  - novelIds = [34..41]
  - carouselNovels_data novelId = 32..36（名称一致）
- 补齐章节/分类/评分/收藏/推荐/评论/回复/点赞/交易/购买/打赏/作者收益/最近阅读/管理日志
- 保持跨表一致性：统计字段 = 实际数据；余额 = 充值 - 消费；作者收益 = 收入之和
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

def esc(s):
    if s is None:
        return 'NULL'
    return "'" + str(s).replace('\\', '\\\\').replace("'", "''") + "'"

def now_offset(days=0, hours=0):
    return (datetime(2026, 8, 10, 10, 0, 0) + timedelta(days=days, hours=hours)).strftime('%Y-%m-%d %H:%M:%S')

# ---------------- 章节内容 ----------------
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

# ---------------- 追加数据定义 ----------------
# 新作者：ID 11、12
NEW_AUTHORS = [
    (11, '墨香客', '古言作家，笔下的故事如画，墨香四溢。', '13800000111'),
    (12, '青衫旧', '青春言情作家，擅长细腻的少年心事。', '13800000112'),
]

# 新小说：ID 32~41，(novel_id, 名称, 作者, 分类, 状态, 简介, 章节数, 基础字数, 千字价[分], 付费起始章)
NEW_NOVELS = [
    (32, '如意姑娘的', 12, ['言情'], '连载', '如意是长安城里最出名的绣娘，一针一线，绣尽人间百态。', 13, 2300, 110, 4),
    (33, '写给鼹鼠先生的情', 6, ['言情'], '连载', '一封封没有署名的情书，从校园寄到职场，写给那个笨拙温柔的鼹鼠先生。', 14, 2200, 110, 4),
    (34, '问九卿', 11, ['言情'], '完结', '九卿之首谢云琅权倾朝野，唯独在她面前，卸下所有锋芒。', 15, 2500, 120, 3),
    (35, '昭娇', 12, ['言情'], '连载', '将门嫡女昭昭重生归来，这一世，她要护住满门忠烈。', 13, 2400, 120, 4),
    (36, '岁时来仪', 11, ['言情'], '完结', '岁岁年年花相似，唯有你，是我岁时里的来仪。', 12, 2300, 110, 3),
    (37, '剑破苍穹', 1, ['玄幻'], '连载', '一柄断剑出鞘，少年萧尘自北境崛起，剑锋所指，苍穹为之震颤。', 16, 3100, 150, 4),
    (38, '都市龙医', 2, ['都市'], '连载', '神秘医道传人龙飞归隐都市，一双回春妙手，搅动风云。', 15, 2700, 120, 4),
    (39, '深空彼岸', 11, ['科幻'], '连载', '深空探测船失联四十年后突然返航，船上的人，一个都没老。', 14, 3000, 150, 3),
    (40, '大明风华', 3, ['历史'], '连载', '永乐年间，少年状元郎身处庙堂之上，见证大明最鼎盛的风华。', 13, 2400, 100, 3),
    (41, '无声证词', 4, ['悬疑'], '完结', '法医顾言能听见尸体无声的证词，一桩跨越十年的悬案就此揭开。', 15, 2800, 130, 3),
]

CH_TITLE = ['风起', '暗流', '抉择', '破晓', '重逢', '迷局', '征途', '归途', '惊变', '真相',
            '远行', '初见', '约定', '风波', '执念', '新生', '孤注', '对峙']

COMMENT_TEXTS = [
    '写得真好，一口气看完了！', '情节紧凑，期待后续更新。', '作者大大加油，太精彩了！',
    '人物塑造很立体，很喜欢主角。', '这章转折太意外了，完全没想到。', '文笔细腻，代入感很强。',
    '已经推荐给朋友了，大家都说好看。', '世界观设定很宏大，期待展开。', '女主太飒了，爱了爱了！',
    '男主深情又克制，看得心都化了。', '悬疑感拉满，晚上都不敢关灯看。', '历史考据很用心，涨知识了。',
]
REPLY_TEXTS = ['同感！', '+1，我也这么觉得。', '有道理，分析得很到位。', '期待下一章！', '说得太好了！']

# ---------------- 生成 SQL ----------------
sql_lines = []
comment_sql = []  # 评论相关单独收集（需要先确定所有 root comment id）

def add(db, table, cols, rows):
    if not rows:
        return
    col_str = ", ".join("`%s`" % c for c in cols)
    sql_lines.append(f"INSERT INTO `{db}`.`{table}` ({col_str}) VALUES")
    vals = []
    for r in rows:
        vals.append("(" + ", ".join(esc(v) for v in r) + ")")
    sql_lines.append(",\n".join(vals) + ";\n")

# ---------- 1. 作者（ID 11、12） ----------
add('userdb', 'author', ['author_id', 'author_name', 'avatar_url', 'earning', 'introduction',
                         'password', 'phone', 'register_time'],
    [(aid, name, f'https://picsum.photos/seed/author{aid}/200/200', 0, intro,
      hash_password('123456'), phone, now_offset(days=-30))
     for aid, name, intro, phone in NEW_AUTHORS])

# ---------- 2. 章节构建 ----------
# novel_meta: (novel_id, author_id, status, name, intro, chapters, cats)
novel_meta = []
for (nid, name, aid, cats, status, intro, n_ch, base_wc, ppk, paid_start) in NEW_NOVELS:
    chapters = []
    for c in range(1, n_ch + 1):
        wc = base_wc + random.randint(-300, 400)
        is_charged = '是' if c >= paid_start else '否'
        chapters.append((f'第{c}章 {random.choice(CH_TITLE)}', wc, is_charged, ppk))
    novel_meta.append((nid, aid, status, name, intro, chapters, cats))

novel_ch_map = {m[0]: m[5] for m in novel_meta}

# 统计
novel_stats = {}
for nid, aid, status, name, intro, chapters, cats in novel_meta:
    total_wc = sum(c[1] for c in chapters)
    charged_price = sum(round(c[1] / 1000.0 * c[3]) for c in chapters if c[2] == '是')
    novel_stats[nid] = {'total_wc': total_wc, 'total_price': round(charged_price * 0.8),
                        'score': 0.0, 'collected': 0, 'recommended': 0}

# ---------- 3. 评分 / 收藏 / 推荐 ----------
collect_rows, rate_rows, recommend_rows = [], [], []
for nid in novel_stats:
    n_readers = random.sample(range(1, 9), random.randint(3, 7))
    for rid in n_readers:
        if random.random() < 0.75:
            collect_rows.append((nid, rid, random.choice(['是', '否'])))
            novel_stats[nid]['collected'] += 1
        if random.random() < 0.8:
            score = random.choice([3.0, 3.5, 4.0, 4.5, 4.5, 5.0, 5.0])
            rate_rows.append((nid, rid, score, now_offset(days=-random.randint(1, 30))))
        if random.random() < 0.5:
            recommend_rows.append((nid, rid, random.choice(['剧情精彩', '文笔细腻', '设定新颖', '强烈推荐', '值得一读'])))
            novel_stats[nid]['recommended'] += 1

for nid in novel_stats:
    scores = [r[2] for r in rate_rows if r[0] == nid]
    if scores:
        novel_stats[nid]['score'] = round(sum(scores) / len(scores), 1)

# ---------- 4. 小说主体 ----------
add('contentdb', 'novel', ['novel_id', 'author_id', 'collected_count', 'cover_url', 'create_time',
                           'introduction', 'novel_name', 'original_novel_id', 'recommend_count',
                           'score', 'status', 'total_price', 'total_word_count'],
    [(nid, aid, novel_stats[nid]['collected'], f'https://picsum.photos/seed/novel{nid}/300/400',
      now_offset(days=-nid * 2 - 1), intro, name, -1, novel_stats[nid]['recommended'],
      novel_stats[nid]['score'], status, novel_stats[nid]['total_price'], novel_stats[nid]['total_wc'])
     for nid, aid, status, name, intro, chapters, cats in novel_meta])

add('contentdb', 'novel_category', ['category_name', 'novel_id'],
    [(c, nid) for nid, aid, status, name, intro, chapters, cats in novel_meta for c in cats])

# ---------- 5. 章节 ----------
chapter_rows = []
for nid, aid, status, name, intro, chapters, cats in novel_meta:
    for cid, (title, wc, is_charged, ppk) in enumerate(chapters, start=1):
        content = gen_content(wc)
        chapter_rows.append((cid, nid, content, is_charged, ppk,
                             now_offset(days=-random.randint(1, 60)), '已发布', title, len(content)))
add('contentdb', 'chapter', ['chapter_id', 'novel_id', 'content', 'is_charged', 'price_per_kilo',
                             'publish_time', 'status', 'title', 'word_count'], chapter_rows)

add('contentdb', 'collect', ['novel_id', 'reader_id', 'is_public'], collect_rows)
add('contentdb', 'rate', ['novel_id', 'reader_id', 'score', 'rating_time'], rate_rows)
add('contentdb', 'recommend', ['novel_id', 'reader_id', 'reason'], recommend_rows)

# ---------- 6. 评论 + 回复 + 点赞（comment_id 从 43 开始） ----------
comment_rows, comment_reply_rows, like_rows = [], [], []
cid = 42  # 现有评论已到 42
comment_first = []
for nid in novel_stats:
    n_comments = random.randint(3, 5)
    for _ in range(n_comments):
        cid += 1
        chapter_id = random.randint(1, len(novel_ch_map[nid]))
        content = random.choice(COMMENT_TEXTS)
        likes = random.randint(0, 60)
        comment_rows.append((cid, chapter_id, content, now_offset(days=-random.randint(1, 40)),
                             likes, nid, random.randint(1, 8), '通过', ''))
        comment_first.append(cid)
        for rid in random.sample(range(1, 9), random.randint(0, 7)):
            like_rows.append((cid, rid))
        if random.random() < 0.6:
            cid += 1
            reply_content = random.choice(REPLY_TEXTS)
            comment_rows.append((cid, chapter_id, reply_content, now_offset(days=-random.randint(1, 25)),
                                 0, nid, random.randint(1, 8), '通过', ''))
            comment_reply_rows.append((comment_first[-1], cid, 1))

add('contentdb', 'comments', ['comment_id', 'chapter_id', 'content', 'create_time', 'likes',
                              'novel_id', 'reader_id', 'status', 'title'], comment_rows)
add('contentdb', 'comment_reply', ['comment_id', 'pre_com_id', 'comment_level'], comment_reply_rows)
add('contentdb', 'likes', ['comment_id', 'reader_id'], like_rows)

# ---------- 7. 最近阅读（新小说，去重） ----------
recent_rows = []
recent_set = set()
while len(recent_set) < 20:
    rid = random.randint(1, 8)
    nid = random.choice([32, 33, 34, 35, 36, 37, 38, 39, 40, 41])
    if (nid, rid) not in recent_set:
        recent_set.add((nid, rid))
        recent_rows.append((nid, rid, random.randint(1, len(novel_ch_map[nid])),
                            now_offset(days=-random.randint(0, 5), hours=-random.randint(1, 12))))
add('userdb', 'recent_readings', ['novel_id', 'reader_id', 'chapter_id', 'recent_reading_time'], recent_rows)

# ---------- 8. 交易闭环（transaction_id 从 64 开始） ----------
transactions, purchase_rows, reward_rows, author_income = [], [], [], []
tid = 63
reader_consume = {rid: 0 for rid in range(1, 9)}
author_earning = {aid: 0 for aid in list(range(1, 7)) + [11, 12]}

def record_consume(rid, amount):
    reader_consume[rid] += amount

# 8.1 解锁章节购买
for nid, aid, status, name, intro, chapters, cats in novel_meta:
    paid_chapters = [c for c in range(1, len(chapters) + 1) if chapters[c - 1][2] == '是']
    if not paid_chapters:
        continue
    for rid in random.sample(range(1, 9), random.randint(3, 6)):
        ch = random.choice(paid_chapters)
        wc = chapters[ch - 1][1]
        ppk = chapters[ch - 1][3]
        price = round(wc / 1000.0 * ppk)
        tid += 1
        t = now_offset(days=-random.randint(1, 30))
        transactions.append((tid, price, rid, t, '解锁章节'))
        purchase_rows.append((tid, ch, nid))
        record_consume(rid, price)
        author_income.append((aid, nid, '章节购买', price, t))
        author_earning[aid] += price

# 8.2 打赏
rewards = [(1, 32, 300), (2, 35, 500), (3, 38, 200), (4, 40, 800), (5, 41, 500), (6, 34, 400),
           (7, 33, 300), (8, 39, 600)]
for rid, nid, amt in rewards:
    tid += 1
    t = now_offset(days=-random.randint(1, 15))
    transactions.append((tid, amt, rid, t, '打赏'))
    reward_rows.append((tid, nid))
    record_consume(rid, amt)
    aid = dict((m[0], m[1]) for m in novel_meta)[nid]
    author_income.append((aid, nid, '打赏', amt, t))
    author_earning[aid] += amt

# 8.3 充值：消费多少补多少（保持余额不降），再随机多充一部分
recharge_extra = {}
for rid in range(1, 9):
    extra = reader_consume[rid] + random.randint(1000, 8000)
    recharge_extra[rid] = extra
    if extra > 0:
        tid += 1
        transactions.append((tid, extra, rid, now_offset(days=-random.randint(10, 50)), '充值'))

add('transactiondb', 'transaction', ['transaction_id', 'amount', 'reader_id', 'time', 'trans_type'], transactions)
add('transactiondb', 'purchase', ['transaction_id', 'chapter_id', 'novel_id'], purchase_rows)
add('transactiondb', 'reward', ['transaction_id', 'novel_id'], reward_rows)
add('transactiondb', 'author_income', ['author_id', 'novel_id', 'type', 'amount', 'create_time'], author_income)

# ---------- 9. 余额 / 作者收益回填（增量更新） ----------
sql_lines.append("UPDATE `userdb`.`reader` SET `balance` = `balance` + CASE `reader_id`")
for rid in range(1, 9):
    sql_lines.append(f"  WHEN {rid} THEN {recharge_extra[rid] - reader_consume[rid]}")
sql_lines.append("  ELSE 0 END;\n")

sql_lines.append("UPDATE `userdb`.`author` SET `earning` = `earning` + CASE `author_id`")
for aid in [11, 12]:
    sql_lines.append(f"  WHEN {aid} THEN {author_earning[aid]}")
sql_lines.append("  ELSE 0 END;\n")

# ---------- 10. admindb：新小说审核日志（management_id 从 5 开始） ----------
mgmt = [
    (5, 2, '审核通过小说《问九卿》', now_offset(days=-20)),
    (6, 1, '审核通过小说《剑破苍穹》', now_offset(days=-15)),
    (7, 3, '审核通过小说《无声证词》', now_offset(days=-10)),
]
add('admindb', 'management', ['management_id', 'manager_id', 'result', 'time'], mgmt)
add('admindb', 'novel_management', ['management_id', 'novel_id'], [(5, 34), (6, 37), (7, 41)])

# ---------------- 输出 ----------------
sql = "SET NAMES utf8mb4;\n" + "\n".join(sql_lines)
out_path = '/home/zhx/TJNovel/scripts/append_seed_data.sql'
with open(out_path, 'w', encoding='utf-8') as f:
    f.write(sql)

print(f"SQL 已生成: {out_path} ({len(sql)/1024:.0f} KB)")
print(f"新增作者 {len(NEW_AUTHORS)} 位 | 小说 {len(novel_meta)} 本 | 章节 {len(chapter_rows)} 章 | 评论 {len(comment_rows)} 条")
print(f"交易 {len(transactions)} 条 | 购买 {len(purchase_rows)} 条 | 打赏 {len(reward_rows)} 条 | 作者收入 {len(author_income)} 条")
print(f"reader 余额变化(分): {[(rid, recharge_extra[rid]-reader_consume[rid]) for rid in range(1,9)]}")
print(f"作者 11/12 收益(分): {[(aid, author_earning[aid]) for aid in (11,12)]}")
