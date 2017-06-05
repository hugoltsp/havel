package com.teles.havel.builder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.teles.havel.domain.exception.HavelException;
import com.teles.havel.domain.input.function.StatementMapperFunction;
import com.teles.havel.domain.update.BulkUpdate;

public class BulkUpdateBuilder<T> extends Builder {

	private static final long DEFAULT_BULK_SIZE = 100;

	private long bulkSize = DEFAULT_BULK_SIZE;
	private StatementMapperFunction<T> statementMapperFunction;
	private Stream<T> data;

	private BulkUpdateBuilder() {
	}

	public static <T> BulkUpdateBuilder<T> create() {
		return new BulkUpdateBuilder<>();
	}

	public BulkUpdateBuilder<T> withData(Stream<T> data) {
		this.data = data;
		return this;
	}

	public BulkUpdateBuilder<T> withConnection(Connection connection) {
		this.connection = connection;
		return this;
	}

	public BulkUpdateBuilder<T> withSqlStatement(String sqlStatement) {
		this.sqlStatement = sqlStatement;
		return this;
	}

	public BulkUpdateBuilder<T> withBulkSize(long size) {
		this.bulkSize = size;
		return this;
	}

	public BulkUpdateBuilder<T> withStatementMapper(StatementMapperFunction<T> statementMapperFunction) {
		this.statementMapperFunction = statementMapperFunction;
		return this;
	}

	public BulkUpdateBuilder<T> withLogger(Logger logger) {
		this.logger = logger;
		return this;
	}

	public BulkUpdate<T> build() throws HavelException, IllegalStateException {
		BulkUpdate<T> bulkUpdate = null;
		try {
			checkConnection();
			bulkUpdate = new BulkUpdate<>(this.logger, this.connection, this.sqlStatement,
					this.connection.prepareStatement(this.sqlStatement), this.bulkSize, this.statementMapperFunction,
					this.data);
		} catch (SQLException e) {
			throw new HavelException("Unable to construct builder", e);
		}
		return bulkUpdate;
	}
}