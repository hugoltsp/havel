package com.havel.data.output;

import java.sql.ResultSet;

public interface OutputMapper<O> {

	O getData(ResultSet result);
}
