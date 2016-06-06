package com.havel.data.utils;

import java.time.Duration;
import java.util.Arrays;

public class BatchUpdateSummary {

	private long updateCount;
	private long[] generatedIds;
	private Duration duration;

	public long getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(long updateCount) {
		this.updateCount = updateCount;
	}

	public void sumUpdateCount(long updateCount) {
		this.updateCount = updateCount + this.updateCount;
	}

	public long[] getGeneratedIds() {
		return generatedIds;
	}

	public void setGeneratedIds(long[] generatedIds) {
		this.generatedIds = generatedIds;
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

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "BatchUpdateSummary [getUpdateCount()=" + getUpdateCount() + ", getGeneratedIds()="
				+ Arrays.toString(getGeneratedIds()) + ", getSeconds()=" + getSeconds() + ", getHours()=" + getHours()
				+ ", getMinutes()=" + getMinutes() + "]";
	}

}