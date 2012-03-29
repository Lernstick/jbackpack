/**
 * Copyright (C) 2010 imedias
 *
 * This file is part of JBackpack.
 *
 * JBackpack is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * JBackpack is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.fhnw.jbackpack.chooser;

import ch.fhnw.util.CurrentOperatingSystem;
import ch.fhnw.util.FileTools;
import ch.fhnw.util.OperatingSystem;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import screenshots.Screenshots;

/**
 *
 * @author mw
 */
public class RdiffFileTest extends TestCase {

    /**
     * tests that hidden files are reported as such
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testIsHidden() throws Exception {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        switch (CurrentOperatingSystem.OS) {
            case Linux:
            case Mac_OS_X:
                // test hidden files
                RdiffFile hiddenFile = new RdiffFile(
                        null, null, null, ".file", 0, 0, false);
                assertTrue(hiddenFile.isHidden());
                RdiffFile hiddenDirectory = new RdiffFile(
                        null, null, null, ".dir", 0, 0, true);
                assertTrue(hiddenDirectory.isHidden());
                break;

            default:
                fail("testing for hidden files not implemented for "
                        + CurrentOperatingSystem.OS);
        }
    }

    /**
     * tests that counting files works as expected
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testFileCounting() throws Exception {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        File tmpDir = null;
        try {
            TestEnvironment testEnvironment = new TestEnvironment();
            tmpDir = testEnvironment.getTempDirectory();
            List<Increment> increments = testEnvironment.getIncrements();

            // the mirror has the following files:
            //  .
            //  file.tgz
            //  symlink
            Increment mirror = increments.get(0);
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 2 : 3, countFiles(mirror.getRdiffRoot()));

            // the increment has the following files:
            //  .
            //  file
            //  file.tgz
            //  subdir/
            //  subdir/subfile
            //  symlink
            Increment increment = increments.get(1);
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 5 : 6, countFiles(increment.getRdiffRoot()));

        } finally {
            if (tmpDir != null) {
                FileTools.recursiveDelete(tmpDir, true);
            }
        }
    }

    /**
     * tests RdiffFile.listFiles(FileFilter filter)
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testListFilesFileFilter() throws Exception {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        File tmpDir = null;
        try {
            TestEnvironment testEnvironment = new TestEnvironment();
            tmpDir = testEnvironment.getTempDirectory();
            List<Increment> increments = testEnvironment.getIncrements();

            FileFilter filter = new FileFilter() {

                public boolean accept(File pathname) {
                    return true;
                }
            };

            // check mirror
            Increment mirror = increments.get(0);
            File[] files = mirror.getRdiffRoot().listFiles(filter);
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 1 : 2, files.length);

            // check increment
            Increment increment = increments.get(1);
            files = increment.getRdiffRoot().listFiles(filter);
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 3 : 4, files.length);
            assertTrue(hasFile(files, "subdir"));
            assertTrue(hasFile(files, "file"));

        } finally {
            if (tmpDir != null) {
                FileTools.recursiveDelete(tmpDir, true);
            }
        }
    }

    /**
     * tests RdiffFile.listFiles(FilenameFilter filter)
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testListFilesFilenameFilter() throws Exception {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        File tmpDir = null;
        try {
            TestEnvironment testEnvironment = new TestEnvironment();
            tmpDir = testEnvironment.getTempDirectory();
            List<Increment> increments = testEnvironment.getIncrements();

            FilenameFilter filter = new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return true;
                }
            };

            // check mirror
            Increment mirror = increments.get(0);
            File[] files = mirror.getRdiffRoot().listFiles(filter);
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 1 : 2, files.length);

            // check increment
            Increment increment = increments.get(1);
            files = increment.getRdiffRoot().listFiles(filter);
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 3 : 4, files.length);
            assertTrue(hasFile(files, "subdir"));
            assertTrue(hasFile(files, "file"));

        } finally {
            if (tmpDir != null) {
                FileTools.recursiveDelete(tmpDir, true);
            }
        }
    }

    /**
     * tests RdiffFile.listFiles()
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testListFiles() throws Exception {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        File tmpDir = null;
        try {
            TestEnvironment testEnvironment = new TestEnvironment();
            tmpDir = testEnvironment.getTempDirectory();
            List<Increment> increments = testEnvironment.getIncrements();

            // check mirror
            Increment mirror = increments.get(0);
            File[] files = mirror.getRdiffRoot().listFiles();
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 1 : 2, files.length);

            // check increment
            Increment increment = increments.get(1);
            files = increment.getRdiffRoot().listFiles();
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 3 : 4, files.length);
            assertTrue(hasFile(files, "subdir"));
            assertTrue(hasFile(files, "file"));

        } finally {
            if (tmpDir != null) {
                FileTools.recursiveDelete(tmpDir, true);
            }
        }
    }

    /**
     * tests getting the list of children with a FilenameFilter
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testListFilenameFilter() throws Exception {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        File tmpDir = null;
        try {
            TestEnvironment testEnvironment = new TestEnvironment();
            tmpDir = testEnvironment.getTempDirectory();
            List<Increment> increments = testEnvironment.getIncrements();

            FilenameFilter filter = new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return true;
                }
            };

            // check mirror
            Increment mirror = increments.get(0);
            String[] fileNames = mirror.getRdiffRoot().list(filter);
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 1 : 2, fileNames.length);

            // check increment
            Increment increment = increments.get(1);
            fileNames = increment.getRdiffRoot().list(filter);
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 3 : 4, fileNames.length);
            assertTrue(hasFile(fileNames, "subdir"));
            assertTrue(hasFile(fileNames, "file"));

        } finally {
            if (tmpDir != null) {
                FileTools.recursiveDelete(tmpDir, true);
            }
        }
    }

    /**
     * tests getting the list of children
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testList() throws Exception {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        File tmpDir = null;
        try {
            TestEnvironment testEnvironment = new TestEnvironment();
            tmpDir = testEnvironment.getTempDirectory();
            List<Increment> increments = testEnvironment.getIncrements();

            // check mirror
            Increment mirror = increments.get(0);
            String[] fileNames = mirror.getRdiffRoot().list();
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 1 : 2, fileNames.length);

            // check increment
            Increment increment = increments.get(1);
            fileNames = increment.getRdiffRoot().list();
            assertEquals(CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 3 : 4, fileNames.length);
            assertTrue(hasFile(fileNames, "subdir"));
            assertTrue(hasFile(fileNames, "file"));

        } finally {
            if (tmpDir != null) {
                FileTools.recursiveDelete(tmpDir, true);
            }
        }
    }

    /**
     * tests getting the length of the file
     *
     * the following case once failed:
     *
     * 1) create a file
     * 2) backup
     * 3) remove the file
     * 4) backup again
     * 5) get the length of the increment
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testLength() throws Exception {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        File tmpDir = null;
        try {
            TestEnvironment testEnvironment = new TestEnvironment();
            tmpDir = testEnvironment.getTempDirectory();
            List<Increment> increments = testEnvironment.getIncrements();

            // check mirror
            Increment mirror = increments.get(0);
            RdiffFile mirrorFile = mirror.getRdiffRoot().getChild("file");
            assertNull(mirrorFile);

            // check increment
            Increment increment = increments.get(1);
            RdiffFile incrementFile = increment.getRdiffRoot().getChild("file");
            assertNotNull(incrementFile);
            assertEquals(testEnvironment.getFileSize(), incrementFile.length());

        } finally {
            if (tmpDir != null) {
                FileTools.recursiveDelete(tmpDir, true);
            }
        }
    }

    /**
     * tests getting the parent file
     */
    @Test
    public void testGetParentFile() {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        RdiffFile root = new RdiffFile(null, null, null, ".", 0, 0, true);
        assertNull(root.getParentFile());
        assertEquals(null, root.getParent());
    }

