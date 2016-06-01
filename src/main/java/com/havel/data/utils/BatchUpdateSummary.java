package com.havel.data.utils;

import java.util.Arrays;

public class BatchUpdateSummary {

	private Long updateCount;
	private long[] generatedIds;

	public Long getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(Long updateCount) {
		this.updateCount = updateCount;
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