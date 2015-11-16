package com.exxeleron.qjava;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Convenience class for converting wrapped byte buffer to primitive types.
 */
public final class ByteInputStream {

    private byte[] buffer;
    private int position;
    private String encoding;

    private ByteInputStreamReader reader;
    private ByteInputStreamReader readerLittleEndian;
    private ByteInputStreamReader readerBigEndian;

    /**
     * Creates new {@link ByteInputStream}.
     * 
     * @param encoding
     *            encoding for symbols conversion
     * @param endianess
     *            byte order of the input stream
     */
    public ByteInputStream(final String encoding, final ByteOrder endianess) {
        this.encoding = encoding;
        this.readerLittleEndian = new ByteLittleEndianInputStream();
        this.readerBigEndian = new ByteBigEndianInputStream();

        setOrder(endianess);
    }

    /**
     * Wraps byte buffer and resets reading position.
     * 
     * @param newBuffer
     *            byte buffer to be wrapped
     */
    public void wrap( final byte[] newBuffer ) {
        buffer = newBuffer;
        position = 0;
    }

    /**
     * Copies part of the wrapped byte buffer into a new array.
     * 
     * @param dest
     *            destination buffer
     * @param start
     *            start position in the wrapped buffer
     * @param length
     *            number of bytes to be copied
     */
    public void get( final byte[] dest, final int start, final int length ) {
        System.arraycopy(buffer, position, dest, start, length);
        position += length;
    }

    /**
     * Retrieves single byte from the wrapped buffer.
     * 
     * @return the <code>byte</code>
     */
    public byte get() {
        return buffer[position++];
    }

    /**
     * Retrieves single short value from the wrapped buffer.
     * 
     * @return the <code>short</code>
     */
    public short getShort() {
        return reader.getShort();
    }

    /**
     * Retrieves single int value from the wrapped buffer.
     * 
     * @return the <code>int</code>
     */
    public int getInt() {
        return reader.getInt();
    }

    /**
     * Retrieves single long value from the wrapped buffer.
     * 
     * @return the <code>long</code>
     */
    public long getLong() {
        return reader.getLong();
    }

    /**
     * Retrieves single float value from the wrapped buffer.
     * 
     * @return the <code>float</code>
     */
    public float getFloat() {
        return Float.intBitsToFloat(getInt());
    }

    /**
     * Retrieves single double value from the wrapped buffer.
     * 
     * @return the <code>double</code>
     */
    public Double getDouble() {
        return Double.longBitsToDouble(getLong());
    }

    /**
     * Retrieves single symbol (Java {@link String}) from the byte buffer.
     * 
     * @return {@link String}
     * @throws UnsupportedEncodingException
     *             if the encoding is unsupported
     */
    public String getSymbol() throws UnsupportedEncodingException {
        final int p = position;

        for ( ; buffer[position++] != 0; ) {
            // empty;
        }
        return (p == position - 1) ? "" : new String(buffer, p, position - 1 - p, encoding);
    }

    /**
     * Retrieves single {@link UUID} from the byte buffer.
     * 
     * @return {@link UUID}
     */
    public UUID getUUID() {
        final long l1 = readerBigEndian.getLong();
        final long l2 = readerBigEndian.getLong();
        return new UUID(l1, l2);
    }

    /**
     * Retrieves byte order for the wrapped buffer.
     * 
     * @return {@link ByteOrder}
     */
    public ByteOrder getOrder() {
        return reader.getOrder();
    }

    /**
     * Sets the byte order for reading the wrapeed buffer.
     * 
     * @param endianess
     *            byte order
     */
    public void setOrder( final ByteOrder endianess ) {
        this.reader = endianess.equals(ByteOrder.LITTLE_ENDIAN) ? readerLittleEndian : readerBigEndian;
    }

    private interface ByteInputStreamReader {

        public abstract short getShort();

        public abstract int getInt();

        public abstract long getLong();

        public abstract ByteOrder getOrder();

    }

    private class ByteBigEndianInputStream implements ByteInputStreamReader {

        public short getShort() {
            final int x = buffer[position++], y = buffer[position++];
            return (short) (x << 8 | y & 0xff);
        }

        public int getInt() {
            final int x = getShort(), y = getShort();
            return x << 16 | y & 0xffff;
        }

        public long getLong() {
            final int x = getInt(), y = getInt();
            return (long) x << 32 | y & 0xffffffffL;
        }

        public ByteOrder getOrder() {
            return ByteOrder.BIG_ENDIAN;
        }

    }

    private class ByteLittleEndianInputStream implements ByteInputStreamReader {

        public short getShort() {
            final int x = buffer[position++], y = buffer[position++];
            return (short) (x & 0xff | y << 8);
        }

        public int getInt() {
            final int x = getShort(), y = getShort();
            return (x & 0xffff | y << 16);
        }

        public long getLong() {
            final int x = getInt(), y = getInt();
            return (x & 0xffffffffL | (long) y << 32);
        }

        public ByteOrder getOrder() {
            return ByteOrder.BIG_ENDIAN;
        }

    }

}