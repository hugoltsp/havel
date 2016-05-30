package com.havel.builder;

import java.sql.Connection;

import com.havel.data.input.Input;
import com.havel.data.output.OutputMapper;

public final class Batch {

	private Batch() {

	}

	public static BulkSelectBuilder bulkSelect() {
		BulkSelectBuilder builder = new BulkSelectBuilder();
		return builder;
	}

	private static class BasicBuilder {

		private static final int DEFAULT_BULK_SIZE = 100;

		private int bulkSize = DEFAULT_BULK_SIZE;
		private Connection datastore;
		private Input input;

		public void datastore(Connection datastore) {
			this.datastore = datastore;
		}

		public void size(int size) {
			this.bulkSize = size;
		}

		public void input(Input input) {
			this.input = input;
		}

		public Input getInput() {
			return input;
		}

	}

	public static class BulkSelectBuilder {

		private BasicBuilder basicBuilder;
		private OutputMapper outputMapper;

		public BulkSelectBuilder datastore(Connection datastore) {
			basicBuilder.datastore(datastore);
			return this;
		}

		public BulkSelectBuilder size(int size) {
			basicBuilder.size(size);
			return this;
		}

		public BulkSelectBuilder input(Input input) {
			basicBuilder.input(input);
			return this;
		}

		public BulkSelectBuilder outputMapper(OutputMapper outputMapper) {
			this.outputMapper = outputMapper;
			return null;
		}
	}

}