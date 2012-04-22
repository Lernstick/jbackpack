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

import ch.fhnw.jbackpack.chooser.RdiffChooserPanel;
import ch.fhnw.util.FileTools;
import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.ListModel;
import junit.framework.TestCase;
import org.junit.Test;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.NameComponentChooser;
import screenshots.Screenshots;

/**
 * tests restoring files
 *
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class RestoreTest extends TestCase {

    private static final Logger LOGGER =
            Logger.getLogger(RestoreTest.class.getName());
    private JFileChooser fileChooser;
    private JButtonOperator restoreButtonOperator;
    private JButtonOperator restoredOKButtonOperator;
    private File testFile1;
    private File testFile2;

    /**
     * tests decrypting a directory
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testRestore() throws Exception {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        File tempDir = FileTools.createTempDirectory(
                RestoreTest.class.getName(), null);
        if (testRestore(tempDir)) {
            FileTools.recursiveDelete(tempDir, true);
        }
    }

    private boolean testRestore(File tempDir) throws Exception {

        LOGGER.log(Level.INFO, "using temporary directory {0}", tempDir);

        // create source and destination directories
        File sourceDir = new File(tempDir, "source");
        if (!sourceDir.mkdir()) {
            fail("could not create source directoy " + sourceDir);
        }
        File testDir = new File(sourceDir, "dir");
        if (!testDir.mkdir()) {
            fail("could not create " + testDir);
        }
        File testSubDir = new File(testDir, "sub");
        if (!testSubDir.mkdir()) {
            fail("could not create " + testSubDir);
        }
        testFile1 = new File(testSubDir, "file1");
        if (!testFile1.createNewFile()) {
            fail("could not create " + testFile1);
        }
        testFile2 = new File(testSubDir, "file2");
        if (!testFile2.createNewFile()) {
            fail("could not create " + testFile2);
        }
        File destinationDir = new File(tempDir, "destination");
        if (!destinationDir.mkdir()) {
            fail("could not create destination directoy " + destinationDir);
        }

        // customize some preferences before starting application
        Preferences preferences =
                Preferences.userNodeForPackage(BackupMainPanel.class);
        preferences.put(BackupMainPanel.SOURCE, sourceDir.getPath());
        preferences.put(BackupMainPanel.DESTINATION, "local");
        preferences.put(BackupMainPanel.LOCAL_DESTINATION_DIRECTORY,
                destinationDir.getPath());
        preferences.putBoolean(BackupMainPanel.EXCLUDES, false);
        preferences.putBoolean(BackupMainPanel.PLAIN_BACKUP_WARNING, false);

        ClassReference classReference = new ClassReference(
                "ch.fhnw.jbackpack.JBackpack");
        classReference.startApplication();
        JFrameOperator frameOperator = new JFrameOperator();

        // backup
        JButtonOperator backupButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("backupButton"));
        backupButtonOperator.push();
        JButtonOperator continueButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("continueButton"));
        continueButtonOperator.waitComponentShowing(true);
        continueButtonOperator.waitComponentEnabled();
        continueButtonOperator.push();

        // remove testDir
        FileTools.recursiveDelete(testDir, true);

        // restore
        JTabbedPaneOperator tabbedPaneOperator = new JTabbedPaneOperator(
                frameOperator, new NameComponentChooser("mainTabbedPane"));
        JComponentOperator restoreCardPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("restoreCardPanel"));
        JComponentOperator rdiffChooserPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("rdiffChooserPanel"));
        RdiffChooserPanel rdiffChooserPanel =
                (RdiffChooserPanel) rdiffChooserPanelOperator.getSource();
        Field fileChooserField =
                RdiffChooserPanel.class.getDeclaredField("fileChooser");
        fileChooserField.setAccessible(true);
        fileChooser = (JFileChooser) fileChooserField.get(rdiffChooserPanel);
        restoreButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("restoreButton"));
        restoredOKButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("restoredOKButton"));
        tabbedPaneOperator.setSelectedComponent(
                restoreCardPanelOperator.getSource());

        // wait until increments are filled
        JListOperator backupsListOperator = new JListOperator(frameOperator,
                new NameComponentChooser("backupsList"));
        ListModel model = backupsListOperator.getModel();
        int waitCount = 0;
        while (model.getSize() == 0) {
            LOGGER.info("model still empty, waiting for a short while...");
            if (waitCount++ >= 30) {
                fail("increments were never filled!");
            }
            Thread.sleep(300);
        }
        Thread.sleep(300);

        File root = fileChooser.getFileSystemView().getRoots()[0];
        File[] rootFiles = root.listFiles();
        final File dir = rootFiles[0];
        File[] dirFiles = dir.listFiles();
        final File subdir = dirFiles[0];
        File[] subdirFiles = subdir.listFiles();
        final File file1 = subdirFiles[0];

        // restore file when target file does not exist
        restore(file1, false);
        assertFalse("more files restored than selected", testFile2.exists());

        // restore file when target file already exists
        restore(file1, true);
        assertFalse("more files restored than selected", testFile2.exists());

        // restore directory when target directory already exists
        restore(subdir, true);
        assertTrue(testFile2.exists());

        return true;
    }

    private void restore(final File file, boolean closeWarningDialog)
            throws Exception {
        EventQueue.invokeAndWait(new Runnable() {

            public void run() {
                fileChooser.setCurrentDirectory(file.getParentFile());
            }
        });
        // without calling setCurrentDirectory() and setSelectedFiles() in two
        // separate runnables and waiting a little in between, file selection
        // fails in Java 6!
        Thread.sleep(1000);
        EventQueue.invokeAndWait(new Runnable() {

            public void run() {
                fileChooser.setSelectedFiles(new File[]{file});
                File[] selectedFiles = fileChooser.getSelectedFiles();
                for (File selectedFile : selectedFiles) {
                    System.out.println("selectedFiles: " + selectedFile);
                }
                System.out.println("selectedFile: "
                        + fileChooser.getSelectedFile());
            }
        });
        restoreButtonOperator.push();
        if (closeWarningDialog) {
            // approve restore warning
            JDialogOperator dialogOperator = new JDialogOperator();
            JButton defaultButton =
                    dialogOperator.getRootPane().getDefaultButton();
            defaultButton.doClick();
        }
        // approve statistics
        restoredOKButtonOperator.waitComponentShowing(true);
        restoredOKButtonOperator.waitComponentEnabled();
        restoredOKButtonOperator.push();

        assertTrue(testFile1.exists());
    }
}
