/*
 * Copyright 2011 Arne Vajhøj.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to process lists and to work with files instead of byte arrays
 * trying to work even with variable length structs.
 */
public class Util2 {
    /**
     * Interface for processing objects read.
     */
    public static interface ObjectHandler<T> {
        /**
         * Process object.
         * @param o object to process
         * @throws RecordException if problem with record definition
         * @throws IOException if problem with stream
         */
        public void process(T o) throws RecordException, IOException;
    }
    /**
     * Interface for converting objects read.
     */
    public static interface Transformer<T1, T2> {
        /**
         * Convert object.
         * @param o object to convert
         * @return converted object
         */
        public T2 convert(T1 o);
    }
    private static final int BUFSIZ = 1000;
    /**
     * Read array of struct in byte array into list of objects.
     * @param t type
     * @param b byte array
     * @param lenpvd length provider
     * @return list of objects
     * @throws RecordException if problem with record definition
     */
    public static <T> List<T> readAll(Class<T> t, byte[] b, LengthProvider2 lenpvd) throws RecordException {
        StructReader sr = new StructReader(b);
        List<T> res = new ArrayList<T>();
        while(sr.more()) {
            res.add(sr.read(t, lenpvd));
        }
        return res;
    }
    /**
     * Write list of objects into array of struct in byte array.
     * @param t type
     * @param lst list of objects
     * @param lenpvd length provider
     * @return byte array
     * @throws RecordException if problem with record definition
     */
    public static <T> byte[] writeAll(Class<T> t, List<T> lst, LengthProvider2 lenpvd) throws RecordException {
    	int maxelm = 16;
        StructWriter sw = new StructWriter(maxelm * calcSize(t, lenpvd));
        for(T o : lst) {
            sw.write(o, lenpvd);
            if(sw.getLength() + calcSize(t, lenpvd) > maxelm * calcSize(t, lenpvd)) {
            	maxelm *= 2;
            	sw.extend(maxelm * calcSize(t, lenpvd));
            }
        }
        return sw.getBytes();
    }
    /**
     * Read array of struct in stream into list of objects. 
     * @param t type
     * @param is stream
     * @param lst list of objects
     * @param lenpvd length provider
     * @throws RecordException if problem with record definition
     * @throws IOException if problem with stream
     */
    public static <T> void readAll(Class<T> t, InputStream is, List<T> lst, LengthProvider2 lenpvd) throws RecordException, IOException {
        final List<T> lst2 = lst;
        readAll(t,
                is,
                new ObjectHandler<T>() {
                    public void process(T o) {
                        lst2.add(o);
                    }
                },
                lenpvd);
    }
    /**
     * Write list of objects into array of struct in stream. 
     * @param t type
     * @param lst list of objects
     * @param os stream
     * @param lenpvd length provider
     * @throws RecordException if problem with record definition
     * @throws IOException if problem with stream
     */
    public static <T> void writeAll(Class<T> t, List<T> lst, OutputStream os, LengthProvider2 lenpvd) throws RecordException, IOException {
        for(int i = 0; i < lst.size(); i += BUFSIZ) {
            os.write(writeAll(t, lst.subList(i, Math.min(i + BUFSIZ, lst.size())), lenpvd));
        }
    }
    /**
     * Read array of struct in stream and processes them by handler. 
     * @param t type
     * @param is stream
     * @param oh handler of objects
     * @param lenpvd length provider
     * @throws RecordException if problem with record definition
     * @throws IOException if problem with stream
     */
    public static <T> void readAll(Class<T> t, InputStream is, ObjectHandler<T> oh, LengthProvider2 lenpvd) throws RecordException, IOException {
        int siz = BUFSIZ * calcSize(t, lenpvd);
        boolean more = true;
        while(more) {
            byte[] b = new byte[siz];
            int n = 0;
            int ix = 0;
            while((n = is.read(b, ix, b.length - ix)) > 0) {
                ix += n;
            }
            if(ix < siz) {
                byte[] tmp = new byte[ix];
                System.arraycopy(b, 0, tmp, 0, ix);
                b = tmp;
                more = false;
            }
            if(ix > 0) {
                for(T o : readAll(t, b, lenpvd)) {
                    oh.process(o);
                }
            }
        }
    }
    /**
     * Convert array of struct in bytes into array of struct in bytes. 
     * @param t1 from type
     * @param b from byte array
     * @param t2 to type
     * @param cvt transformer class
     * @param lenpvd length provider
     * @return to byte array
     * @throws RecordException if problem with record definitions
     */
    public static <T1,T2> byte[] copyAll(Class<T1> t1, byte[] b, Class<T2> t2, Transformer<T1,T2> cvt, LengthProvider2 lenpvd) throws RecordException {
        List<T1> lst1 = readAll(t1, b, lenpvd);
        List<T2> lst2 = new ArrayList<T2>();
        for(T1 o : lst1) {
            lst2.add(cvt.convert(o));
        }
        return writeAll(t2, lst2, lenpvd);
    }
    /**
     * Convert array of struct in stream into array of struct in stream. 
     * @param t1 from type
     * @param is from stream
     * @param t2 to type
     * @param os to stream
     * @param cvt transformer class
     * @param lenpvd length provider
     * @throws RecordException if problem with record definitions
     * @throws IOException if problem with streams
     */
    public static <T1,T2> void copyAll(Class<T1> t1, InputStream is, Class<T2> t2, OutputStream os, Transformer<T1,T2> cvt, LengthProvider2 lenpvd) throws RecordException, IOException {
        final OutputStream os2 = os;
        final Transformer<T1,T2> cvt2 = cvt;
        readAll(t1,
                is,
                new ObjectHandler<T1>() {
                    public void process(T1 o) throws RecordException, IOException {
                        StructWriter sw = new StructWriter();
                        sw.write(cvt2.convert(o));
                        os2.write(sw.getBytes());
                    }
                },
                lenpvd);
    }
    private static <T> int calcSize(Class<T> t, LengthProvider2 lenpvd) throws RecordException {
    	if(lenpvd != null && lenpvd.getMaxLength() >= 0) {
    		return lenpvd.getMaxLength();
    	} else {
            StructInfo si = StructInfoCache.analyze(t);
            if(si.isFixedLength()) {
                return si.getLength();
            } else {
                throw new RecordException("Length provider not supplied and cannot calculate size of " + t.getName() + " because it contains at least one field of type VARSTR");
            }
    	}
    }
}
