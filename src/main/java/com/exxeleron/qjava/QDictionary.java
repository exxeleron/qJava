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
import java.util.Iterator;

/**
 * Represents a q dictionary type.
 */
public final class QDictionary implements Iterable<QDictionary.KeyValuePair> {

    private final Object keys;
    private final Object values;
    private final int length;

    /**
     * Creates new {@link QDictionary} instance with given keys and values arrays.
     * 
     * @param keys
     *            keys array
     * @param values
     *            values array
     * 
     * @throws IllegalArgumentException
     */
    public QDictionary(final Object keys, final Object values) {
        if ( keys == null || !keys.getClass().isArray() ) {
            throw new IllegalArgumentException("Parameter: keys is not an array");
        }
        if ( values == null || !values.getClass().isArray() ) {
            throw new IllegalArgumentException("Parameter: values is not an array");
        }
        length = Array.getLength(keys);
        if ( length != Array.getLength(values) ) {
            throw new IllegalArgumentException("Keys and value arrays cannot have different length");
        }

        this.keys = keys;
        this.values = values;
    }

    /**
     * Returns array containing list of keys stored in the dictionary.
     * 
     * @return an array of keys
     */
    public Object getKeys() {
        return keys;
    }

    /**
     * Returns array containing list of values stored in the dictionary.
     * 
     * @return an array of values
     */
    public Object getValues() {
        return values;
    }

    /**
     * <p>
     * Returns an iterator over a key/value pairs stored in the dictionary.
     * </p>
     * 
     * <p>
     * Note that the iterator returned by this method will throw an {@link UnsupportedOperationException} in response to
     * its <code>remove</code> method.
     * </p>
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<KeyValuePair> iterator() {
        return new Iterator<QDictionary.KeyValuePair>() {

            private int index = 0;

            public boolean hasNext() {
                return index < length;
            }

            public KeyValuePair next() {
                return new KeyValuePair(index++);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Defines a key/value pair that can be retrieved.
     */
    public class KeyValuePair {

        private final int index;

        KeyValuePair(final int index) {
            this.index = index;
        }

        /**
         * Returns key from given pair.
         * 
         * @return key
         */
        public Object getKey() {
            return Array.get(keys, index);
        }

        /**
         * Returns value from given pair.
         * 
         * @return value
         */
        public Object getValue() {
            return Array.get(values, index);
        }
    }

    /**
     * Returns a String that represents the current {@link QDictionary}.
     * 
     * @return a String representation of the {@link QDictionary}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QDictionary: " + Utils.arrayToString(keys) + "!" + Utils.arrayToString(values);
    }

    /**
     * Indicates whether some other object is "equal to" this dictionary. {@link QDictionary} objects are considered
     * equal if the keys and values lists are equal for both instances.
     * 
     * @return <code>true</code> if this object is the same as the obj argument, <code>false</code> otherwise.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ( this == obj ) {
            return true;
        }

        if ( !(obj instanceof QDictionary) ) {
            return false;
        }

        final QDictionary d = (QDictionary) obj;
        return Utils.deepArraysEquals(keys, d.keys) && Utils.deepArraysEquals(values, d.values);
    }

    /**
     * Returns a hash code value for this {@link QDictionary}.
     * 
     * @return a hash code value for this object
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 31 * Utils.arrayHashCode(keys) + Utils.arrayHashCode(values);
    }
}
