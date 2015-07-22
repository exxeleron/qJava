package com.exxeleron.qjava.extras;

import com.exxeleron.qjava.QBasicConnection;
import com.exxeleron.qjava.QConnection;
import com.exxeleron.qjava.QRestorableConnection;
import com.exxeleron.qjava.QRestorableSynchronizedConnection;

public enum QConnectionPoolType {
    BASIC(QBasicConnection.class),
    RESTORABLE(QRestorableConnection.class),
    SYNCHRONIZED(QRestorableSynchronizedConnection.class);

    private final Class<? extends QConnection> clazz;
    private QConnectionPoolType(Class<? extends QConnection> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends QConnection> getClazz() {
        return clazz;
    }
}
