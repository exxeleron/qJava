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
import java.net.UnknownHostException;

/**
 * Interface for the q connector.
 * 
 * @author dev123
 */
public interface QConnection {

    /**
     * Defines IPC message types.
     * 
     * @author dev123
     */
    public static enum MessageType {
        ASYNC,
        SYNC,
        RESPONSE;

        /**
         * Factory method for creating enum based on IPC message byte.
         * 
         * @param i
         *            byte indicating message type
         * @return {@link MessageType} matching the byte
         * 
         * @throws IllegalArgumentException
         */
        public static MessageType getMessageType( byte i ) {
            switch ( i ) {
            case 0:
                return ASYNC;
            case 1:
                return SYNC;
            case 2:
                return RESPONSE;
            default:
                throw new IllegalArgumentException("Unsupported message type.");
            }
        }
    }

    /**
     * Initializes connection with the remote q service.
     * 
     * @throws IOException
     * @throws UnknownHostException
     * @throws QException
     */
    public void open() throws IOException, QException;

    /**
     * Closes connection with the remote q service.
     * 
     * @throws IOException
     */
    public void close() throws IOException;

    /**
     * Reinitializes connection with the remote q service.
     * 
     * @throws IOException
     * @throws UnknownHostException
     * @throws QException
     */
    public void reset() throws IOException, QException;

    /**
     * Check whether connection with the remote q host has been established. Note that this function doesn't check
     * whether the connection is still active. One has to use a heartbeat mechanism in order to check whether the
     * connection is still active.
     * 
     * @return <code>true</code> if connection with remote host is established, <code>false</code> otherwise
     */
    public boolean isConnected();

    /**
     * Executes a synchronous query against the remote q service.
     * 
     * @param query
     *            Query to be executed
     * @param parameters
     *            Additional parameters
     * @return deserialized response from the remote q service
     * @throws QException
     * @throws IOException
     */
    public Object sync( String query, Object... parameters ) throws QException, IOException;

    /**
     * Executes an asynchronous query against the remote q service.
     * 
     * @param query
     *            Query to be executed
     * @param parameters
     *            Additional parameters
     * @throws QException
     * @throws IOException
     */
    public void async( String query, Object... parameters ) throws QException, IOException;

    /**
     * Reads next message from the remote q service.
     * 
     * @param dataOnly
     *            if <code>true</code> returns only data part of the message, if <code>false</code> retuns data and
     *            message meta-information encapsulated in QMessage
     * @param raw
     *            indicates whether message should be parsed to Java object or returned as an array of bytes
     * @return deserialized response from the remote q service
     * @throws IOException
     * @throws QException
     */
    public Object receive( boolean dataOnly, boolean raw ) throws IOException, QException;

    /**
     * Reads next message from the remote q service.
     * 
     * @return deserialized response from the remote q service
     * @throws IOException
     * @throws QException
     */
    public Object receive() throws IOException, QException;

    /**
     * Executes a query against the remote q service. Result of the query has to be retrieved by calling a receive
     * method.
     * 
     * @param msgType
     *            Indicates whether message should be synchronous or asynchronous
     * @param query
     *            Query to be executed
     * @param parameters
     *            Additional parameters
     * @return size of the sent message
     * @throws QException
     * @throws IOException
     */
    public int query( final MessageType msgType, final String query, final Object... parameters ) throws QException, IOException;

    /**
     * Returns the host of a remote q service.
     * 
     * @return host of remote a q service
     */
    public String getHost();

    /**
     * Returns the port of a remote q service.
     * 
     * @return post of remote a q service
     */
    public int getPort();

    /**
     * Returns username for remote authorization.
     * 
     * @return username for remote authorization
     */
    public String getUsername();

    /**
     * Returns password for remote authorization.
     * 
     * @return password for remote authorization
     */
    public String getPassword();

    /**
     * Returns encoding used for serialization/deserialization of string objects.
     * 
     * @return encoding used for serialization/deserialization of string objects
     */
    public String getEncoding();

    /**
     * Retrives version of the IPC protocol for an established connection.
     * 
     * @return protocol version
     */
    public int getProtocolVersion();

    /**
     * Returns whether this connection will attempt reconnection if database socket is broken
     * @return
     */
    public boolean isAttemptReconnect();

    /**
     * Instruct this connection to reconnect to a kdb database at least once if the socket is broken
     * @param reconnect
     */
    public void setAttemptReconnect(boolean reconnect);
}