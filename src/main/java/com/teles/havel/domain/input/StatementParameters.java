package com.teles.havel.domain.input;

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
			this.params.put(++position, ordinal);
		} else {
			this.params.put(++position, value);
		}
		return this;
	}

}