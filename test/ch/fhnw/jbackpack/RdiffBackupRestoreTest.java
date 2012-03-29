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
package ch.fhnw.jbackpack;

import ch.fhnw.jbackpack.chooser.Increment;
import ch.fhnw.jbackpack.chooser.RdiffFile;
import ch.fhnw.jbackpack.chooser.RdiffFileDatabase;
import ch.fhnw.util.FileTools;
import ch.fhnw.util.ProcessExecutor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.junit.Test;
import screenshots.Screenshots;

/**
 *
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class RdiffBackupRestoreTest extends TestCase {

    private static final Logger LOGGER = Logger.getLogger(
            RdiffBackupRestoreTest.class.getName());

    /**
     * test counting of restore files
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testCounting() throws Exception {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);
        // show some logs on console
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        Logger processExecutorLogger =
                Logger.getLogger(ProcessExecutor.class.getName());
        processExecutorLogger.setLevel(Level.ALL);
        processExecutorLogger.addHandler(consoleHandler);

        // work in a temporary directory
        File tempDirectory = null;
        try {
            tempDirectory = File.createTempFile(
                    RdiffBackupRestoreTest.class.getSimpleName()
                    + "testCounting", null);
            tempDirectory.delete();
            if (tempDirectory.mkdirs()) {
                LOGGER.log(Level.INFO,
                        "using temporary directory {0}", tempDirectory);
            } else {
                throw new IOException("could not create " + tempDirectory);
            }

            // create source directory
            File sourceDirectory = new File(tempDirectory, "source");
            if (!sourceDirectory.mkdirs()) {
                throw new IOException("could not create " + sourceDirectory);
            }

            // create a number of files
            int numberOfDirectories = 10;
            int numberOfFiles = 10;
            for (int i = 1; i <= numberOfDirectories; i++) {
                addDirectory(new File(sourceDirectory, "directory" + i),
                        numberOfFiles);
            }

            // backup
            String backupDirectoryPath =
                    tempDirectory.getPath() + File.separatorChar + "back up";
            ProcessExecutor processExecutor = new ProcessExecutor();
            processExecutor.executeProcess("rdiff-backup",
                    sourceDirectory.getPath(), backupDirectoryPath);

            // remove a directory
            numberOfDirectories--;
            deleteDirectory(new File(sourceDirectory, "directory" + 1));
            // delete returns but the file is still there, let's sleep for a
            // while to make rdiff-backup happy, *sigh* ...
            System.gc();
            Thread.sleep(2000);

            // backup again
            processExecutor.executeProcess("rdiff-backup",
                    sourceDirectory.getPath(), backupDirectoryPath);

            // add two direcories
            numberOfDirectories++;
            addDirectory(
                    new File(sourceDirectory, "directoryA"), numberOfFiles);
            numberOfDirectories++;
            addDirectory(
                    new File(sourceDirectory, "directoryB"), numberOfFiles);
            // adding new files seems to need some timout too...
            System.gc();
            Thread.sleep(2000);

            // backup again
            processExecutor.executeProcess("rdiff-backup",
                    sourceDirectory.getPath(), backupDirectoryPath);
            // adding new files seems to need some timout too...
            System.gc();
            Thread.sleep(2000);

            // get increment data
            File backupDirectory = new File(backupDirectoryPath);
            RdiffFileDatabase rdiffFileDatabase =
                    RdiffFileDatabase.getInstance(backupDirectory);
            rdiffFileDatabase.sync();
            List<Increment> increments = rdiffFileDatabase.getIncrements();

            // test mirror
            Increment increment = increments.get(0);
            File root = increment.getRdiffRoot();
            int expectedNumber = 1 + numberOfDirectories
                    + (numberOfDirectories * numberOfFiles);
            assertEquals(expectedNumber, countFiles(root));

            // test first increment
            // (has two directories less than mirror)
            increment = increments.get(1);
            numberOfDirectories -= 2;
            root = increment.getRdiffRoot();
            expectedNumber = 1 + numberOfDirectories
                    + (numberOfDirectories * numberOfFiles);
            assertEquals(expectedNumber, countFiles(root));

            // test second increment
            // (has one directory more than younger increment)
            increment = increments.get(2);
            numberOfDirectories++;
            root = increment.getRdiffRoot();
            expectedNumber = 1 + numberOfDirectories
                    + (numberOfDirectories * numberOfFiles);
            assertEquals(expectedNumber, countFiles(root));
        } finally {
            FileTools.recursiveDelete(tempDirectory, true);
        }
    }

    /**
     * test restore operation
     *
     * @throws IOException if an I/O exception occurs
     * @throws SQLException if an SQL exception occurs
     */
    @Test
    public void testRestore() throws IOException, SQLException {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);
        // show some logs on console
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        Logger processExecutorLogger =
                Logger.getLogger(ProcessExecutor.class.getName());
        processExecutorLogger.setLevel(Level.ALL);
        processExecutorLogger.addHandler(consoleHandler);

        // work in a temporary directory
        File tempDirectory = File.createTempFile(
                RdiffBackupRestoreTest.class.getSimpleName(), null);
        tempDirectory.delete();
        if (tempDirectory.mkdirs()) {
            LOGGER.log(Level.INFO,
                    "using temporary directory {0}", tempDirectory);
        } else {
            throw new IOException("could not create " + tempDirectory);
        }

        // create source directory
        File sourceDirectory = new File(tempDirectory, "source");
        if (!sourceDirectory.mkdirs()) {
            throw new IOException("could not create " + sourceDirectory);
        }

        // create a file
        File sourceFile = new File(sourceDirectory, "file");
        FileOutputStream fileOutputStream = new FileOutputStream(sourceFile);
        Random random = new Random();
        int fileSize = 1 + random.nextInt(1000000);
        byte[] data = new byte[fileSize];
        fileOutputStream.write(data);
        fileOutputStream.close();

        // create a directory
        File sourceDir = new File(sourceDirectory, "dir");
        if (!sourceDir.mkdir()) {
            fail("could not create " + sourceDir);
        }

        RdiffBackupRestore rdiffRestore = new RdiffBackupRestore();

        // backup
        File backupDirectory = new File(tempDirectory, "back up");
        rdiffRestore.backupViaFileSystem(sourceDirectory, backupDirectory,
                null, null, null, true, null, null,
                false, false, false, false, false);

        // delete the source files
        deleteFile(sourceFile);
        deleteFile(sourceDir);

        // get increment data
        RdiffFileDatabase rdiffFileDatabase =
                RdiffFileDatabase.getInstance(backupDirectory);
        rdiffFileDatabase.sync();
        List<Increment> increments = rdiffFileDatabase.getIncrements();

        Increment increment = increments.get(0);

        // restore
        if (rdiffRestore.restore(increment.getRdiffTimestamp(),
                new RdiffFile[]{increment.getRdiffRoot()},
                backupDirectory, sourceDirectory, null, false)) {

            // check that file is there
            assertTrue(sourceFile.exists());
            assertEquals(fileSize, sourceFile.length());

        } else {
            fail("restore failed");
        }

        FileTools.recursiveDelete(tempDirectory, true);
    }

    private void deleteFile(File file) {
        file.delete();
        try {
            // delete returns but the file is still there, let's sleep for a
            // while to make rdiff-backup happy, *sigh* ...
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
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

    private static void deleteDirectory(File directory) {
        // empty directory before deletion
        // (otherwise deletion just fails)
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        // directory should be emtpy now
        directory.delete();
    }

    private void addDirectory(File directory, int numberOfFiles)
            throws IOException {
        assertTrue(directory.mkdirs());
        for (int j = 1; j <= numberOfFiles; j++) {
            File sourceFile = new File(directory, "file" + j);
            FileOutputStream fileOutputStream =
                    new FileOutputStream(sourceFile);
            fileOutputStream.write(new byte[1]);
            fileOutputStream.close();
        }
    }
}
