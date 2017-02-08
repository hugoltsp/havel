package com.teles.havel.tests;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Before;

public abstract class HavelTests {
	
	protected Connection connection;

	@Before
	public void prepareConnection() throws Exception {
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
	}
	
}