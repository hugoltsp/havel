package com.teles.havel.batch.update.function;

import java.util.function.BiFunction;

import com.teles.havel.batch.update.utils.StatementParameters;

@FunctionalInterface
public interface StatementMapperFunction<T> extends BiFunction<StatementParameters, T, StatementParameters> {

}