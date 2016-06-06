package com.havel.data.input;

import java.util.function.Supplier;

public interface InputSupplier<T> extends Supplier<T>{

	boolean isFinished();
	
	void finish();
	
}
