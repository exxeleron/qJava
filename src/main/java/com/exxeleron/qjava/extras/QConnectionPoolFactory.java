package com.exxeleron.qjava.extras;

public class QConnectionPoolFactory {
    public static QConnectionPool createPool(QConnectionPoolType poolType,
                                      String host,
                                      int port,
                                      String userName,
                                      String password,
                                      String encoding,
                                      int poolSize) {
        try {
            QConnectionPool pool = new QConnectionPoolImpl(poolType.getClazz(),host,port,userName,password,encoding);
            pool.reinitialisePool(poolSize);
            return pool;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
