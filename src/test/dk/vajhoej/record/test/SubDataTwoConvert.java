/*
 * Copyright 2012 Arne Vajh�j.
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

package dk.vajhoej.record.test;

import dk.vajhoej.record.FieldType;
import dk.vajhoej.record.Struct;
import dk.vajhoej.record.StructField;

@Struct
public class SubDataTwoConvert extends SuperDataConvert {
	@StructField(n=1,type=FieldType.FIXSTR,length=2)
	private String b;
	public String getB() {
		return b;
	}
	public void setB(String b) {
		this.b = b;
	}
}
