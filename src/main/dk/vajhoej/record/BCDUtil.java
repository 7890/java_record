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

import java.math.BigDecimal;

/**
 * Class TimeUtil converts between bytes with Binary Coded Decimals ant BigDecimal objects.
 */
public class BCDUtil {
    /**
     * Zero zone nibble.
     */
    public static final byte ZERO = (byte)0x00;
    /**
     * EBCDIC zone nibble.
     */
    public static final byte EBCDIC = (byte)0x0F;
    /**
     * ASCII zone nibble.
     */
    public static final byte ASCII = (byte)0x03;
    /**
     * Convert from packed BCD to BigDecimal.
     * @param b bytes with packed BCD
     * @param decimals implied decimals
     * @return BigDecimal object
     */
    public static BigDecimal decodePackedBCD(byte[] b, int decimals) {
        long sum = 0;
        for(int i = 0; i < b.length; i++) {
            int high = (b[i] >> 4) & 0x0F;
            int low = b[i] & 0x0F;
            sum = sum * 10 + high;
            switch(low) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    sum = sum * 10 + low;
                    break;
                case 10:
                case 12:
                case 14:
                case 15:
                    // nothing
                    break;
                case 11:
                case 13:
                    sum = -sum;
                    break;
            }
        }
        return new BigDecimal(sum).scaleByPowerOfTen(-decimals);
    }
    /**
     * Convert from BigDecimal to packed BCD. 
     * @param v BigDecimal object
     * @param decimals implied decimals
     * @param length length
     * @return byte array with packed BCD
     */
    public static byte[] encodePackedBCD(BigDecimal v, int decimals, int length) {
        long v2 = v.scaleByPowerOfTen(decimals).longValue();
        byte[] res = new byte[length];
        int low = 12;
        if(v2 < 0) {
            low = 13;
            v2 = - v2;
        }
        int high = (int) (v2 % 10);
        v2 /= 10;
        res[res.length - 1] = (byte)(high << 4 | low);
        for(int i = res.length - 2; i >= 0; i--) {
            low = (int)(v2 % 10);
            v2 /= 10;
            high = (int)(v2 % 10);
            v2 /= 10;
            res[i] = (byte)(high << 4 | low);
        }
        return res;
    }
    /**
     * Convert from zoned BCD to BigDecimal.
     * @param b bytes with zoned BCD
     * @param zone zone nibble value
     * @param decimals implied decimals
     * @return BigDecimal object
     */
    public static BigDecimal decodeZonedBCD(byte[] b, byte zone, int decimals) {
        long sum = 0;
        for(int i = 0; i < b.length; i++) {
            int high = (b[i] >> 4) & 0x0F;
            int low = b[i] & 0x0F;
            sum = sum * 10 + low;
            if(high != zone) {
                if(i == b.length - 1) {
                    switch(high) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                            throw new IllegalArgumentException("Invalid BCD");
                        case 10:
                        case 12:
                        case 14:
                        case 15:
                            // nothing
                            break;
                        case 11:
                        case 13:
                            sum = -sum;
                            break;
                    }
                } else {
                    throw new IllegalArgumentException("Invalid BCD");
                }
            }
        }
        return new BigDecimal(sum).scaleByPowerOfTen(-decimals);
    }
    /**
     * Convert from BigDecimal to zoned BCD.
     * @param v BigDecimal object
     * @param zone zone nibble value
     * @param decimals implied decimals
     * @param length length
     * @return byte array with zoned BCD
     */
    public static byte[] encodeZonedBCD(BigDecimal v, byte zone, int decimals, int length) {
        long v2 = v.scaleByPowerOfTen(decimals).longValue();
        byte[] res = new byte[length];
        int high = 12;
        if(v2 < 0) {
            high = 13;
            v2 = - v2;
        }
        int low = (int)(v2 % 10);
        v2 /= 10;
        res[res.length - 1] = (byte)(high << 4 | low);
        for(int i = res.length - 2; i >= 0; i--) {
            high = zone;
            low = (int)(v2 % 10);
            v2 /= 10;
            res[i] = (byte)(high << 4 | low);
        }
        return res;
    }
}
