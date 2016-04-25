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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Class StructInfo contains information about a native struct needed for reading and/or writing.
 */
public class StructInfo {
    private static Logger log = Logger.getLogger(StructInfo.class.getName());
	private Endian endianess;
	private Alignment alignment;
	private boolean endpad;
	private List<FieldInfo> fields;
    private boolean fixedLength;
    private int length;
	/**
	 * Create instance of StructInfo.
	 * @param endianess byte order for all fields
	 * @param alignment alignment for all fields
	 * @param endpad pad at end
	 * @param fields array of FieldInfo describing all fields
	 * @param clz class implementing struct
	 * @throws RecordException if error calculating length information
	 */
	public StructInfo(Endian endianess, Alignment alignment, boolean endpad, List<FieldInfo> fields, Class<?> clz) throws RecordException {
		this.endianess = endianess;
		this.alignment = alignment;
		this.endpad = endpad;
		this.fields = fields;
		fixedLength = calculateFixedLength(fields);
		length = calculateLength(fields, alignment, endpad, clz);
	}
	/**
     * Get endianess.
     * @return endianess byte order for all fields
     */
    public Endian getEndianess() {
        return endianess;
    }
    /**
     * Get Alignment.
     * @return alignment for all fields
     */
    public Alignment getAlignment() {
        return alignment;
    }
    /**
     * Get pad at end.
     * @return pad at end
     */
    public boolean getEndpad() {
        return endpad;
    }
	/**
	 * Get fields.
	 * @return fields array of FieldInfo describing all fields
	 */
	public List<FieldInfo> getFields() {
		return fields;
	}
	/**
	 * Is this a fixed length struct.
	 * @return true=fixed length, false=variable length
	 */
    public boolean isFixedLength() {
        return fixedLength;
    }
    /**
     * Get length of fixed length parts of struct.
     * @return length
     */
    public int getLength() {
        return length;
    }
	/**
	 * Analyze class.
	 * @param clz class to analyze
	 * @return StructInfo for class
	 * @throws RecordException if error calculation length information
	 */
    public static StructInfo analyze(Class<?> clz) throws RecordException {
		List<FieldInfo> fi = new ArrayList<FieldInfo>();
		analyze(clz, fi, 0, true);
		log.finer(clz.getName() + " analyzed for StructInfo");
        Struct s = clz.getAnnotation(Struct.class);
        return new StructInfo(s.endianess(), s.alignment(), s.endpad(), fi, clz);
	}
    static int calculatePad(int pos, Alignment align, FieldInfo fi) throws RecordException {
        int nbyte = natural(fi);
        int npad = 0;
        switch(align) {
            case PACKED:
            case ALIGN1:
                npad = 0;
                break;
            case NATURAL:
                npad = (nbyte - pos % nbyte) % nbyte;
                break;
            case ALIGN2:
                npad = (2 - pos % 2) % 2;
                break;
            case ALIGN4:
                npad = (4 - pos % 4) % 4;
                break;
            case ALIGN8:
                npad = (8 - pos % 8) % 8;
                break;
        }
        return npad;
    }
    static int calculateEndPad(int pos, Alignment align, List<FieldInfo> fields) throws RecordException {
        int npad = 0;
        for(FieldInfo fi : fields) {
            npad = Math.max(npad, calculatePad(pos, align, fi));
        }
        return npad;
    }
    private static void analyze(Class<?> clz, List<FieldInfo> allfi, int offset, boolean dosuper) throws RecordException {
        Struct s = clz.getAnnotation(Struct.class);
        if(s == null) {
            throw new IllegalArgumentException(clz.getName() + " is not a struct");
        }
        if(dosuper && !clz.getSuperclass().equals(Object.class)) {
            analyze(clz.getSuperclass(), allfi, offset, true);
        }
		Field[] refl = clz.getDeclaredFields();
        for(int i = 0; i < refl.length; i++) {
            if((refl[i].getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) == 0) {
                allfi.add(null);
            }
        }
        for(int i = 0; i < refl.length; i++) {
            if((refl[i].getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) == 0) {
    		    refl[i].setAccessible(true);
    			StructField sf = refl[i].getAnnotation(StructField.class);
    			if(sf == null) {
    				throw new IllegalArgumentException(clz.getName() + " contains a field " + refl[i].getName() + " with no meta-data");
    			}
    			Selector sel = refl[i].getAnnotation(Selector.class);
    			Map<Integer, SubClassAndPad> selmap = null;
    			boolean selpad = false;
    			if(sel != null) {
    				selmap = new HashMap<Integer, SubClassAndPad>();
    				selpad = sel.pad();
    				if(sel.pad()) {
                        int maxlen = 0;
                        for(SubType st : sel.subtypes()) {
                            maxlen = Math.max(maxlen, calculateExtraLength(st.type(), sf.n() + 1));
                        }
                        for(SubType st : sel.subtypes()) {
                            selmap.put(st.value(), new SubClassAndPad(st.type(), maxlen - calculateExtraLength(st.type(), sf.n() + 1)));
                        }
    				} else {
                        for(SubType st : sel.subtypes()) {
                            selmap.put(st.value(), new SubClassAndPad(st.type(), 0));
                        }
    				}
    			}
    			int elm = 1;
    			ArrayField arr = refl[i].getAnnotation(ArrayField.class);
    			if(arr != null) {
    			    elm = arr.elements();
    			}
    			if(sf.n() - offset < allfi.size() - refl.length || sf.n() - offset >= allfi.size()) {
    				throw new IllegalArgumentException(refl[i].getName() + " in " + clz.getName() + " has illegal number");
    			}
    			if(allfi.get(sf.n() - offset) == null) {
    				allfi.set(sf.n() - offset, new FieldInfo(sf.type(), sf.length(), sf.decimals(), sf.encoding(), sf.zone(), sf.prefixlength(), refl[i].getType(), refl[i], selmap, selpad, elm));
    			} else {
    				throw new IllegalArgumentException(clz.getName() + " has duplicates in field ordering");
    			}
            }
		}
	}
    private static boolean calculateFixedLength(List<FieldInfo> fields) throws RecordException {
        boolean res = true;
        for(FieldInfo fi : fields) {
            if(fi.getStructType() == FieldType.VARSTR || fi.getStructType() == FieldType.REMSTR) {
                res = false;
            } else if(fi.getStructType() == FieldType.STRUCT) {
            	if(fi.getClassType().isArray()) {
                    res = res && analyze(fi.getClassType().getComponentType()).isFixedLength();
            	} else {
                    res = res && analyze(fi.getClassType()).isFixedLength();
            	}
            }
            if(fi.getSelects() != null) {
                res = res && fi.getSelectPad();
            }
        }
        return res;
    }
    private static int calculateLength(List<FieldInfo> fields, Alignment alignment, boolean endpad, Class<?> clz) throws RecordException {
        int res = 0;
        for(int i = 0; i < fields.size(); i++) {
            FieldInfo fi = fields.get(i);
            res += calculatePad(res, alignment, fi);
            int nelm;
            if(fi.getClassType().isArray()) {
            	nelm = fi.getElements();
            } else {
            	nelm = 1;
            }
            switch(fi.getStructType()) {
	            case REMSTR:
	                res += 0;
	                break;
                case INT1:
                case UINT1:
                    res += nelm*1;
                    break;
                case INT2:
                case UINT2:
                    res += nelm*2;
                    break;
                case INT4:
                case UINT4:
                case FP4:
                case UNIXTIME:
                case VAXFP4:
                    res += nelm*4;
                    break;
                case INT8:
                case FP8:
                case JAVATIME:
                case VMSTIME:
                case VAXFP8:
                    res += nelm*8;
                    break;
                case INTX:
                case FIXSTR:
                case FIXSTRNULTERM:
                case BOOLEAN:
                case PACKEDBCD:
                case ZONEDBCD:
                    res += nelm*fi.getLength();
                    break;
                case VARSTR:
                    res += nelm*2;
                    break;
                case VARFIXSTR:
                    res += nelm*(2 + fi.getLength());
                    break;
                case BIT:
                    res += nelm*((fi.getLength() + 7) / 8);
                    break;
                case STRUCT:
                	if(fi.getClassType().isArray()) {
                        res += nelm*analyze(fi.getClassType().getComponentType()).getLength();
                	} else {
                        res += nelm*analyze(fi.getClassType()).getLength();
                	}
                    break;
                default:
                    throw new RecordException(fi.getStructType().name() + " is an unknown type");
            }
            if(fi.getSelects() != null) {
                if(fi.getSelectPad()) {
                    Iterator<SubClassAndPad> it = fi.getSelects().values().iterator();
                    boolean fnd = false; 
                    while(it.hasNext()) {
                        SubClassAndPad scp = it.next();
                        if(scp.getSubClass().equals(clz)) {
                            res += scp.getPad();
                            fnd = true;
                        }
                    }
                    if(!fnd) {
                        SubClassAndPad scp = fi.getSelects().values().toArray(new SubClassAndPad[1])[0];
                        res = analyze(scp.getSubClass()).getLength();
                    }
                }
            }
        }
        if(endpad) {
            res += calculateEndPad(res, alignment, fields);
        }
        return res;
    }
    private static int calculateExtraLength(Class<?> clz, int offset) throws RecordException {
        List<FieldInfo> fi = new ArrayList<FieldInfo>();
        analyze(clz, fi, offset, false);
        Struct s = clz.getAnnotation(Struct.class);
        return calculateLength(fi, s.alignment(), s.endpad(), clz);
    }
    private static int natural(FieldInfo fi) throws RecordException {
        switch(fi.getStructType()) {
            case INT1:
            case UINT1:
            case INTX:
            case FIXSTR:
            case FIXSTRNULTERM:
            case REMSTR:
            case PACKEDBCD:
            case ZONEDBCD:
            case BIT:
            case STRUCT:
                return 1;
            case INT2:
            case UINT2:
            case VARSTR:
            case VARFIXSTR:
                return 2;
            case INT4:
            case UINT4:
            case FP4:
            case UNIXTIME:
            case VAXFP4:
                return 4;
            case INT8:
            case FP8:
            case JAVATIME:
            case VMSTIME:
            case VAXFP8:
                return 8;
            case BOOLEAN:
                return fi.getLength();
            default:
                throw new RecordException(fi.getStructType() + " is an unknown type");
        }
    }
}
