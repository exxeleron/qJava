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
import java.util.Date;

/**
 * Represents q timestamp type.
 */
public final class QTimestamp implements DateTime, Serializable {
    private static final long serialVersionUID = 762296525233866140L;

    private static final String NULL_STR = "0Np";

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
        return Utils.NANOS_PER_MILLI * (Utils.tzOffsetFromQ(datetime.getTime()) - Utils.QEPOCH_MILLIS);
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
            final boolean adjustMillis = (value % Utils.NANOS_PER_SECOND) >= 0;
            final long millis = value / Utils.NANOS_PER_MILLI - (adjustMillis ? 0 : 1);
            datetime = new Date(Utils.tzOffsetToQ(millis + Utils.QEPOCH_MILLIS));
        }
        return datetime;
    }

    /**
     * Returns a String that represents the current {@link QTimestamp}.
     * 
     * @return a String representation of the {@link QTimestamp}
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("deprecation")
    @Override
    public String toString() {
        final Date dt = toDateTime();

        if ( dt == null ) {
            return NULL_STR;
        } else {
            final String zeros = "000000000";
            final String yearZeros = "0000";
            final int year = dt.getYear() + 1900;
            final int month = dt.getMonth() + 1;
            final int day = dt.getDate();
            final int hour = dt.getHours();
            final int minute = dt.getMinutes();
            final int second = dt.getSeconds();
            int nanos = (int) (value % Utils.NANOS_PER_SECOND);
            nanos = nanos >= 0 ? nanos : (int) Utils.NANOS_PER_SECOND + (int) (value % Utils.NANOS_PER_SECOND);
            String yearString;
            String monthString;
            String dayString;
            String hourString;
            String minuteString;
            String secondString;
            String nanosString;
            StringBuilder buffer;

            if ( year < 1000 ) {
                yearString = "" + year;
                yearString = yearZeros.substring(0, (4 - yearString.length())) + yearString;
            } else {
                yearString = "" + year;
            }
            if ( month < 10 ) {
                monthString = "0" + month;
            } else {
                monthString = Integer.toString(month);
            }
            if ( day < 10 ) {
                dayString = "0" + day;
            } else {
                dayString = Integer.toString(day);
            }
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

            buffer = new StringBuilder(20 + nanosString.length());
            buffer.append(yearString);
            buffer.append('.');
            buffer.append(monthString);
            buffer.append('.');
            buffer.append(dayString);
            buffer.append('D');
            buffer.append(hourString);
            buffer.append(':');
            buffer.append(minuteString);
            buffer.append(':');
            buffer.append(secondString);
            buffer.append('.');
            buffer.append(nanosString);

            return (buffer.toString());
        }
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
    @SuppressWarnings("deprecation")
    public static QTimestamp fromString( final String date ) {
        try {
            if ( date == null || date.length() == 0 || date.equals(NULL_STR) ) {
                return new QTimestamp(Long.MIN_VALUE);
            } else {
                final String formatError = "Timestamp format must be yyyy.mm.ddDhh:mm:ss.fffffffff";
                final String zeros = "000000000";
                final int YEAR_LENGTH = 4;
                final int MONTH_LENGTH = 2;
                final int DAY_LENGTH = 2;
                final int MAX_MONTH = 12;
                final int MAX_DAY = 31;
                String date_s;
                String time_s;
                String nanos_s;
                int year = 0;
                int month = 0;
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

                // Parse the date
                final int firstDot = date_s.indexOf('.');
                final int secondDot = date_s.indexOf('.', firstDot + 1);

                // Parse the time
                if ( time_s == null ) {
                    throw new java.lang.IllegalArgumentException(formatError);
                }

                final int firstColon = time_s.indexOf(':');
                final int secondColon = time_s.indexOf(':', firstColon + 1);
                final int period = time_s.indexOf('.', secondColon + 1);

                // Convert the date
                boolean parsedDate = false;
                if ( (firstDot > 0) && (secondDot > 0) && (secondDot < date_s.length() - 1) ) {
                    final String yyyy = date_s.substring(0, firstDot);
                    final String mm = date_s.substring(firstDot + 1, secondDot);
                    final String dd = date_s.substring(secondDot + 1);
                    if ( yyyy.length() == YEAR_LENGTH && (mm.length() >= 1 && mm.length() <= MONTH_LENGTH)
                            && (dd.length() >= 1 && dd.length() <= DAY_LENGTH) ) {
                        year = Integer.parseInt(yyyy);
                        month = Integer.parseInt(mm);
                        day = Integer.parseInt(dd);

                        if ( (month >= 1 && month <= MAX_MONTH) && (day >= 1 && day <= MAX_DAY) ) {
                            parsedDate = true;
                        }
                    }
                }
                if ( !parsedDate ) {
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

                    return new QTimestamp(getNanos(new Date(year - 1900, month - 1, day, hour, minute, second)) + a_nanos);
                } else {
                    throw new java.lang.IllegalArgumentException(formatError);
                }
            }
        } catch (final Exception e) {
            throw new IllegalArgumentException("Cannot parse QTimestamp from: " + date, e);
        }
    }

}
