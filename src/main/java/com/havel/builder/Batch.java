package com.havel.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.havel.data.input.InputSupplier;
import com.havel.data.input.SqlInput;
import com.havel.data.output.OutputMapper;
import com.havel.data.utils.BatchUpdateSummary;
import com.havel.data.utils.config.ConnectionConfig;
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

		private ConnectionConfig connectionConfig;
		private Connection connection;
		private SqlInput input;
		private PreparedStatement preparedStatement;

		private void withConnectionConfig(ConnectionConfig connectionConfig) {
			this.connectionConfig = connectionConfig;
		}

		private void withConnection(Connection datastore) {
			this.connection = datastore;
		}

		private void withSqlInput(SqlInput input) {
			this.input = input;
		}

		private SqlInput getInput() {
			return input;
		}

		@Override
		public void close() throws SQLException {
			if (connectionConfig != null) {
				connectionConfig.onAfter(connection);
			}

			this.preparedStatement.close();
		}

	}

	public static class BulkUpdateBuilder<T> {

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
		private String sqlStatement;
		private List<T> inputData = new ArrayList<>(0);
		private StatementMapperFunction<T> statementMapperFunction;
		private InputSupplier<T> inputSupplier;
		
		public BulkUpdateBuilder() {
			this.basicBuilder = new Builder();
		}

		public BulkUpdateBuilder<T> withConnection(Connection connection) {
			this.basicBuilder.withConnection(connection);
			return this;
		}

		public BulkUpdateBuilder<T> withConnectionConfig(ConnectionConfig connectionConfig) {
			this.basicBuilder.withConnectionConfig(connectionConfig);
			return this;
		}

		public BulkUpdateBuilder<T> withBulkSize(long size) {
			this.bulkSize = size;
			return this;
		}

		public long getBulkSize() {
			return bulkSize;
		}

		public BulkUpdateBuilder<T> withInputSupplier(InputSupplier<T> inputSupplier){
			this.inputSupplier = inputSupplier;
			return this;
		}
		
		public BulkUpdateBuilder<T> withSqlInput(SqlInput input) {
			this.basicBuilder.withSqlInput(input);
			return this;
		}

		public BulkUpdateBuilder<T> withSqlStatement(String sqlStatement) {
			this.sqlStatement = sqlStatement;
			return this;
		}

		public BulkUpdateBuilder<T> addData(T data) {
			this.inputData.add(data);
			return this;
		}

		public BulkUpdateBuilder<T> addData(List<T> data) {
			this.inputData.addAll(data);
			return this;
		}

		public BulkUpdateBuilder<T> withStatementMapper(StatementMapperFunction<T> statementMapperFunction) {
			this.statementMapperFunction = statementMapperFunction;
			return this;
		}

		public BatchUpdateSummary execute() throws HavelException {
			BatchUpdateSummary summary = new BatchUpdateSummary();

			Instant before = Instant.now();

			try (Builder builder = this.basicBuilder) {

				if (builder.connectionConfig != null) {
					builder.connectionConfig.onBefore(builder.connection);
				}

				builder.preparedStatement = builder.connection.prepareStatement(this.sqlStatement);

//				Stream.builder().
				
				this.inputData.stream().map(p -> statementMapperFunction.apply(new StatementMapper(), p)).forEach(s -> {

					long count = 0;

					try {

						for (Entry<Integer, Object> param : s.getParams().entrySet()) {
							builder.preparedStatement.setObject(param.getKey(), param.getValue());
						}

						builder.preparedStatement.addBatch();

						if ((++count % bulkSize) == 0) {
							summary.sumUpdateCount(builder.preparedStatement.executeLargeBatch().length);
							builder.preparedStatement.clearBatch();
						}

					} catch (SQLException e) {
						throw new HavelException(e);
					}

				});

				summary.sumUpdateCount(builder.preparedStatement.executeLargeBatch().length);
				builder.preparedStatement.clearBatch();

			} catch (SQLException e) {
				throw new HavelException(e);
			}

			Instant after = Instant.now();
			Duration duration = Duration.between(before, after);
			summary.setDuration(duration);

			return summary;
		}
	}

	public static class BulkSelectBuilder<O> {

		private Builder basicBuilder;
		private OutputMapper<O> outputMapper;

		public BulkSelectBuilder() {
			this.basicBuilder = new Builder();
		}

		public BulkSelectBuilder<O> withConnection(Connection connection) {
			this.basicBuilder.withConnection(connection);
			return this;
		}

		public BulkSelectBuilder<O> withSqlInput(SqlInput input) {
			this.basicBuilder.withSqlInput(input);
			return this;
		}

		public BulkSelectBuilder<O> withOutputMapper(OutputMapper<O> outputMapper) {
			this.outputMapper = outputMapper;
			return this;
		}

		public BulkSelectBuilder<O> withConnectionConfig(ConnectionConfig connectionConfig) {
			this.basicBuilder.withConnectionConfig(connectionConfig);
			return this;
		}

		public Stream<O> select() throws HavelException {
			ResultSet resultSet = build();
			return StreamSupport.stream(spliterator(resultSet), false).onClose(() -> {
				try {
					basicBuilder.close();
				} catch (Exception e) {
					throw new HavelException(e);
				}
			});
		}

		private AbstractSpliterator<O> spliterator(ResultSet resultSet) {
			return new Spliterators.AbstractSpliterator<O>(Long.MAX_VALUE, Spliterator.ORDERED) {
				@Override
				public boolean tryAdvance(Consumer<? super O> action) {
					try {
						if (!resultSet.next())
							return false;
						action.accept(outputMapper.getData(resultSet));
						return true;
					} catch (SQLException ex) {
						throw new HavelException(ex);
					}
				}
			};
		}

		private ResultSet build() throws HavelException {
			try {
				if (this.basicBuilder.connectionConfig != null) {
					this.basicBuilder.connectionConfig.onBefore(this.basicBuilder.connection);
				}

				this.basicBuilder.preparedStatement = this.basicBuilder.connection
						.prepareStatement(this.basicBuilder.getInput().getStatement());
				ResultSet resultSet = this.basicBuilder.preparedStatement.executeQuery();
				return resultSet;
			} catch (SQLException e) {
				throw new HavelException(e);
			}
		}

	}

}