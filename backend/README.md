# OurNovel API 文档

本文档详细列出了 OurNovel 项目中所有的 API 接口，包括操作方法、URL、参数、功能描述和返回的Response body格式。

## 目录

1. [作者相关 API](#作者相关-api)
2. [作者收入 API](#作者收入-api)
3. [分类 API](#分类-api)
4. [章节 API](#章节-api)
5. [章节管理 API](#章节管理-api)
6. [收藏 API](#收藏-api)
7. [评论管理 API](#评论管理-api)
8. [评论回复 API](#评论回复-api)
9. [评论 API](#评论-api)
10. [点赞 API](#点赞-api)
11. [登录认证 API](#登录认证-api)
12. [管理员登录认证 API](#管理员登录认证-api)
13. [读者登录认证 API](#读者登录认证-api)
14. [管理 API](#管理-api)
15. [管理员 API](#管理员-api)
16. [小说分类 API](#小说分类-api)
17. [小说 API](#小说-api)
18. [小说管理 API](#小说管理-api)
19. [购买 API](#购买-api)
20. [排行榜 API](#排行榜-api)
21. [评分 API](#评分-api)
22. [读者 API](#读者-api)
23. [最近阅读 API](#最近阅读-api)
24. [充值 API](#充值-api)
25. [推荐 API](#推荐-api)
26. [举报 API](#举报-api)
27. [举报管理 API](#举报管理-api)
28. [打赏 API](#打赏-api)
29. [搜索 API](#搜索-api)
30. [统计 API](#统计-api)
31. [测试 API](#测试-api)
32. [交易记录 API](#交易记录-api)
33. [整本购买 API](#整本购买-api)

---

## 作者相关 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/Author/UploadAvatar | authorId (int), avatarFile (IFormFile) | 上传作者头像 | { success: true, avatarUrl: "string" } |
| GET | /api/Author/{authorId}/novel-count | authorId (int) | 获取作者小说数量 | { novelCount: int } |
| GET | /api/Author/{authorId}/total-wordcount | authorId (int) | 获取作者总字数 | { totalWordCount: long } |
| GET | /api/Author/{authorId}/register-days | authorId (int) | 获取作者注册天数 | { registerDays: int } |
| GET | /api/Author/{authorId}/novels | authorId (int) | 获取作者的所有小说 | [Novel] |
| OPTIONS | /api/Author | 无 | 处理 CORS 预检请求 | 200 OK |
| GET | /api/Author | 无 | 获取所有作者 | [Author] |
| GET | /api/Author/{id} | id (int) | 根据ID获取作者 | Author |
| POST | /api/Author | entity (Author) | 创建作者 | Author |
| PUT | /api/Author/{id} | id (int), entity (Author) | 更新作者 | Author |
| DELETE | /api/Author/{id} | id (int) | 删除作者 | 200 OK |

## 作者收入 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| GET | /api/AuthorIncome/list/{authorId} | authorId (long) | 获取作者的收入记录 | [AuthorIncome] |
| GET | /api/AuthorIncome/total/{authorId} | authorId (long) | 获取作者的总收入 | { totalIncome: decimal } |

## 分类 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| OPTIONS | /api/Category | 无 | 处理 CORS 预检请求 | 200 OK |
| GET | /api/Category | 无 | 获取所有分类 | [Category] |
| POST | /api/Category | entity (Category) | 添加分类 | Category |
| GET | /api/Category/{id} | id (string) | 根据ID获取分类 | Category |
| PUT | /api/Category/{id} | id (string), entity (Category) | 更新分类（支持重命名） | Category |
| DELETE | /api/Category/{id} | id (string) | 删除分类 | 200 OK |
| POST | /api/Category/rename | 无 | 单独的改名接口 | { success: boolean, message: string } |

## 章节 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| GET | /api/Chapter | 无 | 获取所有"首次审核"、"审核中"的章节 | [Chapter] |
| GET | /api/Chapter/novel/{novelId} | novelId (int) | 获取指定小说下的所有章节 | [Chapter] |
| GET | /api/Chapter/novels/{novelId}/chapters | novelId (int) | 获取小说的所有章节 | [Chapter] |
| GET | /api/Chapter/{novelId}/{chapterId} | novelId (int), chapterId (int) | 获取指定章节 | Chapter |
| POST | /api/Chapter | entity (Chapter) | 添加章节 | Chapter |
| PUT | /api/Chapter/{novelId}/{chapterId} | novelId (int), chapterId (int), entity (Chapter) | 更新章节 | Chapter |
| DELETE | /api/Chapter/{novelId}/{chapterId} | novelId (int), chapterId (int) | 删除章节 | 200 OK |

## 章节管理 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| GET | /api/ChapterManagement/logs/{chapterId} | chapterId (int), novelId (int) | 获取指定章节的管理日志列表 | [ChapterManagement] |
| GET | /api/ChapterManagement/logs/all | 无 | 获取所有章节的管理日志列表 | [ChapterManagement] |

## 收藏 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/Collect | novelId (int), readerId (int), isPublic (string) | 添加或更新收藏记录 | Collect |
| DELETE | /api/Collect | novelId (int), readerId (int) | 取消收藏 | 200 OK |
| GET | /api/Collect/reader/{readerId} | readerId (int) | 获取某个读者收藏的所有小说记录 | [Collect] |
| GET | /api/Collect/novel/{novelId} | novelId (int) | 获取某部小说被哪些读者收藏 | [Collect] |
| GET | /api/Collect | 无 | 获取所有收藏记录 | [Collect] |

## 评论管理 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| GET | /api/CommentManagement/logs/{commentId} | commentId (int) | 获取指定评论的管理日志列表 | [CommentManagement] |
| GET | /api/CommentManagement/logs/all | 无 | 获取所有评论的管理日志列表 | [CommentManagement] |

## 评论回复 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/CommentReply | reply (CommentReply) | 添加一条评论回复 | CommentReply |
| GET | /api/CommentReply/{commentId} | commentId (int) | 根据评论ID获取它的回复关系 | [CommentReply] |
| GET | /api/CommentReply/parent/{parentId} | parentId (int) | 获取某条评论下的所有直接回复 | [CommentReply] |
| GET | /api/CommentReply | 无 | 获取所有评论回复联系集 | [CommentReply] |

## 评论 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/Comments/Status | commentId (int), status (string), managerId (int), result (string) | 设置评论状态 | { success: boolean, message: string } |
| GET | /api/Comments/ByChapter/{novelId}/{chapterId} | novelId (int), chapterId (int) | 获取某章节下所有通过审核的评论 | [Comment] |
| GET | /api/Comments/ByNovel/{novelId} | novelId (int) | 获取某小说下所有通过审核的评论 | [Comment] |
| DELETE | /api/Comments/DeleteRecursive/{commentId} | commentId (int) | 递归删除评论及其所有子评论 | { success: boolean, message: string } |
| OPTIONS | /api/Comments | 无 | 处理 CORS 预检请求 | 200 OK |
| GET | /api/Comments | 无 | 获取所有评论 | [Comment] |
| GET | /api/Comments/{id} | id (int) | 根据ID获取评论 | Comment |
| POST | /api/Comments | entity (Comment) | 创建评论 | Comment |
| PUT | /api/Comments/{id} | id (int), entity (Comment) | 更新评论 | Comment |
| DELETE | /api/Comments/{id} | id (int) | 删除评论 | 200 OK |

## 点赞 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/Likes/Like | commentId (int), readerId (int) | 给指定评论点赞 | { success: boolean, message: string } |
| POST | /api/Likes/Unlike | commentId (int), readerId (int) | 取消点赞某条评论 | { success: boolean, message: string } |
| GET | /api/Likes/IsLiked | commentId (int), readerId (int) | 检查读者是否已点赞某条评论 | { isLiked: boolean } |
| GET | /api/Likes/Count/{commentId} | commentId (int) | 获取某条评论的点赞数量 | { count: int } |
| GET | /api/Likes | 无 | 获取所有点赞记录 | [Like] |

## 登录认证 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/LogAuthor/register-author | dto (AuthorRegisterDto) | 作者注册接口 | { success: boolean, message: string } |
| POST | /api/LogAuthor/login-author | dto (AuthorRegisterDto) | 作者登录接口，返回 JWT Token | { token: string, authorName: string, authorId: int } |
| POST | /api/LogAuthor/reset-author-password | dto (AuthorRegisterDtoWithPhone) | 重置作者密码，不判断是否与原密码相同 | { success: boolean, message: string } |
| POST | /api/LogAuthor/change-author-password | dto (AuthorChangePasswordDto) | 重置作者密码，判断是否与原密码相同 | { success: boolean, message: string } |

## 管理员登录认证 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/LogManager/register-manager | dto (ManagerRegisterDto) | 管理员注册接口 | { success: boolean, message: string } |
| POST | /api/LogManager/login-manager | dto (ManagerRegisterDto) | 管理员登录接口，返回 JWT Token | { token: string, managerName: string, managerId: int } |
| POST | /api/LogManager/reset-manager-password | dto (ManagerRegisterDto) | 重置管理员密码 | { success: boolean, message: string } |
| POST | /api/LogManager/logout | 无 | 管理员登出接口 | { message: string } |

## 读者登录认证 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/LogReader/register-reader | dto (ReaderRegisterDtoWithPhone) | 读者注册接口 | { success: boolean, message: string } |
| POST | /api/LogReader/login-reader | dto (ReaderRegisterDto) | 读者登录接口，返回 JWT Token | { token: string, readerName: string, readerId: int } |
| POST | /api/LogReader/reset-reader-password | dto (ReaderRegisterDtoWithPhone) | 重置读者密码 | { success: boolean, message: string } |
| POST | /api/LogReader/logout | 无 | 读者登出接口 | { message: string } |

## 管理 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| OPTIONS | /api/Management | 无 | 处理 CORS 预检请求 | 200 OK |
| GET | /api/Management | 无 | 获取所有管理记录 | [Management] |
| GET | /api/Management/{id} | id (int) | 根据ID获取管理记录 | Management |
| POST | /api/Management | entity (Management) | 创建管理记录 | Management |
| PUT | /api/Management/{id} | id (int), entity (Management) | 更新管理记录 | Management |
| DELETE | /api/Management/{id} | id (int) | 删除管理记录 | 200 OK |
| GET | /api/Management/by-manager/{managerId} | managerId (int) | 根据管理员ID筛选管理记录 | [Management] |

## 管理员 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| OPTIONS | /api/Manager | 无 | 处理 CORS 预检请求 | 200 OK |
| GET | /api/Manager | 无 | 获取所有管理员 | [Manager] |
| GET | /api/Manager/{id} | id (int) | 根据ID获取管理员 | Manager |
| POST | /api/Manager | entity (Manager) | 创建管理员 | Manager |
| PUT | /api/Manager/{id} | id (int), entity (Manager) | 更新管理员 | Manager |
| DELETE | /api/Manager/{id} | id (int) | 删除管理员 | 200 OK |

## 小说分类 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/NovelCategory | novelId (int), categoryName (string) | 添加小说与分类关系 | NovelCategory |
| DELETE | /api/NovelCategory | novelId (int), categoryName (string) | 删除小说与分类关系 | 200 OK |
| GET | /api/NovelCategory | 无 | 获取所有小说与分类的关系 | [NovelCategory] |
| GET | /api/NovelCategory/novel/{novelId} | novelId (int) | 获取某本小说的全部分类 | [Category] |
| GET | /api/NovelCategory/category/{categoryName} | categoryName (string) | 获取某个分类下的所有小说 | [Novel] |

## 小说 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/novel/create | authorId (int), novelName (string), introduction (string) | 创建小说 | { success: boolean, message: string, novelId: int } |
| PUT | /api/Novel/{id}/review | id (int), newStatus (string), managerId (int), result (string) | 审核小说 | { success: boolean, message: string } |
| POST | /api/Novel/UploadAvatar | novelId (int), coverFile (IFormFile) | 上传小说封面 | { success: boolean, coverUrl: string } |
| POST | /api/Novel/submit-edit | originalNovelId (int), editedDto (NovelEditDto) | 修改小说信息 | { message: string, novelId: int } |
| GET | /api/Novel/wordcount/{novelId} | novelId (int) | 获取小说总字数 | { novelId: int, totalWords: long } |
| GET | /api/Novel/recommendcount/{novelId} | novelId (int) | 获取小说推荐数 | { novelId: int, RecommendCount: int } |
| GET | /api/Novel/collectcount/{novelId} | novelId (int) | 获取小说收藏数 | { novelId: int, CollectCount: int } |
| GET | /api/Novel/{novelId}/latest-published-chapter | novelId (int) | 获取小说最新已发布章节的ID和发布时间 | LatestPublishedChapterDto |
| GET | /api/Novel/published | 无 | 获取所有已发布的小说（Status == "已发布" 或 "完结"） | [Novel] |
| GET | /published/by-id | page (int), pageSize (int) | 获取所有已发布的小说（分页，按novelID顺序） | PagedResult<Novel> |
| GET | /published/filter-by-id | page (int), pageSize (int), category (string), minWordCount (long), maxWordCount (long), isFinished (bool) | 获取已发布小说（分页+条件筛选，按NovelId顺序） | PagedResult<Novel> |
| OPTIONS | /api/Novel | 无 | 处理 CORS 预检请求 | 200 OK |
| GET | /api/Novel | 无 | 获取所有小说 | [Novel] |
| GET | /api/Novel/{id} | id (int) | 根据ID获取小说 | Novel |
| POST | /api/Novel | entity (Novel) | 创建小说 | Novel |
| PUT | /api/Novel/{id} | id (int), entity (Novel) | 更新小说 | Novel |
| DELETE | /api/Novel/{id} | id (int) | 删除小说 | 200 OK |

## 小说管理 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| GET | /api/NovelManagement/logs/{novelId} | novelId (int) | 获取指定小说的管理日志列表 | [NovelManagement] |
| GET | /api/NovelManagement/logs/all | 无 | 获取所有小说的管理日志列表 | [NovelManagement] |

## 购买 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/Purchase | dto (ChapterPurchaseDto) | 用户购买章节 | PurchaseResultDto |
| GET | /api/Purchase/check | readerId (int), novelId (int), chapterId (int) | 查询某读者是否已购买指定小说章节 | { isPurchased: boolean } |

## 排行榜 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| GET | /api/Ranking/collect | topN (int), status (string) | 获取收藏榜单前 n 名 | [CollectRankingDto] |
| GET | /api/Ranking/recommend | top (int), status (string) | 获取推荐榜单前 n 名 | [RecommendRankingDto] |
| GET | /api/Ranking/score | top (int), status (string) | 获取评分榜单前 n 名 | [ScoreRankingDto] |

## 评分 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/Rate | novelId (int), readerId (int), score (int) | 添加评分 | Rate |
| DELETE | /api/Rate | novelId (int), readerId (int) | 删除评分记录 | 200 OK |
| GET | /api/Rate/novel/{novelId} | novelId (int) | 获取某本小说的全部评分记录 | [Rate] |
| GET | /api/Rate/reader/{readerId} | readerId (int) | 获取某位读者对小说的评分记录 | [Rate] |
| GET | /api/Rate | 无 | 获取全部评分记录 | [Rate] |

## 读者 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/Reader/UploadAvatar | readerId (int), avatarFile (IFormFile) | 上传读者头像 | { success: true, avatarUrl: "string" } |
| POST | /api/Reader/UploadBackGround | readerId (int), bgFile (IFormFile) | 上传读者背景 | { success: true, bgUrl: "string" } |
| GET | /api/Reader/{readerId}/balance | readerId (int) | 获取读者余额 | { balance: decimal } |
| OPTIONS | /api/Reader | 无 | 处理 CORS 预检请求 | 200 OK |
| GET | /api/Reader | 无 | 获取所有读者 | [Reader] |
| GET | /api/Reader/{id} | id (int) | 根据ID获取读者 | Reader |
| POST | /api/Reader | entity (Reader) | 创建读者 | Reader |
| PUT | /api/Reader/{id} | id (int), entity (Reader) | 更新读者 | Reader |
| DELETE | /api/Reader/{id} | id (int) | 删除读者 | 200 OK |

## 最近阅读 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/RecentReadings/add-or-update | readerId (int), novelId (int), chapterId (int) | 添加或更新读者最近阅读记录 | RecentReading |
| DELETE | /api/RecentReadings/delete | readerId (int), novelId (int) | 删除指定读者的某本小说的最近阅读记录 | 200 OK |
| GET | /api/RecentReadings/list | readerId (int) | 获取指定读者的最近阅读记录列表 | [RecentReading] |
| GET | /api/RecentReadings/last-read-chapter | readerId (int), novelId (int) | 获取读者最近阅读的章节ID | { chapterId: int } |

## 充值 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/Recharge/start | dto (RechargeRequestDto) | 发起充值并返回支付URL | PaymentResponse |
| POST | /api/Recharge/notify | 无 | 支付宝异步通知（后台调用） | { success: boolean } |

## 推荐 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/Recommend | novelId (int), readerId (int), reason (string) | 推荐小说 | Recommend |
| DELETE | /api/Recommend | novelId (int), readerId (int) | 取消推荐 | 200 OK |
| GET | /api/Recommend/reader/{readerId} | readerId (int) | 获取某个读者推荐的所有小说 | [Recommend] |
| GET | /api/Recommend/novel/{novelId} | novelId (int) | 获取某部小说被哪些读者推荐 | [Recommend] |
| GET | /api/Recommend | 无 | 获取所有推荐记录 | [Recommend] |

## 举报 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/comment/{commentId}/report | commentId (int), readerId (int), reason (string) | 举报评论 | Report |
| POST | /api/report/{reportId}/process | reportId (int), progress (string), managerId (int), result (string) | 处理举报 | { success: boolean, message: string } |
| OPTIONS | /api/Reports | 无 | 处理 CORS 预检请求 | 200 OK |
| GET | /api/Reports | 无 | 获取所有举报 | [Report] |
| GET | /api/Reports/{id} | id (int) | 根据ID获取举报 | Report |
| POST | /api/Reports | entity (Report) | 创建举报 | Report |
| PUT | /api/Reports/{id} | id (int), entity (Report) | 更新举报 | Report |
| DELETE | /api/Reports/{id} | id (int) | 删除举报 | 200 OK |

## 举报管理 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| GET | /api/ReportManagement/{reportId}/logs | reportId (int) | 获取指定举报的所有管理处理日志 | [ReportManagement] |
| GET | /api/ReportManagement/logs/all | 无 | 获取所有举报的管理日志列表 | [ReportManagement] |
| GET | /api/ReportManagement/reader/{readerId}/reports-with-logs | readerId (int) | 获取指定读者发布的所有举报及其管理处理进度 | [ReportWithLogs] |

## 打赏 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/Reward | dto (RewardRequestDto) | 用户打赏 | { success: boolean, message: string, transactionId: int } |

## 搜索 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| GET | /api/Search/novel | keyword (string) | 通过小说名模糊搜索小说 | [Novel] |
| GET | /api/Search/author | keyword (string) | 通过作者名模糊搜索作者 | [Author] |
| GET | /api/Search/reader | keyword (string) | 通过读者名模糊搜索读者 | [Reader] |

## 统计 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| GET | /api/Statistics/total-novels | 无 | 获取总小说数 | { totalNovels: int } |
| GET | /api/Statistics/total-authors | 无 | 获取作者总数 | { totalAuthors: int } |
| GET | /api/Statistics/total-readers | 无 | 获取读者总数 | { totalReaders: int } |
| GET | /api/Statistics/pending-chapters | 无 | 获取待审核章节数 | { pendingChapters: int } |
| GET | /api/Statistics/pending-novels | 无 | 获取待审核小说数 | { pendingNovels: int } |

## 测试 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/Test/upload | file (IFormFile) | 上传图片测试 | { success: boolean, url: string } |
| DELETE | /api/Test/delete | fileName (string) | 删除图片测试 | { success: boolean, message: string } |

## 交易记录 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| GET | /api/Transaction/reward/{readerId} | readerId (int) | 获取读者的打赏记录 | [VReaderRewardRecord] |
| GET | /api/Transaction/subscription/{readerId} | readerId (int) | 获取读者的订阅记录 | [VReaderSubscriptionRecord] |
| GET | /api/Transaction/recharge/{readerId} | readerId (int) | 获取读者的充值记录 | [VReaderRechargeRecord] |
| GET | /api/Transaction/transaction/{readerId} | readerId (int) | 获取读者的所有交易记录 | [Transaction] |
| OPTIONS | /api/Transaction | 无 | 处理 CORS 预检请求 | 200 OK |
| GET | /api/Transaction | 无 | 获取所有交易记录 | [Transaction] |
| GET | /api/Transaction/{id} | id (int) | 根据ID获取交易记录 | Transaction |
| POST | /api/Transaction | entity (Transaction) | 创建交易记录 | Transaction |
| PUT | /api/Transaction/{id} | id (int), entity (Transaction) | 更新交易记录 | Transaction |
| DELETE | /api/Transaction/{id} | id (int) | 删除交易记录 | 200 OK |

## 整本购买 API

| 操作 | URL | 参数 | 名称 | 返回的Response body格式 |
|------|-----|------|------|-------------------------|
| POST | /api/WholePurchase | dto (WholePurchaseDto) | 整本小说买断接口 | { success: boolean, message: string, transactionId: int } |
| GET | /api/WholePurchase/status | readerId (int), novelId (int) | 查询读者是否已整本买断某小说 | { isWholePurchased: boolean } |

---

*本文档自动生成于 OurNovel 项目，包含所有控制器中定义的 API 接口。*