package com.teles.havel.builder.update;

import java.sql.SQLException;
import java.util.stream.Stream;

import com.teles.havel.builder.Builder;
import com.teles.havel.operation.exception.HavelException;
import com.teles.havel.operation.update.BulkUpdate;
import com.teles.havel.operation.update.function.StatementMapperFunction;

public class BulkUpdateBuilder<T> extends Builder<BulkUpdateBuilder<T>, BulkUpdate<T>> {

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

	public BulkUpdateBuilder<T> withBulkSize(long size) {
		this.bulkSize = size;
		return this;
	}

	public BulkUpdateBuilder<T> withStatementMapper(StatementMapperFunction<T> statementMapperFunction) {
		this.statementMapperFunction = statementMapperFunction;
		return this;
	}

	public BulkUpdate<T> build() throws HavelException, IllegalStateException {
		BulkUpdate<T> bulkUpdate = null;
		try {
			bulkUpdate = new BulkUpdate<>(this.logger, this.connection, this.sqlStatement,
					this.connection.prepareStatement(this.sqlStatement), this.bulkSize, this.statementMapperFunction,
					this.data);
		} catch (SQLException e) {
			throw new HavelException("Unable to build BulkUpdate", e);
		}
		return bulkUpdate;
	}

	protected BulkUpdateBuilder<T> getThis() {
		return this;
	}

}