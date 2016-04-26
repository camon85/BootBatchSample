package com.camon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by user on 2016-03-31.
 */
@SpringBootApplication
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        SpringApplication app = new SpringApplication(App.class);
        app.setBannerMode(Banner.Mode.OFF);
        ConfigurableApplicationContext ctx = app.run(args);

        Job txtToDatabaseJob = ctx.getBean("txtToDatabaseJob", Job.class);
        JobParameters defaultJobParameters = new JobParametersBuilder().toJobParameters();
//        JobParameters dateJobParameters = new JobParametersBuilder().addDate("date", new Date()).toJobParameters();

        JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
        JobExecution jobExecution = jobLauncher.run(txtToDatabaseJob, defaultJobParameters);
        BatchStatus status = jobExecution.getStatus();
        log.info("getStatus -> {}", status);
    }
}
