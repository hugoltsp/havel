package com.teles.havel.builder.select;

import java.sql.SQLException;

import com.teles.havel.builder.Builder;
import com.teles.havel.domain.exception.HavelException;
import com.teles.havel.domain.output.function.OutputMapper;
import com.teles.havel.domain.select.BulkSelect;

public class BulkSelectBuilder<T> extends Builder<BulkSelectBuilder<T>, BulkSelect<T>> {

	private OutputMapper<T> outputMapper;

	private BulkSelectBuilder() {
	}

	public static <T> BulkSelectBuilder<T> create() {
		return new BulkSelectBuilder<>();
	}

	public BulkSelectBuilder<T> withOutputMapper(OutputMapper<T> outputMapper) {
		this.outputMapper = outputMapper;
		return this;
	}

	public BulkSelect<T> build() throws HavelException, IllegalStateException {
		BulkSelect<T> bulkSelect = null;
		try {
			checkConnection();
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