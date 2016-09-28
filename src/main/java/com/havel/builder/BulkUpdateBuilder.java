package com.havel.builder;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.havel.builder.utils.BatchUpdateSummary;
import com.havel.builder.utils.BatchUpdateSummary.UpdateCounter;
import com.havel.exception.HavelException;

public class BulkUpdateBuilder<T> extends Builder {

	public static class StatementMapper {

		private final Map<Integer, Object> params = new HashMap<>();
		private int position;

		private Map<Integer, Object> getParams() {
			return this.params;
		}

		public StatementMapper addParameter(Object value) {
			this.params.put(++position, value);
			return this;
		}

	}

	public static interface StatementMapperFunction<T> extends BiFunction<StatementMapper, T, StatementMapper> {

	}

	private static final long DEFAULT_BULK_SIZE = 100;

	private long bulkSize = DEFAULT_BULK_SIZE;
	private StatementMapperFunction<T> statementMapperFunction;
	private Stream<T> data;

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

	public long getBulkSize() {
		return bulkSize;
	}

	public BatchUpdateSummary execute() throws HavelException, IllegalStateException {
		this.checkState();
		Instant before = Instant.now();
		UpdateCounter counter = new UpdateCounter();

		try (Builder builder = this) {
			builder.connection.setAutoCommit(false);
			builder.preparedStatement = builder.connection.prepareStatement(this.sqlStatement);

			super.logIfAvailable("executing update...");

			this.data.filter(Objects::nonNull).sequential()
					.map(p -> statementMapperFunction.apply(new StatementMapper(), p)).forEach(s -> {

						try {

							for (Entry<Integer, Object> param : s.getParams().entrySet()) {
								builder.preparedStatement.setObject(param.getKey(), param.getValue());
							}

							builder.preparedStatement.addBatch();

							if ((counter.incrementAndGet() % bulkSize) == 0) {
								int updateCount = builder.preparedStatement.executeBatch().length;
								builder.preparedStatement.clearBatch();
								super.logIfAvailable("{} rows updated.", updateCount);
							}

						} catch (SQLException e) {
							throw new HavelException(e);
						}

					});

			int updateCount = builder.preparedStatement.executeBatch().length;
			counter.sum(updateCount);
			builder.preparedStatement.clearBatch();
			
			if (updateCount > 0) {
				super.logIfAvailable("{} rows updated.", updateCount);
			}

			super.logIfAvailable("finished! commiting transaction.");
			builder.connection.commit();
		} catch (SQLException | HavelException e) {
			HavelException exception = new HavelException(e);

			try {
				this.connection.rollback();
			} catch (SQLException e1) {
				e.addSuppressed(e1);
			}

			throw exception;
		}

		Instant after = Instant.now();
		Duration duration = Duration.between(before, after);

		BatchUpdateSummary summary = new BatchUpdateSummary(counter, duration);

		return summary;
	}

	public Future<BatchUpdateSummary> executeAsync() throws HavelException, IllegalStateException {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<BatchUpdateSummary> future = executorService.submit(() -> execute());
		executorService.shutdown();
		return future;
	}

	@Override
	protected void checkState() throws IllegalStateException {
		super.checkState();
		if (this.bulkSize < 1) {
			throw new IllegalStateException("Invalid BulkSize of " + this.bulkSize);
		}

		if (this.data == null) {
			throw new IllegalStateException("A data input Stream must be present");
		}

		if (this.statementMapperFunction == null) {
			throw new IllegalStateException("StatementMapperFunction is null");
		}

	}

}