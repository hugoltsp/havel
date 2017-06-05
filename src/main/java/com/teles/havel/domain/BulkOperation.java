package com.teles.havel.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;

public abstract class BulkOperation implements AutoCloseable {

	protected final Logger logger;
	protected final Connection connection;
	protected final String sqlStatement;
	protected final PreparedStatement preparedStatement;

	public BulkOperation(Logger logger, Connection connection, String sqlStatement,
			PreparedStatement preparedStatement) {
		this.logger = logger;
		this.connection = connection;
		this.sqlStatement = sqlStatement;
		this.preparedStatement = preparedStatement;
	}

	protected void logIfAvailable(String log, Object... params) {
		if (this.logger != null) {
			this.logger.info(log, params);
		}
	}

	protected void checkState() throws IllegalStateException {
		try {
			if (this.connection == null || connection.isClosed()) {
				throw new IllegalStateException("Connection can't be null or closed");
			}

			if (this.sqlStatement == null || "".equals(sqlStatement)) {
				throw new IllegalStateException("Invalid SqlStatement");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Connection can't be null or closed", e);
		}
	}

	public void close() throws SQLException {
		this.preparedStatement.close();
		this.connection.close();
	}

}