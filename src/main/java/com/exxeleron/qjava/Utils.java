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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * Utility methods
 */
class Utils {
    static final long DAY_MILLIS = 86400000L;

    static final long QEPOCH_MILLIS = 10957 * DAY_MILLIS;

    static final TimeZone TIME_ZONE = TimeZone.getDefault();

    static long tzOffsetFromQ( final long dt ) {
        return dt + TIME_ZONE.getOffset(dt);
    }

    static long tzOffsetToQ( final long dt ) {
        return dt - TIME_ZONE.getOffset(dt - TIME_ZONE.getOffset(dt));
    }

    private static final char[] HEXES = "0123456789ABCDEF".toCharArray();

    static String getHex( final byte[] raw ) {
        char[] hexChars = new char[raw.length * 2];
        for ( int j = 0; j < raw.length; j++ ) {
            int v = raw[j] & 0xFF;
            hexChars[j * 2] = HEXES[v >>> 4];
            hexChars[j * 2 + 1] = HEXES[v & 0x0F];
        }
        return new String(hexChars);
    }

    static String arrayToString( final Object list ) {
        if ( list == null || !list.getClass().isArray() || Array.getLength(list) == 0 ) {
            return "[]";
        } else {
            final int length = Array.getLength(list);
            final StringBuilder buffer = new StringBuilder("[");

            Object obj = Array.get(list, 0);
            buffer.append(obj == null ? null : obj.getClass().isArray() ? arrayToString(obj) : obj);
            for ( int i = 1; i < length; i++ ) {
                obj = Array.get(list, i);
                buffer.append(", ");
                buffer.append(obj == null ? null : obj.getClass().isArray() ? arrayToString(obj) : obj);
            }
            buffer.append(']');
            return buffer.toString();
        }
    }

    static boolean deepArraysEquals( final Object l, final Object r ) {
        if ( l == null && r == null ) {
            return true;
        }

        if ( l == null || r == null ) {
            return false;
        }

        if ( l.getClass() != r.getClass() ) {
            return false;
        }

        final int length = Array.getLength(l);
        if ( length != Array.getLength(r) ) {
            return false;
        }

        for ( int i = 0; i < length; i++ ) {
            final Object lv = Array.get(l, i);
            final Object rv = Array.get(r, i);

            if ( lv == rv || lv == null && rv == null ) {
                continue;
            }

            if ( lv == null || rv == null || lv.getClass() != rv.getClass() ) {
                return false;
            }

            if ( lv.getClass().isArray() ) {
                if ( !deepArraysEquals(lv, rv) ) {
                    return false;
                }
            } else {
                if ( !lv.equals(rv) ) {
                    return false;
                }
            }
        }

        return true;
    }

    static int arrayHashCode( final Object list ) {
        if ( list instanceof Object[] ) {
            return Arrays.hashCode((Object[]) list);
        } else if ( list instanceof boolean[] ) {
            return Arrays.hashCode((boolean[]) list);
        } else if ( list instanceof byte[] ) {
            return Arrays.hashCode((byte[]) list);
        } else if ( list instanceof short[] ) {
            return Arrays.hashCode((short[]) list);
        } else if ( list instanceof int[] ) {
            return Arrays.hashCode((int[]) list);
        } else if ( list instanceof long[] ) {
            return Arrays.hashCode((long[]) list);
        } else if ( list instanceof float[] ) {
            return Arrays.hashCode((float[]) list);
        } else if ( list instanceof double[] ) {
            return Arrays.hashCode((double[]) list);
        } else {
            throw new IllegalArgumentException("Argument is not an array");
        }
    }

}
