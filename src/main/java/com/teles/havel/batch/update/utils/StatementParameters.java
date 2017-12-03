package com.teles.havel.batch.update.utils;

import java.util.HashMap;
import java.util.Map;

public final class StatementParameters {

	private final Map<Integer, Object> params = new HashMap<>();
	private int position;

	public Map<Integer, Object> getParameters() {
		return this.params;
	}

	public <T> StatementParameters addParameter(T value) {
		this.params.put(++position, value);
		return this;
	}

}