package com.teles.havel.data.output;

import com.teles.havel.builder.BulkSelectBuilder.Row;
import com.teles.havel.exception.HavelException;

public interface OutputMapper<O> {

	O getData(Row row) throws HavelException;
}
