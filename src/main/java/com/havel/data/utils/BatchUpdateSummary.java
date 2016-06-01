package com.havel.data.utils;

import java.util.Arrays;

public class BatchUpdateSummary {

	private long updateCount;
	private long[] generatedIds;

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

	@Override
	public String toString() {
		return "BatchSummary [updateCount=" + updateCount + ", generatedIds=" + Arrays.toString(generatedIds) + "]";
	}

}