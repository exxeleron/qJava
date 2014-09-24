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
 * Represents q function.
 *
 * Note that the {@link QFunction} instances cannot be serialized to IPC protocol.
 */
public class QFunction {

    private final byte qTypeCode;

    /**
     * Creates representation of q function with given q type code.
     * 
     * @param qTypeCode
     *            q type code
     */
    protected QFunction(final byte qTypeCode) {
        this.qTypeCode = qTypeCode;
    }

    /**
     * Retrieve q type code connected with function.
     * 
     * @return type code for function
     */
    public byte getTypeCode() {
        return qTypeCode;
    }

    @Override
    public String toString() {
        return "QFunction#" + qTypeCode + "h";
    }
}