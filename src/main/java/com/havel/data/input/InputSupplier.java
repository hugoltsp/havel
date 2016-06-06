package com.havel.data.input;

import java.util.Optional;
import java.util.function.Supplier;

public interface InputSupplier<T> extends Supplier<Optional<T>> {

	public Optional<T> get();

}