    /**
     * tests creation of canonical files
     *
     * @throws IOException if an I/O exception occurs
     */
    @Test
    public void testGetCanonicalFile() throws IOException {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        RdiffFile root = new RdiffFile(null, null, null, ".", 0, 0, true);
        RdiffFile dotDotDirectory = root.getDotDotDirectory();
        assertNotNull(dotDotDirectory.getCanonicalFile());
    }

    /**
     * tests canonicalization of paths
     */
    @Test
    public void testCanonicalize() {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        String separator = File.separator;
        // test removal of trailing dots
        // e.g. in Unix "/home/."
        assertEquals(separator + "home", RdiffFile.canonicalize(
                separator + "home" + separator + '.'));

        // test removal of dot directories within path
        // e.g. in Unix "/./home"
        assertEquals(separator + "home", RdiffFile.canonicalize(
                separator + "." + separator + "home"));

        // test removal of intermediate double dots (and the parent)
        // e.g. in Unix "/home/../bin"
        assertEquals(separator + "bin", RdiffFile.canonicalize(
                separator + "home" + separator + ".." + separator + "bin"));

        // test removal of trailing double dots (and the parent)
        // e.g. in Unix "/home/.."
        assertEquals(File.separator, RdiffFile.canonicalize(
                separator + "home" + separator + ".."));

        assertEquals(File.separator, RdiffFile.canonicalize(
                separator + separator + ".." + separator + ".."));
    }

    private boolean hasFile(File[] files, String name) {
        for (File file : files) {
            if (file.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFile(String[] fileNames, String name) {
        for (String fileName : fileNames) {
            if (fileName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private long countFiles(File file) {
        long counter = 1;
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                counter += countFiles(subFile);
            }
        }
        return counter;
    }
}
