/*
 * Copyright 2011 Arne Vajhøj.
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
 * Interface LengthProvider2 represents something that can provide:
 *   - length of a field
 *   - maximum length of the entire struct
 *   - number of elements in array
 */
public interface LengthProvider2 extends LengthProvider {
    /**
     * Get max length of struct.
     * Note: can only be used with struct fields of field types FIXSTR, FIXSTRNULTERM,
     * PACKEDBCD and ZONEDBCD.
     * @return max length (values < 0 indicates that value is to be ignored)
     */
    public int getMaxLength();
    /**
     * Get number of elements in array.
     * Note: can only be used with struct fields that are arrays.
     * @param o object (not completely initialized for read)
     * @param n field number
     * @return elements (values < 0 indicates that value is to be ignored)
     */
    public int getElements(Object o, int n);
    /**
     * Get length provider for sub struct.
     * Note: can only be used with struct fields of type STRUCT.
     * @param o object (not completely initialized for read)
     * @param n field number
     * @return length provider (null indicates that it is to be ignored)
     */
    public LengthProvider2 getLengthProvider(Object o, int n);
}
