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
import com.havel.exception.HavelException;

public final class Batch {

	private Batch() {

	}

	public static <O> BulkSelectBuilder<O> bulkSelect() {
		BulkSelectBuilder<O> builder = new BulkSelectBuilder<>();
		return builder;
	}

	private static class BasicBuilder {

		private Connection connection;
		private Input input;

		public void connection(Connection datastore) {
			this.connection = datastore;
		}

		public void input(Input input) {
			this.input = input;
		}

		public Input getInput() {
			return input;
		}

		public Connection getConnection() {
			return connection;
		}

	}

	private static class UpdateBuilder extends BasicBuilder {

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
		private ResultSet resultSet;

		public BulkSelectBuilder() {
			this.basicBuilder = new BasicBuilder();
		}

		public BulkSelectBuilder<O> connection(Connection connection) {
			basicBuilder.connection(connection);
			return this;
		}

		public BulkSelectBuilder<O> input(Input input) {
			basicBuilder.input(input);
			return this;
		}

		public BulkSelectBuilder<O> outputMapper(OutputMapper<O> outputMapper) {
			this.outputMapper = outputMapper;
			return this;
		}

		public Stream<O> parallelSelect() {
			ResultSet resultSet = build();
			return StreamSupport.stream(spliterator(resultSet), true);
		}

		public Stream<O> select() {
			ResultSet resultSet = build();
			return StreamSupport.stream(spliterator(resultSet), false);
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

		private ResultSet build() {
			try (PreparedStatement ps = this.basicBuilder.getConnection()
					.prepareStatement(this.basicBuilder.getInput().getStatement());) {
				this.resultSet = ps.executeQuery();
				return resultSet;
			} catch (SQLException e) {
				throw new HavelException(e);
			}
		}

	}

}