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

/**
 * Partial reimplementation of {@link java.lang.reflect.Array} class due to performance issues.
 */
final class Array {

    private static RuntimeException illegalArgumentArray( final Object array ) {
        if ( array == null ) {
            return new NullPointerException("Array argument is null");
        } else if ( !array.getClass().isArray() ) {
            return new IllegalArgumentException("Argument is not an array");
        } else {
            return new IllegalArgumentException("Array is of incompatible type");
        }
    }

    /**
     * Returns the length of the specified array object, as an int.
     * 
     * @param array
     *            the array
     * @return the length of the array
     * 
     * @throws NullPointerException
     *             If the specified object is <code>null</code>
     * @throws IllegalArgumentException
     *             if the object argument is not an array
     */
    public static int getLength( final Object array ) {
        if ( array instanceof Object[] ) {
            return ((Object[]) array).length;
        }
        if ( array instanceof boolean[] ) {
            return ((boolean[]) array).length;
        }
        if ( array instanceof byte[] ) {
            return ((byte[]) array).length;
        }
        if ( array instanceof char[] ) {
            return ((char[]) array).length;
        }
        if ( array instanceof short[] ) {
            return ((short[]) array).length;
        }
        if ( array instanceof int[] ) {
            return ((int[]) array).length;
        }
        if ( array instanceof long[] ) {
            return ((long[]) array).length;
        }
        if ( array instanceof float[] ) {
            return ((float[]) array).length;
        }
        if ( array instanceof double[] ) {
            return ((double[]) array).length;
        }
        throw illegalArgumentArray(array);
    }

    /**
     * Returns the value of the indexed component in the specified array object. The value is automatically wrapped in
     * an object if it has a primitive type.
     * 
     * @param array
     *            the array
     * @param index
     *            the index
     * @return the (possibly wrapped) value of the indexed component in the specified array
     * 
     * @throws NullPointerException
     *             If the specified object is <code>null</code>
     * @throws IllegalArgumentException
     *             If the specified object is not an array
     * @throws ArrayIndexOutOfBoundsException
     *             If the specified index argument is negative, or if it is greater than or equal to the length of the
     *             specified array
     */
    public static Object get( final Object array, final int index ) {
        if ( array instanceof Object[] ) {
            return ((Object[]) array)[index];
        }
        if ( array instanceof boolean[] ) {
            return ((boolean[]) array)[index];
        }
        if ( array instanceof byte[] ) {
            return ((byte[]) array)[index];
        }
        if ( array instanceof char[] ) {
            return ((char[]) array)[index];
        }
        if ( array instanceof short[] ) {
            return ((short[]) array)[index];
        }
        if ( array instanceof int[] ) {
            return ((int[]) array)[index];
        }
        if ( array instanceof long[] ) {
            return ((long[]) array)[index];
        }
        if ( array instanceof float[] ) {
            return ((float[]) array)[index];
        }
        if ( array instanceof double[] ) {
            return ((double[]) array)[index];
        }
        throw illegalArgumentArray(array);
    }

    /**
     * Sets the value of the indexed component of the specified array object to the specified boolean value.
     * 
     * @param array
     *            the array
     * @param index
     *            the index into the array
     * @param value
     *            the new value of the indexed component
     * 
     * @throws NullPointerException
     *             If the specified object is <code>null</code>
     * @throws IllegalArgumentException
     *             If the specified object is not an array, or if the specified value cannot be converted to the
     *             underlying array's component type by an identity or a primitive widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             If the specified index argument is negative, or if it is greater than or equal to the length of the
     *             specified array
     */
    public static void setBoolean( final Object array, final int index, final boolean value ) {
        if ( array instanceof boolean[] ) {
            ((boolean[]) array)[index] = value;
        } else {
            throw illegalArgumentArray(array);
        }
    }

