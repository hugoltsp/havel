package com.teles.havel.builder.select;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;

import com.teles.havel.builder.Builder;
import com.teles.havel.domain.exception.HavelException;
import com.teles.havel.domain.output.function.OutputMapper;
import com.teles.havel.domain.select.BulkSelect;

public class BulkSelectBuilder<T> extends Builder {

	private OutputMapper<T> outputMapper;

	private BulkSelectBuilder() {
	}

	public static <T> BulkSelectBuilder<T> create() {
		return new BulkSelectBuilder<>();
	}

	public BulkSelectBuilder<T> withConnection(Connection connection) {
		this.connection = connection;
		return this;
	}

	public BulkSelectBuilder<T> withSqlStatement(String sqlStatement) {
		this.sqlStatement = sqlStatement;
		return this;
	}

	public BulkSelectBuilder<T> withOutputMapper(OutputMapper<T> outputMapper) {
		this.outputMapper = outputMapper;
		return this;
	}

	public BulkSelectBuilder<T> withLogger(Logger logger) {
		this.logger = logger;
		return this;
	}

	public BulkSelect<T> build() throws HavelException, IllegalStateException {
		BulkSelect<T> bulkSelect = null;
		try {
			checkConnection();
			bulkSelect = new BulkSelect<>(logger, connection, sqlStatement,
					this.connection.prepareStatement(sqlStatement), outputMapper);
		} catch (SQLException e) {
			throw new HavelException("Unable to construct builder", e);
		}
		return bulkSelect;
	}
}