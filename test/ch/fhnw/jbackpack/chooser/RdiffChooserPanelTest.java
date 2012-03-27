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

import junit.framework.TestCase;
import org.junit.Test;
import screenshots.Screenshots;

/**
 *
 * @author Ronny Standtke <Ronny.Standtke@gmx.net>
 */
public class RdiffChooserPanelTest extends TestCase {

    /**
     * Test of setSelectedDirectory method, of class RdiffChooserPanel.
     */
    @Test
    public void testSetSelectedDirectory() {

        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        // must accept null pointers
        RdiffChooserPanel instance = new RdiffChooserPanel();
        instance.setSelectedDirectory(null);
    }
}
