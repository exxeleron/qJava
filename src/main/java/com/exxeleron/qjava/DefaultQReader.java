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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Default {@link QReader} implementation.
 */
public class DefaultQReader extends QReader {

    /**
     * @see com.exxeleron.qjava.QReader#readObject()
     */
    protected Object readObject() throws QException, IOException {
        final QType qtype = QType.getQType(reader.get());

        if ( qtype == QType.GENERAL_LIST ) {
            return readGeneralList();
        } else if ( qtype == QType.ERROR ) {
            throw readError();
        } else if ( qtype == QType.DICTIONARY ) {
            return readDictionary();
        } else if ( qtype == QType.TABLE ) {
            return readTable();
        } else if ( qtype.getTypeCode() < 0 ) {
            return readAtom(qtype);
        } else if ( qtype.getTypeCode() >= QType.BOOL_LIST.getTypeCode() && qtype.getTypeCode() <= QType.TIME_LIST.getTypeCode() ) {
            return readList(qtype);
        } else if ( qtype.getTypeCode() >= QType.LAMBDA.getTypeCode() ) {
            return readFunction(qtype);
        }

        throw new QReaderException("Unable to deserialize q type: " + qtype);
    }

    @SuppressWarnings("incomplete-switch")
    protected Object readAtom( final QType qtype ) throws QException, UnsupportedEncodingException {
        switch ( qtype ) {
        case BOOL:
            return reader.get() == 1 ? true : false;
        case GUID:
            return readGuid();
        case BYTE:
            return reader.get();
        case SHORT:
            return reader.getShort();
        case INT:
            return reader.getInt();
        case LONG:
            return reader.getLong();
        case FLOAT:
            return reader.getFloat();
        case DOUBLE:
            return reader.getDouble();
        case CHAR:
            return (char) reader.get();
        case SYMBOL:
            return reader.getSymbol();
        case TIMESTAMP:
            return new QTimestamp(reader.getLong());
        case MONTH:
            return new QMonth(reader.getInt());
        case DATE:
            return new QDate(reader.getInt());
        case DATETIME:
            return new QDateTime(reader.getDouble());
        case TIMESPAN:
            return new QTimespan(reader.getLong());
        case MINUTE:
            return new QMinute(reader.getInt());
        case SECOND:
            return new QSecond(reader.getInt());
        case TIME:
            return new QTime(reader.getInt());
        }

        throw new QReaderException("Unable to deserialize q type: " + qtype);
    }

    @SuppressWarnings("incomplete-switch")
    protected Object readList( final QType qtype ) throws QException, UnsupportedEncodingException {
        reader.get(); // ignore attributes
        final int length = reader.getInt();

        switch ( qtype ) {
        case BOOL_LIST: {
            final boolean[] list = new boolean[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.get() == 1 ? true : false;
            }
            return list;
        }

        case GUID_LIST: {
            final UUID[] list = new UUID[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = readGuid();
            }
            return list;
        }

        case BYTE_LIST: {
            final byte[] list = new byte[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.get();
            }
            return list;
        }
        case SHORT_LIST: {
            final short[] list = new short[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getShort();
            }
            return list;
        }
        case INT_LIST: {
            final int[] list = new int[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getInt();
            }
            return list;
        }
        case LONG_LIST: {
            final long[] list = new long[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getLong();
            }
            return list;
        }
        case FLOAT_LIST: {
            final float[] list = new float[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getFloat();
            }
            return list;
        }
        case DOUBLE_LIST: {
            final double[] list = new double[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getDouble();
            }
            return list;
        }
        case STRING: {
            final byte[] buffer = new byte[length];
            reader.get(buffer, 0, length);
            return new String(buffer, getEncoding()).toCharArray();
        }
        case SYMBOL_LIST: {
            final String[] list = new String[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = reader.getSymbol();
            }
            return list;
        }
        case TIMESTAMP_LIST: {
            final QTimestamp[] list = new QTimestamp[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QTimestamp(reader.getLong());
            }
            return list;
        }
        case MONTH_LIST: {
            final QMonth[] list = new QMonth[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QMonth(reader.getInt());
            }
            return list;
        }
        case DATE_LIST: {
            final QDate[] list = new QDate[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QDate(reader.getInt());
            }
            return list;
        }
        case DATETIME_LIST: {
            final QDateTime[] list = new QDateTime[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QDateTime(reader.getDouble());
            }
            return list;
        }
        case TIMESPAN_LIST: {
            final QTimespan[] list = new QTimespan[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QTimespan(reader.getLong());
            }
            return list;
        }
        case MINUTE_LIST: {
            final QMinute[] list = new QMinute[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QMinute(reader.getInt());
            }
            return list;
        }
        case SECOND_LIST: {
            final QSecond[] list = new QSecond[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QSecond(reader.getInt());
            }
            return list;
        }
        case TIME_LIST: {
            final QTime[] list = new QTime[length];
            for ( int i = 0; i < length; i++ ) {
                list[i] = new QTime(reader.getInt());
            }
            return list;
        }
        }

        throw new QReaderException("Unable to deserialize q type: " + qtype);
    }

