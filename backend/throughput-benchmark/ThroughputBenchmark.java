import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 吞吐对比基准测试：模拟"小说更新通知 / 订单回调"从同步调用改为 RabbitMQ 异步后的
 * 请求关键路径变化，测量端点吞吐量(ops/sec)并给出提升百分比。
 *
 * 核心原理（排队论）：
 *   同步：主请求线程必须等通知投递完成才返回  -> 关键路径 = DB耗时 + 通知耗时
 *   异步：主请求线程只把消息 publish 到 MQ 就返回 -> 关键路径 = DB耗时 + 发布耗时(很小)
 *   在固定并发下，端点吞吐量与关键路径耗时成反比，故异步显著提升吞吐。
 *
 * 运行：
 *   cd backend/throughput-benchmark
 *   javac ThroughputBenchmark.java
 *   java ThroughputBenchmark
 *
 * 说明：NOTIFY_REST_MS 代表"同步直连通知服务"的耗时（网络+下游处理），
 *       可替换为你们生产/压测真实观测到的 P99 延迟；MQ_PUBLISH_MS 为本地实测
 *       的 RabbitTemplate 发布耗时（通常 1~3ms）。比值即吞吐提升的理论上限。
 */
public class ThroughputBenchmark {

    // ===== 可调参数（改成你们自己观测到的真实延迟即可） =====
    static final long DB_WORK_MS        = 30;   // 主业务流程(DB/校验)耗时
    static final long NOTIFY_REST_MS    = 25;   // 同步调用通知服务的耗时（被移出关键路径的部分）
    static final long MQ_PUBLISH_MS     = 2;    // 异步 publish 到 RabbitMQ 的耗时（实测 ~1-3ms）
    static final int  CONCURRENCY       = 64;   // 并发线程数（模拟网关并发连接）
    static final long WARMUP_MS         = 2000; // 预热
    static final long MEASURE_MS        = 5000; // 正式测量时长

    public static void main(String[] args) throws Exception {
        System.out.println("=== TJNovel 异步化吞吐对比基准 ===");
        System.out.printf("DB_WORK_MS=%d  NOTIFY_REST_MS=%d  MQ_PUBLISH_MS=%d  CONCURRENCY=%d%n%n",
                DB_WORK_MS, NOTIFY_REST_MS, MQ_PUBLISH_MS, CONCURRENCY);

        long syncTput = runBenchmark("SYNC(同步调用通知)", ThroughputBenchmark::syncTask);
        long asyncTput = runBenchmark("ASYNC(RabbitMQ异步)", ThroughputBenchmark::asyncTask);

        double improve = (syncTput == 0) ? 0 : (asyncTput - syncTput) * 100.0 / syncTput;
        System.out.println("\n=== 结果 ===");
        System.out.printf("同步端点吞吐 : %d ops/sec%n", syncTput);
        System.out.printf("异步端点吞吐 : %d ops/sec%n", asyncTput);
        System.out.printf("吞吐量提升   : %.1f%%%n", improve);
        System.out.printf("关键路径缩短 : %.1f%%%n",
                NOTIFY_REST_MS * 100.0 / (DB_WORK_MS + NOTIFY_REST_MS));
    }

    // 预热 + 计时，返回完成的操作数/秒
    static long runBenchmark(String name, Runnable task) throws Exception {
        // 预热
        runLoad(task, WARMUP_MS);
        AtomicLong counter = new AtomicLong();
        long ops = runLoad(() -> { task.run(); counter.incrementAndGet(); }, MEASURE_MS);
        System.out.printf("[%s] 完成 %d 次操作 / %d ms -> %d ops/sec%n",
                name, counter.get(), MEASURE_MS, counter.get() * 1000 / MEASURE_MS);
        return counter.get() * 1000 / MEASURE_MS;
    }

    static long runLoad(Runnable task, long durationMs) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENCY);
        long end = System.nanoTime() + durationMs * 1_000_000L;
        CountDownLatch stop = new CountDownLatch(1);
        for (int i = 0; i < CONCURRENCY; i++) {
            pool.submit(() -> {
                while (System.nanoTime() < end) {
                    task.run();
                }
            });
        }
        stop.await(durationMs, TimeUnit.MILLISECONDS);
        Thread.sleep(durationMs);
        pool.shutdownNow();
        return 0;
    }

    // 同步：主线程阻塞等待通知投递完成
    static void syncTask() {
        sleep(DB_WORK_MS);
        sleep(NOTIFY_REST_MS);   // 通知服务调用（占关键路径）
    }

    // 异步：主线程仅发布消息到 MQ 即返回
    static void asyncTask() {
        sleep(DB_WORK_MS);
        sleep(MQ_PUBLISH_MS);    // publish 到 RabbitMQ（极短）
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
