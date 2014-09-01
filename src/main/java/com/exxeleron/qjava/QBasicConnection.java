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
import java.net.UnknownHostException;

/**
 * Base connector class for interfacing with the kdb+ service. Provides methods for synchronous and asynchronous
 * interaction.
 */
public class QBasicConnection implements QConnection {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String encoding;

    protected int protocolVersion;

    protected Socket connection;
    protected DataInputStream inputStream;
    protected OutputStream outputStream;
    protected QReader reader;
    protected QWriter writer;

    /**
     * Initializes a new {@link QBasicConnection} instance.
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
    public QBasicConnection(final String host, final int port, final String username, final String password, final String encoding) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.encoding = encoding;
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
    public QBasicConnection(final String host, final int port, final String username, final String password) {
        this(host, port, username, password, "ISO-8859-1");
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws IOException, QException {
        if ( !isConnected() ) {
            if ( host != null ) {
                initSocket();
                initialize();

                reader = new QReader(inputStream, encoding);
                writer = new QWriter(outputStream, encoding, protocolVersion);
            } else {
                throw new QConnectionException("Host cannot be null");
            }
        }
    }

    private void initSocket() throws UnknownHostException, IOException {
        connection = new Socket(host, port);
        connection.setTcpNoDelay(true);
        inputStream = new DataInputStream(connection.getInputStream());
        outputStream = connection.getOutputStream();
    }

    private void initialize() throws IOException, QException {
        final String credentials = password != null ? String.format("%s:%s", username, password) : username;
        byte[] request = (credentials + "\3\0").getBytes(encoding);
        final byte[] response = new byte[2];

        outputStream.write(request);
        if ( inputStream.read(response, 0, 1) != 1 ) {
            close();
            initSocket();

            request = (credentials + "\0").getBytes(encoding);
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
    public void close() throws IOException {
        if ( isConnected() ) {
            connection.close();
            connection = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reset() throws IOException, QException {
        if ( connection != null ) {
            connection.close();
        }
        connection = null;
        open();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    public Object sync( final String query, final Object... parameters ) throws QException, IOException {
        query(QConnection.MessageType.SYNC, query, parameters);
        final QMessage response = reader.read(false);

        if ( response.getMessageType() == QConnection.MessageType.RESPONSE ) {
            return response.getData();
        } else {
            writer.write(new QException("nyi: qJava expected response message"),
                    response.getMessageType() == QConnection.MessageType.ASYNC ? QConnection.MessageType.ASYNC : QConnection.MessageType.RESPONSE);
            throw new QReaderException("Received message of type: " + response.getMessageType() + " where response was expected");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void async( final String query, final Object... parameters ) throws QException, IOException {
        query(QConnection.MessageType.ASYNC, query, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public int query( final QConnection.MessageType msgType, final String query, final Object... parameters ) throws QException, IOException {
        if ( connection == null ) {
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

    /**
     * {@inheritDoc}
     */
    public Object receive( final boolean dataOnly, final boolean raw ) throws IOException, QException {
        return dataOnly ? reader.read(raw).getData() : reader.read(raw);
    }

    /**
     * {@inheritDoc}
     */
    public Object receive() throws IOException, QException {
        return receive(true, false);
    }

    /**
     * Returns a String that represents the current {@link QBasicConnection}.
     * 
     * @return a String that represents the current {@link QBasicConnection}
     */
    @Override
    public String toString() {
        return String.format(":%s:%s", getHost(), getPort());
    }

    /**
     * {@inheritDoc}
     */
    public String getHost() {
        return host;
    }

    /**
     * {@inheritDoc}
     */
    public int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    public String getUsername() {
        return username;
    }

    /**
     * {@inheritDoc}
     */
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * {@inheritDoc}
     */
    public int getProtocolVersion() {
        return protocolVersion;
    }

}