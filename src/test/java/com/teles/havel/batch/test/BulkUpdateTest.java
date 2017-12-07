package com.teles.havel.batch.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.teles.havel.batch.enums.LogLevel;
import com.teles.havel.batch.update.BulkUpdate;
import com.teles.havel.batch.update.BulkUpdateBuilder;
import com.teles.havel.batch.update.utils.BulkUpdateSummary;

public class BulkUpdateTest {

	private Connection connection;

	@Before
	public void before() throws SQLException {
		this.connection = DriverManager.getConnection(Constants.JDBC_H2_URL);
		this.connection.createStatement().execute(Constants.SQL_CREATE_TABLE);
		this.connection.commit();
	}

	@After
	public void after() throws SQLException {
		this.connection.close();
	}

	@Test
	public void bulk_update_test() throws Exception {

		Stream<User> users = Stream.generate(User::mockUser).limit(1_000_00);

		BulkUpdate<User> bulkUpdateOperation = new BulkUpdateBuilder<User>()
				.withLogger(LoggerFactory.getLogger("myTestLogger")) // optional
				.withLoggerLevel(LogLevel.INFO) // optional
				.withConnection(connection)
				.withSqlStatement(Constants.SQL_INSERT)
				.withData(users)
				.withBulkSize(10000)
				.withCommitBetweenExecutions(true)
				.withStatementMapper((t, u) -> t.addParameter(u.getName()).addParameter(u.getEmail()))
				.build();

		BulkUpdateSummary summary = bulkUpdateOperation.execute();

		System.out.println(summary);
	}

}
