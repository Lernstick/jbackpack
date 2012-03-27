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

import ch.fhnw.util.ProcessExecutor;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.junit.Test;
import screenshots.Screenshots;

/**
 * tests the database syncing
 *
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class SyncTest extends TestCase {

    private static final Logger LOGGER =
            Logger.getLogger(SyncTest.class.getName());

    //private final static Logger
    /**
     * tests database syncing
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testSync() throws Exception {

        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        // work in a temporary directory
        File tempDirectory = File.createTempFile(
                TestEnvironment.class.getSimpleName(), null);
        tempDirectory.delete();
        if (tempDirectory.mkdirs()) {
            LOGGER.log(Level.INFO,
                    "using temporary directory {0}", tempDirectory);
        } else {
            throw new IOException("could not create " + tempDirectory);
        }

        // create source directory
        File sourceDir = new File(tempDirectory, "source");
        if (!sourceDir.mkdirs()) {
            throw new IOException("could not create " + sourceDir);
        }

        // create a normal file in the root directory
        File sourceFile1 = new File(sourceDir, "file1");
        if (!sourceFile1.createNewFile()) {
            fail("could not create " + sourceFile1);
        }

        // backup
        File backupDirectory = new File(tempDirectory, "backup");
        String backupPath = backupDirectory.getPath();
        ProcessExecutor processExecutor = new ProcessExecutor();
        processExecutor.executeProcess(
                "rdiff-backup", sourceDir.getPath(), backupPath);

        // sync
        RdiffFileDatabase database =
                RdiffFileDatabase.getInstance(backupDirectory);
        database.sync();
        List<Increment> increments = database.getIncrements();
        assertEquals("database sync failed", 1, increments.size());

        // add a new file
        File sourceFile2 = new File(sourceDir, "file2");
        if (!sourceFile2.createNewFile()) {
            fail("could not create " + sourceFile2);
        }

        // backup again
        processExecutor.executeProcess(
                "rdiff-backup", sourceDir.getPath(), backupPath);

        // try to sync again
        database = RdiffFileDatabase.getInstance(backupDirectory);
        database.sync();
        increments = database.getIncrements();
        assertEquals("sync after updating mirror failed", 2, increments.size());
    }
}
