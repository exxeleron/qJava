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

import java.nio.ByteOrder;

/**
 * Encapsulates the q message.
 * 
 * @author dev123
 */
public final class QMessage {

    private final ByteOrder endianess;
    private final boolean compressed;
    private final boolean raw;
    private final QConnection.MessageType messageType;
    private final int messageSize;
    private final int dataSize;

    private final Object data;

    /**
     * Creates new {@link QMessage} object.
     * 
     * @param data
     *            data payload
     * @param messageType
     *            type of the q message
     * @param endianess
     *            endianess of the data
     * @param compressed
     *            <code>true</code> if message was compressed, <code>false</code> otherwise
     * @param raw
     *            <code>true</code> if raw message was retrieved, <code>false</code> if message was parsed
     * @param messageSize
     *            size of the message
     * @param dataSize
     *            size of the data payload section
     */
    public QMessage(final Object data, final QConnection.MessageType messageType, final ByteOrder endianess, final boolean compressed, final boolean raw,
            final int messageSize, final int dataSize) {
        this.data = data;
        this.messageType = messageType;
        this.endianess = endianess;
        this.compressed = compressed;
        this.raw = raw;
        this.messageSize = messageSize;
        this.dataSize = dataSize;
    }

    /**
     * Retrieves the data payload associated with the message.
     * 
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * Indicates endianess of the message.
     * 
     * @return endianess of the message
     */
    public ByteOrder getEndianess() {
        return endianess;
    }

    /**
     * Indicates whether message was compressed.
     * 
     * @return <code>true</code> if message was compressed, <code>false</code> otherwise
     */
    public boolean isCompressed() {
        return compressed;
    }

    /**
     * Indicates whether message has been parsed.
     * 
     * @return <code>true</code> if message is returned as a raw byte data, <code>false</code> otherwise
     */
    public boolean isRaw() {
        return raw;
    }

    /**
     * Gets type of the message.
     * 
     * @return type of the message
     */
    public QConnection.MessageType getMessageType() {
        return messageType;
    }

    /**
     * Gets a total size of the message.
     * 
     * @return total size of the message
     */
    public int getMessageSize() {
        return messageSize;
    }

    /**
     * Gets a size of data section in the message.
     * 
     * @return size of data section in the message
     */
    public int getDataSize() {
        return dataSize;
    }

}