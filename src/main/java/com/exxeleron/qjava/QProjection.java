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

import java.util.Arrays;

/**
 * Represents a q projection.
 */
public final class QProjection extends QFunction{
    private final Object[] parameters;

    /**
     * Gets parameters of a q projection.
     * 
     * @return array containing projection parameters
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * Creates new {@link QProjection} instance with given parameters.
     * 
     * @param parameters
     *            array containing projection parameters
     * 
     * @throws IllegalArgumentException
     */
    public QProjection(final Object[] parameters) {
        super(QType.PROJECTION.getTypeCode());
        this.parameters = parameters;
    }

    /**
     * Returns a String that represents the current {@link QProjection}.
     * 
     * @return a String representation of the {@link QProjection}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QProjection: " + (parameters == null ? "<null>" : Utils.arrayToString(parameters));
    }

    /**
     * Indicates whether some other object is "equal to" this projection. {@link QProjection} objects are considered
     * equal if the parameters list are equal for both instances.
     * 
     * @return <code>true</code> if this object is the same as the obj argument, <code>false</code> otherwise.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ( this == obj ) {
            return true;
        }

        if ( !(obj instanceof QProjection) ) {
            return false;
        }

        final QProjection p = (QProjection) obj;
        return Utils.deepArraysEquals(parameters, p.parameters);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(parameters);
    }

}
