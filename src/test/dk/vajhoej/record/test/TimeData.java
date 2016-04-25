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

import java.util.Date;

import dk.vajhoej.record.FieldType;
import dk.vajhoej.record.Struct;
import dk.vajhoej.record.StructField;

@Struct
public class TimeData {
	@StructField(n=0,type=FieldType.JAVATIME)
	private Date t1;
	@StructField(n=1,type=FieldType.VMSTIME)
	private Date t2;
	@StructField(n=2,type=FieldType.UNIXTIME)
	private Date t3;
    public Date getT1() {
        return t1;
    }
    public void setT1(Date t1) {
        this.t1 = t1;
    }
    public Date getT2() {
        return t2;
    }
    public void setT2(Date t2) {
        this.t2 = t2;
    }
    public Date getT3() {
        return t3;
    }
    public void setT3(Date t3) {
        this.t3 = t3;
    }
}
