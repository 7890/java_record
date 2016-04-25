/*
 * Copyright 2009 Arne Vajh�j.
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
public class Data {
    @StructField(n=0,type=FieldType.INT4)
    private int iv;
    @StructField(n=1,type=FieldType.FP8)
    private double xv;
    @StructField(n=2,type=FieldType.FIXSTR,length=8,encoding="ISO-8859-1")
    private String sv;
	public int getIv() {
        return iv;
    }
    public void setIv(int iv) {
        this.iv = iv;
    }
    public double getXv() {
        return xv;
    }
    public void setXv(double xv) {
        this.xv = xv;
    }
    public String getSv() {
        return sv;
    }
    public void setSv(String sv) {
        this.sv = sv;
    }
}
