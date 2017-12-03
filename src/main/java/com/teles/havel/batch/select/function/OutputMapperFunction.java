package com.teles.havel.batch.select.function;

import java.util.function.Function;

import com.teles.havel.batch.select.utils.Row;

@FunctionalInterface
public interface OutputMapperFunction<O> extends Function<Row, O> {

}
