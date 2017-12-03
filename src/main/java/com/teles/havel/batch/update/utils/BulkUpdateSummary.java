package com.teles.havel.batch.update.utils;

import java.time.Duration;
import java.time.Instant;

public class BulkUpdateSummary {

	private final int updateCount;
	private final Instant startTime;
	private final Instant endTime;
	private final Duration duration;

	public BulkUpdateSummary(int updateCount, Instant startTime, Instant endTime) {
		this.updateCount = updateCount;
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = Duration.between(startTime, endTime);
	}

	public Instant getEndTime() {
		return endTime;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public Duration getDuration() {
		return duration;
	}

	public int getUpdateCount() {
		return updateCount;
	}

	@Override
	public String toString() {
		return "BulkUpdateSummary [updateCount=" + updateCount + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", duration=" + duration + "]";
	}

}