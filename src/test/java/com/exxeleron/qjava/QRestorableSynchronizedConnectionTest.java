package com.exxeleron.qjava;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QRestorableSynchronizedConnectionTest {

    private QRestorableSynchronizedConnection connection;

    @Before
    public void setUp() throws Exception {
        // assume
        // [localhost] q -p -5001 -s 10
        connection = new QRestorableSynchronizedConnection("localhost",5001,null,null);
        connection.open();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testOpenAndClose() throws Exception {
        int threadCount = 100;
        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < threadCount; i++) {
            threads.add(new AnnoyingOpenCloseThread(connection));
        }
        for(Thread thread : threads) {
            thread.start();
        }

        //only exit when all threads are done
        while(true) {
            int c = 0;
            for(Thread thread : threads) {
                if(thread.isAlive()) {
                    break;
                }
                else c++;
            }
            System.out.println("Threads finished: " + c);
            if(c == threadCount) {
                break;
            }
        }
    }

    @Test
    public void testReset() throws Exception {
        int threadCount = 10; // can't have too many else will could hit limit of connections
        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < threadCount; i++) {
            threads.add(new AnnoyingResetThread(connection));
        }
        for(Thread thread : threads) {
            thread.start();
        }

        //only exit when all threads are done
        while(true) {
            int c = 0;
            for(Thread thread : threads) {
                if(thread.isAlive()) {
                    break;
                }
                else c++;
            }
            System.out.println("Threads finished: " + c);
            if(c == threadCount) {
                break;
            }
        }
    }

    @Test
    public void testSync() throws Exception {
        int threadCount = 100;
        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < threadCount; i++) {
            threads.add(new AnnoyingQueryThread(connection, true));
        }
        for(Thread thread : threads) {
            thread.start();
        }

        //only exit when all threads are done
        while(true) {
            int c = 0;
            for(Thread thread : threads) {
                if(thread.isAlive()) {
                    break;
                }
                else c++;
            }
            System.out.println("Threads finished: " + c);
            if(c == threadCount) {
                break;
            }
        }
    }

    @Test
    public void testAsync() throws Exception {
        int threadCount = 100;
        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < threadCount; i++) {
            threads.add(new AnnoyingQueryThread(connection, false));
        }
        for(Thread thread : threads) {
            thread.start();
        }

        //only exit when all threads are done
        while(true) {
            int c = 0;
            for(Thread thread : threads) {
                if(thread.isAlive()) {
                    break;
                }
                else c++;
            }
            System.out.println("Threads finished: " + c);
            if(c == threadCount) {
                break;
            }
        }
    }

    private class AnnoyingOpenCloseThread extends Thread {
        private final QConnection qConnection;

        public AnnoyingOpenCloseThread(QConnection qConnection) {
            this.qConnection = qConnection;
        }

        public void run() {
            for(int i = 0; i < 100; i++) {
                try {
                    qConnection.open();
                    Thread.sleep(10);
                    qConnection.close();
                } catch (IOException | QException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class AnnoyingResetThread extends Thread {
        private final QConnection qConnection;

        public AnnoyingResetThread(QConnection qConnection) {
            this.qConnection = qConnection;
        }

        public void run() {
            for(int i = 0; i < 100; i++) {
                try {
                    qConnection.reset();
                    Thread.sleep(10);
                } catch (IOException | QException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class AnnoyingQueryThread extends Thread {
        private final QConnection qConnection;
        private final boolean sync;

        public AnnoyingQueryThread(QConnection qConnection, boolean sync) {
            this.qConnection = qConnection;
            this.sync = sync;
        }

        public void run() {
            for(int i = 0; i < 100; i++) {
                try {
                    if(sync) {
                        qConnection.sync("{2 xexp 10?x} peach 100");
                    }
                    else {
                        qConnection.async("{2 xexp 10?x} peach 100");
                    }
                    Thread.sleep(10);
                } catch (IOException | QException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}