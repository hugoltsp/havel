package com.havel.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.havel.builder.Batch;
import com.havel.data.input.SqlInput;
import com.havel.data.output.JpaOutputMapper;
import com.havel.tests.util.User;
import com.havel.tests.util.UserEntity;

public class OutputTests extends HavelTests {

	@Test
	public void jpaOutputMapperTest() throws Exception {
		Batch.<UserEntity> bulkSelect().connection(connection).input(new SqlInput("select * from user where id=1"))
				.outputMapper(new JpaOutputMapper(UserEntity.class)).select().findAny().ifPresent(System.out::println);

	}

}
