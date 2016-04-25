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

import java.util.Date;

/**
 * Class TimeUtil converts between integers in various time formats and Date objects.
 */
public class TimeUtil {
    /**
     * Convert from long with Java time (milliseconds since 1-Jan-1970) to Date object.
     * @param v Java time
     * @return Date object
     */
    public static Date fromJavaTime(long v) {
        return new Date(v);
    }
    /**
     * Convert from Date object to long with Java time (milliseconds since 1-Jan-1970).
     * @param d Date object
     * @return Java time
     */
    public static long toJavaTime(Date d) {
        return d.getTime();
    }
    /**
     * Convert from int with Unix time (seconds since 1-Jan-1970) to Date object.
     * @param v Unix time
     * @return Date object
     */
    public static Date fromUnixTime(int v) {
        return new Date(v * 1000L);
    }
    /**
     * Convert from Date object to int with Unix time (seconds since 1-Jan-1970).
     * @param d Date object
     * @return Unix time
     */
    public static int toUnixTime(Date d) {
        return (int)(d.getTime() / 1000);
    }
    /**
     * Convert from long with VMS time (100 nanoseconds since 17-Nov-1858) to Date object.
     * @param v VMS time
     * @return Date object
     */
    public static Date fromVMSTime(long v) {
        return new Date(v / 10000 - 3506716800000L);
    }
    /**
     * Convert from Date object to long with VMS time (100 nanoseconds since 17-Nov-1858).
     * @param d Date object
     * @return VMS time
     */
    public static long toVMSTime(Date d) {
        return (d.getTime() + 3506716800000L) * 10000;
    }
}
