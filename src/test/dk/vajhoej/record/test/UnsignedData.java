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
public class UnsignedData {
    @StructField(n=0,type=FieldType.UINT1)
    private short ui1v;
    @StructField(n=1,type=FieldType.UINT2)
    private int ui2v;
    @StructField(n=2,type=FieldType.UINT4)
    private long ui4v;
    public short getUi1v() {
        return ui1v;
    }
    public void setUi1v(short ui1v) {
        this.ui1v = ui1v;
    }
    public int getUi2v() {
        return ui2v;
    }
    public void setUi2v(int ui2v) {
        this.ui2v = ui2v;
    }
    public long getUi4v() {
        return ui4v;
    }
    public void setUi4v(long ui4v) {
        this.ui4v = ui4v;
    }
}
