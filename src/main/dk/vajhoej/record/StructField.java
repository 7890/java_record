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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for fields.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface StructField {
	/**
	 * Field number.
	 * @return number starting at zero
	 */
	public int n();
	/**
	 * Field type.
	 * @return type
	 */
	public FieldType type();
	/**
	 * Field length (for fixed length strings and BCD's).
	 * @return length
	 */
	public int length() default 0;
	/**
	 * Field decimals (for BCD's). 
	 */
	public int decimals() default 0;
    /**
     * Field encoding (for strings).
     * @return encoding name
     */
	public String encoding() default "ISO-8859-1";
	/**
	 * Field zone value (for zoned BCD's).
	 */
	public byte zone() default BCDUtil.EBCDIC;
	/**
	 * Prefix length (for variable length strings).
	 * @return prefix length
	 */
	public int prefixlength() default 2;
}
