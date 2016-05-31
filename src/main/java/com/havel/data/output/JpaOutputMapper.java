package com.havel.data.output;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.persistence.Column;

import com.havel.exception.HavelException;

public class JpaOutputMapper<O> implements OutputMapper<O> {

	private Class<O> entityType;

	public JpaOutputMapper(Class<O> entityType) {
		this.entityType = entityType;
	}

	@Override
	public O getData(ResultSet result) throws HavelException {
		try {
			return this.resultSetToJpaEntity(result);
		} catch (InstantiationException | IllegalAccessException | SQLException e) {
			throw new HavelException(e);
		}
	}

	private O resultSetToJpaEntity(ResultSet rs) throws InstantiationException, IllegalAccessException, SQLException {
		ResultSetMetaData metaData = rs.getMetaData();

		O newInstance = this.entityType.newInstance();

		Field[] fields = this.entityType.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if(field.isAnnotationPresent(Column.class)){
				Column column = field.getAnnotation(Column.class);
				String columnName = column.name();
			}
		}

		return newInstance;
	}

}
