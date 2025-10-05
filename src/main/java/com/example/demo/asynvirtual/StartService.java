package com.example.demo.asynvirtual;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class StartService {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier(BatchConfiguration.SYNC_JOB_NAME)
    Job syncJob;

    public void run() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("ID", "name 1")
                .toJobParameters();

        try {
            System.out.println("----> Running");
            jobLauncher.run(syncJob, jobParameters);

        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | JobRestartException e) {
            throw new RuntimeException(e);
        }
    }
}
