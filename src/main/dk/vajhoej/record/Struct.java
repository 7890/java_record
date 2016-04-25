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
 * Annotation for structs.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Struct {
	/**
	 * Byte order. Default is little endian.
	 * @return little endian or big endian
	 */
	public Endian endianess() default Endian.LITTLE;
    /**
     * Alignment. Default is packed.
     * @return alignment
     */
    public Alignment alignment() default Alignment.PACKED;
    /**
     * End padding. Default is false.
     * @return false if no end padding, true if end padded to new instance of same struct
     */
    public boolean endpad() default false;
}
