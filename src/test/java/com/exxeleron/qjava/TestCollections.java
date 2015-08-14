/**
 *  Copyright (c) 2011-2015 Exxeleron GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.exxeleron.qjava;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Array;
import java.util.Iterator;

import org.junit.Test;

import com.exxeleron.qjava.QKeyedTable.KeyValuePair;
import com.exxeleron.qjava.QTable.Row;

public class TestCollections {

    private static QTable getTestTable() {
        final String[] columns = new String[] { "f", "i", "s" };
        return getTestTable(columns);
    }

    private static QTable getTestTable( final String[] columns ) {
        final Object[] data = new Object[] { new double[] { -1.1, 0.0, 10.32 }, new int[] { 10, 0, -2 }, new String[] { "foo", "bar", "" } };
        final QTable table = new QTable(columns, data);
        return table;
    }

    @Test
    public void testQTable() {
        final QTable t = getTestTable();

        try {
            t.getColumnIndex("unknown");
            fail("NullPointerException was expected");
        } catch ( NullPointerException e ) {
            assertTrue(true);
        } catch ( Exception e ) {
            fail("NullPointerException was expected");
        }

        assertEquals(0, t.getColumnIndex("f"));

        assertTrue(t.hasColumn("f"));
        assertFalse(t.hasColumn("unknown"));

        assertEquals(t, t);
        assertEquals(t, getTestTable());

        int i = 0;
        final Iterator<Row> it = t.iterator();

        while ( it.hasNext() ) {
            final Row row = it.next();
            final Iterator<Object> cit = row.iterator();
            int j = 0;

            while ( cit.hasNext() ) {
                final Object v = cit.next();
                assertEquals(t.get(i).get(j), v);
                j++;
            }

            assertEquals(t.getColumnsCount(), j);
            i++;
        }

        assertEquals(t.getRowsCount(), i);
    }

    @Test
    public void testQKeyedTable() {
        final QKeyedTable t = new QKeyedTable(getTestTable(), getTestTable(new String[] { "ff", "ii", "s" }));

        assertEquals(t, t);
        assertEquals(t.getKeys(), getTestTable());
        assertEquals(t.getValues(), getTestTable(new String[] { "ff", "ii", "s" }));

        assertTrue(t.hasColumn("f"));
        assertTrue(t.hasColumn("ff"));
        assertFalse(t.hasColumn("unknown"));
        
        assertEquals(0, t.getColumnIndex("f"));
        assertEquals(3, t.getColumnIndex("ff"));
        assertEquals(2, t.getColumnIndex("s"));
        
        try {
            t.getColumnIndex("unknown");
            fail("NullPointerException was expected");
        } catch ( NullPointerException e ) {
            assertTrue(true);
        } catch ( Exception e ) {
            fail("NullPointerException was expected");
        }

        int i = 0;
        final Iterator<KeyValuePair> it = t.iterator();

        while ( it.hasNext() ) {
            final KeyValuePair kv = it.next();

            Iterator<Object> cit = kv.getKey().iterator();
            int j = 0;

            while ( cit.hasNext() ) {
                final Object v = cit.next();
                assertEquals(t.getKeys().get(i).get(j), v);
                j++;
            }

            cit = kv.getValue().iterator();
            j = 0;

            while ( cit.hasNext() ) {
                final Object v = cit.next();
                assertEquals(t.getValues().get(i).get(j), v);
                j++;
            }

            i++;
        }

        assertEquals(t.getRowsCount(), i);
    }

    @Test
    public void testQDictionary() {
        final String[] keys = new String[] { "foo", "bar", "z" };
        final Object[] values = new Object[] { 1, "val", null };

        QDictionary d = new QDictionary(keys, values);
        assertEquals(d, d);
        assertEquals(d, new QDictionary(keys, values));

        int i = 0;
        Iterator<com.exxeleron.qjava.QDictionary.KeyValuePair> it = d.iterator();

        while ( it.hasNext() ) {
            com.exxeleron.qjava.QDictionary.KeyValuePair kv = it.next();

            assertEquals(Array.get(d.getKeys(), i), kv.getKey());
            assertEquals(Array.get(d.getValues(), i), kv.getValue());

            i++;
        }

        assertEquals(d.size(), i);

        d = new QDictionary(keys, getTestTable());
        assertEquals(d, d);
        assertEquals(d, new QDictionary(keys, getTestTable()));

        i = 0;
        it = d.iterator();

        while ( it.hasNext() ) {
            com.exxeleron.qjava.QDictionary.KeyValuePair kv = it.next();

            assertEquals(Array.get(d.getKeys(), i), kv.getKey());
            assertArrayEquals(((QTable) d.getValues()).get(i).toArray(), ((Row) kv.getValue()).toArray());

            i++;
        }

        assertEquals(d.size(), i);
    }

}
