package com.havel.tests;

import java.util.stream.Stream;

import org.junit.Test;

import com.havel.builder.Batch;
import com.havel.data.output.PojoOutput;

public class BulkSelectTests {

	@Test
	public void test() throws Exception {
		Stream<String> parallelSelect = Batch.<String> bulkSelect().datastore(null).input(null)
				.outputMapper(new PojoOutput<String>()).parallelSelect();

	}

}