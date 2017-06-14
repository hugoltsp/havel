package com.teles.havel.operation.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.teles.havel.operation.BulkOperation;
import com.teles.havel.operation.exception.HavelException;
import com.teles.havel.operation.update.function.StatementMapperFunction;
import com.teles.havel.operation.update.utils.BulkUpdateSummary;
import com.teles.havel.operation.update.utils.StatementParameters;
import com.teles.havel.operation.update.utils.UpdateCounter;

public class BulkUpdate<T> extends BulkOperation {

	private final long bulkSize;
	private final StatementMapperFunction<T> statementMapperFunction;
	private final Stream<T> data;

	public BulkUpdate(Logger logger, Connection connection, String sqlStatement, PreparedStatement preparedStatement,
			long bulkSize, StatementMapperFunction<T> statementMapperFunction, Stream<T> data) {
		super(logger, connection, sqlStatement, preparedStatement);
		this.bulkSize = bulkSize;
		this.statementMapperFunction = statementMapperFunction;
		this.data = data;
	}

	public BulkUpdateSummary execute() throws HavelException, IllegalStateException {
		this.checkState();
		Instant before = Instant.now();
		UpdateCounter counter = new UpdateCounter();

		try (BulkOperation builder = this) {

			this.connection.setAutoCommit(false);

			super.logIfAvailable("executing update...");

			this.data.filter(Objects::nonNull).sequential()
					.map(p -> statementMapperFunction.apply(new StatementParameters(), p)).forEach(s -> {

						try {

							for (Entry<Integer, Object> param : s.getParams().entrySet()) {
								this.preparedStatement.setObject(param.getKey(), param.getValue());
							}

							this.preparedStatement.addBatch();

							if ((counter.incrementAndGet() % this.bulkSize) == 0) {
								int updateCount = preparedStatement.executeBatch().length;
								preparedStatement.clearBatch();
								super.logIfAvailable("{} rows updated.", updateCount);
							}

						} catch (SQLException e) {
							throw new HavelException(e);
						}

					});

			int updateCount = this.preparedStatement.executeBatch().length;
			counter.sum(updateCount);
			this.preparedStatement.clearBatch();

			if (updateCount > 0) {
				super.logIfAvailable("{} rows updated.", updateCount);
			}

			super.logIfAvailable("finished! commiting transaction.");
			this.connection.commit();
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

		BulkUpdateSummary summary = new BulkUpdateSummary(counter, duration);

		return summary;
	}

	public Future<BulkUpdateSummary> executeAsync() throws HavelException, IllegalStateException {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<BulkUpdateSummary> future = executorService.submit(() -> execute());
		executorService.shutdown();
		return future;
	}

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
