package com.havel.data.input;

public class SqlInput {

	private final String sql;

	public SqlInput(String sql) {
		this.sql = sql;
	}

	public String getStatement() {
		return this.sql;
	}

}
