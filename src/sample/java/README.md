# Samples

## `AsynchQuery`

Asynchronously calls query that returns (also asynchronously) product of query arguments.

### Usage

1. Start q process listening on a specific port (eg. 5000):

	`q -p 5000`

2. Start AsynchQuery process and pass host and port of open q process:

	`java AsynchQuery localhost 5000`

3. You should see receive several async messages:

	```
	Asynchronous query 0 sent. Arguments:  3,  98
	Asynchronous query 1 sent. Arguments: 15,  20
	Asynchronous query 2 sent. Arguments: 26,  61
	Asynchronous query 3 sent. Arguments: 90,  39
	Asynchronous message received.
	message type: ASYNC size: 44 isCompressed: false endianess: LITTLE_ENDIAN
	Asynchronous query 4 sent. Arguments: 64,  10
	Asynchronous query 5 sent. Arguments: 63,  28
	Result: QDictionary: [queryid, result]![0, 294]
	Asynchronous query 6 sent. Arguments: 17,  93
	Asynchronous message received.
	message type: ASYNC size: 44 isCompressed: false endianess: LITTLE_ENDIAN
	Asynchronous query 7 sent. Arguments: 51,  49
	Result: QDictionary: [queryid, result]![1, 300]
	Asynchronous query 8 sent. Arguments: 19,  30
	Asynchronous message received.
	message type: ASYNC size: 44 isCompressed: false endianess: LITTLE_ENDIAN
	Asynchronous query 9 sent. Arguments: 39,   8
	Result: QDictionary: [queryid, result]![2, 1586]
	...
	```

## `Console`

Imitates basic q-console functionality by executing every read line as sync query and printing results.

### Usage

1. Start q process listening on a specific port (eg. 5000):

	`q -p 5000`
2. Start console and pass host and port of open q process:

	`java Console localhost 5000`

3. Execute q expression in opened console:

	`2+3`

## `Publisher`

Publishes data to q process. Generates 10 new entries to table `ask` every second.
`ask` table model:

| Field    | q type    | qJava type
|----------|-----------|-----------
| `time`   | `timestamp` | `QTime`
| `sym`    | `symbol`    | `String`
| `source` | `symbol`    | `String`
| `val`    | `real`      | `float`

### Usage example

1. Start q process listening on a specific port (eg. 5000):

	`q -p 5000`

2. In q process define `.u.upd` function:

	`.u.upd:insert`

3. In q process define empty `ask` table:

	`ask:([] time:0#0Nt;sym:0#`;source:0#`;val:0#0Ne)`

4. Start publisher and pass host and port of open q process:

	`java Publisher localhost 5000`

5. In q process inspect contents of `ask` table:

	`ask`

6. You should be able to see several rows, eg:

	```
	time         sym      source val
	-------------------------------------
	09:45:48.263 INSTR_94 qJava  27.87765
	09:45:48.263 INSTR_36 qJava  4.045489
	09:45:48.263 INSTR_24 qJava  14.40442
	09:45:48.263 INSTR_38 qJava  14.91003
	09:45:48.263 INSTR_29 qJava  4.169513
	09:45:49.267 INSTR_8  qJava  4.517988
	09:45:49.267 INSTR_12 qJava  57.61596
	09:45:49.267 INSTR_47 qJava  1.989117
	09:45:49.267 INSTR_89 qJava  12.05809
	09:45:49.267 INSTR_93 qJava  19.7443
	...
	```

## `Subscriber`

Simple subscriber sample - it sets up value generator in q process (via `.z.ts`), then subscribes and listens for it's values.

### Usage

1. Start q process listening on a specific port (eg. 5000):

	`q -p 5000`
2. Start subscriber and pass host and port of open q process:

	`java Subscriber localhost 5000`

3. You should be able to see incoming messages logged in subscriber process, eg:

	```
	WARNING: this application overwrites: .z.ts and sub functions on q process running on port: 5000
	Press <ENTER> to close application
	Asynchronous message received.
	message type: ASYNC size: 17 isCompressed: false endianess: LITTLE_ENDIAN
	Result: 2016.04.12D07:54:49.522595000
	Asynchronous message received.
	message type: ASYNC size: 17 isCompressed: false endianess: LITTLE_ENDIAN
	Result: 2016.04.12D07:54:50.022226000
	Asynchronous message received.
	message type: ASYNC size: 17 isCompressed: false endianess: LITTLE_ENDIAN
	Result: 2016.04.12D07:54:50.522922000
	```

## `SyncQuery`

Calls simple synchronous queries

### Usage

1. Start q process listening on a port 5001:

	`q -p 5001`

2. Start syncQuery:

	`java SyncQuery`

3. You should be able to see results of queries:

	```
	[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
	sent: 43 bytes
	message type: RESPONSE size: 54 isCompressed: false endianess: LITTLE_ENDIAN
	[0, 2, 4, 6, 8, 10, 12, 14, 16, 18]
	```

## `TickClient`

Graphical interface for displaying updates received from `tick` process.

> Running q `tickHF` process is required for this sample to work. Please see [Exxeleron Enterprise Components](https://github.com/exxeleron/enterprise-components) for reference. [Lesson 01](https://github.com/exxeleron/enterprise-components/tree/develop/tutorial/Lesson01) is sufficient.

### Usage

This sample has graphical interface that allows to specify q connection details. No arguments are passed via command line.

## `TickSubscriber`

Subscribes to `tick` process (table `trade`) and displays updates in console window. 
It connects to local q process listening on port `17010`

> Running q `tickHF` process is required for this sample to work. Please see [Exxeleron Enterprise Components](https://github.com/exxeleron/enterprise-components) for reference. [Lesson 01](https://github.com/exxeleron/enterprise-components/tree/develop/tutorial/Lesson01) is sufficient.


> Table `trade` must be defined on `q` process used by this sample (also covered in [Lesson 01](https://github.com/exxeleron/enterprise-components/tree/develop/tutorial/Lesson01))

### Usage

This sample uses hardcoded q connection to local machine on port `17010` and subscribes to table `trade`. No arguments are passed via command line.