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
 * Encapsulates an error encountered during receiving data from kdb+.
 * 
 * @author dev123
 */
public final class QErrorMessage {

    private final Throwable cause;

    /**
     * Creates new {@link QErrorMessage} object with specified cause.
     * 
     * @param cause
     */
    public QErrorMessage(final Throwable cause) {
        this.cause = cause;
    }

    /**
     * Retrieves the source exception.
     * 
     * @return the exception
     */
    public Throwable getCause() {
        return cause;
    }

}
