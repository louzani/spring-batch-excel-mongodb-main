package com.springbatch.excel.tutorial.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author aek
 */
@Component
public class MultiFilesJobLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiFilesJobLauncher.class);

    private final Job job;

    private final JobLauncher jobLauncher;


    MultiFilesJobLauncher(@Qualifier("readFiles") Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    // run every 2 min
 //   @Scheduled(fixedRate = 300000000)
    void launchFileToJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobInstanceAlreadyCompleteException, JobRestartException {
        LOGGER.info("Starting job");

        JobParameters params = new JobParametersBuilder()
                .addLong("jobId",System.currentTimeMillis())
                .addDate("currentTime",new Date())
                .toJobParameters();

        jobLauncher.run(job, params);

        LOGGER.info("Stopping job");
    }

}
