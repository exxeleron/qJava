This page describes the most important differences between `qJava` versions: `1.x` and `2.x`.

### Class hierarchy

Class hierarchy has been remodeled to enable end-user to subclass in more convenient manner.

* `QConnection` is now interface and can be implemented in 3rd party extensions. E.g.: custom load balancing implementation class may implement `QConnection` and enclose multiple instances of `QBasicConnection` to perform balancing.
* Basic connectivity is now implemented in the `QBasicConnection` class. This class provides minimal, complete set of API methods to interact with remote q processes.
* Functionality for the asynchronous connectivity and listening in the wrapped thread is now refactored to the `QCallbackConnection` class.

### QReader API

`QReader` no longer provides methods: 
* `getDataSize()`
* `getEndianess()`
* `getMessageSize()`
* `getMessageType()`
* `isCompressed()`

As a replacement, `QReader.read(…)` method returns instances of `QMessage` class. The `QMessage` instance provides access to both data payload and meta information like: size of the message, size of data, message type, endianess and compression flag.

One have to use lower level API of the `QConnection` interface to access these information:
* `public abstract Object receive( boolean dataOnly, boolean raw )`
* `public abstract int query( final MessageType msgType, final String query, final Object... parameters )`

### QWriter API

`QWriter.write(…)` method now returns number of written bytes (i.e.: size of the message). Methods: `getMessageSize()` and `getDataSize()` have been removed.

Please note that header in IPC protocol has fixed length of 8 bytes and thus size of data payload is equal to `messageSize – 8`.

### Failover

Generic failover functionality (`QFailoverConnection` class) has been removed. There are no plans to reintroduce this feature at the moment.

### Samples

Please refer to [samples section](Usage-examples.md) for up-to-date usage examples.