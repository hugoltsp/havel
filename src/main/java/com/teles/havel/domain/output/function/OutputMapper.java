package com.teles.havel.domain.output.function;

import com.teles.havel.domain.output.Row;
import com.teles.havel.exception.HavelException;

@FunctionalInterface
public interface OutputMapper<O> {

	O getData(Row row) throws HavelException;
}
