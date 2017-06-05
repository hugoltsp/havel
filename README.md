# Havel

Havel is a simple library for JDBC batch processing, it's mainly written on top of builder classes and a few Java 8 API components (java.util.function and Streams)

## Example

```java
Connection connection = getMyConnection();
Stream<User> users = getMyUsers();

BulkUpdate<User> bulkUpdateOperation = Builders.<User> bulkUpdate()
    .withLogger(LoggerFactory.getLogger("some-logger-name")) //optional
    .withConnection(connection)
    .withSqlStatement("INSERT INTO user (name, email) VALUES (?, ?)")
    .withData(users)
    .withBulkSize(500)
    .withStatementMapper((t, u) -> t.addParameter(u.getName()).addParameter(u.getEmail())).build();

BulkUpdateSummary summary = bulkUpdateOperation.execute();

```


#### Trivia
Named after a Dark Souls character
