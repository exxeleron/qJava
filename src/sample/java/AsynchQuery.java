/**
 *  Copyright (c) 2011-2014 Exxeleron GmbH
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
