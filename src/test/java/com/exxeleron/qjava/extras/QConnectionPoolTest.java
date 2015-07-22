package com.exxeleron.qjava.extras;

import org.junit.Assert;
import org.junit.Test;

public class QConnectionPoolTest {


    @Test
    public void testNext() throws Exception {
        int size = 101;
        QConnectionPool pool = QConnectionPoolFactory.createPool(QConnectionPoolType.SYNCHRONIZED, "localhost", 5001, null, null, null,size);

        for(int i = 0; i < size*2; i++) {
            pool.next().sync("t:"+i);
        }

        Assert.assertTrue(((Long)pool.next().sync("t")) == (size*2)-1);
    }
}