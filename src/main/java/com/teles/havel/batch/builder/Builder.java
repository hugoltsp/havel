package com.teles.havel.batch.builder;

import java.sql.Connection;

import org.slf4j.Logger;

import com.teles.havel.batch.BatchOperation;
import com.teles.havel.batch.enums.LogLevel;

public abstract class Builder<T extends Builder<T, O>, O extends BatchOperation> {

	protected Logger logger;
	protected Connection connection;
	protected String sqlStatement;
	protected LogLevel logLevel;

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

	public T withLoggerLevel(LogLevel level) {
		this.logLevel = level;
		return getThis();
	}

	public abstract O build();

	protected abstract T getThis();

}