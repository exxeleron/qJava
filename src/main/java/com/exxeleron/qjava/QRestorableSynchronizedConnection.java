package com.exxeleron.qjava;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread safe connector class for interfacing with the kdb+ service. Provides methods for synchronous and asynchronous
 * interaction.
 */
public class QRestorableSynchronizedConnection extends QRestorableConnection {

    private final Lock lock = new ReentrantLock();
    private AtomicBoolean connectedFlag = new AtomicBoolean(false);

    /**
     * {@inheritDoc}
     */
    public QRestorableSynchronizedConnection(final String host, final int port, final String username, final String password, final String encoding) {
        super(host, port, username, password, encoding);
    }

    /**
     * {@inheritDoc}
     */
    public QRestorableSynchronizedConnection(final String host, final int port, final String username, final String password) {
        this(host, port, username, password, "ISO-8859-1");
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void open() throws IOException, QException {
        if ( !connectedFlag.get() ) {
            if ( getHost() != null ) {
                initSocket();
                initialize();

                reader = new QReader(inputStream, getEncoding());
                writer = new QWriter(outputStream, getEncoding(), protocolVersion);

                connectedFlag.getAndSet(true);
            } else {
                throw new QConnectionException("Host cannot be null");
            }
        }
    }

    private synchronized void initSocket() throws IOException {
        connection = new Socket(getHost(), getPort());
        connection.setTcpNoDelay(true);
        inputStream = new DataInputStream(connection.getInputStream());
        outputStream = connection.getOutputStream();
    }

    private synchronized void initialize() throws IOException, QException {
        final String credentials = getPassword() != null ? String.format("%s:%s", getUsername(), getPassword()) : getUsername();
        byte[] request = (credentials + "\3\0").getBytes(getEncoding());
        final byte[] response = new byte[2];

        outputStream.write(request);
        if ( inputStream.read(response, 0, 1) != 1 ) {
            close();
            initSocket();

            request = (credentials + "\0").getBytes(getEncoding());
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
    public synchronized void close() throws IOException {
        if ( connectedFlag.get() ) {
            connection.close();
            connection = null;
            connectedFlag.getAndSet(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void reset() throws IOException, QException {
        if ( !connectedFlag.get() ) {
            connection.close();
        }
        connection = null;
        connectedFlag.getAndSet(false);
        open();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConnected() {
        return connectedFlag.get();
    }

    /**
     * {@inheritDoc}
     */
    public Object sync( final String query, final Object... parameters ) throws QException, IOException {
        QMessage response;
        lock.lock();
        try {
            query(QConnection.MessageType.SYNC, query, parameters);
            response = reader.read(false);
        } catch (IOException | QException e) {
            throw e;
        } finally {
            lock.unlock();
        }

        if ( response.getMessageType() == QConnection.MessageType.RESPONSE ) {
            return response.getData();
        } else {
            lock.lock();
            try {
                writer.write(new QException("nyi: qJava expected response message"),
                        response.getMessageType() == QConnection.MessageType.ASYNC ? QConnection.MessageType.ASYNC : QConnection.MessageType.RESPONSE);
                throw new QReaderException("Received message of type: " + response.getMessageType() + " where response was expected");
            } catch (QReaderException e) {
                throw e;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public int query( final QConnection.MessageType msgType, final String query, final Object... parameters ) throws QException, IOException {
        if(attemptReconnect) {
            testAndReopenSocket();
        }

        if ( !connectedFlag.get() ) {
            throw new IOException("Connection is not established.");
        }

        if ( parameters.length > 8 ) {
            throw new QWriterException("Too many parameters.");
        }

        if ( parameters.length == 0 ) // simple string query
        {
            lock.lock();
            try {
                return writer.write(query.toCharArray(), msgType);
            } catch (IOException | QException e) {
                throw e;
            } finally {
                lock.unlock();
            }

        } else {
            final Object[] request = new Object[parameters.length + 1];
            request[0] = query.toCharArray();

            int i = 1;
            for ( final Object param : parameters ) {
                request[i++] = param;
            }

            lock.lock();
            try {
                return writer.write(request, msgType);
            } catch (IOException | QException e) {
                throw e;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Object receive( final boolean dataOnly, final boolean raw ) throws IOException, QException {
        return dataOnly ? reader.read(raw).getData() : reader.read(raw);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Object receive() throws IOException, QException {
        return receive(true, false);
    }


}
