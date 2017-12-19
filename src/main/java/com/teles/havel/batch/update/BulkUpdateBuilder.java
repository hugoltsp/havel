package com.teles.havel.batch.update;

import java.sql.SQLException;
import java.util.stream.Stream;

import com.teles.havel.batch.builder.Builder;
import com.teles.havel.batch.exception.BatchException;
import com.teles.havel.batch.update.function.StatementMapperFunction;

public class BulkUpdateBuilder<T> extends Builder<BulkUpdateBuilder<T>, BulkUpdate<T>> {

	private static final long DEFAULT_BULK_SIZE = 100;

	private long bulkSize = DEFAULT_BULK_SIZE;
	private StatementMapperFunction<T> statementMapperFunction;
	private Stream<T> data;
	private boolean commitBetweenExecutions;

	public BulkUpdateBuilder<T> withCommitBetweenExecutions(boolean commitBetweenExecutions) {
		this.commitBetweenExecutions = commitBetweenExecutions;
		return this;
	}

	public BulkUpdateBuilder<T> withData(Stream<T> data) {
		this.data = data;
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

	public BulkUpdate<T> build() throws BatchException, IllegalStateException {
		BulkUpdate<T> bulkUpdate = null;
		try {
			bulkUpdate = new BulkUpdate<>(logger, logLevel, connection, sqlStatement,
					connection.prepareStatement(sqlStatement), bulkSize, statementMapperFunction, data,
					commitBetweenExecutions);
		} catch (SQLException e) {
			throw new BatchException("Unable to build BulkUpdate", e);
		}
		return bulkUpdate;
	}

	protected BulkUpdateBuilder<T> getThis() {
		return this;
	}

}