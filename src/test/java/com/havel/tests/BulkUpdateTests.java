package com.havel.tests;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.util.function.BiConsumer;

import org.junit.Test;

import com.havel.builder.Batch;
import com.havel.data.utils.config.DefaultConnectionConfigs;
import com.havel.tests.util.User;

public class BulkUpdateTests extends HavelTests {

	@Test
	public void testBulkUpdate() throws Exception {
		Batch.bulkUpdate().connection(connection).connectionConfig(DefaultConnectionConfigs.BEGIN_COMMIT_TRANSACTION)
				.input("", new User[] {}, new BiConsumer<PreparedStatement, User>() {

					@Override
					public void accept(PreparedStatement t, User u) {

					}
				});
	}
}
