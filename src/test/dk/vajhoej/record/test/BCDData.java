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

import java.math.BigDecimal;

import dk.vajhoej.record.FieldType;
import dk.vajhoej.record.Struct;
import dk.vajhoej.record.StructField;

@Struct
public class BCDData {
	@StructField(n=0,type=FieldType.PACKEDBCD,length=4,decimals=2)
    private BigDecimal v1;
    @StructField(n=1,type=FieldType.ZONEDBCD,length=6,decimals=2)
    private BigDecimal v2;
	public BigDecimal getV1() {
        return v1;
    }
    public void setV1(BigDecimal v1) {
        this.v1 = v1;
    }
    public BigDecimal getV2() {
        return v2;
    }
    public void setV2(BigDecimal v2) {
        this.v2 = v2;
    }
}
