/*
 * Copyright 2009 Arne Vajhøj.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package dk.vajhoej.record;

/**
 * Enum FieldType specifies native struct types.
 * <br>
 * Semantics:
 * <table border="yes">
 * <tr>
 * <th>enum value</th>
 * <th>description</th>
 * <th>attributes</th>
 * <th>native implementation</th>
 * <th>Java implementation</th>
 * </tr>
 * <tr>
 * <td>INT1</td>
 * <td></td>
 * <td></td>
 * <td>8 bit signed integer</td>
 * <td>byte</td>
 * </tr>
 * <tr>
 * <td>INT2</td>
 * <td></td>
 * <td></td>
 * <td>16 bit signed integer</td>
 * <td>short</td>
 * </tr>
 * <tr>
 * <td>INT4</td>
 * <td></td>
 * <td></td>
 * <td>32 bit signed integer</td>
 * <td>int</td>
 * </tr>
 * <tr>
 * <td>INT8</td>
 * <td></td>
 * <td></td>
 * <td>64 bit signed integer</td>
 * <td>long</td>
 * </tr>
 * <tr>
 * <td>UINT1</td>
 * <td></td>
 * <td></td>
 * <td>8 bit unsigned integer</td>
 * <td>short</td>
 * </tr>
 * <tr>
 * <td>UINT2</td>
 * <td></td>
 * <td></td>
 * <td>16 bit unsigned integer</td>
 * <td>int</td>
 * </tr>
 * <tr>
 * <td>UINT4</td>
 * <td></td>
 * <td></td>
 * <td>32 bit unsigned integer</td>
 * <td>long</td>
 * </tr>
 * <tr>
 * <td>FP4</td>
 * <td></td>
 * <td></td>
 * <td>32 bit IEEE floating point</td>
 * <td>float</td>
 * </tr>
 * <tr>
 * <td>FP8</td>
 * <td></td>
 * <td></td>
 * <td>64 bit IEEE floating point</td>
 * <td>double</td>
 * </tr>
 * <tr>
 * <td>INTX</td>
 * <td></td>
 * <td>length=&lt;bytes used&gt;</td>
 * <td>bytes</td>
 * <td>ulong</td>
 * </tr>
 * <tr>
 * <td>FIXSTR</td>
 * <td>Fixed length string</td>
 * <td>length=&lt;length of string&gt;<br>encoding=&lt;encoding used&gt;<br>(default encoding is ISO-8859-1)</td>
 * <td>sequence of bytes</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>FIXSTRNULTERM</td>
 * <td>Fixed length string nul terminated</td>
 * <td>length=&lt;length of string&gt;<br>encoding=&lt;encoding used&gt;<br>(default encoding is ISO-8859-1)</td>
 * <td>sequence of bytes with nul bytes added for write and stripped for read
 * </td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>VARSTR</td>
 * <td>Variable length string with length prefix</td>
 * <td>prefixlength=&lt;prefix length&gt;<br>encoding=&lt;encoding used&gt;<br>(default encoding is ISO-8859-1, default prefix length is 2, max. length is limited by prefix length)</td>
 * <td>length + sequence of bytes</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>VARFIXSTR</td>
 * <td>Variable length string with length prefix and padded to max length</td>
 * <td>prefixlength=&lt;prefix length&gt;<br>length=&lt;length of string&gt;<br>encoding=&lt;encoding used&gt;<br>(default encoding is ISO-8859-1, default prefix length is 2, max. length is limited by prefix length)</td>
 * <td>length + sequence of bytes</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>REMSTR</td>
 * <td>Remaing data string</td>
 * <td>encoding=&lt;encoding used&gt;<br/>(default encoding is ISO-8859-1, max. length is 32767)</td>
 * <td>sequence of bytes</td>
 * <td>string</td>
 * </tr>
 * <tr>
 * <td>BOOLEAN</td>
 * <td>Boolean (0=false, other=true)</td>
 * <td>length=&lt;bytes used&gt;</td>
 * <td>bytes</td>
 * <td>boolean</td>
 * </tr>
 * <tr>
 * <td>BIT</td>
 * <td>Bits</td>
 * <td>length=&lt;bits used&gt; (max. bits is 32)</td>
 * <td>bytes</td>
 * <td>int</td>
 * </tr>
 * <tr>
 * <td>JAVATIME</td>
 * <td>Binary time in Java format (milliseconds since 1-Jan-1970)</td>
 * <td></td>
 * <td>64 bit integer</td>
 * <td>java.util.Date</td>
 * </tr>
 * <tr>
 * <td>UNIXTIME</td>
 * <td>Binary time in Unix format (seconds since 1-Jan-1970)</td>
 * <td></td>
 * <td>32 bit integer</td>
 * <td>java.util.Date</td>
 * </tr>
 * <tr>
 * <td>VMSTIME</td>
 * <td>Binary time in VMS format (100 nanoseconds since 17-Nov-1858)</td>
 * <td></td>
 * <td>64 bit integer</td>
 * <td>java.util.Date</td>
 * </tr>
 * <tr>
 * <td>PACKEDBCD</td>
 * <td>Packed BCD (1 byte = 2 decimal digit nibbles)</td>
 * <td>length=&lt;bytes used&gt;<br>decimals=&lt;number of implied decimals&gt;<br>(default decimals is 0)</td>
 * <td>sequence of bytes</td>
 * <td>java.math.BigDecimal</td>
 * </tr>
 * <tr>
 * <td>ZONEDBCD</td>
 * <td>Zoned BCD (1 byte = 1 zone nibble + 1 decimal digit nibble)</td>
 * <td>length=&lt;bytes used&gt;<br>decimals=&lt;number of implied decimals&gt;<br>zone=&lt;zone value&gt;<br>(default decimals i s0, default zone is EBCDIC)</td>
 * <td>sequence of bytes</td>
 * <td>java.math.BigDecimal</td>
 * </tr>
 * <tr>
 * <td>VAXFP4</td>
 * <td>VAX F floating point</td>
 * <td></td>
 * <td>32 bit VAX floating point</td>
 * <td>float</td>
 * </tr>
 * <tr>
 * <td>VAXFP8</td>
 * <td>VAX G floating point</td>
 * <td></td>
 * <td>64 bit VAX floating point</td>
 * <td>double</td>
 * </tr>
 * <tr>
 * <td>STRUCT</td>
 * <td>Sub struct</td>
 * <td></td>
 * <td></td>
 * <td></td>
 * </tr>
 * </table>
 */
