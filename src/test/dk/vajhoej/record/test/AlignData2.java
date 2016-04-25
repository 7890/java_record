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

import dk.vajhoej.record.Alignment;
import dk.vajhoej.record.FieldType;
import dk.vajhoej.record.Struct;
import dk.vajhoej.record.StructField;

@Struct(alignment=Alignment.ALIGN8)
public class AlignData2 {
    @StructField(n=0,type=FieldType.INT1)
    private byte i1;
    @StructField(n=1,type=FieldType.INT2)
    private short i2;
    @StructField(n=2,type=FieldType.INT4)
    private int i4;
    @StructField(n=3,type=FieldType.INT8)
    private long i8;
    public byte getI1() {
        return i1;
    }
    public void setI1(byte i1) {
        this.i1 = i1;
    }
    public short getI2() {
        return i2;
    }
    public void setI2(short i2) {
        this.i2 = i2;
    }
    public int getI4() {
        return i4;
    }
    public void setI4(int i4) {
        this.i4 = i4;
    }
    public long getI8() {
        return i8;
    }
    public void setI8(long i8) {
        this.i8 = i8;
    }
}
