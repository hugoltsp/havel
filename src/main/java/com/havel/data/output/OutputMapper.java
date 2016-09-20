package com.havel.data.output;

import com.havel.builder.BulkSelectBuilder.Row;
import com.havel.exception.HavelException;

public interface OutputMapper<O> {

	O getData(Row row) throws HavelException;
}
