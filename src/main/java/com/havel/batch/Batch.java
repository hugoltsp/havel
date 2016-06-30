package com.havel.batch;

import com.havel.builder.BulkSelectBuilder;
import com.havel.builder.BulkUpdateBuilder;

public final class Batch {

	private Batch() {

	}

	public static <O> BulkSelectBuilder<O> bulkSelect() {
		BulkSelectBuilder<O> builder = new BulkSelectBuilder<>();
		return builder;
	}

	public static <T> BulkUpdateBuilder<T> bulkUpdate() {
		BulkUpdateBuilder<T> bulkUpdateBuilder = new BulkUpdateBuilder<>();
		return bulkUpdateBuilder;
	}

}