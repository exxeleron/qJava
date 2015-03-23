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

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for conversions between q and Java types.
 */
public enum QType {
    NULL_ITEM(101),
    ERROR(-128),
    GENERAL_LIST(0),
    BOOL(-1),
    BOOL_LIST(1),
    GUID(-2),
    GUID_LIST(2),
    BYTE(-4),
    BYTE_LIST(4),
    SHORT(-5),
    SHORT_LIST(5),
    INT(-6),
    INT_LIST(6),
    LONG(-7),
    LONG_LIST(7),
    FLOAT(-8),
    FLOAT_LIST(8),
    DOUBLE(-9),
    DOUBLE_LIST(9),
    CHAR(-10),
    STRING(10),
    SYMBOL(-11),
    SYMBOL_LIST(11),
    TIMESTAMP(-12),
    TIMESTAMP_LIST(12),
    MONTH(-13),
    MONTH_LIST(13),
    DATE(-14),
    DATE_LIST(14),
    DATETIME(-15),
    DATETIME_LIST(15),
    TIMESPAN(-16),
    TIMESPAN_LIST(16),
    MINUTE(-17),
    MINUTE_LIST(17),
    SECOND(-18),
    SECOND_LIST(18),
    TIME(-19),
    TIME_LIST(19),
    TABLE(98),
    KEYED_TABLE(99),
    DICTIONARY(99),
    LAMBDA(100),
    UNARY_PRIMITIVE_FUNC(101),
    BINARY_PRIMITIVE_FUNC(102),
    TERNARY_OPERATOR_FUNC(103),
    COMPOSITION_FUNC(105),
    ADVERB_FUNC_106(106),
    ADVERB_FUNC_107(107),
    ADVERB_FUNC_108(108),
    ADVERB_FUNC_109(109),
    ADVERB_FUNC_110(110),
    ADVERB_FUNC_111(111),
    @Deprecated
    LAMBDA_PART(104),
    PROJECTION(104);

    QType(final int code) {
        this.code = (byte) code;
    }

    private byte code;

    byte getTypeCode() {
        return code;
    }

    @SuppressWarnings("rawtypes")
    private static final Map<Class, QType> toQ = Collections.unmodifiableMap(new HashMap<Class, QType>() {
        private static final long serialVersionUID = 7199217298785029447L;

        {
            put(Object[].class, GENERAL_LIST);
            put(Boolean.class, BOOL);
            put(boolean[].class, BOOL_LIST);
            put(Boolean[].class, BOOL_LIST);
            put(Byte.class, BYTE);
            put(byte[].class, BYTE_LIST);
            put(Byte[].class, BYTE_LIST);
            put(UUID.class, GUID);
            put(UUID[].class, GUID_LIST);
            put(Short.class, SHORT);
            put(short[].class, SHORT_LIST);
            put(Short[].class, SHORT_LIST);
            put(Integer.class, INT);
            put(int[].class, INT_LIST);
            put(Integer[].class, INT_LIST);
            put(Long.class, LONG);
            put(long[].class, LONG_LIST);
            put(Long[].class, LONG_LIST);
            put(Float.class, FLOAT);
            put(float[].class, FLOAT_LIST);
            put(Float[].class, FLOAT_LIST);
            put(Double.class, DOUBLE);
            put(double[].class, DOUBLE_LIST);
            put(Double[].class, DOUBLE_LIST);
            put(Character.class, CHAR);
            put(char[].class, STRING);
            put(char[][].class, GENERAL_LIST);
            put(String.class, SYMBOL);
            put(String[].class, SYMBOL_LIST);
            put(QTimestamp.class, TIMESTAMP);
            put(QTimestamp[].class, TIMESTAMP_LIST);
            put(QMonth.class, MONTH);
            put(QMonth[].class, MONTH_LIST);
            put(QDate.class, DATE);
            put(QDate[].class, DATE_LIST);
            put(QDateTime.class, DATETIME);
            put(QDateTime[].class, DATETIME_LIST);
            put(QTimespan.class, TIMESPAN);
            put(QTimespan[].class, TIMESPAN_LIST);
            put(QMinute.class, MINUTE);
            put(QMinute[].class, MINUTE_LIST);
            put(QSecond.class, SECOND);
            put(QSecond[].class, SECOND_LIST);
            put(QTime.class, TIME);
            put(QTime[].class, TIME_LIST);
            put(QException.class, ERROR);
            put(QDictionary.class, DICTIONARY);
            put(QTable.class, TABLE);
            put(QKeyedTable.class, KEYED_TABLE);
            put(QLambda.class, LAMBDA);
            put(QProjection.class, PROJECTION);
        }
    });

