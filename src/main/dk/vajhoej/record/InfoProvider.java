/*
 * Copyright 2012 Arne Vajhøj.
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
 * Interface InfoProvider represents something that can provide:
 *   - converted selector
 */
public interface InfoProvider extends LengthProvider2 {
    /**
     * Has selector converter that must be used.
     * @return true if has, false if has not
     */
    public boolean hasConvertSelector();
    /**
     * Converts a selector of any type to a usable integer selector.
     * @param o object (not completely initialized for read)
     * @return real selector
     */
    public int convertSelector(Object o);
}
