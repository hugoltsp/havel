package com.havel.tests;

import org.junit.Assert;
import org.junit.Test;

import com.havel.builder.Batch;
import com.havel.data.input.SqlInput;
import com.havel.data.output.JpaOutputMapper;
import com.havel.tests.util.UserEntity;

public class OutputTests extends HavelTests {

	@Test
	public void testJpaOutputMapper() throws Exception {
		UserEntity userEntity = Batch.<UserEntity> bulkSelect().withConnection(connection)
				.withSqlInput(new SqlInput("select * from user limit 1"))
				.withOutputMapper(new JpaOutputMapper<UserEntity>(UserEntity.class)).select().findAny().get();

		Assert.assertTrue(userEntity != null);
		Assert.assertTrue(userEntity.getId() != null);
		Assert.assertTrue(userEntity.getEmail() != null);
		Assert.assertTrue(userEntity.getName() != null);
	}

}
