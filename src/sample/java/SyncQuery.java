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
import java.util.Arrays;

import com.exxeleron.qjava.QBasicConnection;
import com.exxeleron.qjava.QConnection;
import com.exxeleron.qjava.QMessage;
import com.exxeleron.qjava.QConnection.MessageType;
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
