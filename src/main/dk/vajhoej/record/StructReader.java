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
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class StructReader reads a Java object from a byte array containing a native struct.
 */
public class StructReader {
    private static Logger log = Logger.getLogger(StructReader.class.getName());
	private ByteBuffer bb;
	/**
	 * Create instance of StructReader.
	 * @param ba byte array to read from
	 */
	public StructReader(byte[] ba) {
		bb = ByteBuffer.wrap(ba);
		log.fine("StructReader initialized with byte array of length " + ba.length);
		if(log.isLoggable(Level.FINEST)) {
		    log.finest("Byte array:" + LogHelper.byteArrayToString(ba));
		}
	}
    /**
	 * Read.
	 * @param t class of what to read
	 * @return object read
	 * @throws RecordException if impossible to convert between types in class and struct
	 */
    public <T> T read(Class<T> t) throws RecordException {
        return read(t, (InfoProvider)null);
    }
    /**
     * Read.
     * @param t class of what to read
     * @param lenpvd supplies length for fields where it is not given (null indicates that it is to be ignored) 
     * @return object read
     * @throws RecordException if impossible to convert between types in class and struct
     */
    public <T> T read(Class<T> t, LengthProvider lenpvd) throws RecordException {
    	if(lenpvd instanceof LengthProvider2) {
    		return read(t, (LengthProvider2)lenpvd);
    	}
		final LengthProvider tmp = lenpvd;
    	return read(t, new LengthProvider2() {
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
     * Read.
     * @param t class of what to read
     * @param lenpvd supplies length for fields where it is not given (null indicates that it is to be ignored)
     * @return object read
     * @throws RecordException if impossible to convert between types in class and struct
     */
    public <T> T read(Class<T> t, LengthProvider2 lenpvd) throws RecordException {
    	if(lenpvd instanceof InfoProvider) {
    		return read(t, (InfoProvider)lenpvd);
    	}
		final LengthProvider2 tmp = lenpvd;
    	return read(t, new InfoProvider() {
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
     * Read.
     * @param t class of what to read
     * @param lenpvd supplies various length'es where they are not given (null indicates that it is to be ignored)
     * @return object read
     * @throws RecordException if impossible to convert between types in class and struct
     */
    public <T> T read(Class<T> t, InfoProvider lenpvd) throws RecordException {
		try {
		    log.fine("Reading class " + t.getName());
			long bitbuf = 0;
			int nbits = 0;
			bb.mark();
			StructInfo si = StructInfoCache.analyze(t);
			bb.order(si.getEndianess() == Endian.LITTLE ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
			T res = t.newInstance(); 
			for(int i = 0; i < si.getFields().size(); i++) {
			    FieldInfo fi = si.getFields().get(i);
			    int nskip = StructInfo.calculatePad(bb.position(), si.getAlignment(), fi);
			    if(nskip > 0) {
    		        bb.get(new byte[nskip]);
    		        log.finest("Skip " + nskip + " padding bytes");
			    }
			    int nelm;
			    if(lenpvd == null || lenpvd.getElements(res, i) < 0) {
			    	nelm = fi.getElements();
			    } else {
			    	nelm = lenpvd.getElements(res, i);
			    	// if relevant adjust the size of array
			    	if(fi.getField().get(res) != null) {
				    	if(fi.getField().get(res).getClass().isArray()) {
					    	if(Array.getLength(fi.getField().get(res)) != nelm) {
					    		fi.getField().set(res, Array.newInstance(fi.getField().get(res).getClass().getComponentType(), nelm));
					    	}
				    	}
			    	}
			    }
			    for(int ix = 0; ix < nelm; ix++) {
			    	switch(fi.getStructType()) {
    					case INT1:
                            byte vint1 = bb.get();
    						if(fi.getClassType() == byte.class) {
    							fi.getField().setByte(res, vint1);
    						} else if(fi.getClassType() == short.class) {
    							fi.getField().setShort(res, vint1);
    						} else if(fi.getClassType() == int.class) {
    							fi.getField().setInt(res, vint1);
                            } else if(fi.getClassType() == byte[].class) {
                                Array.setByte(fi.getField().get(res), ix, vint1);
    						} else {
    							badConversion(fi.getStructType(), fi.getClassType(), t);
    						}
                            log.fine("Read INT1 with value " + vint1);
    						break;
    					case INT2:
                            short vint2 = bb.getShort();
    						if(fi.getClassType() == short.class) {
    							fi.getField().setShort(res, vint2);
    						} else if(fi.getClassType() == int.class) {
    							fi.getField().setInt(res, vint2);
                            } else if(fi.getClassType() == short[].class) {
                                Array.setInt(fi.getField().get(res), ix, vint2);
    						} else {
    							badConversion(fi.getStructType(), fi.getClassType(), t);
    						}
                            log.fine("Read INT2 with value " + vint2);
    						break;
    					case INT4:
                            int vint4 = bb.getInt();
    						if(fi.getClassType() == int.class) {
    							fi.getField().setInt(res, vint4);
                            } else if(fi.getClassType() == int[].class) {
                                Array.setInt(fi.getField().get(res), ix, vint4);
    						} else {
    							badConversion(fi.getStructType(), fi.getClassType(), t);
    						}
                            log.fine("Read INT4 with value " + vint4);
    						break;
    					case INT8:
                            long vint8 = bb.getLong();
    						if(fi.getClassType() == long.class) {
    							fi.getField().setLong(res, vint8);
                            } else if(fi.getClassType() == long[].class) {
                                Array.setLong(fi.getField().get(res), ix, vint8);
    						} else {
    							badConversion(fi.getStructType(), fi.getClassType(), t);
    						}
                            log.fine("Read INT8 with value " + vint8);
    						break;
                        case UINT1:
                            short vuint1 = (short)(0xFF & bb.get());
                            if(fi.getClassType() == short.class) {
                                fi.getField().setShort(res, vuint1);
                            } else if(fi.getClassType() == int.class) {
                                fi.getField().setInt(res, vuint1);
                            } else if(fi.getClassType() == short[].class) {
                                Array.setShort(fi.getField().get(res), ix, vuint1);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read UINT1 with value " + vuint1);
                            break;
                        case UINT2:
                            int vuint2 = 0xFFFF & bb.getShort();
                            if(fi.getClassType() == int.class) {
                                fi.getField().setInt(res, vuint2);
                            } else if(fi.getClassType() == int[].class) {
                                Array.setInt(fi.getField().get(res), ix, vuint2);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read UINT2 with value " + vuint2);
                            break;
                        case UINT4:
                            long vuint4 = 0xFFFFFFFFL & bb.getInt();
                            if(fi.getClassType() == long.class) {
                                fi.getField().setLong(res, vuint4);
                            } else if(fi.getClassType() == long[].class) {
                                Array.setLong(fi.getField().get(res), ix, vuint4);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read UINT4 with value " + vuint4);
                            break;
    					case FP4:
                            float vfp4 = bb.getFloat();
    						if(fi.getClassType() == float.class) {
    							fi.getField().setFloat(res, vfp4);
                            } else if(fi.getClassType() == float[].class) {
                                Array.setFloat(fi.getField().get(res), ix, vfp4);
    						} else {
    							badConversion(fi.getStructType(), fi.getClassType(), t);
    						}
                            log.fine("Read FP4 with value " + vfp4);
    						break;
    					case FP8:
                            double vfp8 = bb.getDouble();
    						if(fi.getClassType() == double.class) {
    							fi.getField().setDouble(res, vfp8);
                            } else if(fi.getClassType() == double[].class) {
                                Array.setDouble(fi.getField().get(res), ix, vfp8);
    						} else {
    							badConversion(fi.getStructType(), fi.getClassType(), t);
    						}
                            log.fine("Read FP8 with value " + vfp8);
    						break;
                        case INTX:
                            int intxlen;
    					    if(lenpvd == null || lenpvd.getLength(res, i) < 0) {
    					        intxlen = fi.getLength();
    					    } else {
    					        intxlen = lenpvd.getLength(res, i);
    					    }
                            if(intxlen <= 0 || intxlen >= 8) {
                                throw new RecordException("Wrong length of general integer " + fi.getField().getName() + " in " + t.getName() + ": " + intxlen);    
                            }
                            long vintx = 0;
                            if(si.getEndianess() == Endian.BIG) {
                                for(int j = 0; j < intxlen; j++) {
                                    vintx = vintx | ((bb.get() & 0x00FFL) << ((intxlen - 1 - j) * 8));
                                }
                            } else if(si.getEndianess() == Endian.LITTLE) {
                                for(int j = 0; j < intxlen; j++) {
                                    vintx = vintx | ((bb.get() & 0x00FFL) << (j * 8));
                                }
                            }
                            if(fi.getClassType() == long.class) {
                                fi.getField().setLong(res, vintx);
                            } else if(fi.getClassType() == long[].class) {
                                Array.setLong(fi.getField().get(res), ix, vintx);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read INTX with value " + vintx);
                            break;
    					case FIXSTR:
    					    int fixstrlen;
    					    if(lenpvd == null || lenpvd.getLength(res, i) < 0) {
    					        fixstrlen = fi.getLength();
    					    } else {
    					        fixstrlen = lenpvd.getLength(res, i);
    					    }
                            byte[] fixstrba = new byte[fixstrlen];
                            bb.get(fixstrba);
                            String vfixstr = new String(fixstrba, fi.getEncoding());
    						if(fi.getClassType() == String.class) {
    							fi.getField().set(res, vfixstr);
                            } else if(fi.getClassType() == String[].class) {
                                Array.set(fi.getField().get(res), ix, vfixstr);
    						} else {
    							badConversion(fi.getStructType(), fi.getClassType(), t);
    						}
                            log.fine("Read FIXSTR with value " + vfixstr);
    						break;
                        case FIXSTRNULTERM:
                            int fixstrnultermlen;
                            if(lenpvd == null || lenpvd.getLength(res, i) < 0) {
                                fixstrnultermlen = fi.getLength();
                            } else {
                                fixstrnultermlen = lenpvd.getLength(res, i);
                            }
                            byte[] fixstrnultermba = new byte[fixstrnultermlen];
                            bb.get(fixstrnultermba);
                            int actlen = 0;
                            while(actlen < fi.getLength() && fixstrnultermba[actlen] != 0) {
                                actlen++;
                            }
                            String vfixstrnulterm = new String(fixstrnultermba, 0, actlen, fi.getEncoding());
                            if(fi.getClassType() == String.class) {
                                fi.getField().set(res, vfixstrnulterm);
                            } else if(fi.getClassType() == String[].class) {
                                Array.set(fi.getField().get(res), ix, vfixstrnulterm);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read FIXSTRNULTERM with value " + vfixstrnulterm);
                            break;
                        case VARSTR:
                            int varstrlen;
                            switch(fi.getPrefixlength()) {
	                            case 0:
	                            case 2:
	                                varstrlen = bb.getShort() & 0x0000FFFF;
	                                break;
	                            case 1:
	                                varstrlen = bb.get() & 0x000000FF;
	                                break;
	                            case 4:
	                                varstrlen = bb.getInt();
	                                break;
                                default:
                                	varstrlen = -1;
                                	break;
                            }
                            if(varstrlen < 0) {
                                throw new RecordException("Wrong length of string " + fi.getField().getName() + " in " + t.getName() + ": " + varstrlen);
                            }
                            byte[] varstrba = new byte[varstrlen];
                            bb.get(varstrba);
                            String vvarstr = new String(varstrba, fi.getEncoding());
                            if(fi.getClassType() == String.class) {
                                fi.getField().set(res, vvarstr);
                            } else if(fi.getClassType() == String[].class) {
                                Array.set(fi.getField().get(res), ix, vvarstr);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read VARSTR with value " + vvarstr);
                            break;
                        case VARFIXSTR:
                            int varfixstrlen;
                            switch(fi.getPrefixlength()) {
	                            case 0:
	                            case 2:
	                                varfixstrlen = bb.getShort() & 0x0000FFFF;
	                                break;
	                            case 1:
	                                varfixstrlen = bb.get() & 0x000000FF;
	                                break;
	                            case 4:
	                                varfixstrlen = bb.getInt();
	                                break;
                                default:
                                	varfixstrlen = -1;
                                	break;
                            }
                            if(varfixstrlen < 0 || varfixstrlen  > fi.getLength()) {
                                throw new RecordException("Wrong length of string " + fi.getField().getName() + " in " + t.getName() + ": " + varfixstrlen);
                            }
                            byte[] varfixstrba = new byte[varfixstrlen];
                            bb.get(varfixstrba);
                            String vvarfixstr = new String(varfixstrba, fi.getEncoding());
                            if(fi.getClassType() == String.class) {
                                fi.getField().set(res, vvarfixstr);
                            } else if(fi.getClassType() == String[].class) {
                                Array.set(fi.getField().get(res), ix, vvarfixstr);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read VARFIXSTR with value " + vvarfixstr);
                            byte[] zero = new byte[fi.getLength() - varfixstrlen];
                            bb.get(zero);
                            log.finest("Skip " + zero.length + " padding bytes");
                            break;
                        case REMSTR:
                            int remstrlen = bb.remaining();
                            byte[] remstrba = new byte[remstrlen]; 
                            bb.get(remstrba);
                            String vremstr = new String(remstrba, fi.getEncoding());
                            if(fi.getClassType() == String.class) {
                                fi.getField().set(res, vremstr);
                            } else if(fi.getClassType() == String[].class) {
                                Array.set(fi.getField().get(res), ix, vremstr);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read REMSTR with value " + vremstr);
                            break;
                        case BOOLEAN:
                            byte[] booleanba = new byte[fi.getLength()];
                            bb.get(booleanba);
                            boolean vboolean = booleanba[0] != 0;
                            if(fi.getClassType() == boolean.class) {
                                fi.getField().set(res, vboolean);
                            } else if(fi.getClassType() == boolean[].class) {
                                Array.setBoolean(fi.getField().get(res), ix, vboolean);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read BOOLEAN with value " + vboolean);
                            break;
    					case BIT:
                            while(nbits < fi.getLength()) {
                                bitbuf = (bitbuf << 8) | (0xFF & bb.get());
                                nbits += 8;
                            }
                            int vbit = (int)(bitbuf >> (nbits - fi.getLength()));
                            bitbuf ^= (vbit << (nbits - fi.getLength()));
                            nbits -= fi.getLength();
    						if(fi.getClassType() == int.class) {
    	                        fi.getField().setInt(res, vbit);
                            } else if(fi.getClassType() == int[].class) {
                                Array.setInt(fi.getField().get(res), ix, vbit);
    						} else {
    							badConversion(fi.getStructType(), fi.getClassType(), t);
    						}
                            log.fine("Read BIT with value " + vbit);
    						break;
    					case JAVATIME:
                            Date vjavatime = TimeUtil.fromJavaTime(bb.getLong());
    						if(fi.getClassType() == Date.class) {
    							fi.getField().set(res, vjavatime);
                            } else if(fi.getClassType() == Date[].class) {
                                Array.set(fi.getField().get(res), ix, vjavatime);
    						} else {
    							badConversion(fi.getStructType(), fi.getClassType(), t);
    						}
                            log.fine("Read JAVATIME with value " + vjavatime);
    						break;
    					case UNIXTIME:
                            Date vunixtime = TimeUtil.fromUnixTime(bb.getInt());
    						if(fi.getClassType() == Date.class) {
    							fi.getField().set(res, vunixtime);
                            } else if(fi.getClassType() == Date[].class) {
                                Array.set(fi.getField().get(res), ix, vunixtime);
    						} else {
    							badConversion(fi.getStructType(), fi.getClassType(), t);
    						}
                            log.fine("Read UNIXTIME with value " + vunixtime);
    						break;
    					case VMSTIME:
                            Date vvmstime = TimeUtil.fromVMSTime(bb.getLong());
    						if(fi.getClassType() == Date.class) {
    							fi.getField().set(res, vvmstime);
                            } else if(fi.getClassType() == Date[].class) {
                                Array.set(fi.getField().get(res), ix, vvmstime);
    						} else {
    							badConversion(fi.getStructType(), fi.getClassType(), t);
    						}
                            log.fine("Read VMSTIME with value " + vvmstime);
    						break;
                        case PACKEDBCD:
                            int packedbcdlen;
                            if(lenpvd == null || lenpvd.getLength(res, i) < 0) {
                                packedbcdlen = fi.getLength();
                            } else {
                                packedbcdlen = lenpvd.getLength(res, i);
                            }
                            byte[] packedbcdba = new byte[packedbcdlen];
                            bb.get(packedbcdba);
                            BigDecimal vpackedbcd = BCDUtil.decodePackedBCD(packedbcdba, fi.getDecimals());
                            if(fi.getClassType() == BigDecimal.class) {
                                fi.getField().set(res, vpackedbcd);
                            } else if(fi.getClassType() == BigDecimal[].class) {
                                Array.set(fi.getField().get(res), ix, vpackedbcd);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read PACKEDBCD with value " + vpackedbcd);
                            break;
                        case ZONEDBCD:
                            int zonedbcdlen;
                            if(lenpvd == null || lenpvd.getLength(res, i) < 0) {
                                zonedbcdlen = fi.getLength();
                            } else {
                                zonedbcdlen = lenpvd.getLength(res, i);
                            }
                            byte[] zonedbcdba = new byte[zonedbcdlen];
                            bb.get(zonedbcdba);
                            BigDecimal vzonedbcd = BCDUtil.decodeZonedBCD(zonedbcdba, fi.getZone(), fi.getDecimals());
                            if(fi.getClassType() == BigDecimal.class) {
                                fi.getField().set(res, vzonedbcd);
                            } else if(fi.getClassType() == BigDecimal[].class) {
                                Array.set(fi.getField().get(res), ix, vzonedbcd);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read ZONEDBCD with value " + vzonedbcd);
                            break;
                        case VAXFP4:
                            float vvaxfp4 = Float.intBitsToFloat(VAXFloatUtil.f2s(bb.getInt()));
                            if(fi.getClassType() == float.class) {
                                fi.getField().setFloat(res, vvaxfp4);
                            } else if(fi.getClassType() == float[].class) {
                                Array.setFloat(fi.getField().get(res), ix, vvaxfp4);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read VAXFP4 with value " + vvaxfp4);
                            break;
                        case VAXFP8:
                            double vvaxfp8 = Double.longBitsToDouble(VAXFloatUtil.g2t(bb.getLong()));
                            if(fi.getClassType() == double.class) {
                                fi.getField().setDouble(res, vvaxfp8);
                            } else if(fi.getClassType() == double[].class) {
                                Array.setDouble(fi.getField().get(res), ix, vvaxfp8);
                            } else {
                                badConversion(fi.getStructType(), fi.getClassType(), t);
                            }
                            log.fine("Read VAXFP8 with value " + vvaxfp8);
                            break;
    					case STRUCT:
                        	if(lenpvd == null || lenpvd.getLengthProvider(res, i) == null) {
        					    if(fi.getClassType().isArray()) {
        					        Array.set(fi.getField().get(res), ix, read(fi.getClassType().getComponentType()));
        					    } else {
        					        fi.getField().set(res, read(fi.getClassType()));
        					    }
                        	} else {
        					    if(fi.getClassType().isArray()) {
        					        Array.set(fi.getField().get(res), ix, read(fi.getClassType().getComponentType(), lenpvd.getLengthProvider(res, i)));
        					    } else {
        					        fi.getField().set(res, read(fi.getClassType(), lenpvd.getLengthProvider(res, i)));
        					    }
                        	}
    						break;
    					default:
    						badConversion(fi.getStructType(), fi.getClassType(), t);
    				}
			    }
                // the field was a selector
				if(fi.getSelects() != null) {
					// lookup class and padding
					SubClassAndPad scp;
					if(lenpvd != null && lenpvd.hasConvertSelector()) {
					    scp = fi.getSelects().get(lenpvd.convertSelector(fi.getField().get(res)));
					} else {
						scp = fi.getSelects().get(fi.getField().getInt(res));
					}
				    if(scp == null) {
				        throw new RecordException(res.getClass().getName() + " " + fi.getField().getName() + " has invalid selector value: " + fi.getField().get(res));
				    }
					@SuppressWarnings("unchecked")
					Class<? extends T> tt = (Class<? extends T>)scp.getSubClass();
					// if class different from current (to avoid infinite recursion)
					if(!tt.equals(t)) {
					    // go back to start of bytes
						bb.reset();
						// read the sub class
						T o = read(tt, lenpvd);
						log.fine("Restarting for sub class " + tt.getName());
						// read select pad bytes
                        byte[] zero = new byte[scp.getPad()];
                        bb.get(zero);
                        log.finest("Skip " + zero.length + " padding bytes");
                        return o;
					}
				}
			}
			// if necessary read new record pad bytes
            if(si.getEndpad()) {
                int nskip = StructInfo.calculateEndPad(bb.position(), si.getAlignment(), si.getFields());
                if(nskip > 0) {
                    bb.get(new byte[nskip]);
                    log.finest("Skip " + nskip + " end-padding bytes");
                }
            }
			return res;
		} catch(BufferUnderflowException e) {
            throw new RecordException("Not enough bytes in input", e);
		} catch (UnsupportedEncodingException e) {
			throw new RecordException("Unsupported encoding for string field", e);
		} catch (InstantiationException e) {
			throw new RecordException("Cannot instantiate class", e);
		} catch (IllegalAccessException e) {
            throw new RecordException("Cannot access field", e);
		}
	}
	/**
	 * More records available.
	 * @return true=more, false=no more
	 */
	public boolean more() {
	    return bb.remaining() > 0;
	}
	private void badConversion(FieldType ft, Class<?> clz, Class<?> struct) throws RecordException {
		throw new RecordException("Can not convert from " + ft.toString() + " to " + clz.getName() + " in " + struct.getName());
	}
}
