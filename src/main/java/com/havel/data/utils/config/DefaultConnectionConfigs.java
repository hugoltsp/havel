package com.havel.data.utils.config;

import java.sql.Connection;
import java.sql.SQLException;

import com.havel.exception.HavelException;

public enum DefaultConnectionConfigs implements ConnectionConfig {
	BEGIN_COMMIT_TRANSACTION {

		@Override
		public void onBefore(Connection connection) throws HavelException{
			try {
				connection.setAutoCommit(false);
			} catch (SQLException e) {
				throw new HavelException(e);
			}
		}

		@Override
		public void onAfter(Connection connection) throws HavelException{
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				throw new HavelException(e);
			}
		}

	};

}