    /**
     * Sets the value of the indexed component of the specified array object to the specified byte value.
     * 
     * @param array
     *            the array
     * @param index
     *            the index into the array
     * @param value
     *            the new value of the indexed component
     * 
     * @throws NullPointerException
     *             If the specified object is <code>null</code>
     * @throws IllegalArgumentException
     *             If the specified object is not an array, or if the specified value cannot be converted to the
     *             underlying array's component type by an identity or a primitive widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             If the specified index argument is negative, or if it is greater than or equal to the length of the
     *             specified array
     */
    public static void setByte( final Object array, final int index, final byte value ) {
        if ( array instanceof byte[] ) {
            ((byte[]) array)[index] = value;
        } else if ( array instanceof short[] ) {
            ((short[]) array)[index] = value;
        } else if ( array instanceof int[] ) {
            ((int[]) array)[index] = value;
        } else if ( array instanceof long[] ) {
            ((long[]) array)[index] = value;
        } else if ( array instanceof float[] ) {
            ((float[]) array)[index] = value;
        } else if ( array instanceof double[] ) {
            ((double[]) array)[index] = value;
        } else {
            throw illegalArgumentArray(array);
        }
    }

    /**
     * Sets the value of the indexed component of the specified array object to the specified char value.
     * 
     * @param array
     *            the array
     * @param index
     *            the index into the array
     * @param value
     *            the new value of the indexed component
     * 
     * @throws NullPointerException
     *             If the specified object is <code>null</code>
     * @throws IllegalArgumentException
     *             If the specified object is not an array, or if the specified value cannot be converted to the
     *             underlying array's component type by an identity or a primitive widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             If the specified index argument is negative, or if it is greater than or equal to the length of the
     *             specified array
     */
    public static void setChar( final Object array, final int index, final char value ) {
        if ( array instanceof char[] ) {
            ((char[]) array)[index] = value;
        } else if ( array instanceof int[] ) {
            ((int[]) array)[index] = value;
        } else if ( array instanceof long[] ) {
            ((long[]) array)[index] = value;
        } else if ( array instanceof float[] ) {
            ((float[]) array)[index] = value;
        } else if ( array instanceof double[] ) {
            ((double[]) array)[index] = value;
        } else {
            throw illegalArgumentArray(array);
        }
    }

    /**
     * Sets the value of the indexed component of the specified array object to the specified short value.
     * 
     * @param array
     *            the array
     * @param index
     *            the index into the array
     * @param value
     *            the new value of the indexed component
     * 
     * @throws NullPointerException
     *             If the specified object is <code>null</code>
     * @throws IllegalArgumentException
     *             If the specified object is not an array, or if the specified value cannot be converted to the
     *             underlying array's component type by an identity or a primitive widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             If the specified index argument is negative, or if it is greater than or equal to the length of the
     *             specified array
     */
    public static void setShort( final Object array, final int index, final short value ) {
        if ( array instanceof short[] ) {
            ((short[]) array)[index] = value;
        } else if ( array instanceof int[] ) {
            ((int[]) array)[index] = value;
        } else if ( array instanceof long[] ) {
            ((long[]) array)[index] = value;
        } else if ( array instanceof float[] ) {
            ((float[]) array)[index] = value;
        } else if ( array instanceof double[] ) {
            ((double[]) array)[index] = value;
        } else {
            throw illegalArgumentArray(array);
        }
    }

    /**
     * Sets the value of the indexed component of the specified array object to the specified int value.
     * 
     * @param array
     *            the array
     * @param index
     *            the index into the array
     * @param value
     *            the new value of the indexed component
     * 
     * @throws NullPointerException
     *             If the specified object is <code>null</code>
     * @throws IllegalArgumentException
     *             If the specified object is not an array, or if the specified value cannot be converted to the
     *             underlying array's component type by an identity or a primitive widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             If the specified index argument is negative, or if it is greater than or equal to the length of the
     *             specified array
     */
    public static void setInt( final Object array, final int index, final int value ) {
        if ( array instanceof int[] ) {
            ((int[]) array)[index] = value;
        } else if ( array instanceof long[] ) {
            ((long[]) array)[index] = value;
        } else if ( array instanceof float[] ) {
            ((float[]) array)[index] = value;
        } else if ( array instanceof double[] ) {
            ((double[]) array)[index] = value;
        } else {
            throw illegalArgumentArray(array);
        }
    }

