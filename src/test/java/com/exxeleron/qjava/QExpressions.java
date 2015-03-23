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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Utility class for testing serialization and deserialization.
 */
class QExpressions {

    private final Map<String, Object> reference = new LinkedHashMap<String, Object>();
    private final Map<String, Object> referenceSerializationAlt = new LinkedHashMap<String, Object>();

    private void initExpressions() throws QException {
        reference.put("1+`", new QException("type"));
        reference.put("()", new Object[0]);
        reference.put("::", null);
        reference.put("1", 1L);
        reference.put("1i", 1);
        reference.put("-234h", (short) -234);
        reference.put("1b", true);
        reference.put("0x2a", (byte) 0x2a);
        reference.put("89421099511627575j", 89421099511627575L);
        reference.put("3.234", 3.234);
        reference.put("5.5e", (float) 5.5);
        reference.put("\"0\"", '0');
        reference.put("\"abc\"", "abc".toCharArray());
        reference.put("\"\"", "".toCharArray());
        reference.put("\"quick brown fox jumps over a lazy dog\"", "quick brown fox jumps over a lazy dog".toCharArray());
        reference.put("`abc", "abc");
        reference.put("`quickbrownfoxjumpsoveralazydog", "quickbrownfoxjumpsoveralazydog");
        reference.put("2000.01.04D05:36:57.600", new QTimestamp(279417600000000L));
        reference.put("2001.01m", new QMonth(12));
        reference.put("2001.01.01", new QDate(366));
        reference.put("2000.05.01", new QDate(121));
        reference.put("2000.01.04T05:36:57.600", new QDateTime(3.234));
        reference.put("0D05:36:57.600", new QTimespan(20217600000000L));
        reference.put("12:01", new QMinute(721));
        reference.put("12:05:00", new QSecond(43500));
        reference.put("12:04:59.123", new QTime(43499123));
        reference.put("0b", QType.getQNull(QType.BOOL));
        reference.put("0x00", QType.getQNull(QType.BYTE));
        reference.put("0Nh", QType.getQNull(QType.SHORT));
        reference.put("0N", QType.getQNull(QType.LONG));
        reference.put("0Ni", QType.getQNull(QType.INT));
        reference.put("0Nj", QType.getQNull(QType.LONG));
        reference.put("0Ne", QType.getQNull(QType.FLOAT));
        reference.put("0n", QType.getQNull(QType.DOUBLE));
        reference.put("\" \"", QType.getQNull(QType.CHAR));
        reference.put("`", QType.getQNull(QType.SYMBOL));
        reference.put("0Np", QType.getQNull(QType.TIMESTAMP));
        reference.put("0Nm", QType.getQNull(QType.MONTH));
        reference.put("0Nd", QType.getQNull(QType.DATE));
        reference.put("0Nz", QType.getQNull(QType.DATETIME));
        reference.put("0Nn", QType.getQNull(QType.TIMESPAN));
        reference.put("0Nu", QType.getQNull(QType.MINUTE));
        reference.put("0Nv", QType.getQNull(QType.SECOND));
        reference.put("0Nt", QType.getQNull(QType.TIME));
        reference.put("(0b;1b;0b)", new boolean[] { false, true, false });
        referenceSerializationAlt.put("(0b;1b;0b)", new Boolean[] { false, true, false });
        reference.put("(0x01;0x02;0xff)", new byte[] { 1, 2, (byte) 255 });
        referenceSerializationAlt.put("(0x01;0x02;0xff)", new Byte[] { 1, 2, (byte) 255 });
        reference.put("(1h;2h;3h)", new short[] { 1, 2, 3 });
        referenceSerializationAlt.put("(1h;2h;3h)", new Short[] { 1, 2, 3 });
        reference.put("1 2 3", new long[] { 1, 2, 3 });
        referenceSerializationAlt.put("1 2 3", new Long[] { 1L, 2L, 3L });
        reference.put("(1i;2i;3i)", new int[] { 1, 2, 3 });
        referenceSerializationAlt.put("(1i;2i;3i)", new Integer[] { 1, 2, 3 });
        reference.put("(1j;2j;3j)", new long[] { 1, 2, 3 });
        referenceSerializationAlt.put("(1j;2j;3j)", new Long[] { 1L, 2L, 3L });
        reference.put("(5.5e; 8.5e)", new float[] { 5.5f, 8.5f });
        referenceSerializationAlt.put("(5.5e; 8.5e)", new Float[] { 5.5f, 8.5f });
        reference.put("3.23 6.46", new double[] { 3.23, 6.46 });
        referenceSerializationAlt.put("3.23 6.46", new Double[] { 3.23, 6.46 });
        reference.put("(1;`bcd;\"0bc\";5.5e)", new Object[] { 1L, "bcd", "0bc".toCharArray(), (float) 5.5 });
        reference.put("(42;::;`foo)", new Object[] { 42L, null, "foo" });
        reference.put("(enlist 1h; 2; enlist 3j)", new Object[] { new short[] { 1 }, 2L, new long[] { 3 } });
        reference.put("`the`quick`brown`fox", new String[] { "the", "quick", "brown", "fox" });
        reference.put("``quick``fox", new String[] { "", "quick", "", "fox" });
        reference.put("``", new String[] { "", "" });
        reference.put("(\"quick\"; \"brown\"; \"fox\"; \"jumps\"; \"over\"; \"a lazy\"; \"dog\")", new Object[] {"quick".toCharArray(), "brown".toCharArray(), "fox".toCharArray(), "jumps".toCharArray(), "over".toCharArray(), "a lazy".toCharArray(), "dog".toCharArray() });
        reference.put("(\"quick\"; \"brown\"; \"fox\")", new char[][] {"quick".toCharArray(), "brown".toCharArray(), "fox".toCharArray() });
        reference.put("2000.01.04D05:36:57.600 0Np", new QTimestamp[] { new QTimestamp(279417600000000L), new QTimestamp(Long.MIN_VALUE) });
        reference.put("(2001.01m; 0Nm)", new QMonth[] { new QMonth(12), new QMonth(Integer.MIN_VALUE) });
        reference.put("2001.01.01 2000.05.01 0Nd", new QDate[] { new QDate(366), new QDate(121), new QDate(Integer.MIN_VALUE) });
        reference.put("2000.01.04T05:36:57.600 0Nz", new QDateTime[] { new QDateTime(3.234), new QDateTime(Double.NaN) });
        reference.put("0D05:36:57.600 0Nn", new QTimespan[] { new QTimespan(20217600000000L), new QTimespan(Long.MIN_VALUE) });
        reference.put("12:01 0Nu", new QMinute[] { new QMinute(721), new QMinute(Integer.MIN_VALUE) });
        reference.put("12:05:00 0Nv", new QSecond[] { new QSecond(43500), new QSecond(Integer.MIN_VALUE) });
        reference.put("12:04:59.123 0Nt", new QTime[] { new QTime(43499123), new QTime(Integer.MIN_VALUE) });
        reference.put("(enlist `a)!(enlist 1)", new QDictionary(new String[] { "a" }, new long[] { 1 }));
        reference.put("1 2!`abc`cdefgh", new QDictionary(new long[] { 1, 2 }, new String[] { "abc", "cdefgh" }));
        reference.put("(`x`y!(`a;2))", new QDictionary(new String[] { "x", "y" }, new Object[] { "a", 2L }));
        reference.put("`abc`def`gh!([] one: 1 2 3; two: 4 5 6)", new QDictionary(new String[] { "abc", "def", "gh" }, new QTable(new String[] { "one", "two" },
                new Object[] { new long[] { 1, 2, 3 }, new long[] { 4, 5, 6 } })));
        reference.put("(1;2h;3.3;\"4\")!(`one;2 3;\"456\";(7;8 9))", new QDictionary(new Object[] { 1L, (short) 2, 3.3, '4' },
                new Object[] { "one", new long[] { 2, 3 }, "456".toCharArray(), new Object[] { 7L, new long[] { 8, 9 } } }));
        reference.put("(0 1; 2 3)!`first`second", new QDictionary(new Object[] { new long[] { 0, 1 }, new long[] { 2, 3 } }, new String[] { "first", "second" }));
        reference.put("`A`B`C!((1;2.2;3);(`x`y!(`a;2));5.5)", new QDictionary(new String[] { "A", "B", "C" }, new Object[] {
                                                                                                                            new Object[] { 1L, 2.2, 3L },
                                                                                                                            new QDictionary(
                                                                                                                                    new String[] { "x", "y" },
                                                                                                                                    new Object[] { "a", 2L }),
                                                                                                                            5.5 }));
        reference.put("flip `abc`def!(1 2 3; 4 5 6)", new QTable(new String[] { "abc", "def" }, new Object[] { new long[] { 1, 2, 3 }, new long[] { 4, 5, 6 } }));
        reference.put("flip `name`iq!(`Dent`Beeblebrox`Prefect;98 42 126)",
                new QTable(new String[] { "name", "iq" }, new Object[] { new String[] { "Dent", "Beeblebrox", "Prefect" }, new long[] { 98, 42, 126 } }));
        reference.put("flip `name`iq`grade!(`Dent`Beeblebrox`Prefect;98 42 126;\"a c\")",
                new QTable(new String[] { "name", "iq", "grade" }, new Object[] { new String[] { "Dent", "Beeblebrox", "Prefect" }, new long[] { 98, 42, 126 }, new char[]{'a', ' ', 'c' }}));
        reference.put("flip `name`iq`fullname!(`Dent`Beeblebrox`Prefect;98 42 126;(\"Arthur Dent\"; \"Zaphod Beeblebrox\"; \"Ford Prefect\"))",
                new QTable(new String[] { "name", "iq", "fullname" }, new Object[] { new String[] { "Dent", "Beeblebrox", "Prefect" }, new long[] { 98, 42, 126 }, new Object[]{"Arthur Dent".toCharArray(), "Zaphod Beeblebrox".toCharArray(), "Ford Prefect".toCharArray()} }));
        reference.put("([] sc:1 2 3; nsc:(1 2; 3 4; 5 6 7))", new QTable(new String[] { "sc", "nsc" }, new Object[] {
                                                                                                                     new long[] { 1, 2, 3 },
                                                                                                                     new Object[] { new long[] { 1, 2 },
                                                                                                                                   new long[] { 3, 4 },
                                                                                                                                   new long[] { 5, 6, 7 } } }));
        reference.put("([] name:`symbol$(); iq:`int$())", new QTable(new String[] { "name", "iq" }, new Object[] { new String[] {}, new int[] {} }));
        reference.put("([] pos:`d1`d2`d3;dates:(2001.01.01;2000.05.01;0Nd))", new QTable(new String[] { "pos", "dates" },
                new Object[] { new String[] { "d1", "d2", "d3" }, new QDate[] { new QDate(366), new QDate(121), new QDate(Integer.MIN_VALUE) } }));
        reference.put("([eid:1001 1002 1003] pos:`d1`d2`d3;dates:(2001.01.01;2000.05.01;0Nd))", new QKeyedTable(new QTable(new String[] { "eid" },
                new Object[] { new long[] { 1001, 1002, 1003 } }), new QTable(new String[] { "pos", "dates" },
                new Object[] { new String[] { "d1", "d2", "d3" }, new QDate[] { new QDate(366), new QDate(121), new QDate(Integer.MIN_VALUE) } })));
        reference.put("{x+y}", new QLambda("{x+y}"));
        reference.put("{x+y}[3]", new QProjection(new Object[] {new QLambda("{x+y}"), 3L }));
        reference.put("0Ng", new UUID(0, 0));
        reference.put("\"G\"$\"8c680a01-5a49-5aab-5a65-d4bfddb6a661\"", UUID.fromString("8c680a01-5a49-5aab-5a65-d4bfddb6a661"));
        reference.put("(\"G\"$\"8c680a01-5a49-5aab-5a65-d4bfddb6a661\"; 0Ng)",
                new UUID[] { UUID.fromString("8c680a01-5a49-5aab-5a65-d4bfddb6a661"), new UUID(0, 0) });

    }

