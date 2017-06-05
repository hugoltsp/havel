package com.teles.havel.builder;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;

public abstract class Builder {

	protected Logger logger;
	protected Connection connection;
	protected String sqlStatement;

	public abstract Builder withConnection(Connection connection);

	public abstract Builder withSqlStatement(String sqlStatement);

	public abstract Builder withLogger(Logger logger);

	protected void logIfAvailable(String log, Object... params) {
		if (this.logger != null) {
			this.logger.info(log, params);
		}
	}

	protected void checkConnection() throws IllegalStateException {
		try {
			if (this.connection == null || connection.isClosed()) {
				throw new IllegalStateException("Connection can't be null or closed");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Connection can't be null or closed", e);
		}
	}
}
