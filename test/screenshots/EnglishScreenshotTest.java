/**
 * EnglishScreenshotTest.java
 *
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
 *
 * Created on 29. Oktober 2006, 08:41
 *
 */
package screenshots;

import ch.fhnw.util.CurrentOperatingSystem;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 * creates the English screenshots
 *
 * @author Ronny.Standtke@gmx.net
 */
public class EnglishScreenshotTest extends TestCase {

    private static final Logger LOGGER =
            Logger.getLogger(EnglishScreenshotTest.class.getName());

    /**
     * creates the English screenshots
     *
     * @throws Exception if an exception occurs
     */
    public void testDoScreenShots() throws Exception {

        assertTrue("screenshots disabled", Screenshots.UPDATE_SCREENSHOTS);

        switch (CurrentOperatingSystem.OS) {
            case Linux:
                doScreenShots("nimbus", "/home/user/", "/home/", 
                        "/home/alice/\n/home/bob/", "directory/file");
                break;

            case Mac_OS_X:
                doScreenShots("aqua", "/Users/user/", "/Users/",
                        "/Users/alice/\n/Users/bob/", "directory/file");
                break;

            case Windows:
                doScreenShots("windows",
                        "C:\\Documents and Settings\\User\\",
                        "C:\\Documents and Settings\\",
                        "C:\\Documents and Settings\\Alice\\\n"
                        + "C:\\Documents and Settings\\Bob\\",
                        "Directory\\File");
                break;

            default:
                LOGGER.log(Level.WARNING,
                        "{0} is not supported", CurrentOperatingSystem.OS);
        }
    }

    private void doScreenShots(String plaf, String userHome, String excludes,
            String includes, String backupFile) throws Exception {
        Screenshots.doScreenShots(Locale.ENGLISH,
                "doc/docbook/" + plaf + "/en/", userHome, "MainWindow",
                "user", "/backup", "SSH", "LoggedIn", "backup", "SMB",
                "Encryption", "EncryptionControl", "Backup", excludes, includes,
                "Excludes", backupFile, "Running_Backup", "Backup_Statistics",
                "Restore", "Advanced_Settings", "FileMenu", "Preferences1",
                "Preferences2");
    }
}
