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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents q date type.
 */
public final class QDate implements DateTime, Serializable {
    private static final long serialVersionUID = 762296525233866140L;

    private static final String NULL_STR = "0Nd";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

    private transient Date datetime;
    private final Integer value;

    /**
     * Creates new {@link QDate} instance using specified q date value.
     * 
     * @param value
     *            a count of days since 2000.01.01
     */
    public QDate(final Integer value) {
        this.value = value;
    }

    /**
     * Creates new {@link QDate} instance using specified {@link Date}.
     * 
     * @param datetime
     *            {@link Date} to be set
     */
    public QDate(final Date datetime) {
        this.datetime = datetime;
        value = datetime == null ? Integer.MIN_VALUE : (int) (Utils.tzOffsetFromQ(datetime.getTime()) / Utils.DAY_MILLIS - 10957);
    }

    /**
     * Returns a count of days since 2000.01.01
     * 
     * @return raw q value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Converts {@link QDate} object to {@link Date} instance.
     * 
     * @return {@link Date} representing q value.
     */
    public Date toDateTime() {
        if ( datetime == null && value != Integer.MIN_VALUE ) {
            datetime = new Date(Utils.tzOffsetToQ(Utils.QEPOCH_MILLIS + Utils.DAY_MILLIS * value));
        }
        return datetime;
    }

    /**
     * Returns a String that represents the current {@link QDate}.
     * 
     * @return a String representation of the {@link QDate}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final Date dt = toDateTime();
        return dt == null ? NULL_STR : getDateformat().format(dt);
    }

    /**
     * Indicates whether some other object is "equal to" this {@link QDate} . {@link QDate} objects are considered equal
     * if the underlying q raw value is the same for both instances.
     * 
     * @return <code>true</code> if this object is the same as the obj argument, <code>false</code> otherwise.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ( this == obj ) {
            return true;
        }

        if ( !(obj instanceof QDate) ) {
            return false;
        }

        return value.equals(((QDate) obj).getValue());
    }

    /**
     * Returns a hash code value for this {@link QDate}.
     * 
     * @return a hash code value for this object
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Returns a {@link QDate} represented by a given string.
     * 
     * @param date
     *            as {@link String}
     * @return a {@link QDate} instance representing date.
     * @throws IllegalArgumentException
     *             when date cannot be parsed
     */
    public static QDate fromString( final String date ) {
        try {
            return date == null || date.length() == 0 || date.equals(NULL_STR) ? new QDate(Integer.MIN_VALUE) : new QDate(getDateformat().parse(date));
        } catch ( final Exception e ) {
            throw new IllegalArgumentException("Cannot parse QDate from: " + date, e);
        }
    }

    private static DateFormat getDateformat() {
        return (DateFormat) dateFormat.clone();
    }
}
