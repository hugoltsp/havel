package com.havel.data.output;

import com.havel.data.mapper.RowMapper;
import com.havel.data.utils.Result;

public class PojoOutput<O> implements Output<O>, RowMapper {

	@Override
	public O getData(Result result) {
		return null;
	}

}
