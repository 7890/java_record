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

import dk.vajhoej.record.InfoProvider;
import dk.vajhoej.record.LengthProvider;
import dk.vajhoej.record.LengthProvider2;
import dk.vajhoej.record.RecordException;
import dk.vajhoej.record.StructInfoCache;
import dk.vajhoej.record.StructWriter;

public class TestStructWriter {
	private final static int N = 100000;
	@Test
	public void testWriteSimple() {
		byte[] b = { 0x02, 0x01, 0x00, 0x00,
				     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
				     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
		try {
			StructWriter sw = new StructWriter();
			Data o = new Data();
			o.setIv(258);
			o.setXv(123.456);
			o.setSv("ABC     ");
			sw.write(o);
			byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
			for(int i = 0; i < res.length; i++) {
				assertEquals("byte " + i, b[i], res[i]);
			}
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
	@Test
	public void testWriteMulti() {
		byte[] b = { 0x01, 0x00, 0x00, 0x00,
			         0x01, 0x00, 0x00, 0x00,
		             0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
		             0x03, 0x00, 0x00, 0x00,
			         0x02, 0x00, 0x00, 0x00,
			         0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
		try {
			StructWriter sw = new StructWriter();
			SubDataOne o1 = new SubDataOne();
			o1.setId(1);
			o1.setTyp(1);
			o1.setX(123.456);
			SuperData o1m = o1;
			sw.write(o1m);
			SubDataTwo o2 = new SubDataTwo();
			o2.setId(3);
			o2.setTyp(2);
			o2.setS("ABC         ");
			SuperData o2m = o2;
			sw.write(o2m);
			byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
			for(int i = 0; i < res.length; i++) {
				assertEquals("byte " + i, b[i], res[i]);
			}
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
    @Test
    public void testWriteMultiAlt() {
        byte[] b = { 0x01, 0x00, 0x00, 0x00,
                     0x01, 0x00, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x03, 0x00, 0x00, 0x00,
                     0x02, 0x00, 0x00, 0x00,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
        try {
            StructWriter sw = new StructWriter();
            SubDataOne o1 = new SubDataOne();
            o1.setId(1);
            o1.setTyp(1);
            o1.setX(123.456);
            sw.write(o1);
            SubDataTwo o2 = new SubDataTwo();
            o2.setId(3);
            o2.setTyp(2);
            o2.setS("ABC         ");
            sw.write(o2);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteMultiPad() {
        byte[] b = { 0x01, 0x00, 0x00, 0x00,
                     0x01, 0x00, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40, 0x00, 0x00, 0x00, 0x00,
                     0x03, 0x00, 0x00, 0x00,
                     0x02, 0x00, 0x00, 0x00,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
        try {
            StructWriter sw = new StructWriter();
            SubDataOnePad o1 = new SubDataOnePad();
            o1.setId(1);
            o1.setTyp(1);
            o1.setX(123.456);
            SuperDataPad o1m = o1;
            sw.write(o1m);
            SubDataTwoPad o2 = new SubDataTwoPad();
            o2.setId(3);
            o2.setTyp(2);
            o2.setS("ABC         ");
            SuperDataPad o2m = o2;
            sw.write(o2m);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteMultiConvert() {
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
            StructWriter sw = new StructWriter();
            SubDataOneConvert o1 = new SubDataOneConvert();
            o1.setTyp("1");
            o1.setA("A");
            SuperDataConvert o1m = o1;
            sw.write(o1m, inf);
            SubDataTwoConvert o2 = new SubDataTwoConvert();
            o2.setTyp("2");
            o2.setB("BB");
            SuperDataConvert o2m = o2;
            sw.write(o2m, inf);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
	@Test
	public void testWritePerf() {
		byte[] b = { 0x02, 0x01, 0x00, 0x00,
    			     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
	    		     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
		try {
			long t1 = System.currentTimeMillis();
			StructWriter sw = new StructWriter(100*N);
			for(int i = 0; i < N; i++) {
				Data o = new Data();
				o.setIv(258);
				o.setXv(123.456);
				o.setSv("ABC     ");
				sw.write(o);
			}
			byte[] res = sw.getBytes();
			long t2 = System.currentTimeMillis();
			assertEquals("length", N*b.length, res.length);
			assertTrue("time", t2-t1 < 0.1*N);
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
	@Test
	public void testWriteCache() {
		try {
			StructInfoCache.getInstance().reset();
			StructWriter sw = new StructWriter();
			Data o = new Data();
			o.setIv(258);
			o.setXv(123.456);
			o.setSv("ABC     ");
			sw.write(o);
			byte[] res = sw.getBytes();
			assertEquals("length", 20, res.length);
			assertTrue("hit rate", StructInfoCache.getInstance().getHitRate() < 0.001);
			for(int i = 0; i < 9; i++) {
				StructWriter sw2 = new StructWriter();
				Data o2 = new Data();
				o2.setIv(258);
				o2.setXv(123.456);
				o2.setSv("ABC     ");
				sw2.write(o2);
				byte[] res2 = sw.getBytes();
				assertEquals("length", 20, res2.length);
			}
			assertTrue("hit rate", StructInfoCache.getInstance().getHitRate() > 0.899);
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
    @Test
    public void testWriteBoolean() {
        byte[] b = { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };
        try {
            StructWriter sw = new StructWriter();
            BooleanData o = new BooleanData();
            o.setB1(true);
            o.setB2(false);
            o.setB3(true);
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
	@Test
	public void testWriteBits() {
		byte[] b = { 0x01, 0x23, 0x04, (byte)0x80, 0x02 };
		try {
			StructWriter sw = new StructWriter();
			BitData o = new BitData();
			o.setI1(1);
			o.setN1(2);
			o.setN2(3);
			o.setI2(4);
			o.setZ1(1);
			o.setZ2(2);
			sw.write(o);
			byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
			for(int i = 0; i < res.length; i++) {
				assertEquals("byte " + i, b[i], res[i]);
			}
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
	@Test
	public void testWriteTime() {
		byte[] b = { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			         0x10, 0x27, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			         0x01, 0x00, 0x00, 0x00 };
		try {
			StructWriter sw = new StructWriter();
			TimeData o = new TimeData();
			Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
			cal.set(Calendar.YEAR, 1970);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 1);
			o.setT1(cal.getTime());
			cal.set(Calendar.YEAR, 1858);
			cal.set(Calendar.MONTH, Calendar.NOVEMBER);
			cal.set(Calendar.DAY_OF_MONTH, 17);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 1);
			o.setT2(cal.getTime());
			cal.set(Calendar.YEAR, 1970);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 1);
			cal.set(Calendar.MILLISECOND, 0);
			o.setT3(cal.getTime());
			sw.write(o);
			byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
			for(int i = 0; i < res.length; i++) {
				assertEquals("byte " + i, b[i], res[i]);
			}
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
    @Test
    public void testWriteBCD() {
        byte[] b = { 0x12, 0x34, 0x56, 0x7D,
                    (byte)0xF1, (byte)0xF2, (byte)0xF7, (byte)0xF9, (byte)0xF5, (byte)0xC0  };
        try {
            StructWriter sw = new StructWriter();
            BCDData o = new BCDData();
            o.setV1(new BigDecimal("-12345.67"));
            o.setV2(new BigDecimal("1279.50"));
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteVaxFloat() {
        byte[] b = { 0x45, 0x42, (byte)0xA4, 0x70,
                     0x7E, 0x40, 0x2F, (byte)0xDD, (byte)0x9F, 0x1A, 0x77, (byte)0xBE };
        try {
            StructWriter sw = new StructWriter();
            VaxFloatData o = new VaxFloatData();
            o.setV1(12.34f);
            o.setV2(123.456);
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteUnsigned() {
        byte[] b = { (byte)0xFF,
                     (byte)0xFF, (byte)0xFF,
                     (byte)0xFF, (byte)0xFF,(byte)0xFF, (byte)0xFF };
        try {
            StructWriter sw = new StructWriter();
            UnsignedData o = new UnsignedData();
            o.setUi1v((short)255);
            o.setUi2v(65535);
            o.setUi4v(4294967295L);
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteArray() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x03, 0x01, 0x00, 0x00,
                     0x04, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40 };
        try {
            StructWriter sw = new StructWriter();
            ArrayData o = new ArrayData();
            for(int ix = 0; ix < 3; ix++) {
                o.setIv(ix, 258 + ix);
                o.setXv(ix, 123.456);
            }
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteVarArray() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
	                 0x03, 0x01, 0x00, 0x00,
	                 0x04, 0x01, 0x00, 0x00,
	                 0x05, 0x01, 0x00, 0x00,
	                 0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
	                 0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
	                 0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
	                 0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40 };
        try {
            StructWriter sw = new StructWriter();
            VarArrayData o = new VarArrayData();
            for(int ix = 0; ix < 4; ix++) {
                o.setIv(ix, 258 + ix);
                o.setXv(ix, 123.456);
            }
            sw.write(o, new LengthProvider2() {
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
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
	@Test
	public void testWriteStructField() {
		byte[] b = { 0x01, 0x00, 0x00, 0x00,
     			     0x02, 0x00, 0x00, 0x00,
	    		     0x03, 0x00, 0x00, 0x00,
	    		     0x04, 0x00, 0x00, 0x00,
	    		     0x05, 0x00, 0x00, 0x00 };
		try {
			StructWriter sw = new StructWriter();
			MainData o = new MainData();
			o.setI1(1);
			o.setI2(2);
			o.s = new FieldData();
			o.s.setI3(3);
			o.s.setI4(4);
			o.setI5(5);
			sw.write(o);
			byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
			for(int i = 0; i < res.length; i++) {
				assertEquals("byte " + i, b[i], res[i]);
			}
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		}
	}
    @Test
    public void testWriteStructAlignment0() {
        byte[] b = { 0x01, 0x02, 0x00, 0x03,
                     0x00, 0x00, 0x00, 0x04,
                     0x00, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00 };
        try {
            StructWriter sw = new StructWriter();
            AlignData0 o = new AlignData0();
            o.setI1((byte) 1);
            o.setI2((short) 2);
            o.setI4(3);
            o.setI8(4);
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteStructAlignment1() {
        byte[] b = { 0x01, 0x00, 0x02, 0x00,
                     0x03, 0x00, 0x00, 0x00,
                     0x04, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00 };
        try {
            StructWriter sw = new StructWriter();
            AlignData1 o = new AlignData1();
            o.setI1((byte) 1);
            o.setI2((short) 2);
            o.setI4(3);
            o.setI8(4);
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteStructAlignment2() {
        byte[] b = { 0x01, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00,
                     0x02, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00,
                     0x03, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00,
                     0x04, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00 };
        try {
            StructWriter sw = new StructWriter();
            AlignData2 o = new AlignData2();
            o.setI1((byte) 1);
            o.setI2((short) 2);
            o.setI4(3);
            o.setI8(4);
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
	@Test
	public void testWriteError1() {
		try {
			StructWriter sw = new StructWriter();
			ErrorData1 o = new ErrorData1();
			o.setSv("ABC");
			sw.write(o);
			fail("Missing exception");
		} catch (RecordException e) {
		}
	}
	@Test
	public void testWriteError2() {
		try {
			StructWriter sw = new StructWriter();
			ErrorData2 o = new ErrorData2();
			o.setIv(123);
			sw.write(o);
			fail("Missing exception");
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		} catch (IllegalArgumentException e) {
		}
	}
	@Test
	public void testWriteError3() {
		try {
			StructWriter sw = new StructWriter();
			ErrorData3 o = new ErrorData3();
			o.setIv1(123);
			o.setIv2(456);
			sw.write(o);
			fail("Missing exception");
		} catch (RecordException e) {
			fail("Unexpected exception: " + e);
		} catch (IllegalArgumentException e) {
		}
	}
	@Test
    public void testWriteStrings() {
        byte[] b = { 0x41, 0x42, 0x43, 0x44,
                     0x02, 0x00, 0x41, 0x42,
                     0x02, 0x00, 0x41, 0x42, 0x00, 0x00,
                     0x41, 0x42, 0x00, 0x00,
                     0x02, 0x41, 0x42 };
        try {
            StructWriter sw = new StructWriter();
            StringData o = new StringData();
            o.setS1("ABCD");
            o.setS2("AB");
            o.setS3("AB");
            o.setS4("AB");
            o.setS5("AB");
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteVariable() {
        byte[] b = { 0x41,
                     0x41, 0x42,
                     0x41, 0x42, 0x43,
                     0x41, 0x42, 0x43, 0x44 };
        try {
            StructWriter sw = new StructWriter();
            VariableData o = new VariableData();
            o.setS1("A");
            o.setS2("AB");
            o.setS3("ABC");
            o.setS4("ABCD");
            sw.write(o, new LengthProvider() {
                public int getLength(Object o, int n) {
                    return n + 1;
                }
            });
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteBigEndian() {
        byte[] b = { 0x00, 0x00, 0x01, 0x02 };
        try {
            StructWriter sw = new StructWriter();
            BigEndianData o = new BigEndianData();
            o.setIv(258);
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteEndPad1() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x01 };
        try {
            StructWriter sw = new StructWriter();
            EndPadData1 o = new EndPadData1();
            o.setIv(258);
            o.setBv((byte)1);
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteEndPad2() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x01, 0x00, 0x00, 0x00 };
        try {
            StructWriter sw = new StructWriter();
            EndPadData2 o = new EndPadData2();
            o.setIv(258);
            o.setBv((byte)1);
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteEndPad3() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                     0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                     0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        try {
            StructWriter sw = new StructWriter();
            EndPadData3 o = new EndPadData3();
            o.setIv(258);
            o.setXv(0.0);
            o.setBv((byte)1);
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteLogging() {
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
        try {
            StructWriter sw = new StructWriter();
            Data o = new Data();
            o.setIv(258);
            o.setXv(123.456);
            o.setSv("ABC     ");
            sw.write(o);
            byte[] res = sw.getBytes();
            res.toString();
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
        log.removeHandler(h);
        log.setLevel(savlvl);
        String logoutput = lw.getBuffer().toString();
        return logoutput.length();
    }
    @Test
    public void TestWriteGeneralInt() {
        byte[] b = { 0x03, 0x02, 0x01, 0x05, 0x04, 0x03, 0x02, 0x01 };
        try {
            StructWriter sw = new StructWriter();
            GeneralIntData o = new GeneralIntData();
            o.setV1(0x0000000000010203L);
            o.setV2(0x0000000102030405L);
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void TestWriteRemainingString()
    {
        byte[] b = { 0x02, 0x01, 0x00, 0x00, 0x41, 0x42, 0x43 };
        try {
            StructWriter sw = new StructWriter();
            RemainingStringData o = new RemainingStringData();
            o.setIv(258);
            o.setSv("ABC");
            sw.write(o);
            byte[] res = sw.getBytes();
            assertEquals("length", b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
}
