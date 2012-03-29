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

import ch.fhnw.jbackpack.JBackpack;
import ch.fhnw.util.CurrentOperatingSystem;
import ch.fhnw.util.FileTools;
import ch.fhnw.util.OperatingSystem;
import java.io.File;
import junit.framework.TestCase;
import org.junit.Test;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFileChooserOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.util.NameComponentChooser;
import screenshots.Screenshots;

/**
 * tests directory selection
 *
 * @author Ronny Standtke <Ronny.Standtke@gmx.net>
 */
public class KeepFileSelectionTest extends TestCase {

    /**
     * test that selecting an empty directory is allowed
     *
     * @throws Exception if exception occurs
     */
    @Test
    public void testKepFileSelection() throws Exception {

        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        if (CurrentOperatingSystem.OS == OperatingSystem.Mac_OS_X) {
            fail("keeping file selection between increments is not supported on"
                    + " Mac OS X");
        }

        JBackpack.setLookAndFeel();

        File tmpDir = null;
        try {
            TestEnvironment testEnvironment = new TestEnvironment();
            tmpDir = testEnvironment.getTempDirectory();
            File backupDir = testEnvironment.getBackupDirectory();
            final SelectBackupDirectoryDialog dialog =
                    new SelectBackupDirectoryDialog(
                    null, null, backupDir.getPath(), true);
            new Thread() {

                @Override
                public void run() {
                    dialog.setVisible(true);
                }
            }.start();

            JDialogOperator dialogOperator = new JDialogOperator();
            JFileChooserOperator fileChooserOperator =
                    new JFileChooserOperator(dialogOperator);

            // wait until GUI filled up the increments list
            int maxCounter = 0;
            File currentDirectory;
            while (true) {
                currentDirectory = fileChooserOperator.getCurrentDirectory();
                maxCounter++;
                assertTrue("increments list never filled", maxCounter < 100);
                if (currentDirectory instanceof RdiffFile) {
                    break;
                } else {
                    Thread.sleep(100);
                }
            }

            currentDirectory = fileChooserOperator.getCurrentDirectory();
            File[] files = currentDirectory.listFiles();
            fileChooserOperator.setSelectedFiles(files);

            // now select the next increment
            JListOperator backupsListOperator = new JListOperator(
                    dialogOperator, new NameComponentChooser("backupsList"));
            backupsListOperator.setSelectedIndex(1);

            // wait until GUI loaded selected increment
            Increment increment =
                    (Increment) backupsListOperator.getSelectedValue();
            maxCounter = 0;
            RdiffFile nextCurrentDirectory;
            while (true) {
                nextCurrentDirectory =
                        (RdiffFile) fileChooserOperator.getCurrentDirectory();
                maxCounter++;
                assertTrue("increments list never filled", maxCounter < 100);
                if (nextCurrentDirectory.getIncrement() == increment) {
                    break;
                } else {
                    Thread.sleep(100);
                }
            }
            Thread.sleep(2000);

            assertEquals("file selection was not kept",
                    CurrentOperatingSystem.OS == OperatingSystem.Windows
                    ? 1 : 2, fileChooserOperator.getSelectedFiles().length);

        } finally {
            if (tmpDir != null) {
                FileTools.recursiveDelete(tmpDir, true);
            }
        }
    }
}
