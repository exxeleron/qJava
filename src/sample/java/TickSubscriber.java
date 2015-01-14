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

import com.exxeleron.qjava.QCallbackConnection;
import com.exxeleron.qjava.QErrorMessage;
import com.exxeleron.qjava.QMessage;
import com.exxeleron.qjava.QMessagesListener;
import com.exxeleron.qjava.QTable;

public class TickSubscriber {

    public static void main( final String[] args ) throws IOException {
        final QCallbackConnection q = new QCallbackConnection("localhost", 17010, "", "");
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
