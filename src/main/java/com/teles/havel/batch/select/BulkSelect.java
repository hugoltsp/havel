package com.teles.havel.batch.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;

import com.teles.havel.batch.BatchOperation;
import com.teles.havel.batch.enums.LogLevel;
import com.teles.havel.batch.exception.HavelException;
import com.teles.havel.batch.select.function.OutputMapperFunction;
import com.teles.havel.batch.select.utils.Row;

public class BulkSelect<T> extends BatchOperation {

	private final OutputMapperFunction<T> outputMapper;
	private int rowCount;

	BulkSelect(Logger logger, LogLevel logLevel, Connection connection, String sqlStatement,
			PreparedStatement preparedStatement, OutputMapperFunction<T> outputMapper) {
		super(logger, logLevel, connection, sqlStatement, preparedStatement);
		this.outputMapper = outputMapper;
		validateOutputMapper();
	}

	public Stream<T> select() throws HavelException, IllegalStateException {
		logIfAvailable("Fetching rows from database...");
		ResultSet resultSet = fetchResultSet();
		return StreamSupport.stream(spliterator(resultSet), false).onClose(() -> {
			try {
				this.close();
			} catch (SQLException e) {
				logIfAvailable("An error occurred while trying to close this resource", e);
			}
		});
	}

	private AbstractSpliterator<T> spliterator(ResultSet resultSet) {
		return new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
			@Override
			public boolean tryAdvance(Consumer<? super T> action) {
				try {
					if (!resultSet.next()) {
						logIfAvailable("{} rows selected from database", rowCount);
						return false;
					}
					action.accept(outputMapper.apply(new Row(resultSet)));
					rowCount++;
					return true;
				} catch (SQLException ex) {
					throw new HavelException(ex);
				}
			}
		};
	}

	private void validateOutputMapper() {
		if (this.outputMapper == null) {
			throw new IllegalStateException("OutputMapper can't be null");
		}
	}

	private ResultSet fetchResultSet() throws HavelException {
		try {
			ResultSet resultSet = this.preparedStatement.executeQuery();
			return resultSet;
		} catch (SQLException e) {
			throw new HavelException("Could not fetch result set", e);
		}
	}

}