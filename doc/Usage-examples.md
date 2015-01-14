- [Synchronous query](Usage-examples.md#synchronous-query)
- [Asynchronous query](Usage-examples.md#asynchronous-query)
- [Interactive console](Usage-examples.md#interactive-console)
- [Subscribing for asynchronous messages](Usage-examples.md#subscribing-for-asynchronous-messages)
- [Subscribing to tick service](Usage-examples.md#subscribing-to-tick-service)
- [Data publisher](Usage-examples.md#data-publisher)

## Synchronous query
Following example presents how to execute simple, synchronous query against a remote kdb+ process:

```java
import java.io.IOException;
import java.util.Arrays;

import com.exxeleron.qjava.QBasicConnection;
import com.exxeleron.qjava.QConnection;
import com.exxeleron.qjava.QException;

public class SyncQuery {

    public static void main( final String[] args ) throws IOException {
        // create connection to localhost:5001 with login myUser and password myPassword
        final QConnection q = new QBasicConnection("localhost", 5001, "myUser", "myPassword");
        try {
            q.open(); // open connection
            // sending query {til x}[10] and storing its result in list
            final int[] list = (int[]) q.sync("{`int$ til x}", 10);
            // print result
            System.out.println(Arrays.toString(list));

            // low level query
            int msgSize = q.query(MessageType.SYNC, "{2i * `int$ til x}", 10);
            System.out.println("sent: " + msgSize + " bytes");
            // low level receive
            final QMessage message = (QMessage) q.receive(false, false);
            // print message meta data
            System.out.println(String.format("message type: %1s size: %2d isCompressed: %3b endianess: %4s", message.getMessageType(),
                    message.getMessageSize(), message.isCompressed(), message.getEndianess()));
            // print result
            System.out.println(Arrays.toString((int[]) message.getData()));
        } catch ( final QException e ) {
            System.err.println(e);
        } finally {
            q.close(); // close connection
        }
    }

}
```

## Asynchronous query
Following example presents how to execute simple, asynchronous query against a remote kdb+ process:

```java
import java.io.IOException;
import java.util.Random;

import com.exxeleron.qjava.QCallbackConnection;
import com.exxeleron.qjava.QErrorMessage;
import com.exxeleron.qjava.QMessage;
import com.exxeleron.qjava.QMessagesListener;

public class AsynchQuery {

    public static void main( final String[] args ) throws IOException {
        final QCallbackConnection q = new QCallbackConnection(args.length >= 1 ? args[0] : "localhost", args.length >= 2 ? Integer.parseInt(args[1]) : 5001,
                "", "");

        // definition of messageListener that prints every message it gets on stdout
        final QMessagesListener listener = new QMessagesListener() {

            public void messageReceived( final QMessage message ) {
                System.out.println(String.format("Asynchronous message received.\nmessage type: %1s size: %2d isCompressed: %3b endianess: %4s",
                        message.getMessageType(), message.getMessageSize(), message.isCompressed(), message.getEndianess()));
                System.out.println("Result: " + (message.getData().toString()));
            }

            public void errorReceived( final QErrorMessage message ) {
                System.err.println("Error while processing asynchronous query:" + (message.getCause().toString()));
            }
        };

        q.addMessagesListener(listener);
        try {
            q.open(); // open connection

            // definition of asynchronous multiply function
            // queryid - unique identifier of function call - used to identify
            // the result
            // a, b - actual parameters to the query
            q.sync("asynchMult:{[queryid;a;b] res:a*b; (neg .z.w)(`queryid`result!(queryid;res)) }");

            q.startListener(); // activate messageListener

            final Random gen = new Random();
            // send asynchronous queries
            for ( int i = 0; i < 10; i++ ) {
                final int a = gen.nextInt(100), b = gen.nextInt(100);
                System.out.println(String.format("Asynchronous query %1d sent. Arguments: %2d, %3d", i, a, b));
                q.async("asynchMult", i, a, b);
            }

            Thread.sleep(2000);
            q.stopListener();
        } catch ( final Exception e ) {
            System.err.println(e);
        } finally {
            q.close();
        }
    }
}
```

## Interactive console
This example depicts how to create a simple interactive console for communication with a kdb+ process:

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.exxeleron.qjava.QBasicConnection;
import com.exxeleron.qjava.QConnection;
import com.exxeleron.qjava.QException;

public class Console {

    public static void main( final String[] args ) throws IOException {
        final QConnection q = new QBasicConnection(args.length >= 1 ? args[0] : "localhost", args.length >= 2 ? Integer.parseInt(args[1]) : 5001, "user",
                "pwd");
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            q.open();
            System.out.printf("conn: %1$s protocol version: %2$d\n", q, q.getProtocolVersion());

            while ( true ) {
                System.out.print("Q)");
                final String line = bufferedReader.readLine();

                if ( line.equals("\\\\") ) {
                    break;
                } else {
                    try {
                        System.out.println(": " + Utils.resultToString(q.sync(line)));
                    } catch ( final QException e ) {
                        System.err.println("`" + e.getMessage());
                    }
                }
            }
        } catch ( final QException e ) {
            System.err.println(e);
        } finally {
            q.close();
        }
    }
}
```

## Subscribing for asynchronous messages
This example shows how to create a simple subscription for asynchronous data using the `QMessageListener` interface:
> :white_check_mark: Warning:
> this sample code overwrites: .z.ts and sub functions on q process

```java
import java.io.IOException;

import com.exxeleron.qjava.QCallbackConnection;
import com.exxeleron.qjava.QErrorMessage;
import com.exxeleron.qjava.QMessage;
import com.exxeleron.qjava.QMessagesListener;

