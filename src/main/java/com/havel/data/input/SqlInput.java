package com.havel.data.input;

public class SqlInput implements Input {

	private final String sql;

	public SqlInput(String sql) {
		this.sql = sql;
	}
	
	@Override
	public String getStatement() {
		return this.sql;
	}

}
