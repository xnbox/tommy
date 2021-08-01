/*
MIT License

Copyright (c) 2021 xnbox team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

HOME:   https://xnbox.github.io
E-Mail: xnbox.team@outlook.com
*/

package org.tommy.common.utils;

public interface SystemProperties {
	String OS_NAME                            = System.getProperty("os.name");
	String OS_ARCH                            = System.getProperty("os.arch");
	String JAVA_IO_TMPDIR                     = System.getProperty("java.io.tmpdir");
	String JAVA_AWT_HEADLESS                  = System.getProperty("java.awt.headless");
	String JAVA_JAVA_VERSION                  = System.getProperty("java.version");                  //   "12"                                      "1.8.0_201"                             "1.5.0_22"                                           Java Runtime Environment version, which may be interpreted as a Runtime.Version
	String JAVA_JAVA_VERSION_DATE             = System.getProperty("java.version.date");             //   "2019-03-19"                              null                                    null                                                 Java Runtime Environment version date, in ISO-8601 YYYY-MM-DD format, which may be interpreted as a LocalDate
	String JAVA_JAVA_VENDOR                   = System.getProperty("java.vendor");                   //   "Oracle Corporation"                      "Oracle Corporation"                    "Sun Microsystems Inc."                              Java Runtime Environment vendor
	String JAVA_JAVA_VENDOR_VERSION           = System.getProperty("java.vendor.version");           //   null                                      null                                    null                                                 Java vendor version
	String JAVA_JAVA_VENDOR_URL               = System.getProperty("java.vendor.url");               //   "https://java.oracle.com/"                "http://java.oracle.com/"               "http://java.sun.com/"                               Java vendor URL
	String JAVA_JAVA_VENDOR_URL_BUG           = System.getProperty("java.vendor.url.bug");           //   "https://bugreport.java.com/bugreport/"   "http://bugreport.sun.com/bugreport/"   "http://java.sun.com/cgi-bin/bugreport.cgi"          Undocumented
	String JAVA_JAVA_SPECIFICATION_NAME       = System.getProperty("java.specification.name");       //   "Java Platform API Specification"         "Java Platform API Specification"       "Java Platform API Specification"                    Java Runtime Environment specification name
	String JAVA_JAVA_SPECIFICATION_VENDOR     = System.getProperty("java.specification.vendor");     //   "Oracle Corporation"                      "Oracle Corporation"                    "Sun Microsystems Inc."                              Java Runtime Environment specification vendor
	String JAVA_JAVA_SPECIFICATION_VERSION    = System.getProperty("java.specification.version");    //   "12"                                      "1.8"                                   "1.5"                                                Java Runtime Environment specification version, whose value is the feature element of the runtime version
	String JAVA_JAVA_VM_NAME                  = System.getProperty("java.vm.name");                  //   "OpenJDK 64-Bit Server VM"                "Java HotSpot(TM) 64-Bit Server VM"     "Java HotSpot(TM) 64-Bit Server VM"                  Java Virtual Machine implementation name
	String JAVA_JAVA_VM_VENDOR                = System.getProperty("java.vm.vendor");                //   "Oracle Corporation"                      "Oracle Corporation"                    "Sun Microsystems Inc."                              Java Virtual Machine implementation vendor
	String JAVA_JAVA_VM_VERSION               = System.getProperty("java.vm.version");               //   "12+33"                                   "25.201-b09"                            "1.5.0_22-b03"                                       Java Virtual Machine implementation version which may be interpreted as a Runtime.Version
	String JAVA_JAVA_VM_INFO                  = System.getProperty("java.vm.info");                  //   "mixed mode, sharing"                     "mixed mode"                            "mixed mode"                                         Undocumented
	String JAVA_JAVA_VM_SPECIFICATION_NAME    = System.getProperty("java.vm.specification.name");    //   "Java Virtual Machine Specification"      "Java Virtual Machine Specification"    "Java Virtual Machine Specification"                 Java Virtual Machine specification name
	String JAVA_JAVA_VM_SPECIFICATION_VENDOR  = System.getProperty("java.vm.specification.vendor "); //   "Oracle Corporation"                      "Oracle Corporation"                    "Sun Microsystems Inc."                              Java Virtual Machine specification vendor
	String JAVA_JAVA_VM_SPECIFICATION_VERSION = System.getProperty("java.vm.specification.version"); //   "12"                                      "1.8"                                   "1.0"                                                Java Virtual Machine specification version, whose value is the feature element of the runtime version
	String JAVA_JAVA_RUNTIME_NAME             = System.getProperty("java.runtime.name");             //   "OpenJDK Runtime Environment"             "Java(TM) SE Runtime Environment"       "Java(TM) 2 Runtime Environment, Standard Edition"   Undocumented
	String JAVA_JAVA_RUNTIME_VERSION          = System.getProperty("java.runtime.version");          //   "12+33"                                   "1.8.0_201-b09"                         "1.5.0_22-b03"                                       Undocumented
	String JAVA_JAVA_CLASS_VERSION            = System.getProperty("java.class.version");            //   "56.0"                                    "52.0"                                  "49.0"                                               Java class format version number
}
