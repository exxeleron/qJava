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
import java.util.Calendar;
import java.util.Date;

/**
 * Represents q second type.
 */
public final class QSecond implements DateTime, Serializable {
    private static final long serialVersionUID = 762296525233866140L;

    private static final String NULL_STR = "0Nv";

    private transient Date datetime;
    private final Integer value;

    /**
     * Creates new {@link QSecond} instance using specified q date value.
     * 
     * @param value
     *            a count of seconds from midnight
     */
    public QSecond(final Integer value) {
        this.value = value;
    }

    /**
     * Creates new {@link QSecond} instance using specified {@link Date}.
     * 
     * @param datetime
     *            {@link Date} to be set
     */
    public QSecond(final Date datetime) {
        this.datetime = datetime;
        if ( datetime != null ) {
            final Calendar c = Calendar.getInstance();
            c.setTime(datetime);
            value = c.get(Calendar.SECOND) + c.get(Calendar.MINUTE) * 60 + 3600 * c.get(Calendar.HOUR_OF_DAY);
        } else {
            value = Integer.MIN_VALUE;
        }
    }

    /**
     * Returns a count of seconds from midnight.
     * 
     * @return raw q value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Converts {@link QSecond} object to {@link Date} instance.
     * 
     * @return {@link Date} representing q value.
     */
    public Date toDateTime() {
        if ( datetime == null && value != Integer.MIN_VALUE ) {
            final Calendar c = Calendar.getInstance();
            c.set(2000, 0, 1, value / 3600, (value / 60) % 60, value % 60);
            datetime = c.getTime();
        }
        return datetime;
    }

    /**
     * Returns a String that represents the current {@link QSecond}.
     * 
     * @return a String representation of the {@link QSecond}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if ( value == Integer.MIN_VALUE ) {
            return NULL_STR;
        } else {
            final int seconds = Math.abs(value);
            final int minutes = seconds / 60;
            final int hours = minutes / 60;

            return String.format("%s%02d:%02d:%02d", value < 0 ? "-" : "", hours, minutes % 60, seconds % 60);
        }
    }

    /**
     * Indicates whether some other object is "equal to" this date. {@link QSecond} objects are considered equal if the
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

        if ( !(obj instanceof QSecond) ) {
            return false;
        }

        return value.equals(((QSecond) obj).getValue());
    }

    /**
     * Returns a hash code value for this {@link QSecond}.
     * 
     * @return a hash code value for this object
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Returns a {@link QSecond} represented by a given string.
     * 
     * @param date
     *            as {@link String}
     * @return a {@link QSecond} instance representing date.
     * @throws IllegalArgumentException
     *             when date cannot be parsed
     */
    public static QSecond fromString( final String date ) {
        if ( date == null || date.length() == 0 || date.equals(NULL_STR) ) {
            return new QSecond(Integer.MIN_VALUE);
        }

        try {
            final String[] parts = date.split(":");
            final int hours = Integer.parseInt(parts[0]);
            final int minutes = Integer.parseInt(parts[1]);
            final int seconds = Integer.parseInt(parts[2]);
            return new QSecond((seconds + 60 * minutes + 3600 * Math.abs(hours)) * (hours > 0 ? 1 : -1));
        } catch ( final Exception e ) {
            throw new IllegalArgumentException("Cannot parse QSecond from: " + date, e);
        }
    }

}
