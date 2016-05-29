package com.havel.builder;

import com.havel.batch.Batch;
import com.havel.batch.select.BulkSelectBatch;
import com.havel.data.Datastore;
import com.havel.data.input.Input;

public final class Batches {

	private Batches() {

	}

	public static BulkSelectBuilder bulkSelect() {
		BulkSelectBuilder builder = new BulkSelectBuilder();
		return builder;
	}

	private static class Builder<T extends Batch> {

		private static final int DEFAULT_BULK_SIZE = 100;

		private T batch;
		private int bulkSize = DEFAULT_BULK_SIZE;
		private Datastore datastore;
		private Input input;

		public Builder<T> datastore(Datastore datastore) {
			this.datastore = datastore;
			return this;
		}

		public Builder<T> size(int size) {
			this.bulkSize = size;
			return this;
		}

		public Builder<T> input(Input input) {
			this.input = input;
			return this;
		}
	}

	private static class BulkSelectBuilder extends Builder<BulkSelectBatch> {

	}

}