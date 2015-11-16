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
import java.io.OutputStream;

/**
 * Provides serialization to q IPC protocol.
 * <p>
 * Methods of {@link QWriter} are not thread safe.
 * </p>
 */
public abstract class QWriter {

    protected OutputStream stream;
    private String encoding;
    protected ByteOutputStream writer = new ByteOutputStream();
    protected ByteOutputStream header = new ByteOutputStream(8);

    protected int messageSize;
    protected int protocolVersion = 3;

    /**
     * Sets the output stream for serialization.
     *
     * @param outputStream
     *            Output stream for serialized messages
     */
    void setStream( final OutputStream stream ) {
        this.stream = stream;
    }

    /**
     * Sets encoding for string data serialization.
     *
     * @param encoding
     *            Encoding used for serialization of string data
     */
    void setEncoding( final String encoding ) {
        this.encoding = encoding;
    }

    /**
     * Retrieves string encoding
     * 
     * @return charset name
     */
    protected String getEncoding() {
        return encoding;
    }

    /**
     * Set the kdb+ protocol version.
     *
     * @param protocolVersion
     *            kdb+ protocol version
     */
    public void setProtocolVersion( final int protocolVersion ) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * Retrieves kdb+ protocol version.
     *
     * @return {@link int} kdb+ protocol version
     */
    public int getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Serializes object to q IPC protocol and writes as a message to the output stream.
     *
     * @param obj
     *            Object to be serialized
     * @param msgType
     *            Message type
     * @return total size of the message, includes header (8 bytes) and data payload
     * @throws IOException
     * @throws QException
     */
    public int write( final Object obj, final QConnection.MessageType msgType ) throws IOException, QException {
        // serialize object
        writer.reset();
        writeObject(obj);
        messageSize = writer.count() + 8;

        // write header
        header.reset();
        header.write((byte) 1); // endianness
        header.write((byte) msgType.ordinal());
        header.writeShort((short) 0);
        header.writeInt(messageSize);

        // write message
        stream.write(header.buffer(), 0, 8);
        stream.write(writer.buffer(), 0, writer.count());

        return messageSize;
    }

    /**
     * Serializes object into an IPC stream.
     * 
     * @param obj
     *            object to be serialized
     * @throws IOException
     *             in case of IO error
     * @throws QException
     *             in case object cannot be serialized
     */
    protected abstract void writeObject( final Object obj ) throws IOException, QException;

    /**
     * Verifies whether specified q type is compatible with current protocol version.
     * 
     * @param qtype
     *            qtype to be checked
     * @throws QWriterException
     *             if specified q type is not valid in current protocol
     */
    protected void checkProtocolVersionCompatibility( final QType qtype ) throws QWriterException {
        if ( protocolVersion < 3 && (qtype == QType.GUID || qtype == QType.GUID_LIST) ) {
            throw new QWriterException("kdb+ protocol version violation: guid not supported pre kdb+ v3.0");
        }

        if ( protocolVersion < 1 && (qtype == QType.TIMESPAN || qtype == QType.TIMESPAN_LIST) ) {
            throw new QWriterException("kdb+ protocol version violation: timespan not supported pre kdb+ v2.6");
        }

        if ( protocolVersion < 1 && (qtype == QType.TIMESTAMP || qtype == QType.TIMESTAMP_LIST) ) {
            throw new QWriterException("kdb+ protocol version violation: timestamp not supported pre kdb+ v2.6");
        }
    }

}
