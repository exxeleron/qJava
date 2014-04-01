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
