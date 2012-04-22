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

import ch.fhnw.util.FileTools;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.ListModel;
import junit.framework.TestCase;
import org.junit.Test;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.NameComponentChooser;
import screenshots.Screenshots;

/**
 * tests that empty increments are ignored
 *
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class IgnoreEmptyIncrementsTest extends TestCase {

    private static final Logger LOGGER =
            Logger.getLogger(IgnoreEmptyIncrementsTest.class.getName());
    private JButtonOperator backupButtonOperator;
    private JButtonOperator continueButtonOperator;

    /**
     * tests decrypting a directory
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testIgnoreEmptyIncrements() throws Exception {

        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        File tempDir = null;
        try {
            tempDir = FileTools.createTempDirectory(
                    IgnoreEmptyIncrementsTest.class.getName(), null);
            testIgnoreEmptyIncrements(tempDir);
        } finally {
            if (tempDir != null) {
                FileTools.recursiveDelete(tempDir, true);
            }
        }
    }

    private void testIgnoreEmptyIncrements(File tempDir) throws Exception {

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
        File testFile = new File(testDir, "file");
        if (!testFile.createNewFile()) {
            fail("could not create " + testFile);
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
        backupButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("backupButton"));
        continueButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("continueButton"));

        // backup two times in a row
        // (the second backup is ignored by rdiff-backup)
        backup();
        Thread.sleep(1000);
        backup();

        // switch to restore tab
        JTabbedPaneOperator tabbedPaneOperator = new JTabbedPaneOperator(
                frameOperator, new NameComponentChooser("mainTabbedPane"));
        JComponentOperator restoreCardPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("restoreCardPanel"));
        tabbedPaneOperator.setSelectedComponent(
                restoreCardPanelOperator.getSource());
        JListOperator backupsListOperator = new JListOperator(frameOperator,
                new NameComponentChooser("backupsList"));

        // wait until increments are filled
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

        // check that only one increment is shown in list
        assertEquals("empty increments are not ignored", 1, model.getSize());

    }

    private void backup() throws InterruptedException {
        backupButtonOperator.push();
        continueButtonOperator.waitComponentShowing(true);
        continueButtonOperator.waitComponentEnabled();
        continueButtonOperator.push();
    }
}
