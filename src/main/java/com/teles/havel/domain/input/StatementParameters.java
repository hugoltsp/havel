package com.teles.havel.domain.input;

import java.util.HashMap;
import java.util.Map;

public class StatementParameters {

	private final Map<Integer, Object> params = new HashMap<>();
	private int position;

	public Map<Integer, Object> getParams() {
		return this.params;
	}

	public StatementParameters addParameter(Object value) {
		this.params.put(++position, value);
		return this;
	}

}