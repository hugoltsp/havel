package com.teles.havel.batch.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.teles.havel.batch.BatchOperation;
import com.teles.havel.batch.enums.LogLevel;
import com.teles.havel.batch.exception.HavelException;
import com.teles.havel.batch.update.function.StatementMapperFunction;
import com.teles.havel.batch.update.utils.BulkUpdateSummary;
import com.teles.havel.batch.update.utils.StatementParameters;

public class BulkUpdate<T> extends BatchOperation {

	private final long bulkSize;
	private final StatementMapperFunction<T> statementMapperFunction;
	private final Stream<T> data;
	private final boolean commitBetweenExecutions;

	BulkUpdate(Logger logger, LogLevel logLevel, Connection connection, String sqlStatement,
			PreparedStatement preparedStatement, long bulkSize, StatementMapperFunction<T> statementMapperFunction,
			Stream<T> data, boolean commitBetweenExecutions) {
		super(logger, logLevel, connection, sqlStatement, preparedStatement);
		this.bulkSize = bulkSize;
		this.statementMapperFunction = statementMapperFunction;
		this.data = data;
		this.commitBetweenExecutions = commitBetweenExecutions;
		validateBulkSize();
		validateInputData();
		validateStatementMapper();
	}

	public BulkUpdateSummary execute() throws HavelException, IllegalStateException {
		BulkUpdateSummary bulkUpdateSummary = null;

		try (BatchOperation builder = this) {
			BulkUpdateSummaryBuilder bulkUpdateSummaryBuilder = new BulkUpdateSummaryBuilder().start();

			this.connection.setAutoCommit(false);

			logIfAvailable("Commit between updates set to: {}", this.commitBetweenExecutions);
			logIfAvailable("Executing update...");

			this.data.filter(Objects::nonNull).sequential().map(this::dataToStatementParameters).forEach(s -> {

				try {

					for (Entry<Integer, Object> param : s.getParameters().entrySet()) {
						this.preparedStatement.setObject(param.getKey(), param.getValue());
					}

					this.preparedStatement.addBatch();

					if ((bulkUpdateSummaryBuilder.incrementAndGet() % this.bulkSize) == 0) {
						updateAndCount();
					}

				} catch (SQLException e) {
					throw new HavelException(e);
				}

			});

			bulkUpdateSummary = bulkUpdateSummaryBuilder.sumUpdateCount(updateAndCount()).finish().build();
			logIfAvailable("Finished! commiting transaction.");
			this.connection.commit();
		} catch (SQLException e) {
			HavelException exception = new HavelException(e);

			try {
				this.connection.rollback();
			} catch (SQLException sqlException) {
				e.addSuppressed(sqlException);
			}

			throw exception;
		}

		return bulkUpdateSummary;
	}

	private StatementParameters dataToStatementParameters(T data) {
		return statementMapperFunction.apply(new StatementParameters(), data);
	}

	private int updateAndCount() throws SQLException {
		int updateCount = this.preparedStatement.executeBatch().length;
		this.preparedStatement.clearBatch();

		if (updateCount > 0) {
			logIfAvailable("{} rows updated.", updateCount);
			if (commitBetweenExecutions) {
				this.connection.commit();
			}
		}

		return updateCount;
	}

	private void validateStatementMapper() {
		if (this.statementMapperFunction == null) {
			throw new IllegalStateException("StatementMapperFunction is null");
		}
	}

	private void validateInputData() {
		if (this.data == null) {
			throw new IllegalStateException("A data input Stream must be present");
		}
	}

	private void validateBulkSize() {
		if (this.bulkSize < 1) {
			throw new IllegalStateException(String.format("Invalid BulkSize of %s", this.bulkSize));
		}
	}

	private class BulkUpdateSummaryBuilder {

		private Instant startTime;
		private Instant finishTime;
		private int updateCount;

		public BulkUpdateSummaryBuilder start() {
			this.startTime = Instant.now();
			return this;
		}

		public BulkUpdateSummaryBuilder finish() {
			this.finishTime = Instant.now();
			return this;
		}

		public int incrementAndGet() {
			return ++this.updateCount;
		}

		public BulkUpdateSummaryBuilder sumUpdateCount(int count) {
			this.updateCount = this.updateCount + count;
			return this;
		}

		public BulkUpdateSummary build() {
			return new BulkUpdateSummary(updateCount, startTime, finishTime);
		}

	}
}