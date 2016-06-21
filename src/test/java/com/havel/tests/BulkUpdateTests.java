package com.havel.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.havel.builder.Batch;
import com.havel.data.utils.BatchUpdateSummary;
import com.havel.tests.util.User;

public class BulkUpdateTests extends HavelTests {

	private static final String SQL = "INSERT INTO user (name, email) VALUES (?, ?)";

	@Test
	public void testBulkUpdate() throws Exception {
		long expectedUpdateCount = 1_500;

		BatchUpdateSummary summary = Batch.<User> bulkUpdate().withConnection(connection).withSqlStatement(SQL)
				.withDataStream(createMockUsers().stream()).withBulkSize(500)
				.withStatementMapper((t, u) -> t.addParameter(u.getName()).addParameter(u.getEmail())).execute();

		System.out.println(summary);

		Assert.assertEquals(expectedUpdateCount, summary.getUpdateCount());
	}

	private static List<User> createMockUsers() {
		List<User> users = new ArrayList<>();

		users.addAll(Stream.generate(() -> {
			User user = new User();
			user.setEmail(UUID.randomUUID().toString());
			user.setName(UUID.randomUUID().toString());
			return user;
		}).limit(1_500).collect(Collectors.toList()));

		return users;
	}
}
