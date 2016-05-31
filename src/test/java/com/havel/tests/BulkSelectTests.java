package com.havel.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.havel.builder.Batch;
import com.havel.data.input.SqlInput;
import com.havel.data.output.OutputMapper;
import com.havel.exception.HavelException;
import com.havel.tests.util.User;

public class BulkSelectTests {

	private Connection connection;

	@Before
	public void prepareConnection() throws Exception {
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
	}

	@Test
	public void testSelectUsers() throws Exception {

		Stream<User> select = Batch.<User> bulkSelect().connection(connection).input(new SqlInput("select * from user"))
				.outputMapper(new OutputMapper<User>() {

					@Override
					public User getData(ResultSet result) {
						User user = null;
						try {
							Long id = result.getLong("id");
							String name = result.getString("name");
							String email = result.getString("email");

							user = new User();
							user.setId(id);
							user.setEmail(email);
							user.setName(name);
						} catch (SQLException e) {
							throw new HavelException(e);
						}

						return user;
					}
				}).select();

		Assert.assertTrue(select.findAny().isPresent());
	}

}