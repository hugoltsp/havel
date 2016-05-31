package com.havel.data.output;

import java.sql.ResultSet;

import com.havel.exception.HavelException;

public interface OutputMapper<O> {

	O getData(ResultSet result) throws HavelException;
}
