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
 * Represents a q lambda expression.
 */
public final class QLambda {
    private final String expression;
    private final Object[] parameters;

    /**
     * Gets body of a q lambda expression.
     * 
     * @return body of a q lambda expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Gets parameters of a q lambda expression.
     * 
     * @return array containing lambda expression parameters
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * Creates new {@link QLambda} instance with given body and parameters.
     * 
     * @param expression
     *            body of a q lambda expression
     * @param parameters
     *            array containing lambda expression parameters
     */
    public QLambda(final String expression, final Object[] parameters) {
        if ( expression == null || expression.length() == 0 ) {
            throw new IllegalArgumentException("Lambda expression cannot be null or empty");
        }

        this.expression = expression;
        this.parameters = parameters;
    }

    /**
     * Creates new {@link QLambda} instance with given body and no parameters.
     * 
     * @param expression
     *            body of a q lambda expression
     */
    public QLambda(final String expression) {
        this(expression, null);
    }

    /**
     * Returns a String that represents the current {@link QLambda}.
     * 
     * @return a String representation of the {@link QLambda}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QLambda: " + expression + (parameters == null ? "" : Utils.arrayToString(parameters));
    }

    /**
     * Indicates whether some other object is "equal to" this lambda expression. {@link QLambda} objects are considered
     * equal if the expression and parameters list are equal for both instances.
     * 
     * @return <code>true</code> if this object is the same as the obj argument, <code>false</code> otherwise.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ( this == obj ) {
            return true;
        }

        if ( !(obj instanceof QLambda) ) {
            return false;
        }

        final QLambda l = (QLambda) obj;
        return expression.equals(l.expression) && Utils.deepArraysEquals(parameters, l.parameters);
    }

    /**
     * Returns a hash code value for this {@link QLambda}.
     * 
     * @return a hash code value for this object
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return expression.hashCode();
    }

}
