package com.teles.havel.operation.select.function;

import java.util.function.Function;

import com.teles.havel.operation.select.utils.Row;

@FunctionalInterface
public interface OutputMapperFunction<O> extends Function<Row, O>{

}
