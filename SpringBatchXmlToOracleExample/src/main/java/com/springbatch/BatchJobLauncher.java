package com.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.springbatch.config.BatchConfiguration;
import com.springbatch.config.DataSourceConfig;

public class BatchJobLauncher {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(DataSourceConfig.class);
		context.register(BatchConfiguration.class);
		context.refresh();

		JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
		Job job = (Job) context.getBean("firstBatchJob");
		System.out.println("Starting the batch job");
		try {
			JobExecution execution = jobLauncher.run(job, new JobParameters());
			System.out.println("Job Status : " + execution.getStatus());
			System.out.println("Job completed");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Job failed");
		}

		context.close();
	}
}