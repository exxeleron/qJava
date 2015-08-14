/**
 *  Copyright (c) 2011-2015 Exxeleron GmbH
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
 * Common interface for tables.
 */
public interface Table {

    /**
     * Gets a number of rows in current {@link QKeyedTable}.
     * 
     * @return a number of rows
     */
    public int getRowsCount();

    /**
     * @return a number of columns
     */
    public int getColumnsCount();

    /**
     * Checks whether table contains column with given name.
     * 
     * @param column
     *            Name of the column
     * @return <code>true</code> if table contains column with given name, <code>false</code> otherwise
     */
    public boolean hasColumn( final String column );

    /**
     * Gets a column index for specified name or <code>null</code> if column doesn't exist in the table.
     * 
     * @param column
     *            Name of the column
     * @return 0 based column index
     * @throws NullPointerException
     *             if column is not defined
     */
    public int getColumnIndex( final String column );

}