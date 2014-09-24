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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Provides deserialization from q IPC protocol.<br/>
 *
 * Methods of {@link QReader} are not thread safe.
 */
public final class QReader {

    private final static String PROTOCOL_DEBUG_ENV = "QJAVA_PROTOCOL_DEBUG";

    private final DataInputStream stream;
    private final String encoding;
    private final ByteInputStream reader;

    private byte[] header;
    private byte[] rawData;

    /**
     * Initializes a new {@link QReader} instance.
     *
     * @param inputStream
     *            Input stream containing serialized messages
     * @param encoding
     *            Encoding used for deserialization of string data
     */
    public QReader(final DataInputStream inputStream, final String encoding) {
        stream = inputStream;
        this.encoding = encoding;
        reader = new ByteInputStream(ByteOrder.nativeOrder());
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

        final ByteOrder endianess = reader.get() == 0 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        final QConnection.MessageType messageType = QConnection.MessageType.getMessageType(reader.get());
        final boolean compressed = reader.get() == 1;
        reader.get(); // skip 1 byte

        reader.order(endianess);
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
        reader.order(endianess);

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

    private void protocolDebug( final Exception e ) {
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

    private byte[] uncompress( final byte[] compressedData, final ByteOrder endianess ) throws QException {
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

    private Object readObject() throws QException, IOException {
        final QType qtype = QType.getQType(reader.get());

        if ( qtype == QType.GENERAL_LIST ) {
            return readGeneralList();
        } else if ( qtype == QType.ERROR ) {
            throw readError();
        } else if ( qtype == QType.DICTIONARY ) {
            return readDictionary();
        } else if ( qtype == QType.TABLE ) {
            return readTable();
        } else if ( qtype.getTypeCode() < 0 ) {
            return readAtom(qtype);
        } else if ( qtype.getTypeCode() >= QType.BOOL_LIST.getTypeCode() && qtype.getTypeCode() <= QType.TIME_LIST.getTypeCode() ) {
            return readList(qtype);
        } else if ( qtype.getTypeCode() >= QType.LAMBDA.getTypeCode() ) {
            return readFunction(qtype);
        }

        throw new QReaderException("Unable to deserialize q type: " + qtype);
    }

    @SuppressWarnings("incomplete-switch")
    private Object readAtom( final QType qtype ) throws QException, UnsupportedEncodingException {
        switch ( qtype ) {
        case BOOL:
            return reader.get() == 1 ? true : false;
        case GUID:
            return readGuid();
        case BYTE:
            return reader.get();
        case SHORT:
            return reader.getShort();
        case INT:
            return reader.getInt();
        case LONG:
            return reader.getLong();
        case FLOAT:
            return reader.getFloat();
        case DOUBLE:
            return reader.getDouble();
        case CHAR:
            return (char) reader.get();
        case SYMBOL:
            return reader.getSymbol();
        case TIMESTAMP:
            return new QTimestamp(reader.getLong());
        case MONTH:
            return new QMonth(reader.getInt());
        case DATE:
            return new QDate(reader.getInt());
        case DATETIME:
            return new QDateTime(reader.getDouble());
        case TIMESPAN:
            return new QTimespan(reader.getLong());
        case MINUTE:
            return new QMinute(reader.getInt());
        case SECOND:
            return new QSecond(reader.getInt());
        case TIME:
            return new QTime(reader.getInt());
        }

        throw new QReaderException("Unable to deserialize q type: " + qtype);
    }

    @SuppressWarnings("incomplete-switch")
    private Object readList( final QType qtype ) throws QException, UnsupportedEncodingException {
        reader.get(); // ignore attributes
        final int length = reader.getInt();

        switch ( qtype ) {
        case BOOL_LIST: {
            final boolean[] list = new boolean[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.get() == 1 ? true : false;
            }
            return list;
        }

        case GUID_LIST: {
            final UUID[] list = new UUID[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = readGuid();
            }
            return list;
        }

        case BYTE_LIST: {
            final byte[] list = new byte[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.get();
            }
            return list;
        }
        case SHORT_LIST: {
            final short[] list = new short[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getShort();
            }
            return list;
        }
        case INT_LIST: {
            final int[] list = new int[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getInt();
            }
            return list;
        }
        case LONG_LIST: {
            final long[] list = new long[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getLong();
            }
            return list;
        }
        case FLOAT_LIST: {
            final float[] list = new float[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getFloat();
            }
            return list;
        }
        case DOUBLE_LIST: {
            final double[] list = new double[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getDouble();
            }
            return list;
        }
        case STRING: {
            final byte[] buffer = new byte[length];
            reader.get(buffer, 0, length);
            return new String(buffer, encoding).toCharArray();
        }
        case SYMBOL_LIST: {
            final String[] list = new String[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getSymbol();
            }
            return list;
        }
        case TIMESTAMP_LIST: {
            final QTimestamp[] list = new QTimestamp[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QTimestamp(reader.getLong());
            }
            return list;
        }
        case MONTH_LIST: {
            final QMonth[] list = new QMonth[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QMonth(reader.getInt());
            }
            return list;
        }
        case DATE_LIST: {
            final QDate[] list = new QDate[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QDate(reader.getInt());
            }
            return list;
        }
        case DATETIME_LIST: {
            final QDateTime[] list = new QDateTime[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QDateTime(reader.getDouble());
            }
            return list;
        }
        case TIMESPAN_LIST: {
            final QTimespan[] list = new QTimespan[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QTimespan(reader.getLong());
            }
            return list;
        }
        case MINUTE_LIST: {
            final QMinute[] list = new QMinute[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QMinute(reader.getInt());
            }
            return list;
        }
        case SECOND_LIST: {
            final QSecond[] list = new QSecond[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QSecond(reader.getInt());
            }
            return list;
        }
        case TIME_LIST: {
            final QTime[] list = new QTime[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QTime(reader.getInt());
            }
            return list;
        }
        }

        throw new QReaderException("Unable to deserialize q type: " + qtype);
    }

    private UUID readGuid() {
        final ByteOrder currentOrder = reader.order();
        reader.order(ByteOrder.BIG_ENDIAN);
        final long l1 = reader.getLong();
        final long l2 = reader.getLong();
        reader.order(currentOrder);
        return new UUID(l1, l2);
    }

    private Object[] readGeneralList() throws QException, IOException {
        reader.get(); // ignore attributes
        final int length = reader.getInt();
        final Object[] list = new Object[length];

        for ( int i = 0; i < length; i++ ) {
            list[i] = readObject();
        }

        return list;
    }

    private QException readError() throws IOException {
        return new QException(reader.getSymbol());
    }

    private Object readDictionary() throws QException, IOException {
        final Object keys = readObject();
        final Object values = readObject();

        if ( keys != null && keys.getClass().isArray() && (values != null && values.getClass().isArray() || values instanceof QTable) ) {
            return new QDictionary(keys, values);
        } else if ( keys instanceof QTable && values instanceof QTable ) {
            return new QKeyedTable((QTable) keys, (QTable) values);
        }

        throw new QReaderException("Cannot create valid dictionary object from mapping: " + keys + " to " + values);
    }

    private QTable readTable() throws QException, IOException {
        reader.get(); // attributes
        reader.get(); // dict type stamp
        return new QTable((String[]) readObject(), (Object[]) readObject());
    }

    private QFunction readFunction( final QType qtype ) throws QException, IOException {
        if ( qtype == QType.LAMBDA ) {
            reader.getSymbol(); // ignore context
            final String expression = new String((char[]) readObject());
            return new QLambda(expression);
        } else if ( qtype == QType.PROJECTION ) {
            final int length = reader.getInt();
            final Object[] parameters = new Object[length];
            for ( int i = 0; i < length; i++ ) {
                parameters[i] = readObject();
            }
            return new QProjection(parameters);
        } else if ( qtype == QType.UNARY_PRIMITIVE_FUNC ) {
            final byte code = reader.get();
            return code == 0 ? null : new QFunction(qtype.getTypeCode());
        } else if ( qtype.getTypeCode() < QType.PROJECTION.getTypeCode() ) {
            reader.get(); // ignore function code
            return new QFunction(qtype.getTypeCode());
        } else if ( qtype == QType.COMPOSITION_FUNC ) {
            final int length = reader.getInt();
            final Object[] parameters = new Object[length];
            for ( int i = 0; i < length; i++ ) {
                parameters[i] = readObject();
            }
            return new QFunction(qtype.getTypeCode());
        } else {
            readObject(); // ignore function object
            return new QFunction(qtype.getTypeCode());
        }
    }

    private final class ByteInputStream {

        private byte[] buffer;
        private ByteOrder endianess;
        private int position;

        public ByteInputStream(final ByteOrder endianess) {
            this.endianess = endianess;
        }

        protected void wrap( final byte[] newBuffer ) {
            buffer = newBuffer;
            position = 0;
        }

        public void get( final byte[] dest, final int start, final int length ) {
            System.arraycopy(buffer, position, dest, start, length);
            position += length;
        }

        public byte get() {
            return buffer[position++];
        }

        public short getShort() {
            final int x = buffer[position++], y = buffer[position++];
            return (short) (endianess == ByteOrder.LITTLE_ENDIAN ? x & 0xff | y << 8 : x << 8 | y & 0xff);
        }

        public int getInt() {
            final int x = getShort(), y = getShort();
            return endianess == ByteOrder.LITTLE_ENDIAN ? x & 0xffff | y << 16 : x << 16 | y & 0xffff;
        }

        public long getLong() {
            final int x = getInt(), y = getInt();
            return endianess == ByteOrder.LITTLE_ENDIAN ? x & 0xffffffffL | (long) y << 32 : (long) x << 32 | y & 0xffffffffL;
        }

        public float getFloat() {
            return Float.intBitsToFloat(getInt());
        }

        public Double getDouble() {
            return Double.longBitsToDouble(getLong());
        }

        public ByteOrder order() {
            return endianess;
        }

        public void order( @SuppressWarnings("hiding") final ByteOrder endianess ) {
            this.endianess = endianess;
        }

        private String getSymbol() throws UnsupportedEncodingException {
            final int p = position;

            for ( ; buffer[position++] != 0; ) {
                // empty;
            }
            return (p == position - 1) ? "" : new String(buffer, p, position - 1 - p, encoding);
        }
    }

}