    private final Map<String, byte[]> expressions = new LinkedHashMap<String, byte[]>();

    QExpressions(final String file) {
        try {
            initExpressions();
        } catch ( final QException e ) {
            e.printStackTrace();
        }
        try {
            final BufferedReader input = new BufferedReader(new FileReader(file));
            try {
                String query = null;
                String expression = null;

                while ( true ) {
                    query = input.readLine();
                    expression = input.readLine();

                    if ( query != null && expression != null && !expression.equals("") ) {
                        expressions.put(query, stringToByteArray(expression));
                    } else {
                        break;
                    }
                }
            } finally {
                input.close();
            }
        } catch ( final IOException ex ) {
            ex.printStackTrace();
        }
    }

    Set<String> getExpressions() {
        return expressions.keySet();
    }

    Object getReferenceObject( final String expression ) {
        return reference.get(expression);
    }

    boolean hasReferenceObjectAlt( final String expression ) {
        return referenceSerializationAlt.containsKey(expression);
    }

    Object getReferenceObjectAlt( final String expression ) {
        return referenceSerializationAlt.get(expression);
    }

    public byte[] getBinaryExpression( final String expression ) {
        return expressions.get(expression);
    }

    private static byte[] stringToByteArray( final String hex ) {
        final int numberChars = hex.length();
        final byte[] bytes = new byte[numberChars / 2];
        for ( int i = 0; i < numberChars; i += 2 ) {
            bytes[i / 2] = (byte) (short) Short.valueOf(hex.substring(i, i + 2), 16);
        }
        return bytes;
    }

}
