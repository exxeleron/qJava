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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents q time type.
 */
public final class QTime implements DateTime {

    private static final String NULL_STR = "0Nt";

    private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    private Date datetime;
    private Integer value;

    /**
     * Creates new {@link QTime} instance using specified q date value.
     * 
     * @param value
     *            a count of milliseconds from midnight
     */
    public QTime(final Integer value) {
        this.value = value;
    }

    /**
     * Creates new {@link QTime} instance using specified {@link Date}.
     * 
     * @param datetime
     *            {@link Date} to be set
     */
    public QTime(final Date datetime) {
        this.datetime = datetime;
        if ( datetime != null ) {
            final Calendar c = Calendar.getInstance();
            c.setTime(datetime);
            value = c.get(Calendar.MILLISECOND) + 1000 * c.get(Calendar.SECOND) + 60000 * c.get(Calendar.MINUTE) + 3600000 * c.get(Calendar.HOUR_OF_DAY);
        } else {
            value = Integer.MIN_VALUE;
        }
    }

    /**
     * Returns a count of milliseconds from midnight.
     * 
     * @return raw q value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Converts {@link QTime} object to {@link Date} instance.
     * 
     * @return {@link Date} representing q value.
     */
    public Date toDateTime() {
        if ( datetime == null && value != Integer.MIN_VALUE ) {
            datetime = new Date(Utils.tzOffsetToQ(value + Utils.QEPOCH_MILLIS));
        }
        return datetime;
    }

    /**
     * Returns a String that represents the current {@link QTime}.
     * 
     * @return a String representation of the {@link QTime}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final Date dt = toDateTime();
        return dt == null ? NULL_STR : getDateformat().format(dt);
    }

    /**
     * Indicates whether some other object is "equal to" this date. {@link QTime} objects are considered equal if the
     * underlying q raw value is the same for both instances.
     * 
     * @return <code>true</code> if this object is the same as the obj argument, <code>false</code> otherwise.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ( this == obj ) {
            return true;
        }

        if ( !(obj instanceof QTime) ) {
            return false;
        }

        return value.equals(((QTime) obj).getValue());
    }

    /**
     * Returns a hash code value for this {@link QTime}.
     * 
     * @return a hash code value for this object
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Returns a {@link QTime} represented by a given string.
     * 
     * @param date
     *            as {@link String}
     * @return a {@link QTime} instance representing date.
     * @throws IllegalArgumentException
     *             when date cannot be parsed
     */
    public static QTime fromString( final String date ) {
        try {
            return date == null || date.length() == 0 || date.equals(NULL_STR) ? new QTime(Integer.MIN_VALUE) : new QTime(getDateformat().parse(date));
        } catch ( final Exception e ) {
            throw new IllegalArgumentException("Cannot parse QTime from: " + date, e);
        }
    }

    private static synchronized DateFormat getDateformat() {
        return dateFormat;
    }
}
