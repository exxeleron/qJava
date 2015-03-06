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
import java.util.Calendar;
import java.util.Date;

/**
 * Represents q timespan type.
 */
public final class QTimespan implements DateTime, Serializable {
    private static final long serialVersionUID = 762296525233866140L;
    
    private static final String NULL_STR = "0Nn";

    private static final DateFormat dateFormat = new SimpleDateFormat("'D'HH:mm:ss.SSS");
    private static final NumberFormat nanosFormatter = new DecimalFormat("000000");
    private static final int NANOS_PER_SECOND = 1000000;
    private static final long NANOS_PER_DAY = Utils.DAY_MILLIS * 1000000;

    private transient Date datetime;
    private final Long value;

    /**
     * Creates new {@link QTimespan} instance using specified q date value.
     * 
     * @param value
     *            a count of nanoseconds from midnight
     */
    public QTimespan(final Long value) {
        this.value = value;
    }

    private static long getNanos( final Date datetime ) {
        final Calendar c = Calendar.getInstance();
        c.setTime(datetime);
        return (long) (c.get(Calendar.MILLISECOND) + 1000 * c.get(Calendar.SECOND) + 60000 * c.get(Calendar.MINUTE) + 3600000 * c.get(Calendar.HOUR_OF_DAY))
                * NANOS_PER_SECOND;
    }

    /**
     * Creates new {@link QTimespan} instance using specified {@link Date}.
     * 
     * @param datetime
     *            {@link Date} to be set
     */
    public QTimespan(final Date datetime) {
        this.datetime = datetime;
        if ( datetime != null ) {
            value = getNanos(datetime);
        } else {
            value = Long.MIN_VALUE;
        }
    }

    /**
     * Returns a count of nanoseconds from midnight.
     * 
     * @return raw q value
     */
    public Long getValue() {
        return value;
    }

    /**
     * Converts {@link QTimespan} object to {@link Date} instance.
     * 
     * @return {@link Date} representing q value.
     */
    public Date toDateTime() {
        if ( datetime == null && value != Long.MIN_VALUE ) {
            datetime = new Date(Utils.tzOffsetToQ(Math.abs(value) / NANOS_PER_SECOND + Utils.QEPOCH_MILLIS));
        }
        return datetime;
    }

    /**
     * Returns a String that represents the current {@link QTimespan}.
     * 
     * @return a String representation of the {@link QTimespan}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final Date dt = toDateTime();
        return dt == null ? NULL_STR : (value < 0 ? "-" : "") + (Math.abs(value) / NANOS_PER_DAY) + getDateformat().format(dt)
                + getNanosformat().format(Math.abs(value) % NANOS_PER_SECOND);
    }

    /**
     * Indicates whether some other object is "equal to" this date. {@link QTimespan} objects are considered equal if
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

        if ( !(obj instanceof QTimespan) ) {
            return false;
        }

        return value.equals(((QTimespan) obj).getValue());
    }

    /**
     * Returns a hash code value for this {@link QTimespan}.
     * 
     * @return a hash code value for this object
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Returns a {@link QTimespan} represented by a given string.
     * 
     * @param date
     *            as {@link String}
     * @return a {@link QTimespan} instance representing date.
     * @throws IllegalArgumentException
     *             when date cannot be parsed
     */
    public static QTimespan fromString( final String date ) {
        try {
            if ( date == null || date.length() == 0 || date.equals(NULL_STR) ) {
                return new QTimespan(Long.MIN_VALUE);
            } else {
                final long nanos = getNanos(getDateformat().parse(date.substring(date.indexOf("D"), date.lastIndexOf(".") + 3)))
                        + getNanosformat().parse(date.substring(date.lastIndexOf(".") + 3)).longValue();
                return new QTimespan(Integer.valueOf(date.substring(0, date.indexOf("D"))) * NANOS_PER_DAY + ('-' == date.charAt(0) ? -1 : 1) * nanos);
            }
        } catch ( final Exception e ) {
            throw new IllegalArgumentException("Cannot parse QTimespan from: " + date, e);
        }
    }

    private static DateFormat getDateformat() {
        return (DateFormat) dateFormat.clone();
    }

    private static NumberFormat getNanosformat() {
        return (NumberFormat) nanosFormatter.clone();
    }
}
