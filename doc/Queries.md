### Interacting with q

The `qJava` library supports both synchronous and asynchronous queries.

Synchronous query waits for service response and blocks requesting process until it receives the response. 

Asynchronous query does not block the requesting process and returns immediately without any result. The actual query result can be obtained either by issuing a corresponding request later on, or by setting up a listener to await and react accordingly to received data.

The `qJava` library provides following API methods in the `QConnection` interface to query the kdb+: 

```java
// Executes a synchronous query against the remote q service.
public Object sync( String query, Object... parameters ) throws QException, IOException;

// Executes an asynchronous query against the remote q service.
public void async( String query, Object... parameters ) throws QException, IOException;

// Executes a query against the remote q service.
public int query( final MessageType msgType, final String query, final Object... parameters ) throws QException, IOException;
```

where:
* `query` is the definition of the query to be executed,
* `parameters` is a list of additional parameters used when executing given query,
* `msgType` indicates type of the q message to be sent.

In typical use case, `query` is the name of the function to call and parameters are its parameters. When parameters list is empty the query can be an arbitrary q expression (e.g. `0 +/ til 100`).

In order to retrieve query result (for the `async()` or `query()` methods), one has to call:
```java
// Reads next message from the remote q service.
public abstract Object receive() throws IOException, QException;
public abstract Object receive( boolean dataOnly, boolean raw ) throws IOException, QException;
```

If the `dataOnly` parameter is set to `true`, only data part of the message is returned. If set to `false`, both data and message meta-information is returned as a wrapped as instance of `QMessage` class.

If the `raw` parameter is set to `false`, message is parsed and transformed to Java object. If set to `true`, message is not parsed and raw array of bytes is returned.


### Asynchronous subscription

The `QCallbackConnection` class extends the `QBasicConnection` class and enables the subscription mechanism to publish messages received from remote q service.

One can subscribe to q messages via the following event handler:
```java
public synchronized void addMessagesListener( final QMessagesListener listener )
```

The `QCallbackConnection` wraps the thread instance which can be used to listen to incoming q messages and pushing these via the `QMessagesListener`. This thread can be start and stopped via `startListener()` and `stopListener()` methods respectively.
