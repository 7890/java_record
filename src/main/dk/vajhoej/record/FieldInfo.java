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

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Class FieldInfo contains information about a native struct field needed for conversions.
 */
public class FieldInfo {
	private FieldType structType;
	private int length;
	private int decimals;
	private String encoding;
	private byte zone;
	private int prefixlength;
	private Class<?> classType;
	private Field field;
	private Map<Integer, SubClassAndPad> selects;
	private boolean selectPad;
	private int elements;
	/**
	 * Create instance of FieldInfo with all necessary properties.
	 * @param structType native struct type
	 * @param length length of fixed length string
	 * @param decimals number of decimals
	 * @param encoding encoding of string
	 * @param zone zone of zoned BCD
	 * @param prefixlength prefix length of variable length string
	 * @param classType Java class type
	 * @param field corresponding reflection object
	 * @param selects sub class selections
	 * @param selectPad pad sub classes to fixed length
	 * @param elements number of elements
	 */
	public FieldInfo(FieldType structType, int length, int decimals, String encoding, byte zone, int prefixlength, Class<?> classType, Field field, Map<Integer, SubClassAndPad> selects, boolean selectPad, int elements) {
		this.structType = structType;
		this.length = length;
		this.decimals = decimals;
		this.encoding = encoding;
		this.zone = zone;
		this.prefixlength = prefixlength;
		this.classType = classType;
		this.field = field;
		this.selects = selects;
		this.selectPad = selectPad;
		this.elements = elements;
	}
	/**
	 * Get native struct type.
	 * @return native struct type
	 */
	public FieldType getStructType() {
		return structType;
	}
    /**
     * Get length of fixed length string.
     * @return length of fixed length string
     */
    public int getLength() {
        return length;
    }
    /**
     * Get decimals of BCD.
     * @return decimals of BCD
     */
    public int getDecimals() {
        return decimals;
    }
	/**
	 * Get encoding of string.
	 * @return encoding of string
	 */
	public String getEncoding() {
		return encoding;
	}
    /**
     * Get zone of zoned BCD.
     * @return zone of zoned BCD
     */
    public byte getZone() {
        return zone;
    }
    /**
     * Get prefix length.
     * @return prefix length
     */
    public int getPrefixlength() {
    	return prefixlength;
    }
	/**
	 * Get Java class type.
	 * @return Java class type
	 */
	public Class<?> getClassType() {
		return classType;
	}
	/**
	 * Get corresponding reflection object.
	 * @return corresponding reflection object
	 */
	public Field getField() {
		return field;
	}
	/**
	 * Get sub class selections.
	 * @return sub class selections
	 */
	public Map<Integer, SubClassAndPad> getSelects() {
		return selects;
	}
    /**
     * Get sub class padding to fixed length.
     * @return true=pad, false=no pad
     */
    public boolean getSelectPad() {
        return selectPad;
    }
    /**
     * Get number of elements.
     * @return elements
     */
    public int getElements() {
        return elements;
    }
}
