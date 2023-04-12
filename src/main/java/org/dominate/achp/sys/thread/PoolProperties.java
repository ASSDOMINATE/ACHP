package org.dominate.achp.sys.thread;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池读取配置
 * @author dominate
 * @since 2023-04-03
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "thread.pool")
public class PoolProperties {

    /**
     * 核心线程数（默认线程数）
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * 最大线程数
     */
    private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    private int keepAliveTime = 60;
    /**
     * 缓冲队列大小
     */
    private int queueCapacity = 200;

    /**
     * 任务的等待时间 单位秒
     */
    private int awaitTerminationSeconds = 60;

    /**
     * 线程池名前缀
     */
    private String threadNamePrefix = "defaultTask-";

}
