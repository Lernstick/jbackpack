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
import java.io.File;
import junit.framework.TestCase;
import org.junit.Test;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.util.NameComponentChooser;
import screenshots.Screenshots;

/**
 * tests directory selection
 *
 * @author Ronny Standtke <Ronny.Standtke@gmx.net>
 */
public class SelectBackupDirectoryDialogTest extends TestCase {

    /**
     * test that selecting an empty directory is allowed
     *
     * @throws Exception if exception occurs
     */
    @Test
    public void testSetSelectedDirectory() throws Exception {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        JBackpack.setLookAndFeel();

        File tmpDir = null;
        try {
            // create temporary directory
            tmpDir = File.createTempFile("SelectBackupDirDialogTest", null);
            tmpDir.delete();
            if (!tmpDir.mkdirs()) {
                fail("could not create temporary directory");
            }

            final SelectBackupDirectoryDialog dialog =
                    new SelectBackupDirectoryDialog(
                    null, null, tmpDir.getPath(), true);
            new Thread() {

                @Override
                public void run() {
                    dialog.setVisible(true);
                }
            }.start();

            JDialogOperator dialogOperator = new JDialogOperator();
            JButtonOperator selectButtonOperator = new JButtonOperator(
                    dialogOperator, new NameComponentChooser("selectButton"));

            selectButtonOperator.waitComponentEnabled();
        } finally {
            if (tmpDir != null) {
                tmpDir.delete();
            }
        }
    }
}
