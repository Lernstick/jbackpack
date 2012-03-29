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

import ch.fhnw.util.FileTools;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import screenshots.Screenshots;

/**
 *
 * @author Ronny Standtke <Ronny.Standtke@gmx.net>
 */
public class RdiffFileSystemViewTest extends TestCase {

    /**
     * Test of isTraversable method, of class RdiffFileSystemView.
     *
     * @throws IOException if an I/O exception occurs
     * @throws SQLException if syncing the file database fails
     */
    @Test
    public void testIsTraversable() throws IOException, SQLException {
        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        File tmpDir = null;
        try {
            TestEnvironment testEnvironment = new TestEnvironment();
            tmpDir = testEnvironment.getTempDirectory();
            List<Increment> increments = testEnvironment.getIncrements();

            // check root
            Increment increment = increments.get(1);
            RdiffFile root = increment.getRdiffRoot();
            RdiffFileSystemView instance = new RdiffFileSystemView();
            instance.setRoot(root);
            assertTrue(instance.isTraversable(root));

            // check subdir
            RdiffFile subdir = root.getChild("subdir");
            assertTrue(instance.isTraversable(subdir));

            // check file
            RdiffFile file = root.getChild("file");
            assertFalse(instance.isTraversable(file));

        } finally {
            if (tmpDir != null) {
                FileTools.recursiveDelete(tmpDir, true);
            }
        }
    }
}
