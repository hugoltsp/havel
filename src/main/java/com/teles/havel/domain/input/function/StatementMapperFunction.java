package com.teles.havel.domain.input.function;

import java.util.function.BiFunction;

import com.teles.havel.domain.input.StatementParameters;

@FunctionalInterface
public interface StatementMapperFunction<T> extends BiFunction<StatementParameters, T, StatementParameters> {

}