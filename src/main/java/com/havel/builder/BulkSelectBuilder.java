package com.havel.builder;

import java.sql.Connection;
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

import com.havel.data.output.OutputMapper;
import com.havel.exception.HavelException;

public class BulkSelectBuilder<T> extends Builder {

	private OutputMapper<T> outputMapper;

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

	public Stream<T> select() throws HavelException, IllegalStateException {
		this.checkState();
		ResultSet resultSet = build();
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
						return false;
					}
					action.accept(outputMapper.getData(resultSet));
					return true;
				} catch (SQLException ex) {
					throw new HavelException(ex);
				}
			}
		};
	}

	@Override
	protected void checkState() throws IllegalStateException {
		super.checkState();
		if (this.outputMapper == null) {
			throw new IllegalStateException("OutputMapper can't be null");
		}
	}

	private ResultSet build() throws HavelException {
		try {
			this.preparedStatement = this.connection.prepareStatement(this.sqlStatement);
			ResultSet resultSet = this.preparedStatement.executeQuery();
			return resultSet;
		} catch (SQLException e) {
			throw new HavelException(e);
		}
	}

}
