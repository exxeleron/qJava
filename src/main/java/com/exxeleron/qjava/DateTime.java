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

import java.util.Date;

/**
 * Common interface for the q date/time types.
 */
public interface DateTime {

    /**
     * Returns internal q representation.
     * 
     * @return internal q representation
     */
    public Object getValue();

    /**
     * Converts q date/time object to {@link java.util.Date} instance.
     * 
     * @return {@link java.util.Date} representing q value.
     */
    public Date toDateTime();

}
