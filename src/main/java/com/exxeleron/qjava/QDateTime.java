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
 * Represents q datetime type.
 */
public final class QDateTime implements DateTime, Serializable {
    private static final long serialVersionUID = 762296525233866140L;

    private static final String NULL_STR = "0Nz";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss.SSS");

    private transient Date datetime;
    private final Double value;

    /**
     * Creates new {@link QDateTime} instance using specified q date value.
     * 
     * @param value
     *            a fractional day count from midnight 2000.01.01
     */
    public QDateTime(final Double value) {
        this.value = value;
    }

    /**
     * Creates new {@link QDateTime} instance using specified {@link Date}.
     * 
     * @param datetime
     *            {@link Date} to be set
     */
    public QDateTime(final Date datetime) {
        this.datetime = datetime;
        if ( datetime != null ) {
            value = (double) (Utils.tzOffsetFromQ(datetime.getTime()) - Utils.QEPOCH_MILLIS) / Utils.DAY_MILLIS;
        } else {
            value = Double.NaN;
        }
    }

    /**
     * Returns a fractional day count from midnight 2000.01.01.
     * 
     * @return raw q value
     */
    public Double getValue() {
        return value;
    }

    /**
     * Converts {@link QDateTime} object to {@link Date} instance.
     * 
     * @return {@link Date} representing q value.
     */
    public Date toDateTime() {
        if ( datetime == null && !Double.isNaN(value) ) {
            datetime = new Date(Utils.tzOffsetToQ(Utils.QEPOCH_MILLIS + (long) (value * Utils.DAY_MILLIS)));
        }
        return datetime;
    }

    /**
     * Returns a String that represents the current {@link QDateTime}.
     * 
     * @return a String representation of the {@link QDateTime}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final Date dt = toDateTime();
        return dt == null ? NULL_STR : getDateformat().format(dt);
    }

    /**
     * Indicates whether some other object is "equal to" this date. {@link QDateTime} objects are considered equal if
     * the underlying q raw value is the same for both instances.
     * 
     * @return <code>true</code> if this object is the same as the obj argument, <code>false</code> otherwise.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ( this == obj ) {
            return true;
        }

        if ( !(obj instanceof QDateTime) ) {
            return false;
        }

        return value.equals(((QDateTime) obj).getValue());
    }

    /**
     * Returns a hash code value for this {@link QDateTime}.
     * 
     * @return a hash code value for this object
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Returns a {@link QDateTime} represented by a given string.
     * 
     * @param date
     *            as {@link String}
     * @return a {@link QDateTime} instance representing date.
     * @throws IllegalArgumentException
     *             when date cannot be parsed
     */
    public static QDateTime fromString( final String date ) {
        try {
            return date == null || date.length() == 0 || date.equals(NULL_STR) ? new QDateTime(Double.NaN) : new QDateTime(getDateformat().parse(date));
        } catch ( final Exception e ) {
            throw new IllegalArgumentException("Cannot parse QDateTime from: " + date, e);
        }
    }

    private static DateFormat getDateformat() {
        return (DateFormat) dateFormat.clone();
    }
}
