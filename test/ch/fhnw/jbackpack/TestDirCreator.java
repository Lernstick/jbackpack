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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * creates a directory filled with files for testing the backup function
 *
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class TestDirCreator {

    private final static Logger LOGGER =
            Logger.getLogger(TestDirCreator.class.getName());

    /**
     * runs the program
     *
     * @param args the command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // work in a temporary directory
        File tmpDir = File.createTempFile(
                TestDirCreator.class.getSimpleName(), null);
        tmpDir.delete();
        if (tmpDir.mkdirs()) {
            LOGGER.log(Level.INFO, "using temporary directory {0}", tmpDir);
        } else {
            LOGGER.log(Level.SEVERE, "could not create {0}", tmpDir);
            System.exit(-1);
        }

        // create dirs with files
        for (int i = 0; i < 10; i++) {
            File dir = new File(tmpDir, "dir" + i);
            dir.mkdir();
            System.out.println("processing directory " + dir);
            for (int j = 0; j < 100; j++) {
                FileOutputStream fileOutputStream =
                        new FileOutputStream(new File(dir, "file" + j));
                Random random = new Random();
                int randomSize = 1 + random.nextInt(1000000);
                byte[] data = new byte[randomSize];
                fileOutputStream.write(data);
                fileOutputStream.close();
            }
        }
    }
}
