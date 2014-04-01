/**
 *  Copyright (c) 2011-2014 Exxeleron GmbH
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
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestQReader {

    private static final double DELTA = 0.1;

    @Test
    public void testDeserialization() throws IOException, QException {
        final QExpressions qe = new QExpressions("src/test/resources/QExpressions.out");

        for ( final String expr : qe.getExpressions() ) {
            final QWriter.ByteOutputStream writer = new QWriter.ByteOutputStream();
            final byte[] binaryExpr = qe.getBinaryExpression(expr);
            writer.writeByte((byte) 1); // little endian
            writer.writeByte((byte) 0);
            writer.writeByte((byte) 0);
            writer.writeByte((byte) 0);
            writer.writeInt(binaryExpr.length + 8);
            writer.write(binaryExpr);
            writer.flush();

            final QReader reader = new QReader(new DataInputStream(new ByteArrayInputStream(writer.toByteArray())), "ISO-8859-1");

            try {
                final Object obj = reader.read(false).getData();

                if ( obj != null && obj.getClass().isArray() ) {
                    arrayEquals("Deserialization failed for q expression: " + expr, qe.getReferenceObject(expr), obj);
                } else {
                    assertEquals("Deserialization failed for q expression: " + expr, qe.getReferenceObject(expr), obj);
                }
            } catch ( final QException e ) {
                final Object ref = qe.getReferenceObject(expr);
                if ( ref instanceof QException ) {
                    assertEquals("Deserialization failed for q expression: " + expr, ((QException) ref).getMessage(), e.getMessage());
                } else {
                    throw e;
                }
            } finally {
                writer.close();
            }
        }
    }

    @Test
    public void testCompressedDeserialization() throws IOException, QException {
        final QExpressions qe = new QExpressions("src/test/resources/QCompressedExpressions.out");
        final Map<String, Object> reference = new HashMap<String, Object>();

        final String[] q1000 = new String[1000];
        final Object[] q200 = new Object[] { new int[200], new int[200], new String[200] };
        for ( int i = 0; i < q1000.length; i++ ) {
            q1000[i] = "q";
        }
        for ( int i = 0; i < 200; i++ ) {
            ((int[]) q200[0])[i] = i;
            ((int[]) q200[1])[i] = i + 25;
            ((String[]) q200[2])[i] = "a";
        }

        reference.put("1000#`q", q1000);
        reference.put("([] q:1000#`q)", new QTable(new String[] { "q" }, new Object[] { q1000 }));
        reference.put("([] a:til 200;b:25+til 200;c:200#`a)", new QTable(new String[] { "a", "b", "c" }, q200));

        for ( final String expr : qe.getExpressions() ) {
            final QWriter.ByteOutputStream writer = new QWriter.ByteOutputStream();
            final byte[] binaryExpr = qe.getBinaryExpression(expr);
            writer.writeByte((byte) 1); // little endian
            writer.writeByte((byte) 0);
            writer.writeByte((byte) 1); // compressed
            writer.writeByte((byte) 0);
            writer.writeInt(binaryExpr.length + 8);
            writer.write(binaryExpr);
            writer.flush();

            final QReader reader = new QReader(new DataInputStream(new ByteArrayInputStream(writer.toByteArray())), "ISO-8859-1");

            final Object obj = reader.read(false).getData();

            if ( obj != null && obj.getClass().isArray() ) {
                arrayEquals("Deserialization failed for q expression: " + expr, reference.get(expr), obj);
            } else {
                assertEquals("Deserialization failed for q expression: " + expr, reference.get(expr), obj);
            }

            writer.close();
        }
    }

    private static void arrayEquals( final String message, final Object ref, final Object obj ) {
        if ( obj instanceof Object[] && ref instanceof Object[] ) {
            assertArrayEquals(message, (Object[]) ref, (Object[]) obj);
        } else if ( obj instanceof byte[] && ref instanceof byte[] ) {
            assertArrayEquals(message, (byte[]) ref, (byte[]) obj);
        } else if ( obj instanceof short[] && ref instanceof short[] ) {
            assertArrayEquals(message, (short[]) ref, (short[]) obj);
        } else if ( obj instanceof int[] && ref instanceof int[] ) {
            assertArrayEquals(message, (int[]) ref, (int[]) obj);
        } else if ( obj instanceof long[] && ref instanceof long[] ) {
            assertArrayEquals(message, (long[]) ref, (long[]) obj);
        } else if ( obj instanceof char[] && ref instanceof char[] ) {
            assertArrayEquals(message, (char[]) ref, (char[]) obj);
        } else if ( obj instanceof float[] && ref instanceof float[] ) {
            assertEquals(((float[]) ref).length, ((float[]) obj).length);
            for ( int i = 0; i < ((float[]) ref).length; i++ ) {
                assertEquals(((float[]) ref)[i], ((float[]) obj)[i], DELTA);
            }
        } else if ( obj instanceof double[] && ref instanceof double[] ) {
            assertEquals(((double[]) ref).length, ((double[]) obj).length);
            for ( int i = 0; i < ((double[]) ref).length; i++ ) {
                assertEquals(((double[]) ref)[i], ((double[]) obj)[i], DELTA);
            }
        } else if ( obj instanceof boolean[] && ref instanceof boolean[] ) {
            assertEquals(((boolean[]) ref).length, ((boolean[]) obj).length);
            for ( int i = 0; i < ((boolean[]) ref).length; i++ ) {
                assertEquals(((boolean[]) ref)[i], ((boolean[]) obj)[i]);
            }
        } else {
            fail("Array type mismatch for: " + message);
        }
    }

}
