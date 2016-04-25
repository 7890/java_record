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
 * Annotation for selection of sub types.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Selector {
	/**
	 * Available sub types.
	 * @return array of sub types
	 */
	public SubType[] subtypes();
	/**
	 * Pad all sub types to same length.
	 * @return true=pad, false=no pad
	 */
	public boolean pad() default false;
}
