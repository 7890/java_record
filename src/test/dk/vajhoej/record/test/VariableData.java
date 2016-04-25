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
public class VariableData {
    @StructField(n=0,type=FieldType.FIXSTR,length=0)
    private String s1;
    @StructField(n=1,type=FieldType.FIXSTR,length=0)
    private String s2;
    @StructField(n=2,type=FieldType.FIXSTR,length=0)
    private String s3;
    @StructField(n=3,type=FieldType.FIXSTR,length=0)
    private String s4;
    public String getS1() {
        return s1;
    }
    public void setS1(String s1) {
        this.s1 = s1;
    }
    public String getS2() {
        return s2;
    }
    public void setS2(String s2) {
        this.s2 = s2;
    }
    public String getS3() {
        return s3;
    }
    public void setS3(String s3) {
        this.s3 = s3;
    }
    public String getS4() {
        return s4;
    }
    public void setS4(String s4) {
        this.s4 = s4;
    }
}
