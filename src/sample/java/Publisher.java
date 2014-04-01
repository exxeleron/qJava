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
