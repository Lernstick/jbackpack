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

/**
 * collects some properties of a test environment
 *
 * @author Ronny Standtke <Ronny.Standtke@gmx.net>
 */
public class TestEnvironment {

    private final static Logger LOGGER =
            Logger.getLogger(TestEnvironment.class.getName());
    private final File tempDirectory;
    private final File backupDirectory;
    private final List<Increment> increments;
    private final int fileSize;

    /**
     * creates a new TestEnvironment
     *
     * @throws IOException if an I/O exception occurs
     * @throws SQLException if syncing the file database fails
     */
    public TestEnvironment() throws IOException, SQLException {
        // show some logs on console
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        Logger processExecutorLogger =
                Logger.getLogger(ProcessExecutor.class.getName());
        processExecutorLogger.setLevel(Level.ALL);
        processExecutorLogger.addHandler(consoleHandler);

        // work in a temporary directory
        tempDirectory = File.createTempFile(
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
        File sourceFile = new File(sourceDir, "file");
        fileSize = createFile(sourceFile);

        // create a packed file in the root directory
        File packedFile = new File(sourceDir, "file.tgz");
        createFile(packedFile);

        // create a subdirectory
        File subDir = new File(sourceDir, "subdir");
        if (!subDir.mkdirs()) {
            throw new IOException("could not create " + subDir);
        }

        // create another file in the subdirectory
        createFile(new File(subDir, "subfile"));

        ProcessExecutor processExecutor = new ProcessExecutor();

        if (CurrentOperatingSystem.OS != OperatingSystem.Windows) {
            // create a symlink in root to the subdirectory
            File symlink = new File(sourceDir, "symlink");
            processExecutor.executeProcess("ln", "-s",
                    subDir.getAbsolutePath(), symlink.getAbsolutePath());
        }

        // backup
        backupDirectory = new File(tempDirectory, "back up");
        String backupPath = backupDirectory.getPath();
        processExecutor.executeProcess(
                "rdiff-backup", sourceDir.getPath(), backupPath);

        // delete a file and a directory in the root directory
        sourceFile.delete();
        FileTools.recursiveDelete(subDir, true);
        try {
            // delete returns but the file is still there, let's sleep for a
            // while to make rdiff-backup happy, *sigh* ...
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        // backup again
        processExecutor.executeProcess(
                "rdiff-backup", sourceDir.getPath(), backupPath);

        // create list of increments
        RdiffFileDatabase rdiffFileDatabase =
                RdiffFileDatabase.getInstance(backupDirectory);
        rdiffFileDatabase.sync();
        increments = rdiffFileDatabase.getIncrements();
        if (increments.size() != 2) {
            throw new IOException("wrong number of increments");
        }
    }

    /**
     * returns the tempDirectory
     *
     * @return the tempDirectory
     */
    public File getTempDirectory() {
        return tempDirectory;
    }

    /**
     * returns the backupDirectory
     *
     * @return the backupDirectory
     */
    public File getBackupDirectory() {
        return backupDirectory;
    }

    /**
     * returns the increments
     *
     * @return the increments
     */
    public List<Increment> getIncrements() {
        return increments;
    }

    /**
     * returns the fileSize
     *
     * @return the fileSize
     */
    public int getFileSize() {
        return fileSize;
    }

    private int createFile(File file) throws IOException {
        FileOutputStream fileOutputStream =
                new FileOutputStream(file);
        Random random = new Random();
        int size = 1 + random.nextInt(1000000);
        byte[] data = new byte[size];
        fileOutputStream.write(data);
        fileOutputStream.close();
        return size;
    }
}
