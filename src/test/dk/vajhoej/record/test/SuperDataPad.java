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
import dk.vajhoej.record.Selector;
import dk.vajhoej.record.Struct;
import dk.vajhoej.record.StructField;
import dk.vajhoej.record.SubType;

@Struct
public class SuperDataPad {
	@StructField(n=0,type=FieldType.INT4)
	private int id;
	@StructField(n=1,type=FieldType.INT4)
	@Selector(subtypes={@SubType(value=1,type=SubDataOnePad.class),@SubType(value=2,type=SubDataTwoPad.class)},pad=true)
	private int typ;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getTyp() {
        return typ;
    }
    public void setTyp(int typ) {
        this.typ = typ;
    }
}
