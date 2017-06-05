package com.teles.havel.tests;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.teles.havel.builder.BulkUpdateBuilder;
import com.teles.havel.builder.utils.Builders;
import com.teles.havel.domain.update.BulkUpdate;
import com.teles.havel.domain.update.util.BulkUpdateSummary;
import com.teles.havel.tests.util.User;

public class BulkUpdateTests extends HavelTests {

	private static final String SQL = "INSERT INTO user (name, email) VALUES (?, ?)";

	@Test
	public void testBulkUpdate() throws Exception {
		long expectedUpdateCount = 1_500;

		Stream<User> users = getUsers();

		BulkUpdateBuilder<User> builder = Builders.<User> bulkUpdate().withLogger(LoggerFactory.getLogger("Test"))
				.withConnection(connection).withSqlStatement(SQL).withData(users).withBulkSize(500)
				.withStatementMapper((t, u) -> t.addParameter(u.getName()).addParameter(u.getEmail()));

		BulkUpdate<User> bulkUpdate = builder.build();
		BulkUpdateSummary summary = bulkUpdate.execute();

		System.out.println(summary);
		Assert.assertEquals(expectedUpdateCount, summary.getUpdateCount());
	}

	private static Stream<User> getUsers() {

		Stream<User> users = Stream.generate(() -> {
			User user = new User();
			user.setEmail(UUID.randomUUID().toString());
			user.setName(UUID.randomUUID().toString());
			return user;
		}).limit(1_500);

		return users;
	}
}
