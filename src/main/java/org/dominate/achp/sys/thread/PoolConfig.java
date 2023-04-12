package org.dominate.achp.sys.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * 线程池配置
 * @author dominate
 * @since 2023-04-03
 */
@EnableAsync
@Configuration
public class PoolConfig {

    @Primary
    @Bean(name = "commonExecutor")
    public Executor commonExecutorPool(){
        PoolProperties pool = new PoolProperties();
        pool.setThreadNamePrefix("commonExecutor-");
        return create(pool);
    }

    public Executor create(PoolProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数量，线程池创建时候初始化的线程数
        executor.setCorePoolSize(properties.getCorePoolSize());
        //最大线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        //缓冲队列，用来缓冲执行任务的队列
        executor.setQueueCapacity(properties.getQueueCapacity());
        //当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(properties.getKeepAliveTime());
        //设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());
        //用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //该方法用来设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住。
        executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
        //线程池对拒绝任务的处理策略：这里采用了CallerRunsPolicy策略，当线程池没有处理能力的时候，该策略会直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        // 通过spring @Bean方式注入ThreadPoolTaskExecutor实例时，可以不需要这一步。
        // 由于ThreadPoolTaskExecutor继承了ExecutorConfigurationSupport,初始化对象时会调用ExecutorConfigurationSupport.afterPropertiesSet()
        executor.initialize();
        return executor;
    }
}
