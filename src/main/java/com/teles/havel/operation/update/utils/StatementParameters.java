package com.teles.havel.operation.update.utils;

import java.util.HashMap;
import java.util.Map;

public final class StatementParameters {

	private final Map<Integer, Object> params = new HashMap<>();
	private int position;

	public Map<Integer, Object> getParams() {
		return this.params;
	}

	public <T> StatementParameters addParameter(T value) {
		this.put(value);
		return this;
	}

	private <T> void put(T value) {
		this.params.put(++position, value);
	}

}