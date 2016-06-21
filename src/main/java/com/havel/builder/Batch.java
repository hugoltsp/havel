package com.havel.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.havel.data.output.OutputMapper;
import com.havel.data.utils.BatchUpdateSummary;
import com.havel.data.utils.BatchUpdateSummary.UpdateCounter;
import com.havel.exception.HavelException;

public final class Batch {

	private Batch() {

	}

	public static <O> BulkSelectBuilder<O> bulkSelect() {
		BulkSelectBuilder<O> builder = new BulkSelectBuilder<>();
		return builder;
	}

	public static <T> BulkUpdateBuilder<T> bulkUpdate() {
		BulkUpdateBuilder<T> bulkUpdateBuilder = new BulkUpdateBuilder<>();
		return bulkUpdateBuilder;
	}

	private static class Builder implements AutoCloseable {

		private Connection connection;
		private String sqlStatement;
		private PreparedStatement preparedStatement;

		private void withConnection(Connection datastore) {
			this.connection = datastore;
		}

		private void withSqlStatement(String sql) {
			this.sqlStatement = sql;
		}

		private String getSqlStatement() {
			return this.sqlStatement;
		}

		@Override
		public void close() throws SQLException {
			this.preparedStatement.close();
			this.connection.close();
		}

		protected void checkState() throws IllegalStateException {
			try {
				if (this.connection == null || connection.isClosed()) {
					throw new IllegalStateException("Connection can't be null or closed");
				}

				if (sqlStatement == null || "".equals(sqlStatement)) {
					throw new IllegalStateException("Invalid SqlStatement");
				}
			} catch (SQLException e) {
				throw new IllegalStateException("Connection can't be null or closed", e);
			}
		}
	}

	public static class BulkUpdateBuilder<T> extends Builder {

		public static class StatementMapper {

			private Map<Integer, Object> params = new HashMap<>();
			private int position;

			private Map<Integer, Object> getParams() {
				return params;
			}

			public StatementMapper addParameter(Object value) {
				this.params.put(++position, value);
				return this;
			}

		}

		public static interface StatementMapperFunction<T> extends BiFunction<StatementMapper, T, StatementMapper> {

		}

		private static final long DEFAULT_BULK_SIZE = 100;

		private Builder basicBuilder;
		private long bulkSize = DEFAULT_BULK_SIZE;
		private StatementMapperFunction<T> statementMapperFunction;
		private Stream<T> input;

		public BulkUpdateBuilder() {
			this.basicBuilder = new Builder();
		}

		public BulkUpdateBuilder<T> withDataStream(Stream<T> input) {
			this.input = input;
			return this;
		}

		public BulkUpdateBuilder<T> withConnection(Connection connection) {
			this.basicBuilder.withConnection(connection);
			return this;
		}

		public BulkUpdateBuilder<T> withBulkSize(long size) {
			this.bulkSize = size;
			return this;
		}

		public long getBulkSize() {
			return bulkSize;
		}

		public BulkUpdateBuilder<T> withSqlStatement(String sqlStatement) {
			this.basicBuilder.withSqlStatement(sqlStatement);
			return this;
		}

		public BulkUpdateBuilder<T> withStatementMapper(StatementMapperFunction<T> statementMapperFunction) {
			this.statementMapperFunction = statementMapperFunction;
			return this;
		}

		public BatchUpdateSummary execute() throws HavelException, IllegalStateException {
			this.checkState();
			Instant before = Instant.now();
			UpdateCounter updateCount = new UpdateCounter();

			try (Builder builder = this.basicBuilder) {
				builder.connection.setAutoCommit(false);

				builder.preparedStatement = builder.connection.prepareStatement(this.basicBuilder.getSqlStatement());

				this.input.filter(Objects::nonNull).sequential()
						.map(p -> statementMapperFunction.apply(new StatementMapper(), p)).forEach(s -> {

							long count = 0;

							try {

								for (Entry<Integer, Object> param : s.getParams().entrySet()) {
									builder.preparedStatement.setObject(param.getKey(), param.getValue());
								}

								builder.preparedStatement.addBatch();

								if ((++count % bulkSize) == 0) {
									updateCount.sum(builder.preparedStatement.executeLargeBatch().length);
									builder.preparedStatement.clearBatch();
								}

							} catch (SQLException e) {
								throw new HavelException(e);
							}

						});

				updateCount.sum(builder.preparedStatement.executeLargeBatch().length);
				builder.preparedStatement.clearBatch();
				builder.connection.commit();

			} catch (SQLException | HavelException e) {
				HavelException exception = new HavelException(e);

				try {
					this.basicBuilder.connection.rollback();
				} catch (SQLException e1) {
					e.addSuppressed(e1);
				}

				throw exception;
			}

			Instant after = Instant.now();
			Duration duration = Duration.between(before, after);

			BatchUpdateSummary summary = new BatchUpdateSummary(updateCount, duration);

			return summary;
		}

		public Future<BatchUpdateSummary> executeAsync() throws HavelException, IllegalStateException {
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			Future<BatchUpdateSummary> future = executorService.submit(() -> execute());
			return future;
		}

		@Override
		protected void checkState() throws IllegalStateException {
			this.basicBuilder.checkState();
			if (this.bulkSize < 1) {
				throw new IllegalStateException("Invalid BulkSize of " + this.bulkSize);
			}

			if (this.input == null) {
				throw new IllegalStateException("A data input Stream must be present");
			}

			if (this.statementMapperFunction == null) {
				throw new IllegalStateException("StatementMApperFunction is null");
			}

		}

	}

	public static class BulkSelectBuilder<O> extends Builder {

		private Builder basicBuilder;
		private OutputMapper<O> outputMapper;

		public BulkSelectBuilder() {
			this.basicBuilder = new Builder();
		}

		public BulkSelectBuilder<O> withConnection(Connection connection) {
			this.basicBuilder.withConnection(connection);
			return this;
		}

		public BulkSelectBuilder<O> withSqlStatement(String sql) {
			this.basicBuilder.withSqlStatement(sql);
			return this;
		}

		public BulkSelectBuilder<O> withOutputMapper(OutputMapper<O> outputMapper) {
			this.outputMapper = outputMapper;
			return this;
		}

		public Stream<O> select() throws HavelException, IllegalStateException {
			this.checkState();
			ResultSet resultSet = build();
			return StreamSupport.stream(spliterator(resultSet), false).onClose(() -> {
				try {
					basicBuilder.close();
				} catch (Exception e) {
					throw new HavelException(e);
				}
			});
		}

		public Future<Stream<O>> selectAsync() throws HavelException, IllegalStateException {
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			Future<Stream<O>> future = executorService.submit(() -> select());
			return future;
		}

		private AbstractSpliterator<O> spliterator(ResultSet resultSet) {
			return new Spliterators.AbstractSpliterator<O>(Long.MAX_VALUE, Spliterator.ORDERED) {
				@Override
				public boolean tryAdvance(Consumer<? super O> action) {
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
			this.basicBuilder.checkState();
			if (outputMapper == null) {
				throw new IllegalStateException("OutputMapper can't be null");
			}
		}

		private ResultSet build() throws HavelException {
			try {
				this.basicBuilder.preparedStatement = this.basicBuilder.connection
						.prepareStatement(this.basicBuilder.getSqlStatement());
				ResultSet resultSet = this.basicBuilder.preparedStatement.executeQuery();
				return resultSet;
			} catch (SQLException e) {
				throw new HavelException(e);
			}
		}

	}

}