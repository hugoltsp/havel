package com.teles.havel.operation.update.utils;

import java.util.HashMap;
import java.util.Map;

public class StatementParameters {

	private final Map<Integer, Object> params = new HashMap<>();
	private int position;

	public Map<Integer, Object> getParams() {
		return this.params;
	}

	public <T> StatementParameters addParameter(T value) {

		if (value != null && value instanceof Enum<?>) {
			int ordinal = ((Enum<?>) value).ordinal();
			put(ordinal);
		} else {
			put(value);
		}

		return this;
	}

	private <T> void put(T value) {
		this.params.put(++position, value);
	}

}