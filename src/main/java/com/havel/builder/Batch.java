package com.havel.builder;

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

import com.havel.data.input.Input;
import com.havel.data.output.OutputMapper;
import com.havel.data.utils.ConnectionConfig;
import com.havel.exception.HavelException;

public final class Batch {

	private Batch() {

	}

	public static <O> BulkSelectBuilder<O> bulkSelect() {
		BulkSelectBuilder<O> builder = new BulkSelectBuilder<>();
		return builder;
	}

	private static class BasicBuilder implements AutoCloseable {

		private ConnectionConfig connectionConfig;
		private Connection connection;
		private Input input;
		private PreparedStatement preparedStatement;

		private void connectionConfig(ConnectionConfig connectionConfig) {
			this.connectionConfig = connectionConfig;
		}

		private void connection(Connection datastore) {
			this.connection = datastore;
		}

		private void input(Input input) {
			this.input = input;
		}

		private Input getInput() {
			return input;
		}

		@Override
		public void close() throws SQLException {
			this.preparedStatement.close();
			if (connectionConfig != null) {
				connectionConfig.onAfter(connection);
			}
		}

	}

	private static class UpdateBuilder {

		private static final int DEFAULT_BULK_SIZE = 100;
		private int bulkSize = DEFAULT_BULK_SIZE;

		public void size(int size) {
			this.bulkSize = size;
		}

		public int getBulkSize() {
			return bulkSize;
		}

	}

	public static class BulkSelectBuilder<O> {

		private BasicBuilder basicBuilder;
		private OutputMapper<O> outputMapper;

		public BulkSelectBuilder() {
			this.basicBuilder = new BasicBuilder();
		}

		public BulkSelectBuilder<O> connection(Connection connection) {
			this.basicBuilder.connection(connection);
			return this;
		}

		public BulkSelectBuilder<O> input(Input input) {
			this.basicBuilder.input(input);
			return this;
		}

		public BulkSelectBuilder<O> outputMapper(OutputMapper<O> outputMapper) {
			this.outputMapper = outputMapper;
			return this;
		}

		private BulkSelectBuilder<O> connectionConfig(ConnectionConfig connectionConfig) {
			this.basicBuilder.connectionConfig(connectionConfig);
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