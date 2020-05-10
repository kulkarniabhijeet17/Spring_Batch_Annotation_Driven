package com.springbatch.reader;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springbatch.model.CopyTable;

@Configuration
public class OracleItemReader {
	@Autowired
	private DataSource dataSource;

	@Bean(destroyMethod="")
	public ItemStreamReader<CopyTable> reader() {
		JdbcCursorItemReader<CopyTable> reader = new JdbcCursorItemReader<CopyTable>();
		reader.setDataSource(dataSource);
		reader.setSql("select COUNTRY_CODE, copy_tab_id from RECORD_DUMP_TBL");
		reader.setRowMapper((rs, rowNum) -> {
			CopyTable copyTable = new CopyTable();
			copyTable.setCopyTableID(rs.getInt("copy_tab_id"));
			copyTable.setCountryCode(rs.getString("COUNTRY_CODE"));

			return copyTable;
		});

		return reader;
	}
}