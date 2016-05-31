package com.havel.tests;

import org.junit.Assert;
import org.junit.Test;

import com.havel.builder.Batch;
import com.havel.data.input.SqlInput;
import com.havel.data.output.JpaOutputMapper;
import com.havel.tests.util.UserEntity;

public class OutputTests extends HavelTests {

	@Test
	public void jpaOutputMapperTest() throws Exception {
		UserEntity userEntity = Batch.<UserEntity> bulkSelect().connection(connection)
				.input(new SqlInput("select * from user where id=1"))
				.outputMapper(new JpaOutputMapper<UserEntity>(UserEntity.class)).select().findAny().get();

		Assert.assertTrue(userEntity != null);
		Assert.assertTrue(userEntity.getId() != null);
		Assert.assertTrue(userEntity.getEmail() != null);
		Assert.assertTrue(userEntity.getName() != null);
	}

}
