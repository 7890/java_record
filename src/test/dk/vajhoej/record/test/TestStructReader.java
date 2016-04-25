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

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Test;

import dk.vajhoej.record.LengthProvider;
import dk.vajhoej.record.LengthProvider2;
import dk.vajhoej.record.InfoProvider;
import dk.vajhoej.record.RecordException;
import dk.vajhoej.record.StructInfoCache;
import dk.vajhoej.record.StructReader;

public class TestStructReader {
	private final static int N = 100000;
	@Test
	public void testReadSimple() {
		byte[] b = { 0x02, 0x01, 0x00, 0x00,
			         0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
				     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
		try {
			StructReader sr = new StructReader(b);
			Data o = sr.read(Data.class);
			assertEquals("iv", 258, o.getIv());
			assertEquals("xv", 123.456, o.getXv(), 0.0005);
			assertEquals("sv", "ABC     ", o.getSv());
            assertEquals("more", false, sr.more());
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
	@Test
	public void testReadMulti() {
		byte[] b = { 0x01, 0x00, 0x00, 0x00,
				     0x01, 0x00, 0x00, 0x00,
			         0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
			         0x03, 0x00, 0x00, 0x00,
				     0x02, 0x00, 0x00, 0x00,
				     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
		try {
			StructReader sr = new StructReader(b);
			SuperData o1 = sr.read(SuperData.class);
			assertEquals("id", 1, o1.getId());
			assertEquals("typ", 1, o1.getTyp());
			SubDataOne o1m = (SubDataOne)o1;
			assertEquals("x", 123.456, o1m.getX(), 0.0005);
			SuperData o2 = sr.read(SuperData.class);
			assertEquals("id", 3, o2.getId());
			assertEquals("typ", 2, o2.getTyp());
			SubDataTwo o2m = (SubDataTwo)o2;
			assertEquals("s", "ABC         ", o2m.getS());
            assertEquals("more", false, sr.more());
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
    @Test
    public void testReadMultiAlt() {
        byte[] b = { 0x01, 0x00, 0x00, 0x00,
                     0x01, 0x00, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x03, 0x00, 0x00, 0x00,
                     0x02, 0x00, 0x00, 0x00,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
        try {
            StructReader sr = new StructReader(b);
            SubDataOne o1m = sr.read(SubDataOne.class);
            assertEquals("id", 1, o1m.getId());
            assertEquals("typ", 1, o1m.getTyp());
            assertEquals("x", 123.456, o1m.getX(), 0.0005);
            SubDataTwo o2m = sr.read(SubDataTwo.class);
            assertEquals("id", 3, o2m.getId());
            assertEquals("typ", 2, o2m.getTyp());
            assertEquals("s", "ABC         ", o2m.getS());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadMultiPad() {
        byte[] b = { 0x01, 0x00, 0x00, 0x00,
                     0x01, 0x00, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40, 0x00, 0x00, 0x00, 0x00,
                     0x03, 0x00, 0x00, 0x00,
                     0x02, 0x00, 0x00, 0x00,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
        try {
            StructReader sr = new StructReader(b);
            SuperDataPad o1 = sr.read(SuperDataPad.class);
            assertEquals("id", 1, o1.getId());
            assertEquals("typ", 1, o1.getTyp());
            SubDataOnePad o1m = (SubDataOnePad)o1;
            assertEquals("x", 123.456, o1m.getX(), 0.0005);
            SuperDataPad o2 = sr.read(SuperDataPad.class);
            assertEquals("id", 3, o2.getId());
            assertEquals("typ", 2, o2.getTyp());
            SubDataTwoPad o2m = (SubDataTwoPad)o2;
            assertEquals("s", "ABC         ", o2m.getS());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadMultiConvert() {
        byte[] b = { 0x31, 0x41,
        		     0x32, 0x42, 0x42 };
        try {
        	InfoProvider inf = new InfoProvider() {
				public int getLength(Object o, int n) {
					return -1;
				}
				public int getMaxLength() {
					return -1;
				}
				public int getElements(Object o, int n) {
					return -1;
				}
				public LengthProvider2 getLengthProvider(Object o, int n) {
					return null;
				}
				public boolean hasConvertSelector() {
					return true;
				}
				public int convertSelector(Object o) {
					return Integer.parseInt((String)o);
				}
            };
            StructReader sr = new StructReader(b);
            SuperDataConvert o1 = sr.read(SuperDataConvert.class, inf);
            assertEquals("typ", "1", o1.getTyp());
            SubDataOneConvert o1m = (SubDataOneConvert)o1;
            assertEquals("a", "A", o1m.getA());
            SuperDataConvert o2 = sr.read(SuperDataConvert.class, inf);
            assertEquals("typ", "2", o2.getTyp());
            SubDataTwoConvert o2m = (SubDataTwoConvert)o2;
            assertEquals("b", "BB", o2m.getB());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
	//@Test
	public void testReadPerf() {
		byte[] b = { 0x02, 0x01, 0x00, 0x00,
			         0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
				     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
		byte[] bm = new byte[N*b.length];
		for(int i = 0; i < N; i++) {
			System.arraycopy(b, 0, bm, i*b.length, b.length);
		}
		try {
			long t1 = System.currentTimeMillis();
			StructReader sr = new StructReader(bm);
			for(int i = 0; i < N; i++) {
				Data o = sr.read(Data.class);
				assertEquals("iv #" + i, 258, o.getIv());
			}
			long t2 = System.currentTimeMillis();
			assertTrue("time", t2-t1 < 0.1*N);
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
	@Test
	public void testReadCache() {
		byte[] b = { 0x02, 0x01, 0x00, 0x00,
			         0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
				     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
		try {
			StructInfoCache.getInstance().reset();
			StructReader sr = new StructReader(b);
			Data o = sr.read(Data.class);
			assertEquals("iv", 258, o.getIv());
			assertTrue("hit rate", StructInfoCache.getInstance().getHitRate() < 0.001);
			for(int i = 0; i < 9; i++) {
				StructReader sr2 = new StructReader(b);
				Data o2 = sr2.read(Data.class);
				assertEquals("iv", 258, o2.getIv());
			}
			assertTrue("hit rate", StructInfoCache.getInstance().getHitRate() > 0.899);
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
    @Test
    public void testReadBoolean() {
        byte[] b = { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };
        try {
            StructReader sr = new StructReader(b);
            BooleanData o = sr.read(BooleanData.class);
            assertEquals("b1", true, o.isB1());
            assertEquals("b2", false, o.isB2());
            assertEquals("b3", true, o.isB3());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadBits() {
        byte[] b = { 0x01, 0x23, 0x04, (byte)0x80, 0x02 };
        try {
            StructReader sr = new StructReader(b);
            BitData o = sr.read(BitData.class);
            assertEquals("i1", 1, o.getI1());
            assertEquals("n1", 2, o.getN1());
            assertEquals("n2", 3, o.getN2());
            assertEquals("i3", 4, o.getI2());
            assertEquals("z1", 1, o.getZ1());
            assertEquals("z2", 2, o.getZ2());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
	@Test
	public void testReadTime() {
		byte[] b = { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				     0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				     0x01, 0x00, 0x00, 0x00 };
		try {
			StructReader sr = new StructReader(b);
			TimeData o = sr.read(TimeData.class);
			Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
			cal.setTime(o.getT1());
			assertEquals("t1 year", 1970, cal.get(Calendar.YEAR));
			assertEquals("t1 month", Calendar.JANUARY, cal.get(Calendar.MONTH));
			assertEquals("t1 day", 1, cal.get(Calendar.DAY_OF_MONTH));
			assertEquals("t1 hour", 0, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals("t1 minute", 0, cal.get(Calendar.MINUTE));
			assertEquals("t1 second", 0, cal.get(Calendar.SECOND));
			assertEquals("t1 millisecond", 1, cal.get(Calendar.MILLISECOND));
			cal.setTime(o.getT2());
			assertEquals("t2 year", 1858, cal.get(Calendar.YEAR));
			assertEquals("t2 month", Calendar.NOVEMBER, cal.get(Calendar.MONTH));
			assertEquals("t2 day", 17, cal.get(Calendar.DAY_OF_MONTH));
			assertEquals("t2 hour", 0, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals("t2 minute", 0, cal.get(Calendar.MINUTE));
			assertEquals("t2 second", 0, cal.get(Calendar.SECOND));
			assertEquals("t2 millisecond", 0, cal.get(Calendar.MILLISECOND));
			cal.setTime(o.getT3());
			assertEquals("t3 year", 1970, cal.get(Calendar.YEAR));
			assertEquals("t3 month", Calendar.JANUARY, cal.get(Calendar.MONTH));
			assertEquals("t3 day", 1, cal.get(Calendar.DAY_OF_MONTH));
			assertEquals("t3 hour", 0, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals("t3 minute", 0, cal.get(Calendar.MINUTE));
			assertEquals("t3 second", 1, cal.get(Calendar.SECOND));
			assertEquals("t3 millisecond", 0, cal.get(Calendar.MILLISECOND));
            assertEquals("more", false, sr.more());
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
    @Test
    public void testReadBCD() {
        byte[] b = { 0x12, 0x34, 0x56, 0x7D,
                     (byte)0xF1, (byte)0xF2, (byte)0xF7, (byte)0xF9, (byte)0xF5, (byte)0xC0  };
        try {
            StructReader sr = new StructReader(b);
            BCDData o = sr.read(BCDData.class);
            assertEquals("v1", new BigDecimal("-12345.67"), o.getV1());
            assertEquals("v2", new BigDecimal("1279.50"), o.getV2());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadVaxFloat() {
        byte[] b = { 0x45, 0x42, (byte)0xA4, 0x70,
                     0x7E, 0x40, 0x2F, (byte)0xDD, (byte)0x9F, 0x1A, 0x77, (byte)0xBE };
        try {
            StructReader sr = new StructReader(b);
            VaxFloatData o = sr.read(VaxFloatData.class);
            assertEquals("v1", 12.34f, o.getV1(), 0.0001f);
            assertEquals("v2", 123.456, o.getV2(), 0.00001);
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadUnsigned() {
        byte[] b = { (byte)0xFF,
                     (byte)0xFF, (byte)0xFF,
                     (byte)0xFF, (byte)0xFF,(byte)0xFF, (byte)0xFF };
        try {
            StructReader sr = new StructReader(b);
            UnsignedData o = sr.read(UnsignedData.class);
            assertEquals("ui1v", 255, o.getUi1v());
            assertEquals("ui2v", 65535, o.getUi2v());
            assertEquals("ui4v", 4294967295L, o.getUi4v());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadArray() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x03, 0x01, 0x00, 0x00,
                     0x04, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40 };
        try {
            StructReader sr = new StructReader(b);
            ArrayData o = sr.read(ArrayData.class);
            for(int ix = 0; ix < 3; ix++) {
                assertEquals("iv", 258 + ix, o.getIv(ix));
                assertEquals("xv", 123.456, o.getXv(ix), 0.0005);
            }
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadVarArray() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x03, 0x01, 0x00, 0x00,
                     0x04, 0x01, 0x00, 0x00,
                     0x05, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40 };
        try {
            StructReader sr = new StructReader(b);
            VarArrayData o = sr.read(VarArrayData.class, new LengthProvider2() {
				public int getLength(Object o, int n) {
					return 0;
				}
				public int getMaxLength() {
					return 0;
				}
				public int getElements(Object o, int n) {
					return 4;
				}
				public LengthProvider2 getLengthProvider(Object o, int n) {
					return null;
				}
            });
            for(int ix = 0; ix < 4; ix++) {
                assertEquals("iv", 258 + ix, o.getIv(ix));
                assertEquals("xv", 123.456, o.getXv(ix), 0.0005);
            }
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadVarArrayWithSizeAdjust() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x03, 0x01, 0x00, 0x00,
                     0x04, 0x01, 0x00, 0x00,
                     0x05, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40 };
        try {
            StructReader sr = new StructReader(b);
            ArrayData o = sr.read(ArrayData.class, new LengthProvider2() {
				public int getLength(Object o, int n) {
					return 0;
				}
				public int getMaxLength() {
					return 0;
				}
				public int getElements(Object o, int n) {
					return 4;
				}
				public LengthProvider2 getLengthProvider(Object o, int n) {
					return null;
				}
            });
            for(int ix = 0; ix < 4; ix++) {
                assertEquals("iv", 258 + ix, o.getIv(ix));
                assertEquals("xv", 123.456, o.getXv(ix), 0.0005);
            }
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
	@Test
	public void testReadStructField() {
		byte[] b = { 0x01, 0x00, 0x00, 0x00,
     			     0x02, 0x00, 0x00, 0x00,
	    		     0x03, 0x00, 0x00, 0x00,
	    		     0x04, 0x00, 0x00, 0x00,
	    		     0x05, 0x00, 0x00, 0x00 };
		try {
			StructReader sr = new StructReader(b);
			MainData o = sr.read(MainData.class);
			assertEquals("i1", 1, o.getI1());
			assertEquals("i2", 2, o.getI2());
			assertEquals("i3", 3, o.s.getI3());
			assertEquals("i4", 4, o.s.getI4());
			assertEquals("i5", 5, o.getI5());
            assertEquals("more", false, sr.more());
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
    @Test
    public void testReadStructAlignment0() {
        byte[] b = { 0x01, 0x02, 0x00, 0x03,
                     0x00, 0x00, 0x00, 0x04,
                     0x00, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00 };
        try {
            StructReader sr = new StructReader(b);
            AlignData0 o = sr.read(AlignData0.class);
            assertEquals("i1", 1, o.getI1());
            assertEquals("i2", 2, o.getI2());
            assertEquals("i4", 3, o.getI4());
            assertEquals("i8", 4, o.getI8());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadStructAlignment1() {
        byte[] b = { 0x01, 0x00, 0x02, 0x00,
                     0x03, 0x00, 0x00, 0x00,
                     0x04, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00 };
        try {
            StructReader sr = new StructReader(b);
            AlignData1 o = sr.read(AlignData1.class);
            assertEquals("i1", 1, o.getI1());
            assertEquals("i2", 2, o.getI2());
            assertEquals("i4", 3, o.getI4());
            assertEquals("i8", 4, o.getI8());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadStructAlignment2() {
        byte[] b = { 0x01, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00,
                     0x02, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00,
                     0x03, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00,
                     0x04, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00 };
        try {
            StructReader sr = new StructReader(b);
            AlignData2 o = sr.read(AlignData2.class);
            assertEquals("i1", 1, o.getI1());
            assertEquals("i2", 2, o.getI2());
            assertEquals("i4", 3, o.getI4());
            assertEquals("i8", 4, o.getI8());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
	@Test
	public void testReadError1() {
		byte[] b = { 0x00, 0x00, 0x00, 0x00 };
		try {
			StructReader sr = new StructReader(b);
			ErrorData1 o = sr.read(ErrorData1.class);
			fail("Missing exception");
			o.toString();
		} catch (RecordException e) {
		}
	}
	@Test
	public void testReadError2() {
		byte[] b = { };
		try {
			StructReader sr = new StructReader(b);
			ErrorData2 o = sr.read(ErrorData2.class);
			fail("Missing exception");
			o.toString();
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		} catch (IllegalArgumentException e) {
		}
	}
	@Test
	public void testReadError3() {
		byte[] b = { };
		try {
			StructReader sr = new StructReader(b);
			ErrorData3 o = sr.read(ErrorData3.class);
			fail("Missing exception");
			o.toString();
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		} catch (IllegalArgumentException e) {
		}
	}
    @Test
    public void testReadErrorMissing() {
        byte[] b = { };
        try {
            StructReader sr = new StructReader(b);
            Data o = sr.read(Data.class);
            fail("Missing exception");
            o.toString();
        } catch (RecordException e) {
        }
    }
    @Test
    public void testReadStrings() {
        byte[] b = { 0x41, 0x42, 0x43, 0x44,
                     0x02, 0x00, 0x41, 0x42,
                     0x02, 0x00, 0x41, 0x42, 0x00, 0x00,
                     0x41, 0x42, 0x00, 0x00,
                     0x02, 0x41, 0x42 };
        try {
            StructReader sr = new StructReader(b);
            StringData o = sr.read(StringData.class);
            assertEquals("s1", "ABCD", o.getS1());
            assertEquals("s2", "AB", o.getS2());
            assertEquals("s3", "AB", o.getS3());
            assertEquals("s4", "AB", o.getS4());
            assertEquals("s5", "AB", o.getS5());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadVariable() {
        byte[] b = { 0x41,
                     0x41, 0x42,
                     0x41, 0x42, 0x43,
                     0x41, 0x42, 0x43, 0x44 };
        try {
            StructReader sr = new StructReader(b);
            VariableData o = sr.read(VariableData.class, new LengthProvider() {
                public int getLength(Object o, int n) {
                    return n + 1;
                }
            });
            assertEquals("s1", "A", o.getS1());
            assertEquals("s2", "AB", o.getS2());
            assertEquals("s3", "ABC", o.getS3());
            assertEquals("s4", "ABCD", o.getS4());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadBigEndian() {
        byte[] b = { 0x00, 0x00, 0x01, 0x02 };
        try {
            StructReader sr = new StructReader(b);
            BigEndianData o = sr.read(BigEndianData.class);
            assertEquals("iv", 258, o.getIv());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadEndPad1() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x01 };
        try {
            StructReader sr = new StructReader(b);
            EndPadData1 o = sr.read(EndPadData1.class);
            assertEquals("iv", 258, o.getIv());
            assertEquals("bv", 1, o.getBv());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadEndPad2() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x01, 0x00, 0x00, 0x00 };
        try {
            StructReader sr = new StructReader(b);
            EndPadData2 o = sr.read(EndPadData2.class);
            assertEquals("iv", 258, o.getIv());
            assertEquals("bv", 1, o.getBv());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadEndPad3() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                     0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        try {
            StructReader sr = new StructReader(b);
            EndPadData3 o = sr.read(EndPadData3.class);
            assertEquals("iv", 258, o.getIv());
            assertEquals("xv", 0.0, o.getXv(), 0.0005);
            assertEquals("bv", 1, o.getBv());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadLogging() {
        int n1 = testLogging(Level.INFO);
        assertEquals("INFO log level", 0, n1);
        int n2 = testLogging(Level.FINE);
        assertTrue("FINE log level", n2 > n1);
        int n3 = testLogging(Level.FINER);
        assertTrue("FINER log level", n3 > n2);
        int n4 = testLogging(Level.FINEST);
        assertTrue("FINEST log level", n4 > n3);
    }
    public int testLogging(Level lvl) {
        final StringWriter lw = new StringWriter();
        Logger log = Logger.getLogger("dk.vajhoej.record");
        Level savlvl = log.getLevel();
        log.setLevel(lvl);
        Handler h = new Handler() {
            private Formatter fmt = new Formatter() {
                public String format(LogRecord record) {
                    return String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %2$s %3$s: %4$s",
                            record.getMillis(), record.getSourceClassName(), record.getLevel(), record.getMessage());
                }
            };
            public void close() throws SecurityException {
            }
            public void flush() {
            }
            public void publish(LogRecord record) {
                lw.write(fmt.format(record) + "\r\n");
            }
        };
        log.addHandler(h);
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
        try {
            StructReader sr = new StructReader(b);
            Data o = sr.read(Data.class);
            o.toString();
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
        log.removeHandler(h);
        log.setLevel(savlvl);
        String logoutput = lw.getBuffer().toString();
        return logoutput.length();
    }
    @Test
    public void TestReadGeneralInt() {
        byte[] b = { 0x03, 0x02, 0x01, 0x05, 0x04, 0x03, 0x02, 0x01 };
        try {
            StructReader sr = new StructReader(b);
            GeneralIntData o = sr.read(GeneralIntData.class);
            assertEquals("v1", 0x0000000000010203L, o.getV1());
            assertEquals("v2", 0x0000000102030405L, o.getV2());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void TestReadRemainingString() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00, 0x41, 0x42, 0x43 };
        try
        {
            StructReader sr = new StructReader(b);
            RemainingStringData o = sr.read(RemainingStringData.class);
            assertEquals("iv", 258, o.getIv());
            assertEquals("sv", "ABC", o.getSv());
            assertEquals("more", false, sr.more());
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
}
