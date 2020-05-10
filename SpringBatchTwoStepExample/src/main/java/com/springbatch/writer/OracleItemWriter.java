package com.springbatch.writer;

import javax.sql.DataSource;

import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springbatch.model.CopyTable;
import com.springbatch.model.Record;
import com.springbatch.util.ProcedureParameterSetter;

@Configuration
public class OracleItemWriter {
	@Autowired
	private DataSource dataSource;

	@Bean("oracleQueryWriter")
	public JdbcBatchItemWriter<Record> queryWriter() {
		return new JdbcBatchItemWriterBuilder<Record>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Record>())
				.sql("insert into RECORD_DUMP_TBL(COUNTRY_CODE,COUNTRY_NAME,COUNTRY_ISO_CODE,REGION_CODE,USER_CREATED,DATE_CREATED,STATE_CODE,STATE_NAME) values (:countryCode, :countryName, :countryIsdCode, :regionCode, :userCreated, :dateCreated, :stateCode, :stateName)")
				.dataSource(dataSource).build();
	}

	@Bean("oracleProcedureWriter")
	public JdbcBatchItemWriter<CopyTable> procedureWriter() {
		JdbcBatchItemWriter<CopyTable> writer = new JdbcBatchItemWriter<CopyTable>();
		writer.setDataSource(dataSource);
		writer.setItemPreparedStatementSetter(new ProcedureParameterSetter());
		writer.setAssertUpdates(false);
		// writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Record>()); // Used to execute sql queries
		writer.setSql("call data_validation.copy_table(?,?,?,?)");

		return writer;
	}
}