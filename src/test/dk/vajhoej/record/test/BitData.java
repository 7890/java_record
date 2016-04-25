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
public class BitData {
	@StructField(n=0,type=FieldType.INT1)
	private int i1;
	@StructField(n=1,type=FieldType.BIT,length=4)
	private int n1;
	@StructField(n=2,type=FieldType.BIT,length=4)
	private int n2;
	@StructField(n=3,type=FieldType.INT1)
	private int i2;
	@StructField(n=4,type=FieldType.BIT,length=1)
	private int z1;
	@StructField(n=5,type=FieldType.BIT,length=15)
	private int z2;
    public int getI1() {
        return i1;
    }
    public void setI1(int i1) {
        this.i1 = i1;
    }
    public int getN1() {
        return n1;
    }
    public void setN1(int n1) {
        this.n1 = n1;
    }
    public int getN2() {
        return n2;
    }
    public void setN2(int n2) {
        this.n2 = n2;
    }
    public int getI2() {
        return i2;
    }
    public void setI2(int i2) {
        this.i2 = i2;
    }
    public int getZ1() {
        return z1;
    }
    public void setZ1(int z1) {
        this.z1 = z1;
    }
    public int getZ2() {
        return z2;
    }
    public void setZ2(int z2) {
        this.z2 = z2;
    }
}
