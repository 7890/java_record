/*
 * Copyright 2009 Arne Vajh�j.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import dk.vajhoej.record.RecordException;
import dk.vajhoej.record.Util;

public class TestUtil {
    private final static int N = 10000;
    @Test
    public void testReadList() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
        byte[] bm = new byte[N*b.length];
        for(int i = 0; i < N; i++) {
            System.arraycopy(b, 0, bm, i*b.length, b.length);
        }
        try {
            List<Data> lst = Util.readAll(Data.class, bm);
            assertEquals("lst size", N, lst.size());
            for(int i = 0; i < lst.size(); i++) {
                assertEquals("iv #" + i, 258, lst.get(i).getIv());
                assertEquals("xv #" + i, 123.456, lst.get(i).getXv(), 0.0005);
                assertEquals("sv #" + i, "ABC     ", lst.get(i).getSv());
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteList() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
        try {
            List<Data> lst = new ArrayList<Data>();
            for(int i = 0; i < N; i++) {
                Data o = new Data();
                o.setIv(258);
                o.setXv(123.456);
                o.setSv("ABC     ");
                lst.add(o);
            }
            byte[] res = Util.writeAll(Data.class, lst);
            assertEquals("length", N*b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i % b.length], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadStream() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
        byte[] bm = new byte[N*b.length];
        for(int i = 0; i < N; i++) {
            System.arraycopy(b, 0, bm, i*b.length, b.length);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bm);
        try {
            List<Data> lst = new ArrayList<Data>();
            Util.readAll(Data.class, bais, lst);
            assertEquals("lst size", N, lst.size());
            for(int i = 0; i < lst.size(); i++) {
                assertEquals("iv #" + i, 258, lst.get(i).getIv());
                assertEquals("xv #" + i, 123.456, lst.get(i).getXv(), 0.0005);
                assertEquals("sv #" + i, "ABC     ", lst.get(i).getSv());
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testWriteStream() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
        try {
            List<Data> lst = new ArrayList<Data>();
            for(int i = 0; i < N; i++) {
                Data o = new Data();
                o.setIv(258);
                o.setXv(123.456);
                o.setSv("ABC     ");
                lst.add(o);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Util.writeAll(Data.class, lst, baos);
            byte[] res = baos.toByteArray();
            assertEquals("length", N*b.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b[i % b.length], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testReadHandler() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
        byte[] bm = new byte[N*b.length];
        for(int i = 0; i < N; i++) {
            System.arraycopy(b, 0, bm, i*b.length, b.length);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bm);
        try {
            Util.readAll(Data.class,
                    bais,
                    new Util.ObjectHandler<Data>() {
                        public void process(Data o) {
                            assertEquals("iv", 258, o.getIv());
                            assertEquals("xv", 123.456, o.getXv(), 0.0005);
                            assertEquals("sv", "ABC     ", o.getSv());
                        }
                    } );
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testCopyArrays() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
        byte[] b2 = { 0x03, 0x01, 0x00, 0x00,
                      0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                      0x41, 0x42, 0x43, 0x58, 0x58, 0x58, 0x58, 0x58 };
        try {
            byte[] res = Util.copyAll(Data.class,
                                      b,
                                      Data.class,
                                      new Util.Transformer<Data, Data>() {
                                          public Data convert(Data o) {
                                              Data res = new Data();
                                              res.setIv(o.getIv() + 1);
                                              res.setXv(o.getXv());
                                              res.setSv(o.getSv().replace(' ', 'X'));
                                              return res;
                                          }
                                      } );
            assertEquals("length", b2.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b2[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        }
    }
    @Test
    public void testCopyStreams() {
        byte[] b = { 0x02, 0x01, 0x00, 0x00,
                     0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                     0x41, 0x42, 0x43, 0x20, 0x20, 0x20, 0x20, 0x20 };
        byte[] b2 = { 0x03, 0x01, 0x00, 0x00,
                      0x77, (byte)0xBE, (byte)0x9F, 0x1A, 0x2F, (byte)0xDD, 0x5E, 0x40,
                      0x41, 0x42, 0x43, 0x58, 0x58, 0x58, 0x58, 0x58 };
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Util.copyAll(Data.class,
                         bais,
                         Data.class,
                         baos,
                         new Util.Transformer<Data, Data>() {
                             public Data convert(Data o) {
                                 Data res = new Data();
                                 res.setIv(o.getIv() + 1);
                                 res.setXv(o.getXv());
                                 res.setSv(o.getSv().replace(' ', 'X'));
                                 return res;
                             }
                         } );
            byte[] res = baos.toByteArray();
            assertEquals("length", b2.length, res.length);
            for(int i = 0; i < res.length; i++) {
                assertEquals("byte " + i, b2[i], res[i]);
            }
        } catch (RecordException e) {
            fail("Unexpected exception: " + e);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }
}
