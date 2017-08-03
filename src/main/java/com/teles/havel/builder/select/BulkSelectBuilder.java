package com.teles.havel.builder.select;

import java.sql.SQLException;

import com.teles.havel.builder.Builder;
import com.teles.havel.operation.exception.HavelException;
import com.teles.havel.operation.select.BulkSelect;
import com.teles.havel.operation.select.function.OutputMapperFunction;

public class BulkSelectBuilder<T> extends Builder<BulkSelectBuilder<T>, BulkSelect<T>> {

	private OutputMapperFunction<T> outputMapper;

	private BulkSelectBuilder() {
	}

	public static <T> BulkSelectBuilder<T> create() {
		return new BulkSelectBuilder<>();
	}

	public BulkSelectBuilder<T> withOutputMapper(OutputMapperFunction<T> outputMapper) {
		this.outputMapper = outputMapper;
		return this;
	}

	public BulkSelect<T> build() throws HavelException, IllegalStateException {
		BulkSelect<T> bulkSelect = null;
		try {
			bulkSelect = new BulkSelect<>(logger, connection, sqlStatement,
					this.connection.prepareStatement(sqlStatement), outputMapper);
		} catch (SQLException e) {
			throw new HavelException("Unable to build BulkSelect", e);
		}
		return bulkSelect;
	}

	protected BulkSelectBuilder<T> getThis() {
		return this;
	}
}