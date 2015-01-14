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
