package com.teles.havel.builder.utils;

import java.time.Duration;

public class BulkUpdateSummary {

	private final long updateCount;
	private final Duration duration;

	public BulkUpdateSummary(UpdateCounter updateCount, Duration duration) {
		this.updateCount = updateCount.getCount();
		this.duration = duration;
	}

	public long getUpdateCount() {
		return updateCount;
	}

	public long getSeconds() {
		return duration.getSeconds();
	}

	public long getHours() {
		return duration.toHours();
	}

	public long getMinutes() {
		return duration.toMinutes();
	}

	@Override
	public String toString() {
		return "BatchUpdateSummary [getUpdateCount()=" + getUpdateCount() + ", getSeconds()=" + getSeconds()
				+ ", getHours()=" + getHours() + ", getMinutes()=" + getMinutes() + "]";
	}

	public static class UpdateCounter {

		private long counter;

		public void sum(long value) {
			this.counter = value + this.counter;
		}

		public long incrementAndGet() {
			return ++this.counter;
		}

		public long getCount() {
			return counter;
		}

	}

}