package com.teles.havel.builder.utils;

import com.teles.havel.builder.select.BulkSelectBuilder;
import com.teles.havel.builder.update.BulkUpdateBuilder;

public final class Builders {

	private Builders() {
	}

	public static <T> BulkSelectBuilder<T> bulkSelect() {
		BulkSelectBuilder<T> builder = BulkSelectBuilder.create();
		return builder;
	}

	public static <T> BulkUpdateBuilder<T> bulkUpdate() {
		BulkUpdateBuilder<T> bulkUpdateBuilder = BulkUpdateBuilder.create();
		return bulkUpdateBuilder;
	}

}