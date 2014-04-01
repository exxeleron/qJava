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
 * Exception representing q serialization error.
 */
public class QWriterException extends QException {

    private static final long serialVersionUID = -6996779408402779829L;

    /**
     * Constructs a {@link QWriterException} with the specified detailed message and cause.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public QWriterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a {@link QWriterException} with the specified detailed message.
     * 
     * @param message
     *            the detail message
     */
    public QWriterException(final String message) {
        super(message);
    }

}
