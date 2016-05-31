package com.havel.data.utils;

import java.sql.Connection;

public interface ConnectionConfig {

	void onBefore(Connection connection);
	
	void onAfter(Connection connection);
	
}
