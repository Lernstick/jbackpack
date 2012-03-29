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
import ch.fhnw.jbackpack.chooser.RdiffFileDatabase;
import ch.fhnw.util.CurrentOperatingSystem;
import ch.fhnw.util.FileTools;
import ch.fhnw.util.OperatingSystem;
import java.awt.Frame;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import junit.framework.TestCase;
import org.junit.Test;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.NameComponentChooser;
import screenshots.Screenshots;

/**
 * tests decrypting a directory
 *
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class BrowseLongFilenamesTest extends TestCase {

    private static final Logger LOGGER =
            Logger.getLogger(BrowseLongFilenamesTest.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "ch/fhnw/jbackpack/Strings");
    private JButtonOperator encryptionButtonOperator;

    /**
     * tests decrypting a directory
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testBrowseLongFilenames() throws Exception {

        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        if (CurrentOperatingSystem.OS == OperatingSystem.Windows) {
            fail("this test does not work on " + CurrentOperatingSystem.OS);
        }
        File tempDir = null;
        try {
            tempDir = FileTools.createTempDirectory(
                    BrowseLongFilenamesTest.class.getName(), null);
            testBrowseLongFilenames(tempDir);
        } finally {
            if (tempDir != null) {
                FileTools.recursiveDelete(tempDir, true);
            }
        }
    }

    private void testBrowseLongFilenames(File tempDir) throws Exception {

        LOGGER.log(Level.INFO, "using temporary directory {0}", tempDir);

        // create source and destination directories
        File sourceDir = new File(tempDir, "source");
        if (!sourceDir.mkdir()) {
            fail("could not create source directoy " + sourceDir);
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

        ClassReference classReference = new ClassReference(
                "ch.fhnw.jbackpack.JBackpack");
        classReference.startApplication();
        JFrameOperator frameOperator = new JFrameOperator();
        JTabbedPaneOperator tabbedPaneOperator = new JTabbedPaneOperator(
                frameOperator, new NameComponentChooser("mainTabbedPane"));
        JComponentOperator directoriesPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("directoriesPanel"));
        encryptionButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("encryptionButton"));
        JButtonOperator lockButtonOperator = new JButtonOperator(frameOperator,
                new NameComponentChooser("lockButton"));

        tabbedPaneOperator.setSelectedComponent(
                directoriesPanelOperator.getSource());

        // encrypt destination directory
        String password = "123";
        encryptionButtonOperator.pushNoBlock();
        JDialogOperator dialogOperator = new JDialogOperator(
                new NameComponentChooser("NewEncfsDialog"));
        JPasswordFieldOperator passwordField1Operator =
                new JPasswordFieldOperator(dialogOperator,
                new NameComponentChooser("passwordField1"));
        JPasswordFieldOperator passwordField2Operator =
                new JPasswordFieldOperator(dialogOperator,
                new NameComponentChooser("passwordField2"));
        passwordField1Operator.setText(password);
        passwordField2Operator.setText(password);
        JButtonOperator okButtonOperator = new JButtonOperator(dialogOperator,
                new NameComponentChooser("okButton"));
        okButtonOperator.push();
        dialogOperator = new JDialogOperator(
                BUNDLE.getString("Information"));
        dialogOperator.setVisible(false);
        dialogOperator.dispose();

        // create source file with very long name
        FilenameCheckSwingWorker filenameCheckSwingWorker =
                new FilenameCheckSwingWorker((Frame) frameOperator.getSource(),
                sourceDir);
        filenameCheckSwingWorker.execute();
        int maxFileNameLength = filenameCheckSwingWorker.get();
        LOGGER.log(Level.INFO, "maxFileNameLength = {0}", maxFileNameLength);
        StringBuilder stringBuilder = new StringBuilder(maxFileNameLength);
        for (int i = 0; i < maxFileNameLength; i++) {
            stringBuilder.append(i % 10);
        }
        String tooLongFileName = stringBuilder.toString();
        File testFile = new File(sourceDir, tooLongFileName);
        if (!testFile.createNewFile()) {
            fail("could not create " + testFile);
        }

        // backup
        JComponentOperator backupPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("backupCardPanel"));
        tabbedPaneOperator.setSelectedComponent(
                backupPanelOperator.getSource());
        JButtonOperator backupButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("backupButton"));
        backupButtonOperator.push();

        JButtonOperator continueButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("continueButton"));
        continueButtonOperator.waitComponentShowing(true);
        continueButtonOperator.waitComponentEnabled();
        continueButtonOperator.push();

        // get increment file view
        JComponentOperator backupMainPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("backupMainPanel"));
        BackupMainPanel backupMainPanel =
                (BackupMainPanel) backupMainPanelOperator.getSource();
        File backupDirectory = new File(backupMainPanel.getEncfsMountPoint());
        RdiffFileDatabase rdiffFileDatabase =
                RdiffFileDatabase.getInstance(backupDirectory);
        rdiffFileDatabase.sync();
        List<Increment> increments = rdiffFileDatabase.getIncrements();
        Increment mirror = increments.get(0);
        int fileCount = mirror.getRdiffRoot().listFiles().length;

        // cleanup
        rdiffFileDatabase.close();
        tabbedPaneOperator.setSelectedComponent(
                directoriesPanelOperator.getSource());
        lockButtonOperator.waitComponentShowing(true);
        lockButtonOperator.push();
        Thread.sleep(1000);

        // check
        assertEquals(1, fileCount);
    }
}
