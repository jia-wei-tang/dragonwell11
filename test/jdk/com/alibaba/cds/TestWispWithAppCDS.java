/*
 * Copyright (c) 2023, Alibaba Group Holding Limited. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @summary Test EagerAppCDS Flow with wisp
 * @library /lib/testlibrary /test/lib
 * @modules java.base/jdk.internal.misc
 *          java.management
 *          jdk.jartool/sun.tools.jar
 * @modules jdk.compiler
 * @modules java.base/com.alibaba.util:+open
 * @build TestSimpleWispUsage
 * @build Classes4CDS
 * @requires os.arch=="amd64" | os.arch=="aarch64"
 * @run driver ClassFileInstaller  -jar test.jar TestSimpleWispUsage
 * @run main/othervm TestWispWithAppCDS
 */

import jdk.test.lib.cds.CDSTestUtils;
import jdk.test.lib.process.OutputAnalyzer;
import jdk.test.lib.process.ProcessTools;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class TestWispWithAppCDS {
    private static final String TESTJAR = "./test.jar";
    private static final String TESTNAME = "TestSimpleWispUsage";
    private static final String TESTCLASS = TESTNAME + ".class";

    private static final String CLASSLIST_FILE = "./TestWispWithAppCDS.classlist";
    private static final String CLASSLIST_FILE_2 = "./TestWispWithAppCDS.classlist2";
    private static final String ARCHIVE_FILE = "./TestWispWithAppCDS.jsa";
    private static final String BOOTCLASS = "java.lang.Class";
    private static final String TEST_CLASS = System.getProperty("test.classes");

    public static void main(String[] args) throws Exception {

        // dump loaded classes into a classlist file
        dumpLoadedClasses(new String[] { BOOTCLASS, TESTNAME });

        convertClassList();

        // create an archive using the classlist
        dumpArchive();

        // start the java process with shared archive file
        startWithJsa();
    }

    public static List<String> toClassNames(String filename) throws IOException {
        ArrayList<String> classes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) {
            for (; ; ) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                classes.add(line.replaceAll("/", "."));
            }
        }
        return classes;
    }

    static void dumpLoadedClasses(String[] expectedClasses) throws Exception {
        ProcessBuilder pb = ProcessTools.createTestJvm(
            "-Dtest.classes=" + TEST_CLASS,
            "-XX:DumpLoadedClassList=" + CLASSLIST_FILE,
            // trigger JVMCI runtime init so that JVMCI classes will be
            // included in the classlist
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+EagerAppCDS",
            "-XX:+UseWisp2",
            "-XX:ActiveProcessorCount=4",
            "-cp",
            TESTJAR,
            TESTNAME);

        OutputAnalyzer output = CDSTestUtils.executeAndLog(pb, "dump-loaded-classes")
            .shouldHaveExitValue(0);
    }

    static void convertClassList() throws Exception {
        ProcessBuilder pb = Classes4CDS.invokeClasses4CDS(CLASSLIST_FILE, CLASSLIST_FILE_2);

        OutputAnalyzer output = CDSTestUtils.executeAndLog(pb, "convert-class-list")
            .shouldHaveExitValue(0);

    }
    static void dumpArchive() throws Exception {
        ProcessBuilder pb = ProcessTools.createTestJvm(
            "-cp",
            TESTJAR,
            "-XX:SharedClassListFile=" + CLASSLIST_FILE_2,
            "-XX:SharedArchiveFile=" + ARCHIVE_FILE,
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+EagerAppCDS",
            "-XX:+UseWisp2",
            "-XX:ActiveProcessorCount=4",
            "-Xlog:class+eagerappcds=trace",
            "-Xshare:dump",
            "-XX:MetaspaceSize=128M",
            "-XX:MaxMetaspaceSize=128M");

        OutputAnalyzer output = CDSTestUtils.executeAndLog(pb, "dump-archive");
        int exitValue = output.getExitValue();
        if (exitValue == 1) {
            output.shouldContain("Failed allocating metaspace object type");
        } else if (exitValue == 0) {
            output.shouldContain("Loading classes to share");
        } else {
            throw new RuntimeException("Unexpected exit value " + exitValue);
        }
    }

    static void startWithJsa() throws Exception {
        ProcessBuilder pb = ProcessTools.createTestJvm(
            "-Dtest.classes=" + TEST_CLASS,
            "-Xshare:on",
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+EagerAppCDS",
            "-XX:SharedArchiveFile=" + ARCHIVE_FILE,
            "-Xlog:class+eagerappcds=trace",
            "-XX:+UseWisp2",
            "-XX:ActiveProcessorCount=4",
            "-cp",
            TESTJAR,
            TESTNAME);

        OutputAnalyzer output = CDSTestUtils.executeAndLog(pb, "start-with-shared-archive")
            .shouldHaveExitValue(0);
        output.shouldNotContain("[CDS load class Failed");
    }

}
