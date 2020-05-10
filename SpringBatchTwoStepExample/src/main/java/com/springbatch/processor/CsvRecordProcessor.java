package com.springbatch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.springbatch.model.Record;

@Component
public class CsvRecordProcessor implements ItemProcessor<Record, Record> {
	private static final Logger log = LoggerFactory.getLogger(CsvRecordProcessor.class);

	public Record process(final Record record) throws Exception {
		final String countryCode = record.getCountryCode().toUpperCase();
		final String countryName = record.getCountryName().toUpperCase();
		final String countryIsdCode = record.getCountryIsdCode().toUpperCase();
		final String userCreated = record.getUserCreated().toUpperCase();
		final String stateCode = record.getStateCode().toUpperCase();
		final String stateName = record.getStateName().toUpperCase();

		final Record transformedRecord = new Record(countryCode, countryName, countryIsdCode, record.getRegionCode(),
				userCreated, record.getDateCreated(), stateCode, stateName);

		log.info("Converting (" + record + ") into (" + transformedRecord + ")");

		return transformedRecord;
	}
}