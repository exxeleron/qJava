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
 * Represents q timespan type.
 */
public final class QTimespan implements DateTime, Serializable {

    private static final long serialVersionUID = 762296525233866140L;

    static final String NULL_STR = "0Nn";

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
        return (c.get(Calendar.MILLISECOND) + 1000 * c.get(Calendar.SECOND) + 60000 * c.get(Calendar.MINUTE) + 3600000 * c.get(Calendar.HOUR_OF_DAY))
                * Utils.NANOS_PER_MILLI;
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
            datetime = new Date(Utils.tzOffsetToQ(Math.abs(value) / Utils.NANOS_PER_MILLI + Utils.QEPOCH_MILLIS));
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

        if ( dt == null ) {
            return NULL_STR;
        } else {
            final StringBuilder buffer = new StringBuilder(30);
            final String zeros = "000000000";

            if ( value < 0 ) {
                buffer.append('-');
            }
            final long absValue = value < 0 ? -value : value;
            final int days = ((int) (absValue / Utils.NANOS_PER_DAY));
            buffer.append(days);
            buffer.append('D');

            final int hour = (int) ((absValue % Utils.NANOS_PER_DAY) / Utils.NANOS_PER_HOUR);
            final int minute = (int) ((absValue % Utils.NANOS_PER_HOUR) / Utils.NANOS_PER_MINUTE);
            final int second = (int) ((absValue % Utils.NANOS_PER_MINUTE) / Utils.NANOS_PER_SECOND);
            final int nanos = (int) (absValue % Utils.NANOS_PER_SECOND);
            String hourString;
            String minuteString;
            String secondString;
            String nanosString;

            if ( hour < 10 ) {
                hourString = "0" + hour;
            } else {
                hourString = Integer.toString(hour);
            }
            if ( minute < 10 ) {
                minuteString = "0" + minute;
            } else {
                minuteString = Integer.toString(minute);
            }
            if ( second < 10 ) {
                secondString = "0" + second;
            } else {
                secondString = Integer.toString(second);
            }
            if ( nanos == 0 ) {
                nanosString = zeros;
            } else {
                nanosString = Integer.toString(nanos);
                nanosString = zeros.substring(0, (9 - nanosString.length())) + nanosString;
            }

            buffer.append(hourString);
            buffer.append(':');
            buffer.append(minuteString);
            buffer.append(':');
            buffer.append(secondString);
            buffer.append('.');
            buffer.append(nanosString);

            return buffer.toString();
        }
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
            if ( date == null || date.length() == 0 || date.equals(QTimespan.NULL_STR) ) {
                return new QTimespan(Long.MIN_VALUE);
            } else {
                final String formatError = "Timespan format must be [-]dDhh:mm:ss.fffffffff";
                final String zeros = "000000000";

                String date_s;
                String time_s;
                String nanos_s;
                int day = 0;
                int hour;
                int minute;
                int second;
                int a_nanos = 0;

                // Split the string into date and time components
                final String s = date.trim();
                final int dividingCharacter = s.indexOf('D');
                if ( dividingCharacter > 0 ) {
                    date_s = s.substring(0, dividingCharacter);
                    time_s = s.substring(dividingCharacter + 1);
                } else {
                    throw new java.lang.IllegalArgumentException(formatError);
                }

                // Parse the time
                if ( time_s == null ) {
                    throw new java.lang.IllegalArgumentException(formatError);
                }

                final int firstDigit = date_s.indexOf('-') + 1;
                final int firstColon = time_s.indexOf(':');
                final int secondColon = time_s.indexOf(':', firstColon + 1);
                final int period = time_s.indexOf('.', secondColon + 1);

                // Convert the date
                final boolean is_negative = firstDigit > 0;

                if ( date_s.length() >= 0 ) {
                    final String day_s = date_s.substring(firstDigit);
                    day = Integer.parseInt(day_s);
                } else {
                    throw new java.lang.IllegalArgumentException(formatError);
                }

                // Convert the time
                if ( (firstColon > 0) & (secondColon > 0) & (secondColon < time_s.length() - 1) ) {
                    hour = Integer.parseInt(time_s.substring(0, firstColon));
                    minute = Integer.parseInt(time_s.substring(firstColon + 1, secondColon));
                    if ( (period > 0) & (period < time_s.length() - 1) ) {
                        second = Integer.parseInt(time_s.substring(secondColon + 1, period));
                        nanos_s = time_s.substring(period + 1);
                        if ( nanos_s.length() > 9 ) {
                            throw new java.lang.IllegalArgumentException(formatError);
                        }
                        if ( !Character.isDigit(nanos_s.charAt(0)) ) {
                            throw new java.lang.IllegalArgumentException(formatError);
                        }
                        nanos_s = nanos_s + zeros.substring(0, 9 - nanos_s.length());
                        a_nanos = Integer.parseInt(nanos_s);
                    } else if ( period > 0 ) {
                        throw new java.lang.IllegalArgumentException(formatError);
                    } else {
                        second = Integer.parseInt(time_s.substring(secondColon + 1));
                    }
                } else {
                    throw new java.lang.IllegalArgumentException(formatError);
                }

                final long nanos = day * Utils.NANOS_PER_DAY + hour * Utils.NANOS_PER_HOUR + minute * Utils.NANOS_PER_MINUTE
                        + second * Utils.NANOS_PER_SECOND + a_nanos;
                return new QTimespan((is_negative ? -1 : 1) * nanos);
            }
        } catch (final Exception e) {
            throw new IllegalArgumentException("Cannot parse QTimespan from: " + date, e);
        }
    }

}
