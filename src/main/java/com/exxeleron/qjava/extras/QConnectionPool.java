package com.exxeleron.qjava.extras;

import com.exxeleron.qjava.QConnection;

public interface QConnectionPool {
    /**
     * Gets the next available connection.
     *
     * Semantics of "next available" depends on subclass
     * @return the next available connection
     */
    public QConnection next();
    /**
     * Will attempt to close off current connections and create a new pool of the required size
     * @param size of pool
     */
    public void reinitialisePool(int size);
}
