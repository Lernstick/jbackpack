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
import ch.fhnw.util.ProcessExecutor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Ronny Standtke <Ronny.Standtke@gmx.net>
 */
public class FileLister {

    /**
     * @param args the command line arguments
     * @throws IOException if an I/O exception occurs
     * @throws SQLException if syncing the file database fails
     */
    public static void main(String[] args) throws IOException, SQLException {
        String backupDirectoryPath = "/mnt/backup/ronny";
        ProcessExecutor processExecutor = new ProcessExecutor();
        processExecutor.executeProcess(
                "rdiff-backup", "--parsable-output", "-l", backupDirectoryPath);
        File backupDirectory = new File(backupDirectoryPath);
        RdiffFileDatabase rdiffFileDatabase =
                RdiffFileDatabase.getInstance(backupDirectory);
        rdiffFileDatabase.sync();
        List<Increment> increments = rdiffFileDatabase.getIncrements();
        Increment increment = increments.get(1);
        File root = increment.getRdiffRoot();

        File tmpFile = File.createTempFile(
                FileLister.class.getSimpleName(), null);
        FileWriter fileWriter = new FileWriter(tmpFile);
        listFiles(root, fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }

    private static void listFiles(File file, FileWriter fileWriter)
            throws IOException {
        fileWriter.write(file.getAbsolutePath() + "\n");
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                listFiles(subFile, fileWriter);
            }
        }
    }
}
