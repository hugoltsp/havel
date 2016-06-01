package com.havel.data.utils.config;

import java.sql.Connection;

public interface ConnectionConfig {

	void onBefore(Connection connection);
	
	void onAfter(Connection connection);
	
}
