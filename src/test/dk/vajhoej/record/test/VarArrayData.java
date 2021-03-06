/*
 * Copyright 2011 Arne Vajh�j.
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

import dk.vajhoej.record.ArrayField;
import dk.vajhoej.record.FieldType;
import dk.vajhoej.record.Struct;
import dk.vajhoej.record.StructField;

@Struct
public class VarArrayData {
    @ArrayField(elements=0)
    @StructField(n=0,type=FieldType.INT4)
    private int[] iv;
    @ArrayField(elements=0)
    @StructField(n=1,type=FieldType.FP8)
    private double[] xv;
    public VarArrayData() {
        iv = new int[4];
        xv = new double[4];
    }
	public int getIv(int ix) {
        return iv[ix];
    }
    public void setIv(int ix, int iv) {
        this.iv[ix] = iv;
    }
    public double getXv(int ix) {
        return xv[ix];
    }
    public void setXv(int ix, double xv) {
        this.xv[ix] = xv;
    }
}
