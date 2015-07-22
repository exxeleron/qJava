package com.exxeleron.qjava;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 04/06/2015
 */
public class TestReconnect {
    private QRestorableConnection connection;

    @Before
    public void setUp() throws Exception {
        // assume
        // [localhost] q -p -5001 -s 10
        connection = new QRestorableConnection("localhost",5001,null,null);
        connection.setAttemptReconnect(true);
        connection.open();
    }

    @Test
    public void testReconnect() throws Exception{
        Assert.assertTrue(connection.isConnected());

        Assert.assertTrue(2L == (Long)connection.sync("1+1"));

        try {
            connection.sync("@[{hclose each key[.z.W]};::;{}]");
        }
        catch (Exception ex){}

        Assert.assertTrue(2L == (Long) connection.sync("1+1"));
    }
}
