package com.springbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.springbatch.listener.JobCompletionNotificationListener;
import com.springbatch.model.CopyTable;
import com.springbatch.model.Record;
import com.springbatch.processor.CsvRecordProcessor;

@EnableBatchProcessing
public class BatchConfiguration {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("csvFileReader")
	private FlatFileItemReader<Record> csvFileReader;

	@Autowired
	private CsvRecordProcessor csvRecordProcessor;

	@Autowired
	@Qualifier("oracleQueryWriter")
	private JdbcBatchItemWriter<Record> oracleQueryWriter;

	@Autowired
	@Qualifier("oracleProcedureWriter")
	private JdbcBatchItemWriter<CopyTable> oracleProcedureWriter;

	@Autowired
	private ItemStreamReader<CopyTable> oracleItemReader;

	@Bean(name = "firstBatchJob")
	public Job job(JobCompletionNotificationListener listener, @Qualifier("step1") Step step1) {
		return jobBuilderFactory.get("firstBatchJob").listener(listener).start(step1()).next(step2()).build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Record, Record>chunk(10).reader(csvFileReader)
				.processor(csvRecordProcessor).writer(oracleQueryWriter).build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").<CopyTable, CopyTable>chunk(10).reader(oracleItemReader)
				.writer(oracleProcedureWriter).build();
	}
}