    @SuppressWarnings("rawtypes")
    private static final Map<QType, Class> fromQ = Collections.unmodifiableMap(new HashMap<QType, Class>() {
        private static final long serialVersionUID = 7199217298785029445L;

        {
            put(GENERAL_LIST, Object[].class);
            put(BOOL, Boolean.class);
            put(BOOL_LIST, boolean[].class);
            put(BYTE, Byte.class);
            put(BYTE_LIST, byte[].class);
            put(GUID, UUID.class);
            put(GUID_LIST, UUID[].class);
            put(SHORT, Short.class);
            put(SHORT_LIST, short[].class);
            put(INT, Integer.class);
            put(INT_LIST, int[].class);
            put(LONG, Long.class);
            put(LONG_LIST, long[].class);
            put(FLOAT, Float.class);
            put(FLOAT_LIST, float[].class);
            put(DOUBLE, Double.class);
            put(DOUBLE_LIST, double[].class);
            put(CHAR, Character.class);
            put(STRING, char[].class);
            put(SYMBOL, String.class);
            put(SYMBOL_LIST, String[].class);
            put(TIMESTAMP, QTimestamp.class);
            put(TIMESTAMP_LIST, QTimestamp[].class);
            put(MONTH, QMonth.class);
            put(MONTH_LIST, QMonth[].class);
            put(DATE, QDate.class);
            put(DATE_LIST, QDate[].class);
            put(DATETIME, QDateTime.class);
            put(DATETIME_LIST, QDateTime[].class);
            put(TIMESPAN, QTimespan.class);
            put(TIMESPAN_LIST, QTimespan[].class);
            put(MINUTE, QMinute.class);
            put(MINUTE_LIST, QMinute[].class);
            put(SECOND, QSecond.class);
            put(SECOND_LIST, QSecond[].class);
            put(TIME, QTime.class);
            put(TIME_LIST, QTime[].class);
            put(ERROR, QException.class);
            put(DICTIONARY, QDictionary.class);
            put(TABLE, QTable.class);
            put(KEYED_TABLE, QKeyedTable.class);
            put(LAMBDA, QLambda.class);
        }
    });

    private static final Map<QType, Object> qNulls = Collections.unmodifiableMap(new HashMap<QType, Object>() {
        private static final long serialVersionUID = 7199217298785029443L;

        {
            put(BOOL, false);
            put(BYTE, (byte) 0);
            put(GUID, new UUID(0, 0));
            put(SHORT, Short.MIN_VALUE);
            put(INT, Integer.MIN_VALUE);
            put(LONG, Long.MIN_VALUE);
            put(FLOAT, Float.NaN);
            put(DOUBLE, Double.NaN);
            put(CHAR, ' ');
            put(SYMBOL, "");
            put(TIMESTAMP, new QTimestamp(Long.MIN_VALUE));
            put(MONTH, new QMonth(Integer.MIN_VALUE));
            put(DATE, new QDate(Integer.MIN_VALUE));
            put(DATETIME, new QDateTime(Double.NaN));
            put(TIMESPAN, new QTimespan(Long.MIN_VALUE));
            put(MINUTE, new QMinute(Integer.MIN_VALUE));
            put(SECOND, new QSecond(Integer.MIN_VALUE));
            put(TIME, new QTime(Integer.MIN_VALUE));
        }
    });

    private static final Map<Byte, QType> lookup = Collections.unmodifiableMap(new HashMap<Byte, QType>() {
        private static final long serialVersionUID = 7199217298785029441L;

        {
            for ( final QType qtype : EnumSet.allOf(QType.class) ) {
                put(qtype.getTypeCode(), qtype);
            }
        }

    });

    /**
     * Returns {@link QType} based on type code identifier.
     * 
     * @param typecode
     *            type code identifier
     * @return {@link QType} enum bound with type code identifier
     * @throws QReaderException
     */
    public static QType getQType( final Byte typecode ) throws QReaderException {
        if ( lookup.containsKey(typecode) ) {
            return lookup.get(typecode);
        } else {
            throw new QReaderException("Cannot deserialize object of type: " + typecode);
        }
    }

    /**
     * Returns default mapping for particular java object to representative q type.
     * 
     * @param obj
     *            Requested object
     * @return {@link QType} enum being a result of q serialization
     * @throws QWriterException
     */
    public static QType getQType( final Object obj ) throws QWriterException {
        if ( obj == null ) {
            return QType.NULL_ITEM;
        } else if ( toQ.containsKey(obj.getClass()) ) {
            return toQ.get(obj.getClass());
        } else {
            throw new QWriterException("Cannot serialize object of type: " + obj.getClass().getCanonicalName());
        }
    }

    /**
     * Returns default mapping for particular q type.
     * 
     * @param type
     *            Requested q type
     * @return type of the object being a result of q message deserialization
     * @throws QReaderException
     */
    public static Class<?> getType( final QType type ) throws QReaderException {
        if ( fromQ.containsKey(type) ) {
            return fromQ.get(type);
        } else {
            throw new QReaderException("Cannot deserialize object of type: " + type);
        }
    }

    /**
     * Returns object representing q null of particular type.
     * 
     * @param type
     *            Requested null type
     * @return object representing q null
     * @throws QException
     */
    public static Object getQNull( final QType type ) throws QException {
        if ( qNulls.containsKey(type) ) {
            return qNulls.get(type);
        } else {
            throw new QException("Cannot find null value of type: " + type);
        }
    }
}
