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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * {@inheritDoc}
 */
public class QRestorableConnection extends QBasicConnection {

    protected boolean attemptReconnect;

    private static final char[] TEST = new char[]{' '};

    /**
     * {@inheritDoc}
     */
    public QRestorableConnection(final String host, final int port, final String username, final String password, final String encoding) {
        super(host, port, username, password, encoding);
    }

    /**
     * Initializes a new {@link QBasicConnection} instance with encoding set to "ISO-8859-1".
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
    public QRestorableConnection(final String host, final int port, final String username, final String password) {
        this(host, port, username, password, "ISO-8859-1");
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws IOException, QException {
        if ( !isConnected() ) {
            if ( getHost() != null ) {
                initSocket();
                initialize();

                reader = new QReader(inputStream, getEncoding());
                writer = new QWriter(outputStream, getEncoding(), protocolVersion);
            } else {
                throw new QConnectionException("Host cannot be null");
            }
        }
    }

    private void initSocket() throws IOException {
        connection = new Socket(getHost(), getPort());
        connection.setTcpNoDelay(true);
        inputStream = new DataInputStream(connection.getInputStream());
        outputStream = connection.getOutputStream();
    }

    private void initialize() throws IOException, QException {
        final String credentials = getPassword() != null ? String.format("%s:%s", getUsername(), getPassword()) : getUsername();
        byte[] request = (credentials + "\3\0").getBytes(getEncoding());
        final byte[] response = new byte[2];

        outputStream.write(request);
        if ( inputStream.read(response, 0, 1) != 1 ) {
            close();
            initSocket();

            request = (credentials + "\0").getBytes(getEncoding());
            outputStream.write(request);
            if ( inputStream.read(response, 0, 1) != 1 ) {
                throw new QConnectionException("Connection denied.");
            }
        }

        protocolVersion = Math.min(response[0], 3);
    }

    /**
     * {@inheritDoc}
     */
    public int query( final QConnection.MessageType msgType, final String query, final Object... parameters ) throws QException, IOException {
        if(attemptReconnect) {
            testAndReopenSocket();
        }

        if (connection == null) {
            throw new IOException("Connection is not established.");
        }

        if ( parameters.length > 8 ) {
            throw new QWriterException("Too many parameters.");
        }

        if ( parameters.length == 0 ) // simple string query
        {
            return writer.write(query.toCharArray(), msgType);
        } else {
            final Object[] request = new Object[parameters.length + 1];
            request[0] = query.toCharArray();

            int i = 1;
            for ( final Object param : parameters ) {
                request[i++] = param;
            }

            return writer.write(request, msgType);
        }
    }

    protected void testAndReopenSocket() throws QException,IOException {
        try {
            writer.write(TEST, MessageType.SYNC);
            reader.read(false);
        } catch (SocketException ex) {
            try{
                close();
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                open();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAttemptReconnect() {
        return attemptReconnect;
    }

    /**
     * {@inheritDoc}
     */
    public void setAttemptReconnect(boolean reconnect) {
        attemptReconnect = reconnect;
    }


}