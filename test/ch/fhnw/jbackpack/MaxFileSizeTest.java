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

import ch.fhnw.jbackpack.chooser.TestEnvironment;
import ch.fhnw.util.FileTools;
import java.io.File;
import java.io.FileOutputStream;
import java.util.prefs.Preferences;
import junit.framework.TestCase;
import org.junit.Test;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.util.NameComponentChooser;
import screenshots.Screenshots;

/**
 * tests that max file size handling is correct
 *
 * @author Ronny Standtke <Ronny.Standtke@gmx.net>
 */
public class MaxFileSizeTest extends TestCase {

    /**
     * tests that max file size handling is correct
     *
     * @throws Exception if exception occurs
     */
    @Test
    public void testMaxFileSize() throws Exception {

        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        JBackpack.setLookAndFeel();

        File tmpDir = null;
        try {
            TestEnvironment testEnvironment = new TestEnvironment();
            tmpDir = testEnvironment.getTempDirectory();
            File sourceDir = new File(tmpDir, "source");
            File backupDir = testEnvironment.getBackupDirectory();

            // put a "large" file there
            File largeFile = new File(sourceDir, "large_file");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(largeFile);
            int size = 10000000;
            byte[] data = new byte[size];
            fileOutputStream.write(data);
            fileOutputStream.close();

            // customize some preferences before starting application
            Preferences preferences =
                    Preferences.userNodeForPackage(BackupMainPanel.class);
            preferences.put(BackupMainPanel.SOURCE, sourceDir.getPath());
            preferences.put(BackupMainPanel.DESTINATION, "local");
            preferences.put(BackupMainPanel.LOCAL_DESTINATION_DIRECTORY,
                    backupDir.getPath());
            preferences.putBoolean(BackupMainPanel.EXCLUDES, true);
            preferences.put(BackupMainPanel.EXCLUDES_LIST, "");
            preferences.putBoolean(BackupMainPanel.INCLUDES, false);
            preferences.putBoolean(BackupMainPanel.EXCLUDE_LARGE_FILES, true);
            preferences.putLong(BackupMainPanel.MAX_FILE_SIZE, size - 1);
            preferences.putInt(BackupMainPanel.MAX_FILE_SIZE_UNIT, 0);
            preferences.putBoolean(BackupMainPanel.EXCLUDE_SMALL_FILES, false);

            ClassReference classReference = new ClassReference(
                    "ch.fhnw.jbackpack.JBackpack");
            classReference.startApplication();
            JFrameOperator frameOperator = new JFrameOperator();

            // backup
            JButtonOperator backupButtonOperator = new JButtonOperator(
                    frameOperator, new NameComponentChooser("backupButton"));
            backupButtonOperator.waitComponentShowing(true);
            backupButtonOperator.waitComponentEnabled();
            backupButtonOperator.push();
            JButtonOperator continueButtonOperator =
                    new JButtonOperator(frameOperator,
                    new NameComponentChooser("continueButton"));
            continueButtonOperator.waitComponentShowing(true);
            continueButtonOperator.waitComponentEnabled();
            continueButtonOperator.push();

            // check that large file was excluded
            assertFalse("large file was not excluded",
                    (new File(backupDir, "large_file")).exists());

        } finally {
            if (tmpDir != null) {
                FileTools.recursiveDelete(tmpDir, true);
            }
        }
    }
}
