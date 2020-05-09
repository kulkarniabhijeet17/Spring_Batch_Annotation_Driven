package com.springbatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.springbatch.model.Record;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			jdbcTemplate
					.query("SELECT COUNTRY_CODE,COUNTRY_NAME,COUNTRY_ISO_CODE,REGION_CODE,USER_CREATED,DATE_CREATED,STATE_CODE,STATE_NAME FROM RECORD_DUMP_TBL",
							(rs, row) -> new Record(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4),
									rs.getString(5), rs.getDate(6), rs.getString(7), rs.getString(8)))
					.forEach(record -> log.info("Found <" + record + "> in the database."));
		}
	}
}