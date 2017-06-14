package com.teles.havel.operation.update.function;

import java.util.function.BiFunction;

import com.teles.havel.operation.update.utils.StatementParameters;

@FunctionalInterface
public interface StatementMapperFunction<T> extends BiFunction<StatementParameters, T, StatementParameters> {

}