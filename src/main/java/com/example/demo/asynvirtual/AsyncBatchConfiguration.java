package com.example.demo.asynvirtual;

import com.example.demo.asynvirtual.components.LogItemWriter;
import com.example.demo.asynvirtual.components.NumberItemReader;
import com.example.demo.asynvirtual.components.SyncItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.Future;

@Configuration
public class AsyncBatchConfiguration {

    public static final String ASYNC_JOB_NAME = "asyncJob";

    /**
     * Encapsulates our previous processor and gives us the ability to attach a custom TaskExecutor to it.
     *
     * @param itemProcessor
     * @param threadPoolTaskExecutor
     * @return
     */
    @Bean("asyncItemProcessor")
    public AsyncItemProcessor<Integer, Integer> asyncItemProcessor(SyncItemProcessor itemProcessor,
                                                                   TaskExecutor threadPoolTaskExecutor) {
        var asyncItemProcessor = new AsyncItemProcessor<Integer, Integer>();
        asyncItemProcessor.setDelegate(itemProcessor);
        asyncItemProcessor.setTaskExecutor(threadPoolTaskExecutor);
        return asyncItemProcessor;
    }

    @Bean("threadPoolTaskExecutor")
    public TaskExecutor threadPoolTaskExecutor() {
        var taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setThreadNamePrefix("platform-");
        return taskExecutor;
    }

    /**
     *  'AsyncItemWriter<T> implements ItemStreamWriter<Future<T>>' return the Future object, because of that
     *  AsyncItemWriter wraps our previous LogItemWriter and handles the asynchronous output from the processor.
     *
     * @param itemWriter
     * @return
     */
    @Bean("asyncWriter")
    public AsyncItemWriter<Integer> asyncWriter(LogItemWriter itemWriter) {
        var asyncItemWriter = new AsyncItemWriter<Integer>();
        asyncItemWriter.setDelegate(itemWriter);
        return asyncItemWriter;
    }

    @Bean("asyncStep")
    public Step asyncStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          NumberItemReader numberItemReader,
                          AsyncItemProcessor<Integer, Integer> asyncItemProcessor,
                          AsyncItemWriter<Integer> asyncItemWriter) {

        return new StepBuilder("async-step", jobRepository)
                .<Integer, Future<Integer>>chunk(100, transactionManager)
                .reader(numberItemReader)
                .processor(asyncItemProcessor)
                .writer(asyncItemWriter)
                .build();

    }

    @Primary
    @Bean(ASYNC_JOB_NAME)
    public Job asyncJob(JobRepository jobRepository,
                        Step asyncStep) {

        return new JobBuilder(ASYNC_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(asyncStep)
                .build();
    }
}
