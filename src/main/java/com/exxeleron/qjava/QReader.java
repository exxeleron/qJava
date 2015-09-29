/**
 *  Copyright (c) 2011-2015 Exxeleron GmbH
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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Provides deserialization from q IPC protocol.
 * <p>
 * Methods of {@link QReader} are not thread safe.
 * </p>
 */
public abstract class QReader {

    private final static String PROTOCOL_DEBUG_ENV = "QJAVA_PROTOCOL_DEBUG";

    protected DataInputStream stream;
    protected ByteInputStream reader;
    private String encoding;

    protected byte[] header;
    protected byte[] rawData;

    /**
     * Sets the input stream for deserialization.
     * 
     * @param stream
     *            Input stream containing serialized messages
     */
    void setStream( final DataInputStream stream ) {
        this.stream = stream;
    }

    /**
     * Sets the string encoding for deserialization.
     * 
     * @param encoding
     *            Encoding used for deserialization of string data
     */
    void setEncoding( final String encoding ) {
        this.encoding = encoding;
        reader = new ByteInputStream(encoding, ByteOrder.nativeOrder());
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
     * Reads next message from the stream and returns a deserialized object.
     *
     * @param raw
     *            indicates whether reply should be parsed or return as raw data
     * @return {@link QMessage} instance encapsulating a deserialized message.
     *
     * @throws IOException
     * @throws QException
     */
    public QMessage read( final boolean raw ) throws IOException, QException {
        header = new byte[8];
        stream.readFully(header, 0, 8);
        reader.wrap(header);

        // TODO: create 2 instances per each endian and select one based on a flag
        final ByteOrder endianess = reader.get() == 0 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        final QConnection.MessageType messageType = QConnection.MessageType.getMessageType(reader.get());
        final boolean compressed = reader.get() == 1;
        reader.get(); // skip 1 byte

        reader.setOrder(endianess);
        final int messageSize = reader.getInt();
        int dataSize = Math.max(messageSize - 8, 0);

        // read message
        rawData = new byte[dataSize];
        stream.readFully(rawData, 0, dataSize);

        if ( raw ) {
            return new QMessage(rawData, messageType, endianess, compressed, raw, messageSize, dataSize);
        }

        byte[] data = rawData;
        if ( compressed ) {
            data = uncompress(rawData, endianess);
            dataSize = data.length;
        }

        reader.wrap(data);
        reader.setOrder(endianess);

        try {
            return new QMessage(readObject(), messageType, endianess, compressed, raw, messageSize, dataSize);
        } catch ( final QReaderException e ) {
            protocolDebug(e);
            throw e;
        } catch ( final RuntimeException e ) {
            protocolDebug(e);
            throw e;
        }
    }

    /**
     * Conditionally dumps IPC stream to file in case of exception while parsing.
     * 
     * @param e
     *            thrown exception
     */
    protected void protocolDebug( final Exception e ) {
        if ( System.getenv().containsKey(PROTOCOL_DEBUG_ENV) ) {
            final String debugPath = System.getenv(PROTOCOL_DEBUG_ENV) + File.separator + PROTOCOL_DEBUG_ENV + "." + System.currentTimeMillis();
            PrintWriter out = null;

            try {
                out = new PrintWriter(debugPath);
                out.write(Utils.getHex(header));
                out.write(Utils.getHex(rawData));
                out.write("\n");
                e.printStackTrace(out);
            } catch ( final Exception ex ) {
                // ignore
            } finally {
                if ( out != null ) {
                    try {
                        out.close();
                    } catch ( final Exception ex ) {
                        // ignore
                    }
                }
            }
        }
    }

    /**
     * Uncompresses the IPC stream.
     * 
     * @param compressedData
     *            compressed data
     * @param endianess
     *            endianess of the stream
     * @return uncompressed stream
     * @throws QException
     *             in case of uncompression error
     */
    protected byte[] uncompress( final byte[] compressedData, final ByteOrder endianess ) throws QException {
        // size of the uncompressed message is encoded on first 4 bytes
        // size has to be decreased by header length (8 bytes)
        final ByteBuffer byteBuffer = ByteBuffer.wrap(compressedData, 0, 4);
        byteBuffer.order(endianess);
        final int uncompressedSize = -8 + byteBuffer.getInt();

        if ( uncompressedSize <= 0 ) {
            throw new QReaderException("Error while data uncompression.");
        }

        final byte[] uncompressed = new byte[uncompressedSize];
        final int[] buffer = new int[256];
        short i = 0;
        int n = 0, r = 0, f = 0, s = 0, p = 0, d = 4;

        while ( s < uncompressedSize ) {
            if ( i == 0 ) {
                f = 0xff & compressedData[d++];
                i = 1;
            }
            if ( (f & i) != 0 ) {
                r = buffer[0xff & compressedData[d++]];
                uncompressed[s++] = uncompressed[r++];
                uncompressed[s++] = uncompressed[r++];
                n = 0xff & compressedData[d++];
                for ( int m = 0; m < n; m++ ) {
                    uncompressed[s + m] = uncompressed[r + m];
                }
            } else {
                uncompressed[s++] = compressedData[d++];
            }
            while ( p < s - 1 ) {
                buffer[(0xff & uncompressed[p]) ^ (0xff & uncompressed[p + 1])] = p++;
            }
            if ( (f & i) != 0 ) {
                p = s += n;
            }
            i *= 2;
            if ( i == 256 ) {
                i = 0;
            }
        }

        return uncompressed;
    }

    /**
     * Parses a Java object from the IPC stream.
     * 
     * @return parsed representation
     * @throws QException
     *             in case of parsing error
     * @throws IOException
     *             in case of IO error
     */
    protected abstract Object readObject() throws QException, IOException;
}
