package com.teles.havel.batch.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.teles.havel.batch.select.BulkSelect;
import com.teles.havel.batch.select.BulkSelectBuilder;

public class BulkSelectTest {

	private Connection connection;

	@Before
	public void before() throws SQLException {
		this.connection = DriverManager.getConnection(Constants.JDBC_H2_URL);
		this.connection.createStatement().execute(Constants.SQL_CREATE_TABLE);
		PreparedStatement prepareStatement = this.connection.prepareStatement(Constants.SQL_INSERT);
		prepareStatement.setString(1, User.mockUser().getName());
		prepareStatement.setString(2, User.mockUser().getEmail());
		prepareStatement.executeUpdate();
		this.connection.commit();
	}

	@After
	public void after() throws SQLException {
		this.connection.close();
	}

	@Test
	public void bulk_select_test() throws Exception {
		BulkSelect<Object> bulkSelect = new BulkSelectBuilder<>()
				.withLogger(LoggerFactory.getLogger("myTestLogger"))
				.withConnection(connection)
				.withSqlStatement(Constants.SQL_SELECT)
				.withOutputMapper(row -> {
					User u = new User();
					u.setEmail(row.getColumn("EMAIL", String.class));
					u.setName(row.getColumn("NAME", String.class));
					return u;
				}).build();

		bulkSelect.select().forEach(System.out::println);
	}
}
