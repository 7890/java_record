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

import static org.junit.Assert.*;

import org.junit.Test;

import dk.vajhoej.record.RecordException;
import dk.vajhoej.record.StructInfo;
import dk.vajhoej.record.StructInfoCache;

public class TestStructInfo {
    @Test
    public void testSimple() {
        try {
            StructInfo si = StructInfoCache.analyze(Data.class);
            assertEquals("fixed length", true, si.isFixedLength());
            assertEquals("length", 20, si.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testAlign0() {
        try {
            StructInfo si = StructInfoCache.analyze(AlignData0.class);
            assertEquals("fixed length", true, si.isFixedLength());
            assertEquals("length", 15, si.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testAlign1() {
        try {
            StructInfo si = StructInfoCache.analyze(AlignData1.class);
            assertEquals("fixed length", true, si.isFixedLength());
            assertEquals("length", 16, si.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testAlign2() {
        try {
            StructInfo si = StructInfoCache.analyze(AlignData2.class);
            assertEquals("fixed length", true, si.isFixedLength());
            assertEquals("length", 32, si.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testEndPad1() {
        try {
            StructInfo si = StructInfoCache.analyze(EndPadData1.class);
            assertEquals("fixed length", true, si.isFixedLength());
            assertEquals("length", 5, si.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testEndPad2() {
        try {
            StructInfo si = StructInfoCache.analyze(EndPadData2.class);
            assertEquals("fixed length", true, si.isFixedLength());
            assertEquals("length", 8, si.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testEndPad3() {
        try {
            StructInfo si = StructInfoCache.analyze(EndPadData3.class);
            assertEquals("fixed length", true, si.isFixedLength());
            assertEquals("length", 24, si.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testSuperSub() {
        try {
            StructInfo sione = StructInfoCache.analyze(SubDataOne.class);
            assertEquals("fixed length", false, sione.isFixedLength());
            assertEquals("length", 16, sione.getLength());
            StructInfo sitwo = StructInfoCache.analyze(SubDataTwo.class);
            assertEquals("fixed length", false, sitwo.isFixedLength());
            assertEquals("length", 20, sitwo.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testSuperSubPad() {
        try {
            StructInfo si = StructInfoCache.analyze(SuperDataPad.class);
            assertEquals("fixed length", true, si.isFixedLength());
            assertEquals("length", 20, si.getLength());
            StructInfo sione = StructInfoCache.analyze(SubDataOnePad.class);
            assertEquals("fixed length", true, sione.isFixedLength());
            assertEquals("length", 20, sione.getLength());
            StructInfo sitwo = StructInfoCache.analyze(SubDataTwoPad.class);
            assertEquals("fixed length", true, sitwo.isFixedLength());
            assertEquals("length", 20, sitwo.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testArray() {
        try {
            StructInfo si = StructInfoCache.analyze(ArrayData.class);
            assertEquals("fixed length", true, si.isFixedLength());
            assertEquals("length", 36, si.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testVarArray() {
        try {
            StructInfo si = StructInfoCache.analyze(VarArrayData.class);
            assertEquals("fixed length", true, si.isFixedLength());
            assertEquals("length", 0, si.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testVariable() {
        try {
            StructInfo si = StructInfoCache.analyze(VariableData.class);
            assertEquals("fixed length", true, si.isFixedLength());
            assertEquals("length", 0, si.getLength());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
}
