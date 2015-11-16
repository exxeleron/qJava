package com.exxeleron.qjava;

import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link OutputStream} wrapping resizable byte buffer. Provides convenient methods for serializing data to IPC stream.
 */
public class ByteOutputStream extends OutputStream {
    public static final int BUFFER_SIZE = 65536;
    private static final double BUFFER_GROWTH_FACTOR = 1.5;

    protected byte buffer[];

    protected int count;

    /**
     * Creates new {@link ByteOutputStream} with default size.
     */
    public ByteOutputStream() {
        buffer = new byte[BUFFER_SIZE];
    }

    /**
     * Creates new {@link ByteOutputStream} with specified size.
     * 
     * @param bufferSize
     *            initial size of the buffer
     */
    public ByteOutputStream(final int bufferSize) {
        buffer = new byte[bufferSize];
    }

    private void resizeBuffer( final int newcount ) {
        if ( newcount > buffer.length ) {
            final byte[] copy = new byte[Math.max(newcount + BUFFER_SIZE, (int) (buffer.length * BUFFER_GROWTH_FACTOR) + 1)];
            System.arraycopy(buffer, 0, copy, 0, buffer.length);
            buffer = copy;
        }
    }

    /**
     * Resets the buffer writing position.
     */
    public void reset() {
        count = 0;
    }

    /**
     * Writes the specified short to this output stream.
     * 
     * @param value
     *            the <code>short</code>
     */
    public void writeShort( final short value ) {
        writeByte((byte) value);
        writeByte((byte) (value >> 8));
    }

    /**
     * Writes the specified int to this output stream.
     * 
     * @param value
     *            the <code>int</code>
     */
    public void writeInt( final int value ) {
        writeShort((short) value);
        writeShort((short) (value >> 16));
    }

    /**
     * Writes the specified long to this output stream.
     * 
     * @param value
     *            the <code>long</code>
     */
    public void writeLong( final long value ) {
        writeInt((int) value);
        writeInt((int) (value >> 32));
    }

    /**
     * Writes the specified long to this output stream using big endian padding.
     * 
     * @param value
     *            the <code>long</code>
     */
    public void writeLongBigEndian( final long value ) {
        final byte[] arr = new byte[] { (byte) ((value >> 56) & 0xff), (byte) ((value >> 48) & 0xff), (byte) ((value >> 40) & 0xff),
                                       (byte) ((value >> 32) & 0xff), (byte) ((value >> 24) & 0xff), (byte) ((value >> 16) & 0xff),
                                       (byte) ((value >> 8) & 0xff), (byte) ((value >> 0) & 0xff) };
        for ( byte anArr : arr ) {
            writeByte(anArr);
        }
    }

    /**
     * Writes the specified float to this output stream.
     * 
     * @param value
     *            the <code>float</code>
     */
    public void writeFloat( final float value ) {
        writeInt(Float.floatToIntBits(value));
    }

    /**
     * Writes the specified double to this output stream.
     * 
     * @param value
     *            the <code>double</code>
     */
    public void writeDouble( final double value ) {
        writeLong(Double.doubleToLongBits(value));
    }

    /**
     * Writes the specified byte to this output stream.
     * 
     * @param value
     *            the <code>byte</code>
     */
    public void writeByte( final byte value ) {
        final int newcount = count + 1;
        resizeBuffer(newcount);
        buffer[count] = value;
        count = newcount;
    }

    /**
     * Writes the specified byte to this output stream.
     * 
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write( final int b ) {
        final int newcount = count + 1;
        resizeBuffer(newcount);
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
        resizeBuffer(newcount);
        System.arraycopy(b, off, buffer, count, len);
        count = newcount;
    }

    /**
     * Numbers of written bytes.
     * 
     * @return number of bytes
     */
    public int count() {
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

    /**
     * Provides direct access to underlying buffer.
     * 
     * @return buffer
     */
    public byte[] buffer() {
        return buffer;
    }

}