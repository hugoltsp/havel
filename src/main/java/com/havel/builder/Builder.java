package com.havel.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Builder implements AutoCloseable {

	protected Connection connection;
	protected String sqlStatement;
	protected PreparedStatement preparedStatement;

	@Override
	public void close() throws SQLException {
		this.preparedStatement.close();
		this.connection.close();
	}

	public abstract Builder withConnection(Connection connection);

	public abstract Builder withSqlStatement(String sqlStatement);

	protected void checkState() throws IllegalStateException {
		try {
			if (this.connection == null || connection.isClosed()) {
				throw new IllegalStateException("Connection can't be null or closed");
			}

			if (sqlStatement == null || "".equals(sqlStatement)) {
				throw new IllegalStateException("Invalid SqlStatement");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Connection can't be null or closed", e);
		}
	}
}
