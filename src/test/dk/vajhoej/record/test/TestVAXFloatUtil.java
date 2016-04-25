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

import java.util.Random;

import org.junit.Test;

import dk.vajhoej.record.VAXFloatUtil;

public class TestVAXFloatUtil {
    private static final int N = 1000;
    private static final Random rng = new Random();
    @Test
    public void testVAX2IEEE() {
        int f = 0x70A44245;
        long g = 0xBE771A9FDD2F407EL;
        int s = 0x414570A4;
        long t = 0x405EDD2F1A9FBE77L;
        assertEquals("f2s", s, VAXFloatUtil.f2s(f));
        assertEquals("g2t", t, VAXFloatUtil.g2t(g));
    }
    @Test
    public void testIEEE2VAX() {
        int f = 0x70A44245;
        long g = 0xBE771A9FDD2F407EL;
        int s = 0x414570A4;
        long t = 0x405EDD2F1A9FBE77L;
        assertEquals("s2f", f, VAXFloatUtil.s2f(s));
        assertEquals("t2g", g, VAXFloatUtil.t2g(t));
    }
    @Test
    public void testMulti32() {
        for(int i = 0; i < N; i++) {
            float xs = rng.nextFloat();
            int s = Float.floatToRawIntBits(xs);
            int f = VAXFloatUtil.s2f(s);
            int z = VAXFloatUtil.f2s(f);
            float xz = Float.intBitsToFloat(z);
            assertEquals("s2f2s", xs, xz, 0.0000001);
        }
    }
    @Test
    public void testMulti64() {
        for(int i = 0; i < N; i++) {
            double xt = rng.nextDouble();
            long t = Double.doubleToLongBits(xt);
            long g = VAXFloatUtil.t2g(t);
            long z = VAXFloatUtil.g2t(g);
            double xz = Double.longBitsToDouble(z);
            assertEquals("t2g2t", xt, xz, 0.0000001);
        }
    }
}