public enum FieldType {
    /**
     * 8 bit signed integer.
     */
	INT1,
	/**
	 * 16 bit signed integer.
	 */
	INT2,
	/**
	 * 32 bit signed integer.
	 */
	INT4,
	/**
	 * 64 bit signed integer.
	 */
	INT8,
    /**
     * 8 bit unsigned integer.
     */
	UINT1,
    /**
     * 16 bit unsigned integer.
     */
	UINT2,
    /**
     * 32 bit unsigned integer.
     */
	UINT4,
	/**
	 * 32 bit IEEE floating point.
	 */
	FP4,
	/**
	 * 64 bit IEEE floating point.
	 */
	FP8,
    /**
     * 8-56 bit integer (intended for 24, 40, 48 and 56 bits).
     */
    INTX,
    /**
     * Fixed length string.
     */
    FIXSTR,
    /**
     * Fixed length string nul terminated.
     */
    FIXSTRNULTERM,
    /**
     * Variable length string with 2 byte length prefix.
     */
    VARSTR,
    /**
     * Variable length string with 2 byte length prefix and padded to max length.
     */
    VARFIXSTR,
    /**
     * Remaining data string.
     */
    REMSTR,
    /**
     * Boolean.
     */
    BOOLEAN,
	/**
	 * Bits.
	 */
	BIT,
	/**
	 * Binary time in Java format. 
	 */
	JAVATIME,
	/**
	 * Binary time in Unix format.
	 */
	UNIXTIME,
	/**
	 * Binary time in VMS format.
	 */
	VMSTIME,
	/**
	 * Packed BCD.
	 */
	PACKEDBCD,
	/**
	 * Zoned BCD.
	 */
	ZONEDBCD,
	/**
	 * VAX F floating point.
	 */
	VAXFP4,
	/**
	 * VAX G floating point.
	 */
	VAXFP8,
	/**
	 * Sub struct.
	 */
	STRUCT
}