    /**
     * Sets the value of the indexed component of the specified array object to the specified long value.
     * 
     * @param array
     *            the array
     * @param index
     *            the index into the array
     * @param value
     *            the new value of the indexed component
     * 
     * @throws NullPointerException
     *             If the specified object is <code>null</code>
     * @throws IllegalArgumentException
     *             If the specified object is not an array, or if the specified value cannot be converted to the
     *             underlying array's component type by an identity or a primitive widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             If the specified index argument is negative, or if it is greater than or equal to the length of the
     *             specified array
     */
    public static void setLong( final Object array, final int index, final long value ) {
        if ( array instanceof long[] ) {
            ((long[]) array)[index] = value;
        } else if ( array instanceof float[] ) {
            ((float[]) array)[index] = value;
        } else if ( array instanceof double[] ) {
            ((double[]) array)[index] = value;
        } else {
            throw illegalArgumentArray(array);
        }
    }

    /**
     * Sets the value of the indexed component of the specified array object to the specified float value.
     * 
     * @param array
     *            the array
     * @param index
     *            the index into the array
     * @param value
     *            the new value of the indexed component
     * 
     * @throws NullPointerException
     *             If the specified object is <code>null</code>
     * @throws IllegalArgumentException
     *             If the specified object is not an array, or if the specified value cannot be converted to the
     *             underlying array's component type by an identity or a primitive widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             If the specified index argument is negative, or if it is greater than or equal to the length of the
     *             specified array
     */
    public static void setFloat( final Object array, final int index, final float value ) {
        if ( array instanceof float[] ) {
            ((float[]) array)[index] = value;
        } else if ( array instanceof double[] ) {
            ((double[]) array)[index] = value;
        } else {
            throw illegalArgumentArray(array);
        }
    }

    /**
     * Sets the value of the indexed component of the specified array object to the specified double value.
     * 
     * @param array
     *            the array
     * @param index
     *            the index into the array
     * @param value
     *            the new value of the indexed component
     * 
     * @throws NullPointerException
     *             If the specified object is <code>null</code>
     * @throws IllegalArgumentException
     *             If the specified object is not an array, or if the specified value cannot be converted to the
     *             underlying array's component type by an identity or a primitive widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             If the specified index argument is negative, or if it is greater than or equal to the length of the
     *             specified array
     */
    public static void setDouble( final Object array, final int index, final double value ) {
        if ( array instanceof double[] ) {
            ((double[]) array)[index] = value;
        } else {
            throw illegalArgumentArray(array);
        }
    }

    /**
     * Sets the value of the indexed component of the specified array object to the specified new value. The new value
     * is first automatically unwrapped if the array has a primitive component type.
     * 
     * @param array
     *            the array
     * @param index
     *            the index into the array
     * @param value
     *            the new value of the indexed component
     * 
     * @throws NullPointerException
     *             If the specified object is <code>null</code>
     * @throws IllegalArgumentException
     *             If the specified object is not an array, or if the array component type is primitive and an unwrapping conversion fails
     * @throws ArrayIndexOutOfBoundsException
     *             If the specified index argument is negative, or if it is greater than or equal to the length of the
     *             specified array
     */
    public static void set( final Object array, final int index, final Object value ) {
        if ( array instanceof Object[] ) {
            try {
                ((Object[]) array)[index] = value;
            } catch ( final ArrayStoreException e ) {
                throw illegalArgumentArray(array);
            }
        } else if ( value instanceof Boolean ) {
            setBoolean(array, index, (Boolean) value);
        } else if ( value instanceof Byte ) {
            setByte(array, index, (Byte) value);
        } else if ( value instanceof Short ) {
            setShort(array, index, (Short) value);
        } else if ( value instanceof Character ) {
            setChar(array, index, (Character) value);
        } else if ( value instanceof Integer ) {
            setInt(array, index, (Integer) value);
        } else if ( value instanceof Long ) {
            setLong(array, index, (Long) value);
        } else if ( value instanceof Float ) {
            setFloat(array, index, (Float) value);
        } else if ( value instanceof Double ) {
            setDouble(array, index, (Double) value);
        } else {
            throw illegalArgumentArray(array);
        }
    }
}
