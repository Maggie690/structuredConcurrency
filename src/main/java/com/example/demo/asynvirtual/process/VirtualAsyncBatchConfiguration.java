package com.example.demo.asynvirtual.process;

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
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.Future;

@Configuration
public class VirtualAsyncBatchConfiguration {

    public static final String VIRTUAL_ASYNC_NAME = "virtualAsyncJob";

    @Bean
    public AsyncItemProcessor<Integer, Integer> virtualAsyncItemprocessor(SyncItemProcessor syncItemProcessor) {
        var asyncItemProcessor = new AsyncItemProcessor<Integer, Integer>();
        asyncItemProcessor.setDelegate(syncItemProcessor);
        asyncItemProcessor.setTaskExecutor(new VirtualThreadTaskExecutor("virtual-"));

        return asyncItemProcessor;
    }

    @Bean("virtualAsyncStep")
    public Step virtualAsyncStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 NumberItemReader numberItemReader,
                                 AsyncItemProcessor<Integer, Integer> asyncItemProcessor,
                                 AsyncItemWriter<Integer> asyncItemWriter) {

        return new StepBuilder("virtual_async-step", jobRepository)
                .<Integer, Future<Integer>>chunk(100, transactionManager)
                .reader(numberItemReader)
                .processor(asyncItemProcessor)
                .writer(asyncItemWriter)
                .build();
    }

    @Bean(VIRTUAL_ASYNC_NAME)
    public Job virtualJob(JobRepository jobRepository,
                          Step step) {
        return new JobBuilder(VIRTUAL_ASYNC_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }
}
