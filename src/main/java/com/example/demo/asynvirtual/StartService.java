package com.example.demo.asynvirtual;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class StartService {

    @Autowired
    JobLauncher jobLauncher;

//    @Autowired
//    @Qualifier(AsyncBatchConfiguration.ASYNC_JOB_NAME)
//    Job asyncJob;

    @Autowired
    @Qualifier(VirtualAsyncBatchConfiguration.VIRTUAL_ASYNC_NAME)
    Job virtualAsyncJob;

    public void run() {
        try (SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor()) {

            taskExecutor.execute(() -> {

                try {
                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString(VirtualAsyncBatchConfiguration.VIRTUAL_ASYNC_NAME, "name 1")
                            .toJobParameters();

                    jobLauncher.run(virtualAsyncJob, jobParameters);
                } catch (JobExecutionAlreadyRunningException | JobRestartException |
                         JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
                    throw new RuntimeException(e);
                }

            });
        }
    }
}
