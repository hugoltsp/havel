package com.teles.havel.builder;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;

import com.teles.havel.operation.BulkOperation;

public abstract class Builder<T extends Builder<T, O>, O extends BulkOperation> {

	protected Logger logger;
	protected Connection connection;
	protected String sqlStatement;

	public T withConnection(Connection connection) {
		this.connection = connection;
		return getThis();
	}

	public T withSqlStatement(String sqlStatement) {
		this.sqlStatement = sqlStatement;
		return getThis();
	}

	public T withLogger(Logger logger) {
		this.logger = logger;
		return getThis();
	}

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

	public abstract O build();

	protected abstract T getThis();
}