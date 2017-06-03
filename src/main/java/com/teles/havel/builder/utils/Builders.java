package com.teles.havel.builder.utils;

import com.teles.havel.builder.BulkSelectBuilder;
import com.teles.havel.builder.BulkUpdateBuilder;

public final class Builders {

	private Builders() {

	}

	public static <T> BulkSelectBuilder<T> bulkSelect() {
		BulkSelectBuilder<T> builder = new BulkSelectBuilder<>();
		return builder;
	}

	public static <T> BulkUpdateBuilder<T> bulkUpdate() {
		BulkUpdateBuilder<T> bulkUpdateBuilder = new BulkUpdateBuilder<>();
		return bulkUpdateBuilder;
	}

}