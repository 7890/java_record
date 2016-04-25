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
public class MainData {
	@StructField(n=0,type=FieldType.INT4)
	private int i1;
	@StructField(n=1,type=FieldType.INT4)
	private int i2;
	@StructField(n=2,type=FieldType.STRUCT)
	public FieldData s;
	@StructField(n=3,type=FieldType.INT4)
	private int i5;
    public int getI1() {
        return i1;
    }
    public void setI1(int i1) {
        this.i1 = i1;
    }
    public int getI2() {
        return i2;
    }
    public void setI2(int i2) {
        this.i2 = i2;
    }
    public FieldData getS() {
        return s;
    }
    public void setS(FieldData s) {
        this.s = s;
    }
    public int getI5() {
        return i5;
    }
    public void setI5(int i5) {
        this.i5 = i5;
    }
}