    protected UUID readGuid() {
        final ByteOrder currentOrder = reader.getOrder();
        reader.setOrder(ByteOrder.BIG_ENDIAN);
        final long l1 = reader.getLong();
        final long l2 = reader.getLong();
        reader.setOrder(currentOrder);
        return new UUID(l1, l2);
    }

    protected Object[] readGeneralList() throws QException, IOException {
        reader.get(); // ignore attributes
        final int length = reader.getInt();
        final Object[] list = new Object[length];

        for ( int i = 0; i < length; i++ ) {
            list[i] = readObject();
        }

        return list;
    }

    protected QException readError() throws IOException {
        return new QException(reader.getSymbol());
    }

    protected Object readDictionary() throws QException, IOException {
        final Object keys = readObject();
        final Object values = readObject();

        if ( keys != null && keys.getClass().isArray() && (values != null && values.getClass().isArray() || values instanceof QTable) ) {
            return new QDictionary(keys, values);
        } else if ( keys instanceof QTable && values instanceof QTable ) {
            return new QKeyedTable((QTable) keys, (QTable) values);
        }

        throw new QReaderException("Cannot create valid dictionary object from mapping: " + keys + " to " + values);
    }

    protected QTable readTable() throws QException, IOException {
        reader.get(); // attributes
        reader.get(); // dict type stamp
        return new QTable((String[]) readObject(), (Object[]) readObject());
    }

    protected QFunction readFunction( final QType qtype ) throws QException, IOException {
        if ( qtype == QType.LAMBDA ) {
            reader.getSymbol(); // ignore context
            final String expression = new String((char[]) readObject());
            return new QLambda(expression);
        } else if ( qtype == QType.PROJECTION ) {
            final int length = reader.getInt();
            final Object[] parameters = new Object[length];
            for ( int i = 0; i < length; i++ ) {
                parameters[i] = readObject();
            }
            return new QProjection(parameters);
        } else if ( qtype == QType.UNARY_PRIMITIVE_FUNC ) {
            final byte code = reader.get();
            return code == 0 ? null : new QFunction(qtype.getTypeCode());
        } else if ( qtype.getTypeCode() < QType.PROJECTION.getTypeCode() ) {
            reader.get(); // ignore function code
            return new QFunction(qtype.getTypeCode());
        } else if ( qtype == QType.COMPOSITION_FUNC ) {
            final int length = reader.getInt();
            final Object[] parameters = new Object[length];
            for ( int i = 0; i < length; i++ ) {
                parameters[i] = readObject();
            }
            return new QFunction(qtype.getTypeCode());
        } else {
            readObject(); // ignore function object
            return new QFunction(qtype.getTypeCode());
        }
    }

    @SuppressWarnings("rawtypes")
    private static final Map<QType, Class> fromQ = Collections.unmodifiableMap(new HashMap<QType, Class>() {
        private static final long serialVersionUID = 7199217298785029445L;

        {
            put(QType.GENERAL_LIST, Object[].class);
            put(QType.BOOL, Boolean.class);
            put(QType.BOOL_LIST, boolean[].class);
            put(QType.BYTE, Byte.class);
            put(QType.BYTE_LIST, byte[].class);
            put(QType.GUID, UUID.class);
            put(QType.GUID_LIST, UUID[].class);
            put(QType.SHORT, Short.class);
            put(QType.SHORT_LIST, short[].class);
            put(QType.INT, Integer.class);
            put(QType.INT_LIST, int[].class);
            put(QType.LONG, Long.class);
            put(QType.LONG_LIST, long[].class);
            put(QType.FLOAT, Float.class);
            put(QType.FLOAT_LIST, float[].class);
            put(QType.DOUBLE, Double.class);
            put(QType.DOUBLE_LIST, double[].class);
            put(QType.CHAR, Character.class);
            put(QType.STRING, char[].class);
            put(QType.SYMBOL, String.class);
            put(QType.SYMBOL_LIST, String[].class);
            put(QType.TIMESTAMP, QTimestamp.class);
            put(QType.TIMESTAMP_LIST, QTimestamp[].class);
            put(QType.MONTH, QMonth.class);
            put(QType.MONTH_LIST, QMonth[].class);
            put(QType.DATE, QDate.class);
            put(QType.DATE_LIST, QDate[].class);
            put(QType.DATETIME, QDateTime.class);
            put(QType.DATETIME_LIST, QDateTime[].class);
            put(QType.TIMESPAN, QTimespan.class);
            put(QType.TIMESPAN_LIST, QTimespan[].class);
            put(QType.MINUTE, QMinute.class);
            put(QType.MINUTE_LIST, QMinute[].class);
            put(QType.SECOND, QSecond.class);
            put(QType.SECOND_LIST, QSecond[].class);
            put(QType.TIME, QTime.class);
            put(QType.TIME_LIST, QTime[].class);
            put(QType.ERROR, QException.class);
            put(QType.DICTIONARY, QDictionary.class);
            put(QType.TABLE, QTable.class);
            put(QType.KEYED_TABLE, QKeyedTable.class);
            put(QType.LAMBDA, QLambda.class);
        }
    });

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
}