public class Subscriber {

    public static void main( final String[] args ) throws IOException {
        final int port = args.length >= 2 ? Integer.parseInt(args[1]) : 5001;
        final QCallbackConnection q = new QCallbackConnection(args.length >= 1 ? args[0] : "localhost", port, "", "");

        final QMessagesListener listener = new QMessagesListener() {

            // definition of messageListener that prints every message it gets on stdout
            public void messageReceived( final QMessage message ) {
                System.out.println(String.format("Asynchronous message received.\nmessage type: %1s size: %2d isCompressed: %3b endianess: %4s",
                        message.getMessageType(), message.getMessageSize(), message.isCompressed(), message.getEndianess()));
                System.out.println("Result: " + (message.getData().toString()));
            }

            public void errorReceived( final QErrorMessage message ) {
                System.err.println((message.getCause()));
            }
        };

        q.addMessagesListener(listener);
        try {
            q.open(); // open connection
            System.out.println("WARNING: this application overwrites: .z.ts and sub functions on q process running on port: " + port);
            System.out.println("Press <ENTER> to close application");

            q.sync("sub:{[x] .sub.h: .z.w }"); // subscription definition
            q.sync(".z.ts:{ (neg .sub.h) .z.p}"); // data generation definition
            q.sync("system \"t 500\""); // start data generation
            q.startListener(); // activate messageListener
            q.async("sub", 0); // subscription

            System.in.read();

            q.stopListener();
            q.sync("system \"t 0\""); // stop data generation
        } catch ( final Exception e ) {
            System.err.println(e);
        } finally {
            q.close();
        }
    }
}
```

## Subscribing to tick service
This example depicts how to subscribe to standard kdb+ tickerplant service:

```java
import java.io.IOException;
import java.util.Arrays;

import com.exxeleron.qjava.QCallbackConnection;
import com.exxeleron.qjava.QErrorMessage;
import com.exxeleron.qjava.QMessage;
import com.exxeleron.qjava.QMessagesListener;
import com.exxeleron.qjava.QTable;

public class TickSubscriber {

    public static void main( final String[] args ) throws IOException {
        final QCallbackConnection q = new QCallbackConnection("localhost", 9020, "usr", "pwd");
        final QMessagesListener listener = new QMessagesListener() {

            public void messageReceived( final QMessage message ) {
                final Object data = message.getData();
                if ( data instanceof Object[] ) {
                    // unpack upd message
                    final Object[] params = ((Object[]) data);
                    if ( params.length == 3 && params[0].equals("upd") && params[2] instanceof QTable ) {
                        final QTable table = (QTable) params[2];
                        for ( final QTable.Row row : table ) {
                            System.out.println(Arrays.toString(row.toArray()));
                        }
                    }
                }
            }

            public void errorReceived( final QErrorMessage message ) {
                System.err.println(Utils.resultToString(message.getCause()));
            }
        };

        q.addMessagesListener(listener);
        try {
            q.open();
            System.out.println("Press <ENTER> to close application");

            final Object response = q.sync(".u.sub", "trade", ""); // subscribe to tick
            final QTable model = (QTable) ((Object[]) response)[1]; // get table model

            q.startListener(); // activate messageListener

            System.in.read();
            q.stopListener();
        } catch ( final Exception e ) {
            System.err.println(e);
        } finally {
            q.close();
        }
    }
}
```

## Data publisher
This example shows how to stream data to the kdb+ process using standard tickerplant API:

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;

import com.exxeleron.qjava.QBasicConnection;
import com.exxeleron.qjava.QConnection;
import com.exxeleron.qjava.QException;
import com.exxeleron.qjava.QTime;

public class Publisher {

    public static void main( final String[] args ) throws IOException {
        final QConnection q = new QBasicConnection(args.length >= 1 ? args[0] : "localhost", args.length >= 2 ? Integer.parseInt(args[1]) : 5001, "", "");
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            q.open();
            System.out.printf("conn: %1$s protocol version: %2$d\n", q, q.getProtocolVersion());
            System.out.println("Press <ENTER> to close application");

            final PublisherTask pt = new PublisherTask(q);
            final Thread t = new Thread(pt, "publisher-thread");
            t.start();

            bufferedReader.readLine();
            pt.stop();
            t.join();
        } catch ( final Exception e ) {
            System.err.println(e);
        } finally {
            q.close();
        }
    }

}

class PublisherTask implements Runnable {
    private final QConnection q;
    boolean running = true;
    private final Random r;

    public PublisherTask(final QConnection q) {
        this.q = q;
        this.r = new Random(System.currentTimeMillis());
    }

    public void stop() {
        running = false;
    }

    public void run() {
        while ( running ) {
            try {
                // publish data to tick
                // function: .u.upd
                // table: ask
                q.sync(".u.upd", "ask", getAskData());
            } catch ( final QException e1 ) {
                // q error
                e1.printStackTrace();
            } catch ( final IOException e1 ) {
                // problem with connection
                running = false;
            }

            try {
                Thread.sleep(1000);
            } catch ( final InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

    private Object[] getAskData() {
        final int c = r.nextInt(10);
        final Object[] data = new Object[] { new QTime[c], new String[c], new String[c], new float[c] };

        for ( int i = 0; i < c; i++ ) {
            ((QTime[]) data[0])[i] = new QTime(new Date());
            ((String[]) data[1])[i] = "INSTR_" + r.nextInt(100);
            ((String[]) data[2])[i] = "qJava";
            ((float[]) data[3])[i] = r.nextFloat() * r.nextInt(100);
        }

        return data;
    }
}
```