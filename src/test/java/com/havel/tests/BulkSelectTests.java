package com.havel.tests;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.havel.batch.Batch;
import com.havel.tests.util.User;

public class BulkSelectTests extends HavelTests {

	@Test
	public void testSelectUsers() throws Exception {

		Stream<User> select = Batch.<User>bulkSelect().withConnection(connection)
				.withSqlStatement("select * from user limit 1").withOutputMapper(result -> {
					User user = null;
					Long id = result.getColumn("id", Long.class);
					String name = result.getColumn("name", String.class);
					String email = result.getColumn("email", String.class);

					user = new User();
					user.setId(id);
					user.setEmail(email);
					user.setName(name);
					return user;
				}).select();

		Assert.assertTrue(select.findAny().isPresent());
	}

}