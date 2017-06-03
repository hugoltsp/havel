package com.teles.havel.domain.output.function;

import com.teles.havel.domain.exception.HavelException;
import com.teles.havel.domain.output.Row;

@FunctionalInterface
public interface OutputMapper<O> {

	O getData(Row row) throws HavelException;
}
