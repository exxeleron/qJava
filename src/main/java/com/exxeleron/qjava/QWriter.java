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
import java.lang.reflect.Array;
import java.util.UUID;

/**
 * Provides serialization to q IPC protocol.<br/>
 * 
 * Methods of {@link QWriter} are not thread safe.
 */
public final class QWriter {

    private final OutputStream stream;
    private ByteOutputStream writer;
    private final String encoding;

    private int messageSize;
    private final int protocolVersion;

    /**
     * Initializes a new {@link QWriter} instance.
     * 
     * @param outputStream
     *            Output stream for serialized messages
     * @param encoding
     *            Encoding used for serialization of string data
     * @param protocolVersion
     *            kdb+ protocol version
     */
    public QWriter(final OutputStream outputStream, final String encoding, int protocolVersion) {
        this.stream = outputStream;
        this.encoding = encoding;
        this.protocolVersion = protocolVersion;
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
        writer = new ByteOutputStream(calculateDataSize(obj));
        writeObject(obj);
        messageSize = writer.size() + 8;

        // write header
        @SuppressWarnings("resource")
        final ByteOutputStream header = new ByteOutputStream(8);
        header.write((byte) 1); // endianness
        header.write((byte) msgType.ordinal());
        header.writeShort((short) 0);
        header.writeInt(messageSize);

        // write message
        stream.write(header.buffer, 0, 8);
        stream.write(writer.buffer, 0, writer.count);

        return messageSize;
    }

    private static int[] atomSize = { 0, 1, 16, 0, 1, 2, 4, 8, 4, 8, 1, 0, 8, 4, 4, 8, 8, 4, 4, 4 };

    /**
     * Calculates approximation of the object size
     */
    private int calculateDataSize( final Object obj ) throws QException {
        final QType qtype = QType.getQType(obj);
        if ( qtype == QType.KEYED_TABLE ) {
            return 1 + calculateDataSize(((QKeyedTable) obj).getKeys()) + calculateDataSize(((QKeyedTable) obj).getValues());
        } else if ( qtype == QType.TABLE ) {
            return 3 + calculateDataSize(((QTable) obj).getColumns()) + calculateDataSize(((QTable) obj).getData());
        } else if ( qtype == QType.DICTIONARY ) {
            return 1 + calculateDataSize(((QDictionary) obj).getKeys()) + calculateDataSize(((QDictionary) obj).getValues());
        } else if ( qtype == QType.SYMBOL ) {
            // approximation - actual string encoding is an expensive operation
            return 2 + 2 * ((String) obj).length();
        } else if ( qtype.getTypeCode() <= QType.BOOL.getTypeCode() && qtype.getTypeCode() >= QType.TIME.getTypeCode() ) {
            return 1 + atomSize[-qtype.getTypeCode()];
        } else if ( qtype == QType.GENERAL_LIST ) {
            final Object[] list = (Object[]) obj;
            int size = 6;
            for ( final Object object : list ) {
                size += calculateDataSize(object);
            }
            return size;
        } else if ( qtype == QType.SYMBOL_LIST ) {
            final String[] list = (String[]) obj;
            int size = 6;
            for ( final String object : list ) {
                // approximation - actual string encoding is an expensive
                // operation
                size += 1 + 2 * object.length();
            }
            return size;
        } else if ( qtype.getTypeCode() >= QType.BOOL_LIST.getTypeCode() && qtype.getTypeCode() <= QType.TIME_LIST.getTypeCode() ) {
            return 6 + atomSize[qtype.getTypeCode()] * Array.getLength(obj);
        }

        return ByteOutputStream.BUFFER_SIZE;
    }

