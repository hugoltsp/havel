package com.teles.havel.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;

public abstract class Builder implements AutoCloseable {

	protected Logger logger;
	protected Connection connection;
	protected String sqlStatement;
	protected PreparedStatement preparedStatement;

	public abstract Builder withLogger(Logger logger);
	
	public abstract Builder withConnection(Connection connection);

	public abstract Builder withSqlStatement(String sqlStatement);

	protected void logIfAvailable(String log, Object...params){
		if(this.logger != null){
			this.logger.info(log, params);
		}
	}
	
	protected void checkState() throws IllegalStateException {
		try {
			if (this.connection == null || connection.isClosed()) {
				throw new IllegalStateException("Connection can't be null or closed");
			}

			if (this.sqlStatement == null || "".equals(sqlStatement)) {
				throw new IllegalStateException("Invalid SqlStatement");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Connection can't be null or closed", e);
		}
	}

	@Override
	public void close() throws SQLException {
		this.preparedStatement.close();
		this.connection.close();
	}

}
