package com.springbatch.reader;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.springbatch.model.Record;

@Configuration
public class CsvItemReader {
	// Issue 4 Fix
	@Bean(name = "csvFileReader", destroyMethod = "")
	public FlatFileItemReader<Record> reader() {
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
}