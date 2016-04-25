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

package dk.vajhoej.record;

/**
 * Class VAXFloatUtil converts between VAX floating point and IEEE floating point.
 */
public class VAXFloatUtil {
    private static class FP32 {
        private int ebit;
        private int fbit;
        private int smsk;
        private int emsk;
        private int fmsk;
        public FP32(int sbit, int ebit, int fbit) {
            if((sbit != 1) || ((sbit + ebit + fbit) != 32)) {
                throw new IllegalArgumentException("Unsupported 32 bit floating point format");
            }
            this.ebit = ebit;
            this.fbit = fbit;
            smsk = ~(-1 << sbit);
            emsk = ~(-1 << ebit);
            fmsk = ~(-1 << fbit);
        }
        public int getS(int v) {
            return (v >> (ebit + fbit)) & smsk;
        }
        public int getE(int v) {
            return (v >> fbit) & emsk;
        }
        public int getF(int v) {
            return v & fmsk;
        }
        public int get(int s, int e, int f) {
            return (s << (ebit + fbit)) | (e << fbit) | f;
        }
    }
    private static class FP64 {
        private int ebit;
        private int fbit;
        private long smsk;
        private long emsk;
        private long fmsk;
        public FP64(int sbit, int ebit, int fbit) {
            if((sbit != 1) || ((sbit + ebit + fbit) != 64)) {
                throw new IllegalArgumentException("Unsupported 64 bit floating point format");
            }
            this.ebit = ebit;
            this.fbit = fbit;
            smsk = ~(-1L << sbit);
            emsk = ~(-1L << ebit);
            fmsk = ~(-1L << fbit);
        }
        public long getS(long v) {
            return (v >> (ebit + fbit)) & smsk;
        }
        public long getE(long v) {
            return (v >> fbit) & emsk;
        }
        public long getF(long v) {
            return v & fmsk;
        }
        public long get(long s, long e, long f) {
            return (s << (ebit + fbit)) | (e << fbit) | f;
        }
    }
    private static FP32 F = new FP32(1, 8, 23);
    private static FP64 G = new FP64(1, 11, 52);
    private static FP32 S = new FP32(1, 8, 23);
    private static FP64 T = new FP64(1, 11, 52);
    private static int wordswap(int v) {
        return (v >>> 16) | (v << 16);
    }
    private static long wordswap(long v) {
        int high = (int)(v >>> 32);
        int low = (int)(v & 0x00000000FFFFFFFFL);
        high = wordswap(high);
        low = wordswap(low);
        return ((long)low << 32) | (high & 0x00000000FFFFFFFFL);
    }
    /**
     * Convert from F floating to S floating.
     * @param v F floating
     * @return S floating
     */
    public static int f2s(int v) {
        int v2 = wordswap(v);
        return (int)S.get(F.getS(v2), F.getE(v2) - 2, F.getF(v2));
    }
    /**
     * Convert from S floating to F floating.
     * @param v S floating
     * @return F floating
     */
    public static int s2f(int v) {
        return wordswap(F.get(S.getS(v), S.getE(v) + 2, S.getF(v)));
    }
    /**
     * Convert from G floating to T floating.
     * @param v G floating
     * @return T floating
     */
    public static long g2t(long v) {
        long v2 = wordswap(v);
        return T.get(G.getS(v2), G.getE(v2) - 2, G.getF(v2));
    }
    /**
     * Convert from T floating to G floating.
     * @param v T floating
     * @return G floating
     */
    public static long t2g(long v) {
        return wordswap(G.get(T.getS(v), T.getE(v) + 2, T.getF(v)));
    }
}
