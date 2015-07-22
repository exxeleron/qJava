package com.exxeleron.qjava.extras;

import com.exxeleron.qjava.QConnection;
import com.exxeleron.qjava.QException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Simple round-robin connection pool
 */
public class QConnectionPoolImpl implements QConnectionPool {

    private Constructor<? extends QConnection> constructor = null;

    private final Class<? extends QConnection> clazz;
    private final String host;
    private final int port;
    private final String userName;
    private final String password;
    private final String encoding;

    private QConnection[] currentPool;
    private int currentIndex = -1;

    public QConnectionPoolImpl(Class<? extends QConnection> clazz,
                               String host,
                               int port,
                               String userName,
                               String password,
                               String encoding) throws NoSuchMethodException {
        this.clazz = clazz;
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.encoding = encoding;

        cacheConstructor(clazz);
    }

    private void cacheConstructor(Class<? extends QConnection> clazz) throws NoSuchMethodException {
        if(encoding != null && encoding.length() != 0) {
            constructor = clazz.getConstructor(String.class, Integer.TYPE, String.class, String.class, String.class);
        }
        else {
            constructor = clazz.getConstructor(String.class, Integer.TYPE, String.class, String.class);
        }
    }

    private QConnection createInstance() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if(encoding != null) {
            return constructor.newInstance(host,port,userName,password,encoding);
        }
        else {
            return constructor.newInstance(host,port,userName,password);
        }
    }

    @Override
    public synchronized QConnection next() throws IllegalStateException{
        if(currentPool == null || currentPool.length == 0) {
            throw new IllegalStateException("Pool not initialised");
        }

        // could use %
        if(currentIndex > currentPool.length-1) {
            currentIndex = 0;
        }

        return currentPool[currentIndex++];
    }

    @Override
    public synchronized void reinitialisePool(int size) {
        if (size > 1024) {//default handle limit
            throw new IllegalArgumentException("handle limit >1024");
        }

        if (currentPool != null) {
            for (int i = 0; i < currentPool.length; i++) {
                try {
                    currentPool[i].close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        currentPool = new QConnection[size];
        for (int i = 0; i < currentPool.length; i++) {

            try {
                currentPool[i] = createInstance();
                currentPool[i].open();
            } catch (IOException | QException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                currentPool = new QConnection[0];
                return;
            }

            currentIndex = 0;
        }
    }
}
