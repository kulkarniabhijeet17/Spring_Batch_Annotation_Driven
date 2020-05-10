package com.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.springbatch.config.BatchConfiguration;
import com.springbatch.config.DataSourceConfig;
import com.springbatch.reader.CsvItemReader;
import com.springbatch.reader.OracleItemReader;
import com.springbatch.writer.OracleItemWriter;

public class BatchJobLauncher {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(DataSourceConfig.class);
		context.register(CsvItemReader.class);
		// Issue 2 Fix: Start
		context.register(OracleItemWriter.class);
		context.register(OracleItemReader.class);
		context.register(BatchConfiguration.class);
		// Issue 2 Fix: End
		// Issue 3 Fix: Start
		context.scan("com");
		// Issue 3 Fix: End
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