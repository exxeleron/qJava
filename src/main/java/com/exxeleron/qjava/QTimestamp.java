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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents q timestamp type.
 */
public final class QTimestamp implements DateTime, Serializable {
    private static final long serialVersionUID = 762296525233866140L;
    
    private static final String NULL_STR = "0Np";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd'D'HH:mm:ss.SSS");
    private static final NumberFormat nanosFormatter = new DecimalFormat("000000");
    private static final int NANOS_PER_SECOND = 1000000;

    private transient Date datetime;
    private final Long value;

    /**
     * Creates new {@link QTimestamp} instance using specified q date value.
     * 
     * @param value
     *            a count of nanoseconds from midnight 2000.01.01
     */
    public QTimestamp(final Long value) {
        this.value = value;
    }

    private static long getNanos( final Date datetime ) {
        return NANOS_PER_SECOND * (Utils.tzOffsetFromQ(datetime.getTime()) - Utils.QEPOCH_MILLIS);
    }

    /**
     * Creates new {@link QTimestamp} instance using specified {@link Date}.
     * 
     * @param datetime
     *            {@link Date} to be set
     */
    public QTimestamp(final Date datetime) {
        this.datetime = datetime;
        if ( datetime != null ) {
            value = getNanos(datetime);
        } else {
            value = Long.MIN_VALUE;
        }
    }

    /**
     * Returns a count of nanoseconds from midnight 2000.01.01.
     * 
     * @return raw q value
     */
    public Long getValue() {
        return value;
    }

    /**
     * Converts {@link QTimestamp} object to {@link Date} instance.
     * 
     * @return {@link Date} representing q value.
     */
    public Date toDateTime() {
        if ( datetime == null && value != Long.MIN_VALUE ) {
            datetime = new Date(Utils.tzOffsetToQ(value / NANOS_PER_SECOND + Utils.QEPOCH_MILLIS));
        }
        return datetime;
    }

    /**
     * Returns a String that represents the current {@link QTimestamp}.
     * 
     * @return a String representation of the {@link QTimestamp}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final Date dt = toDateTime();
        return dt == null ? NULL_STR : getDateformat().format(dt) + getNanosformat().format(value % NANOS_PER_SECOND);

    }

    /**
     * Indicates whether some other object is "equal to" this date. {@link QTimestamp} objects are considered equal if
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

        if ( !(obj instanceof QTimestamp) ) {
            return false;
        }

        return value.equals(((QTimestamp) obj).getValue());
    }

    /**
     * Returns a hash code value for this {@link QTimestamp}.
     * 
     * @return a hash code value for this object
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Returns a {@link QTimestamp} represented by a given string.
     * 
     * @param date
     *            as {@link String}
     * @return a {@link QTimestamp} instance representing date.
     * @throws IllegalArgumentException
     *             when date cannot be parsed
     */
    public static QTimestamp fromString( final String date ) {
        try {

            if ( date == null || date.length() == 0 || date.equals(NULL_STR) ) {
                return new QTimestamp(Long.MIN_VALUE);
            } else {
                return new QTimestamp(getNanos(getDateformat().parse(date.substring(0, date.lastIndexOf(".") + 3)))
                        + getNanosformat().parse(date.substring(date.lastIndexOf(".") + 3)).longValue());
            }
        } catch ( final Exception e ) {
            throw new IllegalArgumentException("Cannot parse QTimestamp from: " + date, e);
        }
    }

    private static DateFormat getDateformat() {
        return (DateFormat) dateFormat.clone();
    }

    private static NumberFormat getNanosformat() {
        return (NumberFormat) nanosFormatter.clone();
    }
}
