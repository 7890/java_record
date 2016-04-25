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

import java.math.BigDecimal;
import java.util.Random;

import org.junit.Test;

import dk.vajhoej.record.BCDUtil;

public class TestBCDUtil {
    @Test
    public void testPackedBCD() {
        byte[] b1 = { 0x12, 0x7C };
        BigDecimal v1 = BCDUtil.decodePackedBCD(b1, 0);
        assertEquals("v1", new BigDecimal("127"), v1);
        BigDecimal v2 = new BigDecimal("127");
        byte[] b2 = BCDUtil.encodePackedBCD(v2, 0, 2);
        assertEquals("b2 length", b1.length, b2.length);
        for(int i = 0; i < b2.length; i++) {
            assertEquals("b2 element " + i, b1[i], b2[i]);
        }
        byte[] b3 = { 0x12, 0x34, 0x56, 0x7D };
        BigDecimal v3 = BCDUtil.decodePackedBCD(b3, 2);
        assertEquals("v3", new BigDecimal("-12345.67"), v3);
        BigDecimal v4 = new BigDecimal("-12345.67");
        byte[] b4 = BCDUtil.encodePackedBCD(v4, 2, 4);
        assertEquals("b4 length", b3.length, b4.length);
        for(int i = 0; i < b4.length; i++) {
            assertEquals("b4 element " + i, b3[i], b4[i]);
        }
    }
    @Test
    public void testZonedBCD() {
        byte[] b1 = { (byte)0xF1, (byte)0xF2, (byte)0xD3 };
        BigDecimal v1 = BCDUtil.decodeZonedBCD(b1, BCDUtil.EBCDIC, 0);
        assertEquals("v1", new BigDecimal("-123"), v1);
        BigDecimal v2 = new BigDecimal("-123");
        byte[] b2 = BCDUtil.encodeZonedBCD(v2, BCDUtil.EBCDIC, 0, 3);
        assertEquals("b2 length", b1.length, b2.length);
        for(int i = 0; i < b2.length; i++) {
            assertEquals("b2 element " + i, b1[i], b2[i]);
        }
        byte[] b3 = { (byte)0xF1, (byte)0xF2, (byte)0xF7, (byte)0xF9, (byte)0xF5, (byte)0xC0 };
        BigDecimal v3 = BCDUtil.decodeZonedBCD(b3, BCDUtil.EBCDIC, 2);
        assertEquals("v3", new BigDecimal("1279.50"), v3);
        BigDecimal v4 = new BigDecimal("1279.50");
        byte[] b4 = BCDUtil.encodeZonedBCD(v4, BCDUtil.EBCDIC, 2, 6);
        assertEquals("b4 length", b3.length, b4.length);
        for(int i = 0; i < b4.length; i++) {
            assertEquals("b4 element " + i, b3[i], b4[i]);
        }
    }
    private static final int REP = 1000;
    private static Random rng = new Random();
    @Test
    public void testPackedBCDMany() {
        for(int i = 0; i < REP; i++) {
            int v = rng.nextInt();
            String s = String.format("%d.%02d", v / 100, Math.abs(v % 100));
            assertEquals("packed BCD " + s, new BigDecimal(s), BCDUtil.decodePackedBCD(BCDUtil.encodePackedBCD(new BigDecimal(s), 2, 10), 2));
        }
    }
    @Test
    public void testZonedBCDMany() {
        for(int i = 0; i < REP; i++) {
            int v = rng.nextInt();
            String s = String.format("%d.%02d", v / 100, Math.abs(v % 100));
            assertEquals("zoned BCD " + s, new BigDecimal(s), BCDUtil.decodeZonedBCD(BCDUtil.encodeZonedBCD(new BigDecimal(s), BCDUtil.EBCDIC, 2, 10), BCDUtil.EBCDIC, 2));
        }
    }
}
