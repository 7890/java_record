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
 * Enum Alignment specifies alignment within native struct.
 */
public enum Alignment {
    /**
     * No padding.
     */
    PACKED,
    /**
     * Padding to natural alignment.
     */
    NATURAL,
    /**
     * Padding to multipla of 1 alignment (same as PACKED). 
     */
    ALIGN1,
    /**
     * Padding to multipla of 2 alignment. 
     */
    ALIGN2,
    /**
     * Padding to multipla of 4 alignment. 
     */
    ALIGN4,
    /**
     * Padding to multipla of 8 alignment. 
     */
    ALIGN8
}
