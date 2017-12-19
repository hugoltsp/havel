package com.teles.havel.batch.select;

import java.sql.SQLException;

import com.teles.havel.batch.builder.Builder;
import com.teles.havel.batch.exception.BatchException;
import com.teles.havel.batch.select.function.OutputMapperFunction;

public class BulkSelectBuilder<T> extends Builder<BulkSelectBuilder<T>, BulkSelect<T>> {

	private OutputMapperFunction<T> outputMapper;

	public BulkSelectBuilder<T> withOutputMapper(OutputMapperFunction<T> outputMapper) {
		this.outputMapper = outputMapper;
		return this;
	}

	public BulkSelect<T> build() throws BatchException, IllegalStateException {
		BulkSelect<T> bulkSelect = null;
		try {
			bulkSelect = new BulkSelect<>(logger, logLevel, connection, sqlStatement,
					this.connection.prepareStatement(sqlStatement), outputMapper);
		} catch (SQLException e) {
			throw new BatchException("Unable to build BulkSelect", e);
		}
		return bulkSelect;
	}

	protected BulkSelectBuilder<T> getThis() {
		return this;
	}
}