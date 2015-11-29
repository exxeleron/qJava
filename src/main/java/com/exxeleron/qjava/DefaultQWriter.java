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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Default {@link QWriter} implementation.
 */
public class DefaultQWriter extends QWriter {

    /**
     * @see com.exxeleron.qjava.QWriter#writeObject(java.lang.Object)
     */
    @Override
    protected void writeObject( final Object obj ) throws IOException, QException {
        if ( obj instanceof Collection<?> ) {
            writeCollection((Collection<?>) obj);
        } else if ( obj instanceof Map<?, ?> ) {
            writeMap((Map<?, ?>) obj);
        } else {
            final QType qtype = getQType(obj);
            checkProtocolVersionCompatibility(qtype);

            if ( qtype == QType.STRING ) {
                writeString((char[]) obj);
            } else if ( qtype == QType.GENERAL_LIST ) {
                writeGeneralList((Object[]) obj);
            } else if ( qtype == QType.NULL_ITEM ) {
                writeNullItem();
            } else if ( qtype == QType.ERROR ) {
                writeError((Exception) obj);
            } else if ( qtype == QType.DICTIONARY ) {
                writeDictionary((QDictionary) obj);
            } else if ( qtype == QType.TABLE ) {
                writeTable((QTable) obj);
            } else if ( qtype == QType.KEYED_TABLE ) {
                writeKeyedTable((QKeyedTable) obj);
            } else if ( qtype.getTypeCode() < 0 ) {
                writeAtom(obj, qtype);
            } else if ( qtype.getTypeCode() >= QType.BOOL_LIST.getTypeCode() && qtype.getTypeCode() <= QType.TIME_LIST.getTypeCode() ) {
                writeList(obj, qtype);
            } else if ( qtype == QType.LAMBDA ) {
                writeLambda((QLambda) obj);
            } else if ( qtype == QType.PROJECTION ) {
                writeProjection((QProjection) obj);
            } else {
                throw new QWriterException("Unable to serialize q type: " + qtype);
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected void writeAtom( final Object obj, final QType qtype ) throws IOException {
        writer.writeByte(qtype.getTypeCode());
        switch ( qtype ) {
        case BOOL:
            writer.writeByte((byte) ((Boolean) obj ? 1 : 0));
            break;
        case GUID:
            writeGuid((UUID) obj);
            break;
        case BYTE:
            writer.writeByte((Byte) obj);
            break;
        case SHORT:
            writer.writeShort((Short) obj);
            break;
        case INT:
            writer.writeInt((Integer) obj);
            break;
        case LONG:
            writer.writeLong((Long) obj);
            break;
        case FLOAT:
            writer.writeFloat((Float) obj);
            break;
        case DOUBLE:
            writer.writeDouble((Double) obj);
            break;
        case CHAR:
            writer.writeByte((byte) (char) (Character) obj);
            break;
        case SYMBOL:
            writeSymbol((String) obj);
            break;
        case TIMESTAMP:
            writer.writeLong(((QTimestamp) obj).getValue());
            break;
        case MONTH:
            writer.writeInt(((QMonth) obj).getValue());
            break;
        case DATE:
            writer.writeInt(((QDate) obj).getValue());
            break;
        case DATETIME:
            writer.writeDouble(((QDateTime) obj).getValue());
            break;
        case TIMESPAN:
            writer.writeLong(((QTimespan) obj).getValue());
            break;
        case MINUTE:
            writer.writeInt(((QMinute) obj).getValue());
            break;
        case SECOND:
            writer.writeInt(((QSecond) obj).getValue());
            break;
        case TIME:
            writer.writeInt(((QTime) obj).getValue());
            break;
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected void writeList( final Object obj, final QType qtype ) throws IOException {
        writer.writeByte(qtype.getTypeCode());
        writer.writeByte((byte) 0); // attributes

        switch ( qtype ) {
        case BOOL_LIST: {
            if ( obj instanceof boolean[] ) {
                final boolean[] list = (boolean[]) obj;
                writer.writeInt(list.length);
                for ( final boolean e : list ) {
                    writer.writeByte((byte) (e ? 1 : 0));
                }
            } else if ( obj instanceof Boolean[] ) {
                final Boolean[] list = (Boolean[]) obj;
                writer.writeInt(list.length);
                for ( final Boolean e : list ) {
                    writer.writeByte((byte) (e ? 1 : 0));
                }
            }
            break;
        }
        case GUID_LIST: {
            final UUID[] list = (UUID[]) obj;
            writer.writeInt(list.length);
            for ( final UUID e : list ) {
                writeGuid(e);
            }
            break;
        }
        case BYTE_LIST: {
            if ( obj instanceof byte[] ) {
                final byte[] list = (byte[]) obj;
                writer.writeInt(list.length);
                for ( final byte e : list ) {
                    writer.writeByte(e);
                }
            } else if ( obj instanceof Byte[] ) {
                final Byte[] list = (Byte[]) obj;
                writer.writeInt(list.length);
                for ( final Byte e : list ) {
                    writer.writeByte(e);
                }
            }
            break;
        }
        case SHORT_LIST: {
            if ( obj instanceof short[] ) {
                final short[] list = (short[]) obj;
                writer.writeInt(list.length);
                for ( final short e : list ) {
                    writer.writeShort(e);
                }
            } else if ( obj instanceof Short[] ) {
                final Short[] list = (Short[]) obj;
                writer.writeInt(list.length);
                for ( final Short e : list ) {
                    writer.writeShort(e);
                }
            }
            break;
        }
        case INT_LIST: {
            if ( obj instanceof int[] ) {
                final int[] list = (int[]) obj;
                writer.writeInt(list.length);
                for ( final int e : list ) {
                    writer.writeInt(e);
                }
            } else if ( obj instanceof Integer[] ) {
                final Integer[] list = (Integer[]) obj;
                writer.writeInt(list.length);
                for ( final Integer e : list ) {
                    writer.writeInt(e);
                }
            }
            break;
        }
        case LONG_LIST: {
            if ( obj instanceof long[] ) {
                final long[] list = (long[]) obj;
                writer.writeInt(list.length);
                for ( final long e : list ) {
                    writer.writeLong(e);
                }
            } else if ( obj instanceof Long[] ) {
                final Long[] list = (Long[]) obj;
                writer.writeInt(list.length);
                for ( final Long e : list ) {
                    writer.writeLong(e);
                }
            }
            break;
        }
        case FLOAT_LIST: {
            if ( obj instanceof float[] ) {
                final float[] list = (float[]) obj;
                writer.writeInt(list.length);
                for ( final float e : list ) {
                    writer.writeFloat(e);
                }
            } else if ( obj instanceof Float[] ) {
                final Float[] list = (Float[]) obj;
                writer.writeInt(list.length);
                for ( final Float e : list ) {
                    writer.writeFloat(e);
                }
            }
            break;
        }
        case DOUBLE_LIST: {
            if ( obj instanceof double[] ) {
                final double[] list = (double[]) obj;
                writer.writeInt(list.length);
                for ( final double e : list ) {
                    writer.writeDouble(e);
                }
            } else if ( obj instanceof Double[] ) {
                final Double[] list = (Double[]) obj;
                writer.writeInt(list.length);
                for ( final Double e : list ) {
                    writer.writeDouble(e);
                }
            }
            break;
        }
        case SYMBOL_LIST: {
            final String[] list = (String[]) obj;
            writer.writeInt(list.length);
            for ( final String e : list ) {
                writeSymbol(e);
            }
            break;
        }
        case TIMESTAMP_LIST: {
            final QTimestamp[] list = (QTimestamp[]) obj;
            writer.writeInt(list.length);
            for ( final QTimestamp e : list ) {
                writer.writeLong(e.getValue());
            }
            break;
        }
        case MONTH_LIST: {
            final QMonth[] list = (QMonth[]) obj;
            writer.writeInt(list.length);
            for ( final QMonth e : list ) {
                writer.writeInt(e.getValue());
            }
            break;
        }
        case DATE_LIST: {
            final QDate[] list = (QDate[]) obj;
            writer.writeInt(list.length);
            for ( final QDate e : list ) {
                writer.writeInt(e.getValue());
            }
            break;
        }
        case DATETIME_LIST: {
            final QDateTime[] list = (QDateTime[]) obj;
            writer.writeInt(list.length);
            for ( final QDateTime e : list ) {
                writer.writeDouble(e.getValue());
            }
            break;
        }
        case TIMESPAN_LIST: {
            final QTimespan[] list = (QTimespan[]) obj;
            writer.writeInt(list.length);
            for ( final QTimespan e : list ) {
                writer.writeLong(e.getValue());
            }
            break;
        }
        case MINUTE_LIST: {
            final QMinute[] list = (QMinute[]) obj;
            writer.writeInt(list.length);
            for ( final QMinute e : list ) {
                writer.writeInt(e.getValue());
            }
            break;
        }
        case SECOND_LIST: {
            final QSecond[] list = (QSecond[]) obj;
            writer.writeInt(list.length);
            for ( final QSecond e : list ) {
                writer.writeInt(e.getValue());
            }
            break;
        }
        case TIME_LIST: {
            final QTime[] list = (QTime[]) obj;
            writer.writeInt(list.length);
            for ( final QTime e : list ) {
                writer.writeInt(e.getValue());
            }
            break;
        }
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected void writeCollection( final Collection<?> collection ) throws IOException, QException {
        final Iterator<?> it = collection.iterator();
        QType qtype = QType.GENERAL_LIST;
        if ( it.hasNext() ) {
            final Object firstElem = it.next();
            qtype = firstElem != null && toQ.containsKey(firstElem.getClass()) ? toQ.get(firstElem.getClass()) : QType.GENERAL_LIST;
            if ( qtype != QType.STRING ) {
                qtype = QType.valueOf((byte) (-1 * qtype.getTypeCode()));
            } else {
                qtype = QType.GENERAL_LIST;
            }
        }

        checkProtocolVersionCompatibility(qtype);

        writer.writeByte(qtype.getTypeCode());
        writer.writeByte((byte) 0); // attributes
        writer.writeInt(collection.size());

        switch ( qtype ) {
        case BOOL_LIST: {
            for ( final Object e : collection ) {
                writer.writeByte((byte) ((Boolean) e ? 1 : 0));
            }
            break;
        }
        case GUID_LIST: {
            for ( final Object e : collection ) {
                writeGuid((UUID) e);
            }
            break;
        }
        case BYTE_LIST: {
            for ( final Object e : collection ) {
                writer.writeByte((Byte) e);
            }
            break;
        }
        case SHORT_LIST: {
            for ( final Object e : collection ) {
                writer.writeShort((Short) e);
            }
            break;
        }
        case INT_LIST: {
            for ( final Object e : collection ) {
                writer.writeInt((Integer) e);
            }
            break;
        }
        case LONG_LIST: {
            for ( final Object e : collection ) {
                writer.writeLong((Long) e);
            }
            break;
        }
        case FLOAT_LIST: {
            for ( final Object e : collection ) {
                writer.writeFloat((Float) e);
            }
            break;
        }
        case DOUBLE_LIST: {
            for ( final Object e : collection ) {
                writer.writeDouble((Double) e);
            }
            break;
        }
        case SYMBOL_LIST: {
            for ( final Object e : collection ) {
                writeSymbol((String) e);
            }
            break;
        }
        case TIMESTAMP_LIST: {
            for ( final Object e : collection ) {
                writer.writeLong(((QTimestamp) e).getValue());
            }
            break;
        }
        case MONTH_LIST:
            for ( final Object e : collection ) {
                writer.writeInt(((QMonth) e).getValue());
            }
            break;
        case DATE_LIST: {
            for ( final Object e : collection ) {
                writer.writeInt(((QDate) e).getValue());
            }
            break;
        }
        case DATETIME_LIST: {
            for ( final Object e : collection ) {
                writer.writeDouble(((QDateTime) e).getValue());
            }
            break;
        }
        case TIMESPAN_LIST: {
            for ( final Object e : collection ) {
                writer.writeLong(((QTimespan) e).getValue());
            }
            break;
        }
        case MINUTE_LIST: {
            for ( final Object e : collection ) {
                writer.writeInt(((QMinute) e).getValue());
            }
            break;
        }
        case SECOND_LIST: {
            for ( final Object e : collection ) {
                writer.writeInt(((QSecond) e).getValue());
            }
            break;
        }
        case TIME_LIST: {
            for ( final Object e : collection ) {
                writer.writeInt(((QTime) e).getValue());
            }
            break;
        }
        case GENERAL_LIST: {
            for ( final Object e : collection ) {
                writeObject(e);
            }
            break;
        }
        }
    }

    protected void writeGeneralList( final Object[] list ) throws IOException, QException {
        writer.writeByte(QType.GENERAL_LIST.getTypeCode());
        writer.writeByte((byte) 0); // attributes
        writer.writeInt(list.length);
        for ( final Object obj : list ) {
            writeObject(obj);
        }
    }

    protected void writeSymbol( final String s ) throws IOException {
        writer.write(s.getBytes(getEncoding()));
        writer.writeByte((byte) 0);
    }

    protected void writeGuid( final UUID obj ) {
        writer.writeLongBigEndian(obj.getMostSignificantBits());
        writer.writeLongBigEndian(obj.getLeastSignificantBits());
    }

    protected void writeString( final char[] s ) throws IOException {
        writer.writeByte(QType.STRING.getTypeCode());
        writer.writeByte((byte) 0); // attributes
        final byte[] encoded = String.valueOf(s).getBytes(getEncoding());
        writer.writeInt(encoded.length);
        writer.write(encoded);
    }

    protected void writeNullItem() {
        writer.writeByte(QType.NULL_ITEM.getTypeCode());
        writer.writeByte((byte) 0);
    }

    protected void writeError( final Exception e ) throws IOException {
        writer.writeByte(QType.ERROR.getTypeCode());
        writeSymbol(e.getMessage());
    }

    protected void writeDictionary( final QDictionary d ) throws IOException, QException {
        writer.writeByte(QType.DICTIONARY.getTypeCode());
        writeObject(d.getKeys());
        writeObject(d.getValues());
    }

    protected void writeMap( final Map<?, ?> m ) throws IOException, QException {
        writer.writeByte(QType.DICTIONARY.getTypeCode());
        writer.writeByte(QType.GENERAL_LIST.getTypeCode());
        writer.writeByte((byte) 0); // attributes
        writer.writeInt(m.size());

        for ( final Object key : m.keySet() ) {
            writeObject(key);
        }

        writer.writeByte(QType.GENERAL_LIST.getTypeCode());
        writer.writeByte((byte) 0); // attributes
        writer.writeInt(m.size());

        for ( final Object key : m.keySet() ) {
            writeObject(m.get(key));
        }
    }

    protected void writeTable( final QTable t ) throws IOException, QException {
        writer.writeByte(QType.TABLE.getTypeCode());
        writer.writeByte((byte) 0); // attributes
        writer.writeByte(QType.DICTIONARY.getTypeCode());
        writeObject(t.getColumns());
        writeObject(t.getData());
    }

    protected void writeKeyedTable( final QKeyedTable t ) throws IOException, QException {
        writer.writeByte(QType.KEYED_TABLE.getTypeCode());
        writeObject(t.getKeys());
        writeObject(t.getValues());
    }

    protected void writeLambda( final QLambda l ) throws IOException {
        writer.writeByte(QType.LAMBDA.getTypeCode());
        writer.writeByte((byte) 0);
        writeString(l.getExpression().toCharArray());
    }

    protected void writeProjection( final QProjection p ) throws IOException, QException {
        writer.writeByte(QType.PROJECTION.getTypeCode());
        final int length = p.getParameters().length;
        writer.writeInt(length);

        for ( int i = 0; i < length; i++ ) {
            writeObject(p.getParameters()[i]);
        }
    }

    @SuppressWarnings("rawtypes")
    private static final Map<Class, QType> toQ = Collections.unmodifiableMap(new HashMap<Class, QType>() {
        private static final long serialVersionUID = 7199217298785029447L;

        {
            put(Object[].class, QType.GENERAL_LIST);
            put(Boolean.class, QType.BOOL);
            put(boolean[].class, QType.BOOL_LIST);
            put(Boolean[].class, QType.BOOL_LIST);
            put(Byte.class, QType.BYTE);
            put(byte[].class, QType.BYTE_LIST);
            put(Byte[].class, QType.BYTE_LIST);
            put(UUID.class, QType.GUID);
            put(UUID[].class, QType.GUID_LIST);
            put(Short.class, QType.SHORT);
            put(short[].class, QType.SHORT_LIST);
            put(Short[].class, QType.SHORT_LIST);
            put(Integer.class, QType.INT);
            put(int[].class, QType.INT_LIST);
            put(Integer[].class, QType.INT_LIST);
            put(Long.class, QType.LONG);
            put(long[].class, QType.LONG_LIST);
            put(Long[].class, QType.LONG_LIST);
            put(Float.class, QType.FLOAT);
            put(float[].class, QType.FLOAT_LIST);
            put(Float[].class, QType.FLOAT_LIST);
            put(Double.class, QType.DOUBLE);
            put(double[].class, QType.DOUBLE_LIST);
            put(Double[].class, QType.DOUBLE_LIST);
            put(Character.class, QType.CHAR);
            put(char[].class, QType.STRING);
            put(char[][].class, QType.GENERAL_LIST);
            put(String.class, QType.SYMBOL);
            put(String[].class, QType.SYMBOL_LIST);
            put(QTimestamp.class, QType.TIMESTAMP);
            put(QTimestamp[].class, QType.TIMESTAMP_LIST);
            put(QMonth.class, QType.MONTH);
            put(QMonth[].class, QType.MONTH_LIST);
            put(QDate.class, QType.DATE);
            put(QDate[].class, QType.DATE_LIST);
            put(QDateTime.class, QType.DATETIME);
            put(QDateTime[].class, QType.DATETIME_LIST);
            put(QTimespan.class, QType.TIMESPAN);
            put(QTimespan[].class, QType.TIMESPAN_LIST);
            put(QMinute.class, QType.MINUTE);
            put(QMinute[].class, QType.MINUTE_LIST);
            put(QSecond.class, QType.SECOND);
            put(QSecond[].class, QType.SECOND_LIST);
            put(QTime.class, QType.TIME);
            put(QTime[].class, QType.TIME_LIST);
            put(QException.class, QType.ERROR);
            put(QDictionary.class, QType.DICTIONARY);
            put(QTable.class, QType.TABLE);
            put(QKeyedTable.class, QType.KEYED_TABLE);
            put(QLambda.class, QType.LAMBDA);
            put(QProjection.class, QType.PROJECTION);
        }
    });

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
}
