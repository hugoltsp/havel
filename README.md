# Havel

Havel is a simple library for JDBC batch processing, it's mainly written on top of builder classes and a few Java 8 API components (java.util.function and Streams)

## Examples

##### Update

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
#### Select

```java
Connection connection = getMyConnection();

BulkSelect<User> bulkSelectOperation = Builders.<User>bulkSelect()
    .withLogger(LoggerFactory.getLogger("some-logger-name")) //optional
    .withConnection(connection)
    .withSqlStatement("SELECT name, email FROM user")
    .withOutputMapper(row -> {
       String name = row.getColumn("name", String.class);
       String email = row.getColumn("email", String.class);
       User u = new User();
       u.setEmail(email);
       u.setName(name);
       return u;
     }).build();
				
Stream<User> users = bulkSelectOperation.select();

```


#### Trivia
Named after a Dark Souls character
