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
 * Interface LengthProvider represents something that can provide length of a field.
 * Note: can only be used with struct fields of field types FIXSTR, FIXSTRNULTERM,
 * PACKEDBCD and ZONEDPBCD.
 */
public interface LengthProvider {
    /**
     * Get length of field.
     * @param o object (not completely initialized for read)
     * @param n field number
     * @return length (values < 0 indicates that value is to be ignored)
     */
    public int getLength(Object o, int n);
}
