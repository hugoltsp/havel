package com.havel.tests;

import org.junit.Test;

import com.havel.builder.Batch;

public class BulkSelectTests {

	@Test
	public void test() throws Exception {
		Batch.bulkSelect().datastore(null).input(null).size(0).outputMapper(null);
	}
	
}