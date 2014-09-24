### Types mapping

The `QType` class defines mapping between the q and corresponding Java types.

```
| qJava type identifier | q num type     | q  type               | Java type        |
|-----------------------|----------------|-----------------------|------------------|
| BOOL                  | -1             | boolean               | boolean          |
| BOOL_LIST             |  1             | boolean list          | boolean[]        |
| BYTE                  | -4             | byte                  | byte             |
| BYTE_LIST             |  4             | byte list             | byte[]           |
| GUID                  | -2             | guid                  | java.util.UUID   |
| GUID_LIST             |  2             | guid list             | java.util.UUID[] |
| SHORT                 | -5             | short                 | short            |
| SHORT_LIST            |  5             | short list            | short[]          |
| INT                   | -6             | integer               | int              |
| INT_LIST              |  6             | integer list          | int[]            |
| LONG                  | -7             | long                  | long             |
| LONG_LIST             |  7             | long list             | long[]           |
| FLOAT                 | -8             | real                  | float            |
| FLOAT_LIST            |  8             | real list             | float[]          |
| DOUBLE                | -9             | float                 | double           |
| DOUBLE_LIST           |  9             | float list            | double[]         |
| CHAR                  | -10            | character             | char             |
| STRING                |  10            | string                | char[]           |
| SYMBOL                | -11            | symbol                | String           |
| SYMBOL_LIST           |  11            | symbol list           | String[]         |
| TIMESTAMP             | -12            | timestamp             | QTimestamp       |
| TIMESTAMP_LIST        |  12            | timestamp list        | QTimestamp[]     |
| MONTH                 | -13            | month                 | QMonth           |
| MONTH_LIST            |  13            | month list            | QMonth[]         |
| DATE                  | -14            | date                  | QDate            |
| DATE_LIST             |  14            | date list             | QDate[]          |
| DATETIME              | -15            | datetime              | QDateTime        |
| DATETIME_LIST         |  15            | datetime list         | QDateTime[]      |
| TIMESPAN              | -16            | timespan              | QTimespan        |
| TIMESPAN_LIST         |  16            | timespan list         | QTimespan[]      |
| MINUTE                | -17            | minute                | QMinute          |
| MINUTE_LIST           |  17            | minute list           | QMinute[]        |
| SECOND                | -18            | second                | QSecond          |
| SECOND_LIST           |  18            | second list           | QSecond[]        |
| TIME                  | -19            | time                  | QTime            |
| TIME_LIST             |  19            | time list             | QTime[]          |
| GENERAL_LIST          |  0             | general list          | Object[]         |
| TABLE                 |  98            | table                 | QTable           |
| KEYED_TABLE           |  99            | keyed table           | QKeyedTable      |
| DICTIONARY            |  99            | dictionary            | QDictionary      |
| LAMBDA                |  100           | function body         | QLambda          |
| PROJECTION            |  104           | function projection   | QProjection      |
| UNARY_PRIMITIVE_FUNC  |  101           | function              | QFunction        |
| BINARY_PRIMITIVE_FUNC |  102           | function              | QFunction        |
| TERNARY_OPERATOR_FUNC |  103           | function              | QFunction        |
| COMPOSITION_FUNC      |  105           | function              | QFunction        |
| ADVERB_FUNC           |  106-111       | function              | QFunction        |
```

Note that q list are represented as arrays of primitive type by the qJava 
library. It is possible to send to q arrays of primitive type (e.g. `int[]`) as 
well as of boxed type (e.g. `Integer[]`).

### Temporal types
q language provides multiple types for operating on temporal data. The qJava 
library provides a corresponding temporal class for each q temporal type.

Instance of each class can be created:
* from the the underlying base type (e.g. `Long` in case of `QTimespan` and `QTimestamp`, `Double` in case of `QDateTime`),
* via conversion from `java.util.Date`,
* from q String representation via factory method `fromString(...)`.

Every temporal class in the qJava library implements `DateTime` interface:

```java
public Object getValue()        // Returns internal q representation of the temporal data.
public DateTime toDateTime()	// Represents q date/time with the instance of java.util.Date.
```

### Functions, lambdas and projections

IPC protocol type codes 100+ are used to represent functions, lambdas and 
projections. These types are represented as instances of base class 
`QFunction` or descendent classes:

* `QLambda` - represents q lambda expression, note that expression is required
  to be either:
  * q expression enclosed in {}, e.g.: `{x + y}`
  * k expression, e.g.: `k){x + y}`
 
* `QProjection` - represents function projection with parameters, e.g.:
  ```java
  // { x + y}[3]
  new QProjection(new Object[] {new QLambda("{x+y}"), 3L });
  ```

Note that only `QLambda` and `QProjection` are serializable. qJava doesn't 
provide means to serialize other function types.

### Null values
The `QType` enumeration exposes a utility static method `getQNull(QType type)` 
that returns corresponding q null value of given type. Keep in mind that null 
values are only defined and available for primitive q types.

As null values in q are represented as arbitrary values, it is also possible to 
produce null value without explicitly calling the `getQNull` method. Q null 
values are mapped to Java according to the following table:

```
| q type    | Java null                       |
|-----------|---------------------------------|
| bool      | false                           |
| byte      | (byte) 0                        |
| guid      | new UUID(0, 0)                  |
| short     | Short.MIN_VALUE                 |
| int       | Integer.MIN_VALUE               |
| long      | Long.MIN_VALUE                  |
| real      | Float.NaN                       |
| float     | Double.NaN                      |
| character | ' '                             |
| symbol    | ""                              |
| timestamp | new QTimestamp(Long.MIN_VALUE)  |
| Month     | new QMonth(Integer.MIN_VALUE)   |
| Date      | new QDate(Integer.MIN_VALUE)    |
| datetime  | new QDateTime(Double.NaN)       |
| timespan  | new QTimespan(Long.MIN_VALUE)   |
| minute    | new QMinute(Integer.MIN_VALUE)  |
| second    | new QSecond(Integer.MIN_VALUE)  |
| time      | new QTime(Integer.MIN_VALUE)    |
```