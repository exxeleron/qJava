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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

public class TestQWriter {

    @Test
    public void testSerialization() throws IOException, QException {
        final QExpressions qe = new QExpressions("src/test/resources/QExpressions.out");

        for ( final String expr : qe.getExpressions() ) {
            serializeObject(qe.getReferenceObject(expr), qe, expr);

            if ( qe.hasReferenceObjectAlt(expr) ) {
                serializeObject(qe.getReferenceObjectAlt(expr), qe, expr);
            }
        }
    }

    protected void serializeObject( final Object referenceObject, final QExpressions qe, final String expr ) throws IOException, QException,
            ArrayComparisonFailure {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final QWriter writer = new QWriter(stream, "ISO-8859-1", 3);
        writer.write(referenceObject, QConnection.MessageType.SYNC);

        final byte[] out = stream.toByteArray();

        assertArrayEquals("Serialization failed for q expression: " + expr, qe.getBinaryExpression(expr), copyOfRange(out, 8, out.length));
    }

    public static byte[] copyOfRange( final byte[] original, final int from, final int to ) {
        final int newLength = to - from;
        if ( newLength < 0 ) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        final byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }
}
