package com.teles.havel.batch.select.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.teles.havel.batch.exception.BatchException;

public class Row {

	private final int columnCount;
	private final ResultSet resultSet;

	public Row(ResultSet resultSet) throws SQLException {
		this.resultSet = resultSet;
		this.columnCount = resultSet.getMetaData().getColumnCount();
	}

	public <T> T getColumn(String name, Class<T> clazz) throws BatchException {
		T columnObject = null;

		
		try {

			columnObject = getColumn(resultSet.findColumn(name), clazz);
			
		} catch (SQLException e) {
			throw new BatchException(e);
		}

		return columnObject;
	}

	public <T> T getColumn(int number, Class<T> clazz) throws BatchException, IllegalArgumentException {

		if (number < 1 || number > this.columnCount) {
			throw new IllegalArgumentException("Column number out of bounds: " + number);
		}

		T columnObject = null;

		try {

			columnObject = this.resultSet.getObject(number, clazz);

		} catch (SQLException e) {
			throw new BatchException(e);
		}

		return columnObject;
	}

}