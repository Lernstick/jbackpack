/**
 * GermanScreenshotTest.java
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
 * creates the German screenshots
 *
 * @author Ronny.Standtke@gmx.net
 */
public class GermanScreenshotTest extends TestCase {

    private static final Logger LOGGER =
            Logger.getLogger(EnglishScreenshotTest.class.getName());

    /**
     * creates the German screenshots
     *
     * @throws Exception if an exception occurs
     */
    public void testDoScreenShots() throws Exception {

        assertTrue("screenshots disabled", Screenshots.UPDATE_SCREENSHOTS);

        switch (CurrentOperatingSystem.OS) {
            case Linux:
                doScreenShots("nimbus", "/home/benutzer/", "verzeichnis/datei");
                break;

            case Mac_OS_X:
                doScreenShots("aqua", "/Users/benutzer/", "verzeichnis/datei");
                break;

            case Windows:
                doScreenShots("windows",
                        "C:\\Dokumente und Einstellungen\\Benutzer\\",
                        "Verzeichnis\\Datei");
                break;

            default:
                LOGGER.log(Level.WARNING,
                        "{0} is not supported", CurrentOperatingSystem.OS);
        }
    }

    private void doScreenShots(String plaf, String userHome, String backupFile)
            throws Exception {
        Screenshots.doScreenShots(Locale.GERMAN, "doc/docbook/" + plaf + "/de/",
                userHome, "Hauptfenster", "benutzer", "/datensicherung", "SSH",
                "Angemeldet", "datensicherung", "SMB", "Verschluesselung",
                "VerschluesselungSteuerung", "Datensicherung",
                "DateienAusschliessen", backupFile, "Laufende_Datensicherung",
                "Datensicherungsstatistik", "Wiederherstellung",
                "Erweiterte_Einstellungen", "DateiMenue", "Einstellungen1",
                "Einstellungen2");
    }
}
