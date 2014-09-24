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

import java.util.regex.Pattern;

/**
 * Represents a q lambda expression.
 */
public final class QLambda extends QFunction {
    private static final Pattern LAMBDA_REGEX = Pattern.compile("\\s*(k\\))?\\s*\\{.*\\}");
    
    private final String expression;

    /**
     * Gets body of a q lambda expression.
     *
     * @return body of a q lambda expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Creates new {@link QLambda} instance with given body. Note that expression is trimmed and required to be enclosed
     * in { and } brackets.
     *
     * @param expression
     *            body of a q lambda expression
     *
     * @throws IllegalArgumentException
     */
    public QLambda(final String expression) {
        super(QType.LAMBDA.getTypeCode());
        
        if ( expression == null ) {
            throw new IllegalArgumentException("Lambda expression cannot be null or empty");
        }

        this.expression = expression.trim();
        if ( this.expression.length() == 0 ) {
            throw new IllegalArgumentException("Lambda expression cannot be null or empty");
        }

        if ( !LAMBDA_REGEX.matcher(expression).matches() ) {
            throw new IllegalArgumentException("Invalid lambda expression: " + expression);
        }
    }

    /**
     * Returns a String that represents the current {@link QLambda}.
     *
     * @return a String representation of the {@link QLambda}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QLambda: " + expression;
    }

    /**
     * Indicates whether some other object is "equal to" this lambda expression. {@link QLambda} objects are considered
     * equal if the expression is equal for both instances.
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
        return expression.equals(l.expression);
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
