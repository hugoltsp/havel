package com.havel.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.havel.builder.Batch;
import com.havel.data.utils.config.DefaultConnectionConfigs;
import com.havel.tests.util.User;

public class BulkUpdateTests extends HavelTests {

	@Test
	public void testBulkUpdate() throws Exception {
		Batch.<User> bulkUpdate().withConnection(connection).withBulkSize(1000)
				.withConnectionConfig(DefaultConnectionConfigs.BEGIN_COMMIT_TRANSACTION)
				.withInput("INSERT INTO user (name, email) VALUES (?, ?)", createMockUsers(),
						(t, u) -> t.addParameter(u.getName()).addParameter(u.getEmail()))
				.execute();
		;
	}

	private static User[] createMockUsers() {
		List<User> users = new ArrayList<>();

		users.addAll(Stream.generate(() -> {
			User user = new User();
			user.setEmail(UUID.randomUUID().toString());
			user.setName(UUID.randomUUID().toString());
			return user;
		}).limit(100000).collect(Collectors.toList()));

		return users.toArray(new User[users.size()]);
	}
}
