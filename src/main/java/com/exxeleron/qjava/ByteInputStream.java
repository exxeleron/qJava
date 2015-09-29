package com.exxeleron.qjava;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;

/**
 * Convenience class for converting wrapped byte buffer to primitive types.
 */
public final class ByteInputStream {

    private byte[] buffer;
    private ByteOrder endianess;
    private int position;
    private String encoding;

    /**
     * Creates new {@link ByteInputStream}.
     * 
     * @param encoding
     *            encoding for symbols convertion
     * @param endianess
     *            byte order of the input stream
     */
    public ByteInputStream(final String encoding, final ByteOrder endianess) {
        this.encoding = encoding;
        this.endianess = endianess;
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
        final int x = buffer[position++], y = buffer[position++];
        return (short) (endianess == ByteOrder.LITTLE_ENDIAN ? x & 0xff | y << 8 : x << 8 | y & 0xff);
    }

    /**
     * Retrieves single int value from the wrapped buffer.
     * 
     * @return the <code>int</code>
     */
    public int getInt() {
        final int x = getShort(), y = getShort();
        return endianess == ByteOrder.LITTLE_ENDIAN ? x & 0xffff | y << 16 : x << 16 | y & 0xffff;
    }

    /**
     * Retrieves single long value from the wrapped buffer.
     * 
     * @return the <code>long</code>
     */
    public long getLong() {
        final int x = getInt(), y = getInt();
        return endianess == ByteOrder.LITTLE_ENDIAN ? x & 0xffffffffL | (long) y << 32 : (long) x << 32 | y & 0xffffffffL;
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
     * Retrieves byte order for the wrapped buffer.
     * 
     * @return {@link ByteOrder}
     */
    public ByteOrder getOrder() {
        return endianess;
    }

    /**
     * Sets the byte order for reading the wrapeed buffer.
     * 
     * @param endianess
     *            byte order
     */
    public void setOrder( final ByteOrder endianess ) {
        this.endianess = endianess;
    }

}