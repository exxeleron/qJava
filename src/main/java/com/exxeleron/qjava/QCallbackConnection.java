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
package com.exxeleron.qjava;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The {@link QCallbackConnection}, in addition to {@link QBasicConnection}, provides an internal thread-based mechanism
 * for asynchronous subscription.
 * 
 * Methods of {@link QCallbackConnection} are not thread safe.
 */
public class QCallbackConnection extends QBasicConnection {

    protected QListener messageListener;
    protected Thread listenerThread;
    final CopyOnWriteArraySet<QMessagesListener> messagesListeners;

    /**
     * Initializes a new QCallbackConnection instance.
     * 
     * @param host
     *            Host of remote q service
     * @param port
     *            Port of remote q service
     * @param username
     *            Username for remote authorization
     * @param password
     *            Password for remote authorization
     * @param encoding
     *            Encoding used for serialization/deserialization of string objects
     */
    public QCallbackConnection(final String host, final int port, final String username, final String password, final String encoding) {
        super(host, port, username, password, encoding);

        this.messagesListeners = new CopyOnWriteArraySet<QMessagesListener>();
    }

    /**
     * Initializes a new QCallbackConnection instance.
     * 
     * @param host
     *            Host of remote q service
     * @param port
     *            Port of remote q service
     * @param username
     *            Username for remote authorization
     * @param password
     *            Password for remote authorization
     */
    public QCallbackConnection(final String host, final int port, final String username, final String password) {
        this(host, port, username, password, "ISO-8859-1");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if ( connection != null ) {
            stopListener();
            messagesListeners.clear();
        }
        super.close();
    }

    /**
     * Spawns a new thread which listens for asynchronous messages from the remote q host. If a messageListener thread
     * already exists, nothing happens.
     */
    public synchronized void startListener() {
        startListener("qJava-listener" + this.toString());
    }

    /**
     * Spawns a new thread which listens for asynchronous messages from the remote q host. If a messageListener thread
     * already exists, nothing happens.
     * 
     * @param threadName
     *            listener thread name
     * 
     */
    public synchronized void startListener( final String threadName ) {
        if ( messageListener == null ) {
            messageListener = new QListener();
            listenerThread = new Thread(messageListener, threadName);
            listenerThread.start();
        }
    }

    /**
     * Indicates that a messageListener thread should stop. The messageListener thread is stopped after receiving next
     * message from the remote q host. If a messageListener doesn't exists, nothing happens.
     */
    public synchronized void stopListener() {
        if ( messageListener != null ) {
            messageListener.running = false;
            messageListener = null;
            try {
                listenerThread.join(500);
            } catch ( final InterruptedException e ) {
                // ignore
            }
        }
    }

    /**
     * Registers messageListener so that it will receive {@link QMessage} when data from kdb+ has been received.
     * 
     * @param listener
     *            a {@link QMessagesListener} to be registered
     */
    public void addMessagesListener( final QMessagesListener listener ) {
        messagesListeners.add(listener);
    }

    /**
     * Unregisters messageListener so that it will no longer receive {@link QMessage}.
     * 
     * @param listener
     *            a {@link QMessagesListener} to be unregistered
     */
    public void removeMessagesListener( final QMessagesListener listener ) {
        messagesListeners.remove(listener);
    }

    /**
     * Support for reporting incoming messages from kdb+ services.
     * 
     * @param message
     *            a message to be distributed among messages listeners
     */
    protected void fireMessageReceivedEvent( final QMessage message ) {
        for ( final QMessagesListener listener : messagesListeners ) {
            listener.messageReceived(message);
        }
    }

    /**
     * Support for reporting incoming messages from kdb+ services.
     * 
     * @param message
     *            a message to be distributed among messages listeners
     */
    protected void fireErrorReceivedEvent( final QErrorMessage message ) {
        for ( final QMessagesListener listener : messagesListeners ) {
            listener.errorReceived(message);
        }
    }

    class QListener implements Runnable {

        boolean running = true;

        public void run() {
            while ( running && isConnected() ) {
                try {
                    final QMessage message = reader.read(false);
                    fireMessageReceivedEvent(message);
                } catch ( final QException e ) {
                    fireErrorReceivedEvent(new QErrorMessage(e));
                } catch ( final Exception e ) {
                    fireErrorReceivedEvent(new QErrorMessage(e));
                    running = false;
                    break;
                }
            }
        }
    }
}
