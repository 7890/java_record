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
 * Class SubClassAndPad contains information about class and padding for select field.
 */
public class SubClassAndPad {
    private Class<?> subClass;
    private int pad;
    /**
     * Create instance of ClassAndPad with all necessary properties.
     * @param subClass class
     * @param pad padding
     */
    public SubClassAndPad(Class<?> subClass, int pad) {
        this.subClass = subClass;
        this.pad = pad;
    }
    /**
     * Get class.
     * @return class
     */
    public Class<?> getSubClass() {
        return subClass;
    }
    /**
     * Get padding.
     * @return padding
     */
    public int getPad() {
        return pad;
    }
}
