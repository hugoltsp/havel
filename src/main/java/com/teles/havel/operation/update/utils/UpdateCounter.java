package com.teles.havel.operation.update.utils;

public class UpdateCounter {

	private long counter;

	public void sum(long value) {
		this.counter = value + this.counter;
	}

	public long incrementAndGet() {
		return ++this.counter;
	}

	public long getCount() {
		return counter;
	}
}
