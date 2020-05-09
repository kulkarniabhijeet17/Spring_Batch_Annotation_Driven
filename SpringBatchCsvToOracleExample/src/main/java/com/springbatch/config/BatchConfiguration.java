package com.springbatch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.springbatch.model.Record;
import com.springbatch.processor.CsvRecordProcessor;

@EnableBatchProcessing
public class BatchConfiguration {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Value("input/record_dump.xml")
	private Resource inputXml;

	@Autowired
	private DataSource dataSource;

	@Bean
	ItemReader<Record> reader() {
		FlatFileItemReader<Record> csvFileReader = new FlatFileItemReader<Record>();
		csvFileReader.setResource(new ClassPathResource("input/record_dump.csv"));
		// It will skip reading of first record
		// csvFileReader.setLinesToSkip(1);

		LineMapper<Record> lineMapper = createLineMapper();
		csvFileReader.setLineMapper(lineMapper);

		return csvFileReader;
	}

	private LineMapper<Record> createLineMapper() {
		DefaultLineMapper<Record> lineMapper = new DefaultLineMapper<Record>();

		LineTokenizer lineTokenizer = createLineTokenizer();
		lineMapper.setLineTokenizer(lineTokenizer);

		FieldSetMapper<Record> informationMapper = createInformationMapper();
		lineMapper.setFieldSetMapper(informationMapper);

		return lineMapper;
	}

	private LineTokenizer createLineTokenizer() {
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(";");
		lineTokenizer.setNames(new String[] { "countryCode", "countryName", "countryIsdCode", "regionCode",
				"userCreated", "dateCreated", "stateCode", "stateName" });
		return lineTokenizer;
	}

	private FieldSetMapper<Record> createInformationMapper() {
		BeanWrapperFieldSetMapper<Record> informationMapper = new BeanWrapperFieldSetMapper<Record>();
		informationMapper.setTargetType(Record.class);
		return informationMapper;
	}

	@Bean
	public ItemProcessor<Record, Record> processor() {
		return new CsvRecordProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Record> itemWriter() {
		return new JdbcBatchItemWriterBuilder<Record>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Record>())
				.sql("insert into RECORD_DUMP_TBL(COUNTRY_CODE,COUNTRY_NAME,COUNTRY_ISO_CODE,REGION_CODE,USER_CREATED,DATE_CREATED,STATE_CODE,STATE_NAME) values (:countryCode, :countryName, :countryIsdCode, :regionCode, :userCreated, :dateCreated, :stateCode, :stateName)")
				.dataSource(dataSource).build();
	}

	@Bean
	protected Step step1(ItemReader<Record> reader, ItemProcessor<Record, Record> processor,
			ItemWriter<Record> writer) {
		return stepBuilderFactory.get("step1").<Record, Record>chunk(10).reader(reader).processor(processor)
				.writer(writer).build();
	}

	@Bean(name = "firstBatchJob")
	public Job job(@Qualifier("step1") Step step1) {
		return jobBuilderFactory.get("firstBatchJob").start(step1).build();
	}
}