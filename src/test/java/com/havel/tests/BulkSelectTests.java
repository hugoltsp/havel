package com.havel.tests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.havel.builder.Batch;
import com.havel.data.input.SqlInput;
import com.havel.data.output.OutputMapper;
import com.havel.exception.HavelException;
import com.havel.tests.util.User;

public class BulkSelectTests extends HavelTests {

	@Test
	public void testSelectUsers() throws Exception {

		Stream<User> select = Batch.<User> bulkSelect().withConnection(connection).withInput(new SqlInput("select * from user"))
				.withOutputMapper(new OutputMapper<User>() {

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