package com.teles.havel.domain.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;

import com.teles.havel.domain.BulkOperation;
import com.teles.havel.domain.exception.HavelException;
import com.teles.havel.domain.output.Row;
import com.teles.havel.domain.output.function.OutputMapper;

public class BulkSelect<T> extends BulkOperation {

	private final OutputMapper<T> outputMapper;
	private int rowCount;

	public BulkSelect(Logger logger, Connection connection, String sqlStatement, PreparedStatement preparedStatement,
			OutputMapper<T> outputMapper) {
		super(logger, connection, sqlStatement, preparedStatement);
		this.outputMapper = outputMapper;
	}

	public Stream<T> select() throws HavelException, IllegalStateException {
		this.checkState();
		super.logIfAvailable("fetching rows from database...");
		ResultSet resultSet = fetchResultSet();
		return StreamSupport.stream(spliterator(resultSet), false).onClose(() -> {
			try {
				this.close();
			} catch (Exception e) {
				throw new HavelException(e);
			}
		});
	}

	public Future<Stream<T>> selectAsync() throws HavelException, IllegalStateException {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<Stream<T>> future = executorService.submit(() -> select());
		executorService.shutdown();
		return future;
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
					action.accept(outputMapper.getData(new Row(resultSet)));
					rowCount++;
					return true;
				} catch (SQLException ex) {
					throw new HavelException(ex);
				}
			}
		};
	}

	protected void checkState() throws IllegalStateException {
		super.checkState();
		if (this.outputMapper == null) {
			throw new IllegalStateException("OutputMapper can't be null");
		}
	}

	private ResultSet fetchResultSet() throws HavelException {
		try {
			ResultSet resultSet = this.preparedStatement.executeQuery();
			return resultSet;
		} catch (SQLException e) {
			throw new HavelException(e);
		}
	}

}