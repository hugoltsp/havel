package com.teles.havel.operation.update.utils;

import java.time.Duration;
import java.time.Instant;

public final class BulkUpdateSummary {

	private int updateCount;
	private Instant startTime;
	private Duration duration;

	private BulkUpdateSummary() {
		startTime = Instant.now();
	}

	public static BulkUpdateSummary start() {
		BulkUpdateSummary bulkUpdateSummary = new BulkUpdateSummary();
		return bulkUpdateSummary;
	}

	public void finish() {
		this.duration = Duration.between(startTime, Instant.now());
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

	public int incrementAndGet() {
		return ++this.updateCount;
	}

	public void sumUpdateCount(int sum) {
		this.updateCount = this.updateCount + sum;
	}

	public long getUpdateCount() {
		return updateCount;
	}

	@Override
	public String toString() {
		return "BulkUpdateSummary [updateCount=" + updateCount + ", startTime=" + startTime + ", duration=" + duration
				+ ", getSeconds()=" + getSeconds() + ", getHours()=" + getHours() + ", getMinutes()=" + getMinutes()
				+ "]";
	}

}