# Havel

Havel is a simple library for JDBC batch processing, it's mainly written on top of builder classes and a few Java 8 API components (java.util.function and Streams)

## Examples

##### Update

```java
Connection connection = getMyConnection();
Stream<User> users = getMyUsers();

BulkUpdate<User> bulkUpdateOperation = new BulkUpdateBuilder<User>()
		.withLogger(LoggerFactory.getLogger("myLogger")) //optional
		.withLoggerLevel(LogLevel.INFO) //optional
		.withConnection(connection)
		.withSqlStatement("INSERT INTO USER (NAME, EMAIL) VALUES (?, ?)")
		.withData(users)
		.withBulkSize(10_000)
		.withCommitBetweenExecutions(true) //optional
		.withStatementMapper((t, u) -> t.addParameter(u.getName()).addParameter(u.getEmail()))
		.build();

BulkUpdateSummary summary = bulkUpdateOperation.execute();

```
#### Select

```java
Connection connection = getMyConnection();

BulkSelect<User> bulkSelect = new BulkSelectBuilder<>()
			.withLogger(LoggerFactory.getLogger("myLogger"))
			.withConnection(connection)
			.withSqlStatement("SELECT NAME, EMAIL FROM USER")
			.withOutputMapper(row -> {
				User u = new User();
				u.setEmail(row.getColumn("EMAIL", String.class));
				u.setName(row.getColumn("NAME", String.class));
				return u;
			}).build();
			
Stream<User> users = bulkSelect.select();

```


#### Trivia
Named after a Dark Souls character
