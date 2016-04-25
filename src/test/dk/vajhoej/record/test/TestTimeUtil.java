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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

import org.junit.Test;

import dk.vajhoej.record.TimeUtil;

public class TestTimeUtil {
    @Test
    public void testJavaTimeBasic() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.YEAR, 1970);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        Date d = cal.getTime();
        long t = TimeUtil.toJavaTime(d);
        assertEquals("to Java", 1, t);
        Date d2 = TimeUtil.fromJavaTime(t);
        assertEquals("from Java", d.getTime(), d2.getTime());
    }
    @Test
    public void testUnixTimeBasic() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.YEAR, 1970);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 0);
        Date d = cal.getTime();
        int t = TimeUtil.toUnixTime(d);
        assertEquals("to Unix", 1, t);
        Date d2 = TimeUtil.fromUnixTime(t);
        assertEquals("from Unix", d.getTime(), d2.getTime());
    }
    @Test
    public void testVMSTimeBasic() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.YEAR, 1858);
        cal.set(Calendar.MONTH, Calendar.NOVEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 17);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        Date d = cal.getTime();
        long t = TimeUtil.toVMSTime(d);
        assertEquals("to VMS", 10000, t);
        Date d2 = TimeUtil.fromVMSTime(t);
        assertEquals("from VMS", d.getTime(), d2.getTime());
    }
    private static final int REP = 1000;
    private static Random rng = new Random();
    @Test
    public void testJavaTimeMany() {
        for(int i = 0; i < REP; i++) {
            long v = rng.nextLong();
            assertEquals("Java time " + v, v, TimeUtil.toJavaTime(TimeUtil.fromJavaTime(v)));
        }
    }
    @Test
    public void testUnixTimeMany() {
        for(int i = 0; i < REP; i++) {
            int v = rng.nextInt();
            assertEquals("Unix time " + v, v, TimeUtil.toUnixTime(TimeUtil.fromUnixTime(v)));
        }
    }
    @Test
    public void testVMSTimeMany() {
        for(int i = 0; i < REP; i++) {
            long v = rng.nextLong();
            v = Math.abs(v);
            v = (v / 10000) * 10000;
            assertEquals("VMS time " + v, v, TimeUtil.toVMSTime(TimeUtil.fromVMSTime(v)));
        }
    }
}
