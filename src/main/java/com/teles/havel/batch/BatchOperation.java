package com.teles.havel.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;

import com.teles.havel.batch.enums.LogLevel;

public abstract class BatchOperation implements AutoCloseable {

	protected final Logger logger;
	protected final Connection connection;
	protected final String sqlStatement;
	protected final PreparedStatement preparedStatement;
	protected final LogLevel logLevel;

	protected BatchOperation(Logger logger, LogLevel logLevel, Connection connection, String sqlStatement,
			PreparedStatement preparedStatement) {
		this.logger = logger;
		this.logLevel = logLevel;
		this.connection = connection;
		this.sqlStatement = sqlStatement;
		this.preparedStatement = preparedStatement;
		validateConnection();
		validateStatement();
	}

	protected void logIfAvailable(String log, Object... params) {
		if (this.logger != null) {

			if (LogLevel.DEBUG.equals(logLevel)) {
				this.logger.debug(log, params);
			} else if (LogLevel.TRACE.equals(logLevel)) {
				this.logger.trace(log, params);
			} else {
				this.logger.info(log, params);
			}

		}
	}

	private void validateStatement() {
		if (this.sqlStatement == null || "".equals(this.sqlStatement)) {
			throw new IllegalStateException("Invalid SqlStatement");
		}
	}

	private void validateConnection() {
		try {

			if (this.connection == null || this.connection.isClosed()) {
				throw new IllegalStateException("Connection can't be null or closed");
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