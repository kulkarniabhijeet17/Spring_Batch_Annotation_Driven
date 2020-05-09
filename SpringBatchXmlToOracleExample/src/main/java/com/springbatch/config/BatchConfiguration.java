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
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.springbatch.model.Record;
import com.springbatch.processor.XmlRecordProcessor;

@EnableBatchProcessing
public class BatchConfiguration {
	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Value("input/record_dump.xml")
	private Resource inputXml;

	@Autowired
	private DataSource dataSource;

	@Bean
	ItemReader<Record> itemReader() {
		StaxEventItemReader<Record> xmlFileReader = new StaxEventItemReader<Record>();
		xmlFileReader.setResource(inputXml);
		xmlFileReader.setFragmentRootElementName("record");

		Jaxb2Marshaller recordMarshaller = new Jaxb2Marshaller();
		recordMarshaller.setClassesToBeBound(Record.class);
		xmlFileReader.setUnmarshaller(recordMarshaller);

		return xmlFileReader;
	}

	@Bean
	public ItemProcessor<Record, Record> itemProcessor() {
		return new XmlRecordProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Record> itemWriter() {
		return new JdbcBatchItemWriterBuilder<Record>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Record>())
				.sql("insert into RECORD_DUMP_TBL(COUNTRY_CODE,COUNTRY_NAME,COUNTRY_ISO_CODE,REGION_CODE,USER_CREATED,DATE_CREATED,STATE_CODE,STATE_NAME) values (:countryCode, :countryName, :countryIsdCode, :regionCode, :userCreated, :dateCreated, :stateCode, :stateName)")
				.dataSource(dataSource).build();
	}

	@Bean
	public Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(new Class[] { Record.class });
		return marshaller;
	}

	@Bean
	protected Step step1(ItemReader<Record> reader, ItemProcessor<Record, Record> processor,
			ItemWriter<Record> writer) {
		return steps.get("step1").<Record, Record>chunk(10).reader(reader).processor(processor).writer(writer).build();
	}

	@Bean(name = "firstBatchJob")
	public Job job(@Qualifier("step1") Step step1) {
		return jobs.get("firstBatchJob").start(step1).build();
	}
}