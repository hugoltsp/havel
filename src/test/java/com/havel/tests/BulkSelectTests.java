package com.havel.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.stream.Stream;

import org.junit.Test;

import com.havel.builder.Batch;
import com.havel.data.input.SqlInput;
import com.havel.data.output.PojoOutput;

public class BulkSelectTests {

	@Test
	public void test() throws Exception {
	    Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:aname", "sa", "");
		Stream<String> parallelSelect = Batch.<String> bulkSelect().connection(connection)
				.input(new SqlInput("select * from usuario")).
				outputMapper(new PojoOutput<String>()).
				parallelSelect();

	}

}