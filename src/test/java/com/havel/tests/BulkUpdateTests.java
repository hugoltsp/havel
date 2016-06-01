package com.havel.tests;

import org.junit.Test;

import com.havel.builder.Batch;
import com.havel.builder.Batch.BulkUpdateBuilder.StatementMapper;
import com.havel.builder.Batch.BulkUpdateBuilder.StatementMapperFunction;
import com.havel.data.utils.config.DefaultConnectionConfigs;
import com.havel.tests.util.User;

public class BulkUpdateTests extends HavelTests {

	@Test
	public void testBulkUpdate() throws Exception {
		Batch.<User> bulkUpdate().connection(connection)
				.connectionConfig(DefaultConnectionConfigs.BEGIN_COMMIT_TRANSACTION)
				.input("", new User[] {}, new StatementMapperFunction<User>() {

					@Override
					public StatementMapper apply(StatementMapper t, User u) {
						return null;
					}

				});
	}
}
