package com.teles.havel.batch.test;

final class Constants {

	static final String SQL_CREATE_TABLE = "CREATE TABLE USER (NAME VARCHAR(255), EMAIL VARCHAR(255))";
	static final String JDBC_H2_URL = "jdbc:h2:mem:testcase;";
	static final String SQL_INSERT = "INSERT INTO USER (NAME, EMAIL) VALUES (?, ?)";
	static final String SQL_SELECT = "SELECT NAME, EMAIL FROM USER";

	private Constants() {

	}

}
