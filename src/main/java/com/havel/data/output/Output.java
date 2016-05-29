package com.havel.data.output;

import com.havel.data.utils.Result;

public interface Output<O> {

	O getData(Result result);
}
