### Connection objects

Connection to the q process is wrapped by the instances of classes implementing the `QConnection` interface.

The `qJava` library provides two implementations of the `QConnection` interface:
* `QBasicConnection` - basic connector implementation,
* `QCallbackConnection` - in addition to `QBasicConnection` provides wrapped thread which enables the subscription to messages received from q.

The `QBasicConnection` class provides following constructors:
```java
QBasicConnection(String host, int port, String username, String password)
QBasicConnection(String host, int port, String username, String password, String encoding)
```

The `QCallbackConnection` class provides equivalent constructors.


### Managing the remote connection

Note that the connection is not established when the connector instance is created. 
The connection is initialized explicitly by calling the `open()` method.

In order to terminate the remote connection, one has to invoke the `close()` method.

The `QConnection` interface provides the `reset()` method which terminates current connection and opens a new one.
