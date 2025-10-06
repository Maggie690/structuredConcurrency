package com.example.demo.asynvirtual.process;

//import com.example.demo.asynvirtual.components.JobListener;
import com.example.demo.asynvirtual.components.LogItemWriter;
import com.example.demo.asynvirtual.components.NumberItemReader;
import com.example.demo.asynvirtual.components.SyncItemProcessor;
import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
public class BatchConfiguration {

    public static final String SYNC_JOB_NAME = "syncJob";

    /**
     * It uses chunk-oriented processing to read/process one item at a time,
     * and then write them all as a group (or chunk) of a certain size (defined as 100 in our case).
     *
     * @param jobRepository
     * @param transactionManager
     * @param numberItemReader
     * @param syncItemProcessor
     * @param logItemWriter
     * @return Step
     */
  //  @Bean("syncStep")
    public Step syncStep(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager,
                         NumberItemReader numberItemReader,
                         SyncItemProcessor syncItemProcessor,
                         LogItemWriter logItemWriter) {
        return new StepBuilder("sync-step", jobRepository)
                .<Integer, Integer>chunk(100, transactionManager)
                .reader(numberItemReader)
                .processor(syncItemProcessor)
                .writer(logItemWriter)
                .build();
    }

  //  @Bean(SYNC_JOB_NAME)
    public Job syncJob(JobRepository jobRepository,
                       Step syncStep) {
        return new JobBuilder(SYNC_JOB_NAME, jobRepository)
              //  .listener(jobListener())
                .incrementer(new RunIdIncrementer())
                .start(syncStep)
                .build();
    }
}
