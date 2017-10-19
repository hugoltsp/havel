package com.teles.havel.builder;

import java.sql.Connection;

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

	public abstract O build();

	protected abstract T getThis();
}