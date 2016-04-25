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

package dk.vajhoej.record.test;

import dk.vajhoej.record.FieldType;
import dk.vajhoej.record.Struct;
import dk.vajhoej.record.StructField;

@Struct
public class ErrorData3 {
	@StructField(n=0,type=FieldType.INT4)
	private int iv1;
	@StructField(n=0,type=FieldType.INT4)
	private int iv2;
    public int getIv1() {
        return iv1;
    }
    public void setIv1(int iv1) {
        this.iv1 = iv1;
    }
    public int getIv2() {
        return iv2;
    }
    public void setIv2(int iv2) {
        this.iv2 = iv2;
    }
}
