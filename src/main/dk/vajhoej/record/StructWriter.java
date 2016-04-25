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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class StructWriter writes a Java object to a byte array as a native struct.
 */
public class StructWriter {
    private static Logger log = Logger.getLogger(StructWriter.class.getName());
	private final static int DEFAULT_BUFSIZ = 10000;
	private ByteBuffer bb;
	/**
	 * Construct instance of StructWriter with default buffer size.
	 */
	public StructWriter() {
		this(DEFAULT_BUFSIZ);
	}
	/**
	 * Construct instance of StructWriter.
	 * @param bufsiz size of byte array to write to
	 */
	public StructWriter(int bufsiz) {
		bb = ByteBuffer.allocate(bufsiz);
		log.fine("StructWriter initialized with buffersize " + bufsiz);
	}
    /**
     * Write.
     * @param o object to write
     * @throws RecordException if impossible to convert between types in class and struct
     */
    public void write(Object o) throws RecordException {
	    write(o, (InfoProvider)null);
	}
    /**
     * Write.
     * @param o object to write
     * @param lenpvd supplies length for fields where it is not given (null indicates that it is to be ignored)
     * @throws RecordException if impossible to convert between types in class and struct
     */
    public void write(Object o, LengthProvider lenpvd) throws RecordException {
    	if(lenpvd instanceof LengthProvider2) {
    		write(o, (LengthProvider2)lenpvd);
    		return;
    	}
		final LengthProvider tmp = lenpvd;
    	write(o, new LengthProvider2() {
			public int getLength(Object o, int n) {
				return tmp != null ? tmp.getLength(o, n) : -1;
			}
			public int getMaxLength() {
				return 1000;
			}
			public int getElements(Object o, int n) {
				return 1;
			}
			public LengthProvider2 getLengthProvider(Object o, int n) {
				return null;
			}
    	});
    }
    /**
     * Write.
     * @param o object to write
     * @param lenpvd supplies length for fields where it is not given (null indicates that it is to be ignored)
     * @throws RecordException if impossible to convert between types in class and struct
     */
    public void write(Object o, LengthProvider2 lenpvd) throws RecordException {
    	if(lenpvd instanceof InfoProvider) {
    		write(o, (InfoProvider)lenpvd);
    		return;
    	}
		final LengthProvider2 tmp = lenpvd;
    	write(o, new InfoProvider() {
			public int getLength(Object o, int n) {
				return tmp != null ? tmp.getLength(o, n) : -1;
			}
			public int getMaxLength() {
				return tmp != null ? tmp.getMaxLength() : -1;
			}
			public int getElements(Object o, int n) {
				return tmp != null ? tmp.getElements(o, n) : -1;
			}
			public LengthProvider2 getLengthProvider(Object o, int n) {
				return tmp != null ? tmp.getLengthProvider(o, n) : null;
			}
			public boolean hasConvertSelector() {
				return false;
			}
			public int convertSelector(Object o) {
				return 0;
			}
    	});
    }
    /**
     * Write.
     * @param o object to write
     * @param lenpvd supplies various length'es where they are not given (null indicates that it is to be ignored)
     * @throws RecordException if impossible to convert between types in class and struct
     */
    public void write(Object o, InfoProvider lenpvd) throws RecordException {
		try {
			long bitbuf = 0;
			int nbits = 0;
			int selpad = 0;
			Class<?> t = o.getClass();
			log.fine("Writing class " + t.getName());
			StructInfo si = StructInfoCache.analyze(t);
            bb.order(si.getEndianess() == Endian.LITTLE ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
			for(int i = 0; i < si.getFields().size(); i++) {
			    FieldInfo fi = si.getFields().get(i);
			    int npad = StructInfo.calculatePad(bb.position(), si.getAlignment(), fi);
			    if(npad > 0) {
	                bb.put(new byte[npad]);
	                log.finer("Write " + npad + " padding bytes");
			    }
			    int nelm;
			    if(lenpvd == null || lenpvd.getElements(o, i) < 0) {
			    	nelm = fi.getElements();
			    } else {
			    	nelm = lenpvd.getElements(o, i);
			    }
			    for(int ix = 0; ix < nelm; ix++) {
	                switch(fi.getStructType()) {
                        case INT1:
                            byte vint1;
                            if(fi.getClassType() == byte.class) {
                                vint1 = fi.getField().getByte(o);
                            } else if(fi.getClassType() == short.class) {
                                vint1 = (byte)fi.getField().getShort(o);
                            } else if(fi.getClassType() == int.class) {
                                vint1 = (byte)fi.getField().getInt(o);
                            } else if(fi.getClassType() == byte[].class) {
                                vint1 = Array.getByte(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vint1 = 0;
                            }
                            bb.put(vint1);
                            log.fine("Write INT1 with value " + vint1);
                            break;
                        case INT2:
                            short vint2;
                            if(fi.getClassType() == short.class) {
                                vint2 = fi.getField().getShort(o);
                            } else if(fi.getClassType() == int.class) {
                                vint2 = (short)fi.getField().getInt(o);
                            } else if(fi.getClassType() == short[].class) {
                                vint2 = Array.getShort(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vint2 = 0;
                            }
                            bb.putShort(vint2);
                            log.fine("Write INT2 with value " + vint2);
                            break;
                        case INT4:
                            int vint4;
                            if(fi.getClassType() == int.class) {
                                vint4 = fi.getField().getInt(o);
                            } else if(fi.getClassType() == int[].class) {
                                vint4 = Array.getInt(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vint4 = 0;
                            }
                            bb.putInt(vint4);
                            log.fine("Write INT4 with value " + vint4);
                            break;
                        case INT8:
                            long vint8;
                            if(fi.getClassType() == long.class) {
                                vint8 = fi.getField().getLong(o);
                            } else if(fi.getClassType() == long[].class) {
                                vint8 = Array.getLong(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vint8 = 0;
                            }
                            bb.putLong(vint8);
                            log.fine("Write INT8 with value " + vint8);
                            break;
                        case UINT1:
                            short vuint1;
                            if(fi.getClassType() == short.class) {
                                vuint1 = fi.getField().getShort(o);
                            } else if(fi.getClassType() == int.class) {
                                vuint1 = (short)fi.getField().getInt(o);
                            } else if(fi.getClassType() == short[].class) {
                                vuint1 = Array.getShort(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vuint1 = 0;
                            }
                            bb.put((byte)vuint1);
                            log.fine("Write UINT1 with value " + vuint1);
                            break;
                        case UINT2:
                            int vuint2;
                            if(fi.getClassType() == int.class) {
                                vuint2 = fi.getField().getInt(o);
                            } else if(fi.getClassType() == int[].class) {
                                vuint2 = Array.getInt(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vuint2 = 0;
                            }
                            bb.putShort((short)vuint2);
                            log.fine("Write UINT2 with value " + vuint2);
                            break;
                        case UINT4:
                            long vuint4;
                            if(fi.getClassType() == long.class) {
                                vuint4 = fi.getField().getLong(o);
                            } else if(fi.getClassType() == long[].class) {
                                vuint4 = Array.getLong(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vuint4 = 0;
                            }
                            bb.putInt((int)vuint4);
                            log.fine("Write UINT4 with value " + vuint4);
                            break;
                        case FP4:
                            float vfp4;
                            if(fi.getClassType() == float.class) {
                                vfp4 = fi.getField().getFloat(o);
                            } else if(fi.getClassType() == float[].class) {
                                vfp4 = Array.getFloat(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vfp4 = 0;
                            }
                            bb.putFloat(vfp4);
                            log.fine("Write FP4 with value " + vfp4);
                            break;
                        case FP8:
                            double vfp8;
                            if(fi.getClassType() == double.class) {
                                vfp8 = fi.getField().getDouble(o);
                            } else if(fi.getClassType() == double[].class) {
                                vfp8 = Array.getDouble(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vfp8 = 0;
                            }
                            bb.putDouble(vfp8);
                            log.fine("Write FP8 with value " + vfp8);
                            break;
                        case INTX:
                            int intxlen;
                            if(lenpvd == null || lenpvd.getLength(o, i) < 0) {
                                intxlen = fi.getLength();
                            } else {
                                intxlen = lenpvd.getLength(o, i);
                            }
                            long vintx;
                            if(fi.getClassType() == long.class) {
                                vintx = fi.getField().getLong(o);
                            } else if(fi.getClassType() == long[].class) {
                                vintx = Array.getLong(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vintx = 0;
                            }
                            if(intxlen > 0 && intxlen < 8) {
                                if(si.getEndianess() == Endian.BIG) {
                                    for(int j = 0; j < intxlen; j++) {
                                        bb.put(((byte)((vintx >> ((intxlen - 1 - j) * 8)) & 0x00FF)));
                                    }
                                } else if(si.getEndianess() == Endian.LITTLE) {
                                    for(int j = 0; j < intxlen; j++) {
                                        bb.put(((byte)((vintx >> (j * 8)) & 0x00FF)));
                                    }
                                }
                            } else {
                                throw new RecordException("Wrong length of general integer " + fi.getField().getName() + " in " + t.getName());
                            }
                            log.fine("Write INTX with value " + vintx);
                            break;
                        case FIXSTR:
                            int fixstrlen;
                            if(lenpvd == null || lenpvd.getLength(o, i) < 0) {
                                fixstrlen = fi.getLength();
                            } else {
                                fixstrlen = lenpvd.getLength(o, i);
                            }
                            String vfixstr;
                            if(fi.getClassType() == String.class) {
                                vfixstr = (String)fi.getField().get(o);
                            } else if(fi.getClassType() == String[].class) {
                                vfixstr = (String)Array.get(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vfixstr = null;
                            }
                            byte[] fixstrba = vfixstr.getBytes(fi.getEncoding());
                            if(fixstrba.length == fixstrlen) {
                                bb.put(fixstrba);
                            } else {
                                throw new RecordException("Wrong length of string " + fi.getField().getName() + " in " + t.getName());
                            }
                            log.fine("Write FIXSTR with value " + vfixstr);
                            break;
                        case FIXSTRNULTERM:
                            int fixstrnultermlen;
                            if(lenpvd == null || lenpvd.getLength(o, i) < 0) {
                                fixstrnultermlen = fi.getLength();
                            } else {
                                fixstrnultermlen = lenpvd.getLength(o, i);
                            }
                            String vfixstrnulterm;
                            if(fi.getClassType() == String.class) {
                                vfixstrnulterm = (String)fi.getField().get(o);
                            } else if(fi.getClassType() == String[].class) {
                                vfixstrnulterm = (String)Array.get(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vfixstrnulterm = null;
                            }
                            byte[] fixstrnultermba = vfixstrnulterm.getBytes(fi.getEncoding());
                            if(fixstrnultermba.length <= fixstrnultermlen) {
                                bb.put(fixstrnultermba);
                                byte[] zero = new byte[fixstrnultermlen - fixstrnultermba.length];
                                bb.put(zero);
                            } else {
                                throw new RecordException("Wrong length of string " + fi.getField().getName() + " in " + t.getName());
                            }
                            log.fine("Write FIXSTRNULTERM with value " + vfixstrnulterm);
                            break;
                        case VARSTR:
                            String vvarstr;
                            if(fi.getClassType() == String.class) {
                                vvarstr = (String)fi.getField().get(o);
                            } else if(fi.getClassType() == String[].class) {
                                vvarstr = (String)Array.get(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vvarstr = null;
                            }
                            byte[] varstrba = vvarstr.getBytes(fi.getEncoding());
                            if((fi.getPrefixlength() == 0 || fi.getPrefixlength() == 2) && varstrba.length < 32768) {
                                bb.putShort((short)varstrba.length);
                                bb.put(varstrba);
                            } else if(fi.getPrefixlength() == 1 && varstrba.length < 128) {
                                bb.put((byte)varstrba.length);
                                bb.put(varstrba);
                            } else if(fi.getPrefixlength() == 4) {
                                bb.putInt(varstrba.length);
                                bb.put(varstrba);
                            } else {
                                throw new RecordException("Wrong length of string " + fi.getField().getName() + " in " + t.getName());
                            }
                            log.fine("Write VARSTR with value " + vvarstr);
                            break;
                        case VARFIXSTR:
                            String vvarfixstr;
                            if(fi.getClassType() == String.class) {
                                vvarfixstr = (String)fi.getField().get(o);
                            } else if(fi.getClassType() == String[].class) {
                                vvarfixstr = (String)Array.get(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vvarfixstr = null;
                            }
                            byte[] varfixstrba = vvarfixstr.getBytes(fi.getEncoding());
                            if(varfixstrba.length > fi.getLength()) {
                                throw new RecordException("Wrong length of string " + fi.getField().getName() + " in " + t.getName());
                            } else if((fi.getPrefixlength() == 0 || fi.getPrefixlength() == 2) && varfixstrba.length < 32768) {
                                bb.putShort((short)varfixstrba.length);
                                bb.put(varfixstrba);
                            } else if(fi.getPrefixlength() == 1 && varfixstrba.length < 128) {
                                bb.put((byte)varfixstrba.length);
                                bb.put(varfixstrba);
                            } else if(fi.getPrefixlength() == 4) {
                                bb.putInt(varfixstrba.length);
                                bb.put(varfixstrba);
                            } else {
                                throw new RecordException("Wrong length of string " + fi.getField().getName() + " in " + t.getName());
                            }
                            log.fine("Write VARFIXSTR with value " + vvarfixstr);
                            byte[] zero = new byte[fi.getLength() - varfixstrba.length];
                            bb.put(zero);
                            log.finer("Write " + zero.length + " padding bytes");
                            break;
                        case REMSTR:
                            String vremstr;
                            if(fi.getClassType() == String.class) {
                                vremstr = (String)fi.getField().get(o);
                            } else if(fi.getClassType() == String[].class) {
                                vremstr = (String)Array.get(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vremstr = null;
                            }
                            byte[] remstrba = vremstr.getBytes(fi.getEncoding());
                            bb.put(remstrba);
                            log.fine("Write REMSTR with value " + vremstr);
                            break;
                        case BOOLEAN:
                            boolean vboolean;
                            if(fi.getClassType() == boolean.class) {
                                vboolean = (Boolean)fi.getField().get(o);
                            } else if(fi.getClassType() == boolean[].class) {
                                vboolean = Array.getBoolean(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vboolean = false;
                            }
                            byte[] booleanba = new byte[fi.getLength()];
                            booleanba[0] =  vboolean ? (byte)1 : (byte)0;
                            bb.put(booleanba);
                            log.fine("Write BOOLEAN with value " + vboolean);
                            break;
                        case BIT:
                            int vbit;
                            if(fi.getClassType() == int.class) {
                                vbit = fi.getField().getInt(o);
                            } else if(fi.getClassType() == int[].class) {
                                vbit = Array.getInt(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vbit = 0;
                            }
                            bitbuf = bitbuf << fi.getLength() | vbit;
                            nbits += fi.getLength();
                            while(nbits >= 8) {
                                byte tmp = (byte)(bitbuf >> (nbits - 8));
                                bb.put(tmp);
                                bitbuf ^= (tmp << (nbits - 8));
                                nbits -= 8;
                            }
                            log.fine("Write BIT with value " + vbit);
                            break;
                        case JAVATIME:
                            Date vjavatime;
                            if(fi.getClassType() == Date.class) {
                                vjavatime = (Date)fi.getField().get(o);
                            } else if(fi.getClassType() == Date[].class) {
                                vjavatime = (Date)Array.get(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vjavatime = null;
                            }
                            bb.putLong(TimeUtil.toJavaTime(vjavatime));
                            log.fine("Write JAVATIME with value " + vjavatime);
                            break;
                        case UNIXTIME:
                            Date vunixtime;
                            if(fi.getClassType() == Date.class) {
                                vunixtime = (Date)fi.getField().get(o);
                            } else if(fi.getClassType() == Date[].class) {
                                vunixtime = (Date)Array.get(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vunixtime = null;
                            }
                            bb.putInt(TimeUtil.toUnixTime(vunixtime));
                            log.fine("Write UNIXTIME with value " + vunixtime);
                            break;
                        case VMSTIME:
                            Date vvmstime;
                            if(fi.getClassType() == Date.class) {
                                vvmstime = (Date)fi.getField().get(o);
                            } else if(fi.getClassType() == Date[].class) {
                                vvmstime = (Date)Array.get(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vvmstime = null;
                            }
                            bb.putLong(TimeUtil.toVMSTime(vvmstime));
                            log.fine("Write VMSTIME with value " + vvmstime);
                            break;
                        case PACKEDBCD:
                            int packedbcdlen;
                            if(lenpvd == null || lenpvd.getLength(o, i) < 0) {
                                packedbcdlen = fi.getLength();
                            } else {
                                packedbcdlen = lenpvd.getLength(o, i);
                            }
                            BigDecimal vpackedbcd;
                            if(fi.getClassType() == BigDecimal.class) {
                                vpackedbcd = (BigDecimal)fi.getField().get(o);
                            } else if(fi.getClassType() == BigDecimal[].class) {
                                vpackedbcd = (BigDecimal)Array.get(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vpackedbcd = null;
                            }
                            bb.put(BCDUtil.encodePackedBCD(vpackedbcd, fi.getDecimals(), packedbcdlen));
                            log.fine("Write PACKEDBCD with value " + vpackedbcd);
                            break;
                        case ZONEDBCD:
                            int zonedbcdlen;
                            if(lenpvd == null || lenpvd.getLength(o, i) < 0) {
                                zonedbcdlen = fi.getLength();
                            } else {
                                zonedbcdlen = lenpvd.getLength(o, i);
                            }
                            BigDecimal vzonedbcd;
                            if(fi.getClassType() == BigDecimal.class) {
                                vzonedbcd = (BigDecimal)fi.getField().get(o);
                            } else if(fi.getClassType() == BigDecimal[].class) {
                                vzonedbcd = (BigDecimal)Array.get(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vzonedbcd = null;
                            }
                            bb.put(BCDUtil.encodeZonedBCD(vzonedbcd, fi.getZone(), fi.getDecimals(), zonedbcdlen));
                            log.fine("Write ZONEDBCD with value " + vzonedbcd);
                            break;
                        case VAXFP4:
                            float vvaxfp4;
                            if(fi.getClassType() == float.class) {
                                vvaxfp4 = fi.getField().getFloat(o);
                            } else if(fi.getClassType() == float[].class) {
                                vvaxfp4 = Array.getFloat(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vvaxfp4 = 0;
                            }
                            bb.putInt(VAXFloatUtil.s2f(Float.floatToRawIntBits(vvaxfp4)));
                            log.fine("Write VAXFP4 with value " + vvaxfp4);
                            break;
                        case VAXFP8:
                            double vvaxfp8;
                            if(fi.getClassType() == double.class) {
                                vvaxfp8 = fi.getField().getDouble(o);
                            } else if(fi.getClassType() == double[].class) {
                                vvaxfp8 = Array.getDouble(fi.getField().get(o), ix);
                            } else {
                                badConversion(fi.getClassType(), fi.getStructType(), t);
                                vvaxfp8 = 0;
                            }
                            bb.putLong(VAXFloatUtil.t2g(Double.doubleToRawLongBits(vvaxfp8)));
                            log.fine("Write VAXFP8 with value " + vvaxfp8);
                            break;
                        case STRUCT:
                        	if(lenpvd == null) {
                                write(fi.getField().get(o));
                        	} else {
                                write(fi.getField().get(o), lenpvd.getLengthProvider(o, i));
                        	}
                            break;
                        default:
                            badConversion(fi.getClassType(), fi.getStructType(), t);
                    }
			    }
                // the field was a selector
                if(fi.getSelects() != null) {
                    // lookup class and padding
					SubClassAndPad scp;
					if(lenpvd != null && lenpvd.hasConvertSelector()) {
					    scp = fi.getSelects().get(lenpvd.convertSelector(fi.getField().get(o)));
					} else {
					    scp = fi.getSelects().get(fi.getField().getInt(o));
					}
                    if(scp == null) {
                        throw new RecordException(o.getClass().getName() + " " + fi.getField().getName() + " has invalid selector value: " + fi.getField().get(o));
                    }
                    // add select padding
                    selpad += scp.getPad();
                }
			}
			// write select pad bytes
            byte[] zero = new byte[selpad];
            bb.put(zero);
            log.finer("Write " + zero.length + " padding bytes");
            // if necessary write new record pad bytes
			if(si.getEndpad()) {
                int npad = StructInfo.calculateEndPad(bb.position(), si.getAlignment(), si.getFields());
                if(npad > 0) {
                    bb.put(new byte[npad]);
                    log.finer("Write " + npad + " end-padding bytes");
			    }
			}
		} catch (UnsupportedEncodingException e) {
			throw new RecordException("Unsupported encoding for string field", e);
		} catch (IllegalAccessException e) {
			throw new RecordException("Cannot access field", e);
		}
	}
	/**
	 * Get bytes.
	 * @return the resulting byte array
	 */
	public byte[] getBytes() {
		int n = bb.position();
		byte[] res = new byte[n];
		bb.rewind();
		bb.get(res);
		log.fine("Returning byte array of length " + res.length);
        if(log.isLoggable(Level.FINEST)) {
            log.finest("Byte array:" + LogHelper.byteArrayToString(res));
        }
		return res;
	}
	/**
	 * Get length.
	 * @return the length
	 */
	public int getLength() {
		return bb.position();
	}
	/**
	 * Extend capacity.
	 * @param newbufsiz new size of byte array to write to
	 */
	public void extend(int newbufsiz) {
		int n = bb.position();
		byte[] buf = new byte[n];
		bb.rewind();
		bb.get(buf);
		bb = ByteBuffer.allocate(newbufsiz);
		bb.put(buf);
		log.fine("StructWriter extended to buffersize " + newbufsiz);
		
	}
	private void badConversion(Class<?> clz, FieldType ft, Class<?> struct) throws RecordException {
		throw new RecordException("Can not convert from " + clz.getName() + " to " + ft.toString() + " in " + struct.getName());
	}
}
