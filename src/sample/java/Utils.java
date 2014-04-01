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
import java.lang.reflect.Array;

import com.exxeleron.qjava.QDictionary;
import com.exxeleron.qjava.QKeyedTable;
import com.exxeleron.qjava.QTable;
import com.exxeleron.qjava.QDictionary.KeyValuePair;
import com.exxeleron.qjava.QTable.Row;

public final class Utils {

    static String arrayToString( final Object list ) {
        if ( list == null || !list.getClass().isArray() || Array.getLength(list) == 0 ) {
            return "[]";
        } else {
            final int length = Array.getLength(list);
            final StringBuffer buffer = new StringBuffer("[");

            Object obj = Array.get(list, 0);
            buffer.append(obj == null ? null : obj.getClass().isArray() ? arrayToString(obj) : obj);
            for ( int i = 1; i < length; i++ ) {
                obj = Array.get(list, i);
                buffer.append(", ");
                buffer.append(obj == null ? null : obj.getClass().isArray() ? arrayToString(obj) : obj);
            }
            buffer.append(']');
            return buffer.toString();
        }
    }

    static String resultToString( final Object obj ) {
        if ( obj == null ) {
            return "null";
        } else if ( obj.getClass().isArray() ) {
            return Utils.arrayToString(obj);
        } else if ( obj instanceof QDictionary ) {
            final StringBuffer buffer = new StringBuffer();
            final QDictionary d = (QDictionary) obj;
            for ( final KeyValuePair e : d ) {
                buffer.append(resultToString(e.getKey()));
                buffer.append(" | ");
                buffer.append(resultToString(e.getValue()));
                buffer.append('\n');
            }
            return buffer.toString();
        } else if ( obj instanceof QTable ) {
            final QTable t = (QTable) obj;
            final StringBuffer buffer = new StringBuffer();
            for ( final Row row : t ) {
                for ( final Object object : row ) {
                    buffer.append(object);
                    buffer.append('\t');
                }
                buffer.append('\n');
            }

            return buffer.toString();
        } else if ( obj instanceof QKeyedTable ) {
            final QKeyedTable kt = (QKeyedTable) obj;
            final StringBuffer buffer = new StringBuffer();
            for ( final QKeyedTable.KeyValuePair kv : kt ) {
                for ( final Object object : kv.getKey() ) {
                    buffer.append(object);
                    buffer.append('\t');
                }
                buffer.append(" | ");
                for ( final Object object : kv.getValue() ) {
                    buffer.append(object);
                    buffer.append('\t');
                }
                buffer.append('\n');
            }

            return buffer.toString();
        } else {
            return obj.toString();
        }
    }
}