    private void writeObject( final Object obj ) throws IOException, QException {
        final QType qtype = QType.getQType(obj);

        if ( qtype == QType.STRING ) {
            writeString((char[]) obj);
        } else if ( qtype == QType.GENERAL_LIST ) {
            writeGeneralList((Object[]) obj);
        } else if ( qtype == QType.NULL_ITEM ) {
            writeNullItem();
        } else if ( qtype == QType.ERROR ) {
            writeError((Exception) obj);
        } else if ( qtype == QType.DICTIONARY ) {
            writeDictionary((QDictionary) obj);
        } else if ( qtype == QType.TABLE ) {
            writeTable((QTable) obj);
        } else if ( qtype == QType.KEYED_TABLE ) {
            writeKeyedTable((QKeyedTable) obj);
        } else if ( qtype.getTypeCode() < 0 ) {
            writeAtom(obj, qtype);
        } else if ( qtype.getTypeCode() >= QType.BOOL_LIST.getTypeCode() && qtype.getTypeCode() <= QType.TIME_LIST.getTypeCode() ) {
            writeList(obj, qtype);
        } else if ( qtype == QType.LAMBDA ) {
            writeLambda((QLambda) obj);
        } else if ( qtype == QType.PROJECTION ) {
            writeProjection((QProjection) obj);
        } else {
            throw new QWriterException("Unable to serialize q type: " + qtype);
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void writeAtom( final Object obj, final QType qtype ) throws IOException, QException {
        writer.writeByte(qtype.getTypeCode());
        switch ( qtype ) {
        case BOOL:
            writer.writeByte((byte) ((Boolean) obj ? 1 : 0));
        break;
        case GUID:
            if ( protocolVersion < 3 ) {
                throw new QWriterException("kdb+ protocol version violation: guid not supported pre kdb+ v3.0");
            }
            writeGuid((UUID) obj);
        break;
        case BYTE:
            writer.writeByte((Byte) obj);
        break;
        case SHORT:
            writer.writeShort((Short) obj);
        break;
        case INT:
            writer.writeInt((Integer) obj);
        break;
        case LONG:
            writer.writeLong((Long) obj);
        break;
        case FLOAT:
            writer.writeFloat((Float) obj);
        break;
        case DOUBLE:
            writer.writeDouble((Double) obj);
        break;
        case CHAR:
            writer.writeByte((byte) (char) (Character) obj);
        break;
        case SYMBOL:
            writeSymbol((String) obj);
        break;
        case TIMESTAMP:
            if ( protocolVersion < 1 ) {
                throw new QWriterException("kdb+ protocol version violation: timestamp not supported pre kdb+ v2.6");
            }
            writer.writeLong(((QTimestamp) obj).getValue());
        break;
        case MONTH:
            writer.writeInt(((QMonth) obj).getValue());
        break;
        case DATE:
            writer.writeInt(((QDate) obj).getValue());
        break;
        case DATETIME:
            writer.writeDouble(((QDateTime) obj).getValue());
        break;
        case TIMESPAN:
            if ( protocolVersion < 1 ) {
                throw new QWriterException("kdb+ protocol version violation: timespan not supported pre kdb+ v2.6");
            }
            writer.writeLong(((QTimespan) obj).getValue());
        break;
        case MINUTE:
            writer.writeInt(((QMinute) obj).getValue());
        break;
        case SECOND:
            writer.writeInt(((QSecond) obj).getValue());
        break;
        case TIME:
            writer.writeInt(((QTime) obj).getValue());
        break;
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void writeList( final Object obj, final QType qtype ) throws IOException, QException {
        writer.writeByte(qtype.getTypeCode());
        writer.writeByte((byte) 0); // attributes

        switch ( qtype ) {
        case BOOL_LIST: {
            if ( obj instanceof boolean[] ) {
                final boolean[] list = (boolean[]) obj;
                writer.writeInt(list.length);
                for ( final boolean a : list ) {
                    writer.writeByte((byte) (a ? 1 : 0));
                }
            } else if ( obj instanceof Boolean[] ) {
                final Boolean[] list = (Boolean[]) obj;
                writer.writeInt(list.length);
                for ( final Boolean a : list ) {
                    writer.writeByte((byte) (a ? 1 : 0));
                }
            }
            break;
        }
        case GUID_LIST: {
            if ( protocolVersion < 3 ) {
                throw new QWriterException("kdb+ protocol version violation: guid not supported pre kdb+ v3.0");
            }
            final UUID[] list = (UUID[]) obj;
            writer.writeInt(list.length);
            for ( final UUID a : list ) {
                writeGuid(a);
            }
            break;
        }
        case BYTE_LIST: {
            if ( obj instanceof byte[] ) {
                final byte[] list = (byte[]) obj;
                writer.writeInt(list.length);
                for ( final byte a : list ) {
                    writer.writeByte(a);
                }
            } else if ( obj instanceof Byte[] ) {
                final Byte[] list = (Byte[]) obj;
                writer.writeInt(list.length);
                for ( final Byte a : list ) {
                    writer.writeByte(a);
                }
            }
            break;
        }
        case SHORT_LIST: {
            if ( obj instanceof short[] ) {
                final short[] list = (short[]) obj;
                writer.writeInt(list.length);
                for ( final short a : list ) {
                    writer.writeShort(a);
                }
            } else if ( obj instanceof Short[] ) {
                final Short[] list = (Short[]) obj;
                writer.writeInt(list.length);
                for ( final Short a : list ) {
                    writer.writeShort(a);
                }
            }
            break;
        }
        case INT_LIST: {
            if ( obj instanceof int[] ) {
                final int[] list = (int[]) obj;
                writer.writeInt(list.length);
                for ( final int a : list ) {
                    writer.writeInt(a);
                }
            } else if ( obj instanceof Integer[] ) {
                final Integer[] list = (Integer[]) obj;
                writer.writeInt(list.length);
                for ( final Integer a : list ) {
                    writer.writeInt(a);
                }
            }
            break;
        }
        case LONG_LIST: {
            if ( obj instanceof long[] ) {
                final long[] list = (long[]) obj;
                writer.writeInt(list.length);
                for ( final long a : list ) {
                    writer.writeLong(a);
                }
            } else if ( obj instanceof Long[] ) {
                final Long[] list = (Long[]) obj;
                writer.writeInt(list.length);
                for ( final Long a : list ) {
                    writer.writeLong(a);
                }
            }
            break;
        }
        case FLOAT_LIST: {
            if ( obj instanceof float[] ) {
                final float[] list = (float[]) obj;
                writer.writeInt(list.length);
                for ( final float a : list ) {
                    writer.writeFloat(a);
                }
            } else if ( obj instanceof Float[] ) {
                final Float[] list = (Float[]) obj;
                writer.writeInt(list.length);
                for ( final Float a : list ) {
                    writer.writeFloat(a);
                }
            }
            break;
        }
        case DOUBLE_LIST: {
            if ( obj instanceof double[] ) {
                final double[] list = (double[]) obj;
                writer.writeInt(list.length);
                for ( final double a : list ) {
                    writer.writeDouble(a);
                }
            } else if ( obj instanceof Double[] ) {
                final Double[] list = (Double[]) obj;
                writer.writeInt(list.length);
                for ( final Double a : list ) {
                    writer.writeDouble(a);
                }
            }
            break;
        }
        case SYMBOL_LIST: {
            final String[] list = (String[]) obj;
            writer.writeInt(list.length);
            for ( final String a : list ) {
                writeSymbol(a);
            }
            break;
        }
        case TIMESTAMP_LIST: {
            if ( protocolVersion < 1 ) {
                throw new QWriterException("kdb+ protocol version violation: timestamp not supported pre kdb+ v2.6");
            }
            final QTimestamp[] list = (QTimestamp[]) obj;
            writer.writeInt(list.length);
            for ( final QTimestamp a : list ) {
                writer.writeLong(a.getValue());
            }
            break;
        }
        case MONTH_LIST: {
            final QMonth[] list = (QMonth[]) obj;
            writer.writeInt(list.length);
            for ( final QMonth a : list ) {
                writer.writeInt(a.getValue());
            }
            break;
        }
        case DATE_LIST: {
            final QDate[] list = (QDate[]) obj;
            writer.writeInt(list.length);
            for ( final QDate a : list ) {
                writer.writeInt(a.getValue());
            }
            break;
        }
        case DATETIME_LIST: {
            final QDateTime[] list = (QDateTime[]) obj;
            writer.writeInt(list.length);
            for ( final QDateTime a : list ) {
                writer.writeDouble(a.getValue());
            }
            break;
        }
        case TIMESPAN_LIST: {
            if ( protocolVersion < 1 ) {
                throw new QWriterException("kdb+ protocol version violation: timespan not supported pre kdb+ v2.6");
            }
            final QTimespan[] list = (QTimespan[]) obj;
            writer.writeInt(list.length);
            for ( final QTimespan a : list ) {
                writer.writeLong(a.getValue());
            }
            break;
        }
        case MINUTE_LIST: {
            final QMinute[] list = (QMinute[]) obj;
            writer.writeInt(list.length);
            for ( final QMinute a : list ) {
                writer.writeInt(a.getValue());
            }
            break;
        }
        case SECOND_LIST: {
            final QSecond[] list = (QSecond[]) obj;
            writer.writeInt(list.length);
            for ( final QSecond a : list ) {
                writer.writeInt(a.getValue());
            }
            break;
        }
        case TIME_LIST: {
            final QTime[] list = (QTime[]) obj;
            writer.writeInt(list.length);
            for ( final QTime a : list ) {
                writer.writeInt(a.getValue());
            }
            break;
        }
        }
    }

    private void writeGeneralList( final Object[] list ) throws IOException, QException {
        writer.writeByte(QType.GENERAL_LIST.getTypeCode());
        writer.writeByte((byte) 0); // attributes
        writer.writeInt(list.length);
        for ( final Object obj : list ) {
            writeObject(obj);
        }
    }

    private void writeSymbol( final String s ) throws IOException {
        writer.write(s.getBytes(encoding));
        writer.writeByte((byte) 0);
    }

    private void writeGuid( final UUID obj ) throws QException {
        if ( protocolVersion < 3 ) {
            throw new QWriterException("kdb+ protocol version violation: Guid not supported pre kdb+ v3.0");
        }

        writer.writeLongBigEndian(obj.getMostSignificantBits());
        writer.writeLongBigEndian(obj.getLeastSignificantBits());
    }

    private void writeString( final char[] s ) throws IOException {
        writer.writeByte(QType.STRING.getTypeCode());
        writer.writeByte((byte) 0); // attributes
        final byte[] encoded = String.valueOf(s).getBytes(encoding);
        writer.writeInt(encoded.length);
        writer.write(encoded);
    }

    private void writeNullItem() {
        writer.writeByte(QType.NULL_ITEM.getTypeCode());
        writer.writeByte((byte) 0);
    }

    private void writeError( final Exception e ) throws IOException {
        writer.writeByte(QType.ERROR.getTypeCode());
        writeSymbol(e.getMessage());
    }

    private void writeDictionary( final QDictionary d ) throws IOException, QException {
        writer.writeByte(QType.DICTIONARY.getTypeCode());
        writeObject(d.getKeys());
        writeObject(d.getValues());
    }

    private void writeTable( final QTable t ) throws IOException, QException {
        writer.writeByte(QType.TABLE.getTypeCode());
        writer.writeByte((byte) 0); // attributes
        writer.writeByte(QType.DICTIONARY.getTypeCode());
        writeObject(t.getColumns());
        writeObject(t.getData());
    }

    private void writeKeyedTable( final QKeyedTable t ) throws IOException, QException {
        writer.writeByte(QType.KEYED_TABLE.getTypeCode());
        writeObject(t.getKeys());
        writeObject(t.getValues());
    }

    private void writeLambda( final QLambda l ) throws IOException {
        writer.writeByte(QType.LAMBDA.getTypeCode());
        writer.writeByte((byte) 0);
        writeString(l.getExpression().toCharArray());
    }
    
    private void writeProjection( final QProjection p ) throws IOException, QException {
        writer.writeByte(QType.PROJECTION.getTypeCode());
        final int length = p.getParameters().length;
        writer.writeInt(length);
        
        for ( int i = 0; i < length; i++ ) {
            writeObject(p.getParameters()[i]);
        }
    }

    static class ByteOutputStream extends OutputStream {
        private static final int BUFFER_SIZE = 128;

        protected byte buffer[];

        protected int count;

        ByteOutputStream() {
            buffer = new byte[BUFFER_SIZE];
        }

        ByteOutputStream(final int bufferSize) {
            buffer = new byte[bufferSize];
        }

        public void writeShort( final short value ) {
            writeByte((byte) value);
            writeByte((byte) (value >> 8));
        }

        public void writeInt( final int value ) {
            writeShort((short) value);
            writeShort((short) (value >> 16));
        }

        public void writeLong( final long value ) {
            writeInt((int) value);
            writeInt((int) (value >> 32));
        }

        public void writeLongBigEndian( final long value ) {
            final byte[] arr = new byte[] { (byte) ((value >> 56) & 0xff), (byte) ((value >> 48) & 0xff), (byte) ((value >> 40) & 0xff),
                                           (byte) ((value >> 32) & 0xff), (byte) ((value >> 24) & 0xff), (byte) ((value >> 16) & 0xff),
                                           (byte) ((value >> 8) & 0xff), (byte) ((value >> 0) & 0xff) };
            for (byte anArr : arr) {
                writeByte(anArr);
            }
        }

        public void writeFloat( final float value ) {
            writeInt(Float.floatToIntBits(value));
        }

        public void writeDouble( final double value ) {
            writeLong(Double.doubleToLongBits(value));
        }

        public void writeByte( final byte value ) {
            final int newcount = count + 1;
            if ( newcount > buffer.length ) {
                final byte[] copy = new byte[count + BUFFER_SIZE];
                System.arraycopy(buffer, 0, copy, 0, buffer.length);
                buffer = copy;
            }
            buffer[count] = value;
            count = newcount;
        }

        @Override
        public void write( final int b ) {
            final int newcount = count + 1;
            if ( newcount > buffer.length ) {
                final byte[] copy = new byte[count + BUFFER_SIZE];
                System.arraycopy(buffer, 0, copy, 0, buffer.length);
                buffer = copy;
            }
            buffer[count] = (byte) b;
            count = newcount;
        }

        @Override
        public void write( final byte b[], final int off, final int len ) {
            if ( (off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0) ) {
                throw new IndexOutOfBoundsException("Attempt to write outside of the buffer. Offset: " + off + ", Size: " + len + ", Buffer: " + b.length + ".");
            } else if ( len == 0 ) {
                return;
            }
            final int newcount = count + len;
            if ( newcount > buffer.length ) {
                final byte[] copy = new byte[Math.max(count + BUFFER_SIZE, newcount)];
                System.arraycopy(buffer, 0, copy, 0, buffer.length);
                buffer = copy;
            }
            System.arraycopy(b, off, buffer, count, len);
            count = newcount;
        }

        public int size() {
            return count;
        }

        @Override
        public void close() throws IOException {
            //
        }

        /**
         * Creates copy of internal buffer.
         * 
         * @return copy of the internal buffer as byte[]
         */
        public byte[] toByteArray() {
            final byte[] copy = new byte[count];
            System.arraycopy(buffer, 0, copy, 0, Math.min(buffer.length, count));
            return copy;
        }

    }
}
