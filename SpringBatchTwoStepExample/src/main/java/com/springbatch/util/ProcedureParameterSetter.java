package com.springbatch.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import com.springbatch.model.CopyTable;

public class ProcedureParameterSetter implements ItemPreparedStatementSetter<CopyTable> {
	public void setValues(CopyTable copyTable, PreparedStatement ps) throws SQLException {
		ps.setInt(1, copyTable.getCopyTableID());
		ps.setString(2, copyTable.getCountryCode());
		ps.setInt(3, java.sql.Types.INTEGER);
		ps.setString(4, String.valueOf(java.sql.Types.VARCHAR));
	}
}