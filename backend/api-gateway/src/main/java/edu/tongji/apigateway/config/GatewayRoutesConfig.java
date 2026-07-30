package edu.tongji.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder,
                               @Value("${services.user:http://localhost:7081}") String userService,
                               @Value("${services.content:http://localhost:7082}") String contentService,
                               @Value("${services.transaction:http://localhost:7083}") String transactionService,
                               @Value("${services.admin:http://localhost:7084}") String adminService,
                               @Value("${services.notification:http://localhost:7085}") String notificationService) {
        return builder.routes()
                // Reader API路由：/api/Reader/** -> user-service:7081/readers/**
                // 例如：/api/Reader/1 -> http://localhost:7081/readers/1
                // 处理根路径：/api/Reader -> /readers
                .route("reader-api-root", r -> r
                        .path("/api/Reader")
                        .filters(f -> f.rewritePath("/api/Reader", "/readers"))
                        .uri(userService))
                // Reader余额API路由：/api/Reader/{readerId}/balance -> user-service:7081/readers/{readerId}/balance
                .route("reader-balance-api", r -> r
                        .path("/api/Reader/{readerId}/balance")
                        .filters(f -> f.rewritePath("/api/Reader/(?<readerId>[^/]+)/balance", "/readers/${readerId}/balance"))
                        .uri(userService))
                // Reader扣除余额API路由：/api/Reader/{readerId}/deduct-balance -> user-service:7081/readers/{readerId}/deduct-balance
                .route("reader-deduct-balance-api", r -> r
                        .path("/api/Reader/{readerId}/deduct-balance")
                        .filters(f -> f.rewritePath("/api/Reader/(?<readerId>[^/]+)/deduct-balance", "/readers/${readerId}/deduct-balance"))
                        .uri(userService))
                .route("reader-api", r -> r
                        .path("/api/Reader/**")
                        .filters(f -> f.rewritePath("/api/Reader/(?<segment>.*)", "/readers/${segment}"))
                        .uri(userService))
                // Author Authentication API路由：/api/LogAuthor/** -> user-service:7081/api/LogAuthor/**
                .route("author-auth-api", r -> r
                        .path("/api/LogAuthor/**")
                        .filters(f -> f.rewritePath("/api/LogAuthor/(?<segment>.*)", "/authors/${segment}"))
                        .uri(userService))
                // Author API路由：/api/Author/** -> user-service:7081/authors/**
                // 例如：/api/Author -> http://localhost:7081/authors
                // 处理根路径：/api/Author -> /authors
                .route("author-api-root", r -> r
                        .path("/api/Author")
                        .filters(f -> f.rewritePath("/api/Author", "/authors"))
                        .uri(userService))
                // Author注册天数API路由：/api/Author/{authorId}/register-days -> user-service:7081/authors/{authorId}/register-days
                .route("author-register-days-api", r -> r
                        .path("/api/Author/{authorId}/register-days")
                        .filters(f -> f.rewritePath("/api/Author/(?<authorId>[^/]+)/register-days", "/authors/${authorId}/register-days"))
                        .uri(userService))
                // Author增加收入API路由：/api/Author/{authorId}/add-earning -> user-service:7081/authors/{authorId}/add-earning
                .route("author-add-earning-api", r -> r
                        .path("/api/Author/{authorId}/add-earning")
                        .filters(f -> f.rewritePath("/api/Author/(?<authorId>[^/]+)/add-earning", "/authors/${authorId}/add-earning"))
                        .uri(userService))
                // 作者小说数量API路由：/api/Author/{authorId}/novel-count -> content-service:7082/author/{authorId}/novel-count
                .route("author-novel-count-api", r -> r
                        .path("/api/Author/{authorId}/novel-count")
                        .filters(f -> f.rewritePath("/api/Author/(?<authorId>[^/]+)/novel-count", "/author/${authorId}/novel-count"))
                        .uri(contentService))
                // 作者总字数API路由：/api/Author/{authorId}/total-wordcount -> content-service:7082/author/{authorId}/total-wordcount
                .route("author-total-wordcount-api", r -> r
                        .path("/api/Author/{authorId}/total-wordcount")
                        .filters(f -> f.rewritePath("/api/Author/(?<authorId>[^/]+)/total-wordcount", "/author/${authorId}/total-wordcount"))
                        .uri(contentService))
                // 作者小说列表API路由：/api/Author/{authorId}/novels -> content-service:7082/author/{authorId}/novels
                .route("author-novels-api", r -> r
                        .path("/api/Author/{authorId}/novels")
                        .filters(f -> f.rewritePath("/api/Author/(?<authorId>[^/]+)/novels", "/author/${authorId}/novels"))
                        .uri(contentService))
                .route("author-api", r -> r
                        .path("/api/Author/**")
                        .filters(f -> f.rewritePath("/api/Author/(?<segment>.*)", "/authors/${segment}"))
                        .uri(userService))
                // Reader Authentication API路由：/api/LogReader/** -> user-service:7081/readers/**
                // 例如：/api/LogReader/login-reader -> http://localhost:7081/readers/login-reader
                .route("reader-auth-api", r -> r
                        .path("/api/LogReader/**")
                        .filters(f -> f.rewritePath("/api/LogReader/(?<segment>.*)", "/readers/${segment}"))
                        .uri(userService))
                // Manager Authentication API路由：/api/LogManager/** -> user-service:7081/api/LogManager/**
                .route("manager-auth-api", r -> r
                        .path("/api/LogManager/**")
                        .filters(f -> f.rewritePath("/api/LogManager/(?<segment>.*)", "/managers/${segment}"))
                        .uri(userService))
                // Manager API路由：/api/Manager/** -> user-service:7081/managers/**
                // 例如：/api/Manager -> http://localhost:7081/managers
                .route("manager-api", r -> r
                        .path("/api/Manager/**")
                        .filters(f -> f.rewritePath("/api/Manager/(?<segment>.*)", "/managers/${segment}"))
                        .uri(userService))
                // Collect API路由：/api/Collect/** -> content-service:7082/api/Collect/**
                // 例如：/api/Collect/reader/1 -> http://localhost:7082/api/Collect/reader/1
                .route("collect-api", r -> r
                        .path("/api/Collect/**")
                        .uri(contentService))
                // Recommend API路由：/api/Recommend/** -> content-service:7082/api/Recommend/**
                // 例如：/api/Recommend/reader/1 -> http://localhost:7082/api/Recommend/reader/1
                .route("recommend-api", r -> r
                        .path("/api/Recommend/**")
                        .uri(contentService))
                // Rate API路由：/api/Rate/** -> content-service:7082/api/Rate/**
                // 例如：/api/Rate/novel/1 -> http://localhost:7082/api/Rate/novel/1
                .route("rate-api", r -> r
                        .path("/api/Rate/**")
                        .uri(contentService))
                // Ranking API路由：/api/Ranking/** -> content-service:7082/api/Ranking/**
                // 例如：/api/Ranking/collect -> http://localhost:7082/api/Ranking/collect
                .route("ranking-api", r -> r
                        .path("/api/Ranking/**")
                        .uri(contentService))
                // Comments API路由：/api/Comments/** -> content-service:7082/api/Comments/**
                // 例如：/api/Comments/ByChapter/1/1 -> http://localhost:7082/api/Comments/ByChapter/1/1
                .route("comments-api", r -> r
                        .path("/api/Comments/**")
                        .uri(contentService))
                // CommentReply API路由：/api/CommentReply/** -> content-service:7082/api/CommentReply/**
                // 例如：/api/CommentReply/1 -> http://localhost:7082/api/CommentReply/1
                .route("comment-reply-api", r -> r
                        .path("/api/CommentReply/**")
                        .uri(contentService))
                // Likes API路由：/api/Likes/** -> content-service:7082/api/Likes/**
                // 例如：/api/Likes/Like -> http://localhost:7082/api/Likes/Like
                .route("likes-api", r -> r
                        .path("/api/Likes/**")
                        .uri(contentService))
                // Novel API路由：/api/Novel/** -> content-service:7082/novels/**
                // 例如：/api/Novel -> http://localhost:7082/novels
                // 处理根路径：/api/Novel -> /novels
                .route("novel-api-root", r -> r
                        .path("/api/Novel")
                        .filters(f -> f.rewritePath("/api/Novel", "/novels"))
                        .uri(contentService))
                // Novel审核API路由：/api/Novel/{id}/review -> content-service:7082/novels/{id}/review
                .route("novel-review-api", r -> r
                        .path("/api/Novel/{id}/review")
                        .filters(f -> f.rewritePath("/api/Novel/(?<id>[^/]+)/review", "/novels/${id}/review"))
                        .uri(contentService))
                // Novel字数统计API路由：/api/Novel/wordcount/{novelId} -> content-service:7082/novels/wordcount/{novelId}
                .route("novel-wordcount-api", r -> r
                        .path("/api/Novel/wordcount/{novelId}")
                        .filters(f -> f.rewritePath("/api/Novel/wordcount/(?<novelId>[^/]+)", "/novels/wordcount/${novelId}"))
                        .uri(contentService))
                // Novel推荐数统计API路由：/api/Novel/recommendcount/{novelId} -> content-service:7082/novels/recommendcount/{novelId}
                .route("novel-recommendcount-api", r -> r
                        .path("/api/Novel/recommendcount/{novelId}")
                        .filters(f -> f.rewritePath("/api/Novel/recommendcount/(?<novelId>[^/]+)", "/novels/recommendcount/${novelId}"))
                        .uri(contentService))
                // Novel收藏数统计API路由：/api/Novel/collectcount/{novelId} -> content-service:7082/novels/collectcount/{novelId}
                .route("novel-collectcount-api", r -> r
                        .path("/api/Novel/collectcount/{novelId}")
                        .filters(f -> f.rewritePath("/api/Novel/collectcount/(?<novelId>[^/]+)", "/novels/collectcount/${novelId}"))
                        .uri(contentService))
                // Novel最新已发布章节API路由：/api/Novel/{novelId}/latest-published-chapter -> content-service:7082/novels/{novelId}/latest-published-chapter
                .route("novel-latest-chapter-api", r -> r
                        .path("/api/Novel/{novelId}/latest-published-chapter")
                        .filters(f -> f.rewritePath("/api/Novel/(?<novelId>[^/]+)/latest-published-chapter", "/novels/${novelId}/latest-published-chapter"))
                        .uri(contentService))
                .route("novel-api", r -> r
                        .path("/api/Novel/**")
                        .filters(f -> f.rewritePath("/api/Novel/(?<segment>.*)", "/novels/${segment}"))
                        .uri(contentService))
                // Novel创建API路由：/api/novel/create -> content-service:7082/novels/create
                .route("novel-create-api", r -> r
                        .path("/api/novel/create")
                        .filters(f -> f.rewritePath("/api/novel/create", "/novels/create"))
                        .uri(contentService))
                // Chapter API路由：/api/Chapter/** -> content-service:7082/chapters/**
                // 例如：/api/Chapter -> http://localhost:7082/chapters
                // 处理根路径：/api/Chapter -> /chapters
                .route("chapter-api-root", r -> r
                        .path("/api/Chapter")
                        .filters(f -> f.rewritePath("/api/Chapter", "/chapters"))
                        .uri(contentService))
                // Chapter小说章节API路由：/api/Chapter/novel/{novelId} -> content-service:7082/chapters/novel/{novelId}
                .route("chapter-novel-api", r -> r
                        .path("/api/Chapter/novel/{novelId}")
                        .filters(f -> f.rewritePath("/api/Chapter/novel/(?<novelId>[^/]+)", "/chapters/novel/${novelId}"))
                        .uri(contentService))
                // Chapter小说所有章节API路由：/api/Chapter/novels/{novelId}/chapters -> content-service:7082/chapters/novels/{novelId}/chapters
                .route("chapter-novels-chapters-api", r -> r
                        .path("/api/Chapter/novels/{novelId}/chapters")
                        .filters(f -> f.rewritePath("/api/Chapter/novels/(?<novelId>[^/]+)/chapters", "/chapters/novels/${novelId}/chapters"))
                        .uri(contentService))
                // Chapter章节详情API路由：/api/Chapter/{novelId}/{chapterId} -> content-service:7082/chapters/{novelId}/{chapterId}
                .route("chapter-detail-api", r -> r
                        .path("/api/Chapter/{novelId}/{chapterId}")
                        .filters(f -> f.rewritePath("/api/Chapter/(?<novelId>[^/]+)/(?<chapterId>[^/]+)", "/chapters/${novelId}/${chapterId}"))
                        .uri(contentService))
                .route("chapter-api", r -> r
                        .path("/api/Chapter/**")
                        .filters(f -> f.rewritePath("/api/Chapter/(?<segment>.*)", "/chapters/${segment}"))
                        .uri(contentService))
                // Category API路由：/api/Category/** -> content-service:7082/api/Category/**
                // 例如：/api/Category -> http://localhost:7082/api/Category
                .route("category-api", r -> r
                        .path("/api/Category/**")
                        .uri(contentService))
                // NovelCategory API路由：/api/NovelCategory/** -> content-service:7082/api/NovelCategory/**
                // 例如：/api/NovelCategory -> http://localhost:7082/api/NovelCategory
                .route("novel-category-api", r -> r
                        .path("/api/NovelCategory/**")
                        .uri(contentService))
                // RecentReadings API路由：/api/RecentReadings/** -> user-service:7081/recent-readings/**
                // 例如：/api/RecentReadings/list?readerId=1 -> http://localhost:7081/recent-readings/list?readerId=1
                .route("recent-readings-api", r -> r
                        .path("/api/RecentReadings/**")
                        .filters(f -> f.rewritePath("/api/RecentReadings/(?<segment>.*)", "/recent-readings/${segment}"))
                        .uri(userService))
                // ChapterManagement API路由：/api/ChapterManagement/** -> admin-service:7084/api/ChapterManagement/**
                // 例如：/api/ChapterManagement/logs/1 -> http://localhost:7084/api/ChapterManagement/logs/1
                .route("chapter-management-api", r -> r
                        .path("/api/ChapterManagement/**")
                        .uri(adminService))
                // Management API路由：/api/Management/** -> admin-service:7084/api/Management/**
                // 例如：/api/Management -> http://localhost:7084/api/Management
                .route("management-api", r -> r
                        .path("/api/Management/**")
                        .uri(adminService))
                // NovelManagement API路由：/api/NovelManagement/** -> admin-service:7084/api/NovelManagement/**
                // 例如：/api/NovelManagement/logs/1 -> http://localhost:7084/api/NovelManagement/logs/1
                .route("novel-management-api", r -> r
                        .path("/api/NovelManagement/**")
                        .uri(adminService))
                // CommentManagement API路由：/api/CommentManagement/** -> admin-service:7084/api/CommentManagement/**
                // 例如：/api/CommentManagement/logs/1 -> http://localhost:7084/api/CommentManagement/logs/1
                .route("comment-management-api", r -> r
                        .path("/api/CommentManagement/**")
                        .uri(adminService))
                // 搜索API路由：/api/Search/novel -> content-service:7082/novels/search
                .route("novel-search-api", r -> r
                        .path("/api/Search/novel")
                        .filters(f -> f.rewritePath("/api/Search/novel", "/novels/search"))
                        .uri(contentService))
                // 搜索API路由：/api/Search/author -> user-service:7081/authors/search
                .route("author-search-api", r -> r
                        .path("/api/Search/author")
                        .filters(f -> f.rewritePath("/api/Search/author", "/authors/search"))
                        .uri(userService))
                // 搜索API路由：/api/Search/reader -> user-service:7081/readers/search
                .route("reader-search-api", r -> r
                        .path("/api/Search/reader")
                        .filters(f -> f.rewritePath("/api/Search/reader", "/readers/search"))
                        .uri(userService))
                // 统计API路由：/api/Statistics/total-novels -> content-service:7082/novels/statistics/total-novels
                .route("total-novels-statistics-api", r -> r
                        .path("/api/Statistics/total-novels")
                        .filters(f -> f.rewritePath("/api/Statistics/total-novels", "/novels/statistics/total-novels"))
                        .uri(contentService))
                // 统计API路由：/api/Statistics/pending-novels -> content-service:7082/novels/statistics/pending-novels
                .route("pending-novels-statistics-api", r -> r
                        .path("/api/Statistics/pending-novels")
                        .filters(f -> f.rewritePath("/api/Statistics/pending-novels", "/novels/statistics/pending-novels"))
                        .uri(contentService))
                // 统计API路由：/api/Statistics/total-authors -> user-service:7081/authors/statistics/total-authors
                .route("total-authors-statistics-api", r -> r
                        .path("/api/Statistics/total-authors")
                        .filters(f -> f.rewritePath("/api/Statistics/total-authors", "/authors/statistics/total-authors"))
                        .uri(userService))
                // 统计API路由：/api/Statistics/total-readers -> user-service:7081/readers/statistics/total-readers
                .route("total-readers-statistics-api", r -> r
                        .path("/api/Statistics/total-readers")
                        .filters(f -> f.rewritePath("/api/Statistics/total-readers", "/readers/statistics/total-readers"))
                        .uri(userService))
                // 统计API路由：/api/Statistics/pending-chapters -> content-service:7082/chapters/statistics/pending-chapters
                .route("pending-chapters-statistics-api", r -> r
                        .path("/api/Statistics/pending-chapters")
                        .filters(f -> f.rewritePath("/api/Statistics/pending-chapters", "/chapters/statistics/pending-chapters"))
                        .uri(contentService))
                // 统计API路由：/api/Statistics/pending-reports -> content-service:7082/reports/statistics/pending-reports
                .route("pending-reports-statistics-api", r -> r
                        .path("/api/Statistics/pending-reports")
                        .filters(f -> f.rewritePath("/api/Statistics/pending-reports", "/reports/statistics/pending-reports"))
                        .uri(contentService))
                // 整本购买API路由：/api/WholePurchase -> content-service:7082/wholepurchase
                .route("wholepurchase-api", r -> r
                        .path("/api/WholePurchase/**")
                        .filters(f -> f.rewritePath("/api/WholePurchase/(?<segment>.*)", "/wholepurchase/${segment}"))
                        .uri(contentService))
                // 举报API路由：/api/comment/{commentId}/report -> content-service:7082/reports/comment/{commentId}/report
                .route("report-comment-api", r -> r
                        .path("/api/comment/{commentId}/report")
                        .filters(f -> f.rewritePath("/api/comment/(?<commentId>.*)/report", "/reports/comment/${commentId}/report"))
                        .uri(contentService))
                // 举报API路由：/api/report/{reportId}/process -> content-service:7082/reports/report/{reportId}/process
                .route("process-report-api", r -> r
                        .path("/api/report/{reportId}/process")
                        .filters(f -> f.rewritePath("/api/report/(?<reportId>.*)/process", "/reports/report/${reportId}/process"))
                        .uri(contentService))
                // 举报API路由：/api/Reports/** -> content-service:7082/reports/**
                // 处理根路径：/api/Reports -> /reports
                .route("reports-api-root", r -> r
                        .path("/api/Reports")
                        .filters(f -> f.rewritePath("/api/Reports", "/reports"))
                        .uri(contentService))
                .route("reports-api", r -> r
                        .path("/api/Reports/**")
                        .filters(f -> f.rewritePath("/api/Reports/(?<segment>.*)", "/reports/${segment}"))
                        .uri(contentService))
                // 举报管理API路由：/api/ReportManagement/** -> admin-service:7084/api/ReportManagement/**
                .route("report-management-api", r -> r
                        .path("/api/ReportManagement/**")
                        .uri(adminService))
                // 作者收入API路由：/api/AuthorIncome/** -> transaction-service:7083/author-income/**
                // 例如：/api/AuthorIncome/list/1 -> http://localhost:7083/author-income/list/1
                .route("author-income-api", r -> r
                        .path("/api/AuthorIncome/**")
                        .filters(f -> f.rewritePath("/api/AuthorIncome/(?<segment>.*)", "/author-income/${segment}"))
                        .uri(transactionService))
                // 交易记录API路由：/api/Transaction/** -> transaction-service:7083/transactions/**
                // 例如：/api/Transaction/reward/1 -> http://localhost:7083/transactions/reward/1
                .route("transaction-api", r -> r
                        .path("/api/Transaction/**")
                        .filters(f -> f.rewritePath("/api/Transaction/(?<segment>.*)", "/transactions/${segment}"))
                        .uri(transactionService))
                // 打赏API路由：/api/Reward/** -> transaction-service:7083/rewards/**
                // 例如：/api/Reward -> http://localhost:7083/rewards
                .route("reward-api", r -> r
                        .path("/api/Reward/**")
                        .filters(f -> f.rewritePath("/api/Reward/(?<segment>.*)", "/rewards/${segment}"))
                        .uri(transactionService))
                // 章节购买API路由：/api/Purchase/** -> transaction-service:7083/api/Purchase/**
                // 例如：/api/Purchase -> http://localhost:7083/api/Purchase
                .route("purchase-api", r -> r
                        .path("/api/Purchase/**")
                        .uri(transactionService))
                // 充值API路由：/api/Recharge/** -> transaction-service:7083/api/Recharge/**
                // 例如：/api/Recharge/start -> http://localhost:7083/api/Recharge/start
                .route("recharge-api", r -> r
                        .path("/api/Recharge/**")
                        .uri(transactionService))
                // 支付宝支付通知路由：/api/payment/alipay/notify -> transaction-service:7083/api/Recharge/notify
                // 用于支付宝异步通知回调
                .route("alipay-payment-notify-api", r -> r
                        .path("/api/payment/alipay/notify")
                        .filters(f -> f.rewritePath("/api/payment/alipay/notify", "/api/Recharge/notify"))
                        .uri(transactionService))
                .route("published-novels-api", r -> r
                        .path("/published/**")
                        .filters(f -> f.rewritePath("/published/(?<segment>.*)", "/novels/published/${segment}"))
                        .uri(contentService))
                // WebSocket 通知推送路由：/ws/** -> notification-service:7085/ws/**
                // 网关会自动透传 WebSocket 升级握手
                .route("notification-ws-api", r -> r
                        .path("/ws/**")
                        .uri(notificationService))
                .build();
    }
}

