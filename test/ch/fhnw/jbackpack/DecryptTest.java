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

import ch.fhnw.util.CurrentOperatingSystem;
import ch.fhnw.util.FileTools;
import ch.fhnw.util.OperatingSystem;
import java.awt.Frame;
import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import junit.framework.TestCase;
import org.junit.Test;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.NameComponentChooser;
import screenshots.Screenshots;

/**
 * tests decrypting a directory
 *
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class DecryptTest extends TestCase {

    private static final Logger LOGGER =
            Logger.getLogger(DecryptTest.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "ch/fhnw/jbackpack/Strings");
    private JButtonOperator encryptionButtonOperator;

    /**
     * tests decrypting a directory
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testDecrypt() throws Exception {

        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        if (CurrentOperatingSystem.OS == OperatingSystem.Windows) {
            fail("this test does not work on " + CurrentOperatingSystem.OS);
        }
        File tempDir = null;
        try {
            tempDir = FileTools.createTempDirectory(
                    DecryptTest.class.getName() + " test", null);
            testDecrypt(tempDir);
        } finally {
            if (tempDir != null) {
                FileTools.recursiveDelete(tempDir, true);
            }
        }
    }

    private void testDecrypt(File tempDir) throws Exception {
        // customize some preferences before starting application
        Preferences preferences =
                Preferences.userNodeForPackage(BackupMainPanel.class);
        preferences.put(BackupMainPanel.SOURCE, "does_not_exist");
        preferences.put(BackupMainPanel.DESTINATION, "local");
        preferences.put(BackupMainPanel.LOCAL_DESTINATION_DIRECTORY,
                tempDir.getPath());
        preferences.flush();
        Thread.sleep(1000);

        ClassReference classReference = new ClassReference(
                "ch.fhnw.jbackpack.JBackpack");
        classReference.startApplication();
        JFrameOperator frameOperator = new JFrameOperator();
        JTabbedPaneOperator tabbedPaneOperator = new JTabbedPaneOperator(
                frameOperator, new NameComponentChooser("mainTabbedPane"));
        JComponentOperator directoriesPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("directoriesPanel"));
        JComponentOperator backupCardPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("backupCardPanel"));
        JLabelOperator backupErrorLabelOperator = new JLabelOperator(
                frameOperator, new NameComponentChooser("backupErrorLabel"));
        encryptionButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("encryptionButton"));
        JButtonOperator lockButtonOperator = new JButtonOperator(frameOperator,
                new NameComponentChooser("lockButton"));

        tabbedPaneOperator.setSelectedComponent(
                directoriesPanelOperator.getSource());

        // test error handling when filenames are too long for encfs
        FilenameCheckSwingWorker filenameCheckSwingWorker =
                new FilenameCheckSwingWorker((Frame) frameOperator.getSource(),
                tempDir);
        filenameCheckSwingWorker.execute();
        int maxFileNameLength = filenameCheckSwingWorker.get();

        LOGGER.log(Level.INFO, "maxFileNameLength = {0}", maxFileNameLength);
        StringBuilder stringBuilder = new StringBuilder(maxFileNameLength);
        for (int i = 0; i < maxFileNameLength; i++) {
            stringBuilder.append(i % 10);
        }
        String tooLongFileName = stringBuilder.toString();
        File testFile = new File(tempDir, tooLongFileName);
        if (!testFile.createNewFile()) {
            fail("could not create " + testFile);
        }

        // try to encrypt destination directory
        String password = "123";
        encrypt(password);

        // check that helpful error message regarding too long filenames pops up
        JDialogOperator dialogOperator = new JDialogOperator(
                new NameComponentChooser("TooLongFilenamesDialog"));
        dialogOperator.setVisible(false);
        dialogOperator.dispose();

        // delete too long file
        if (!testFile.delete()) {
            fail("coult not delete " + testFile);
        }

        // try again (should work now...)
        encrypt(password);

        // close success dialog
        dialogOperator = new JDialogOperator(BUNDLE.getString("Information"));
        dialogOperator.setVisible(false);
        dialogOperator.dispose();

        // test password change
        JButtonOperator changePasswordButtonOperator =
                new JButtonOperator(frameOperator,
                new NameComponentChooser("changePasswordButton"));
        changePasswordButtonOperator.waitComponentShowing(true);
        changePasswordButtonOperator.pushNoBlock();
        dialogOperator = new JDialogOperator();
        JPasswordFieldOperator oldPasswordFieldOperator =
                new JPasswordFieldOperator(dialogOperator,
                new NameComponentChooser("oldPasswordField"));
        JPasswordFieldOperator passwordField1FieldOperator =
                new JPasswordFieldOperator(dialogOperator,
                new NameComponentChooser("passwordField1"));
        JPasswordFieldOperator passwordField2FieldOperator =
                new JPasswordFieldOperator(dialogOperator,
                new NameComponentChooser("passwordField2"));
        JButtonOperator okButtonOperator = new JButtonOperator(
                dialogOperator, new NameComponentChooser("okButton"));
        oldPasswordFieldOperator.setText(password);
        String newPassword = password + "_new";
        passwordField1FieldOperator.setText(newPassword);
        passwordField2FieldOperator.setText(newPassword);
        password = newPassword;
        okButtonOperator.pushNoBlock();
        dialogOperator.waitComponentShowing(false);
        dialogOperator = new JDialogOperator();
        JLabelOperator jLabelOperator = new JLabelOperator(
                dialogOperator, BUNDLE.getString("Password_Changed"));
        dialogOperator.setVisible(false);

        // lock destination directory
        lockButtonOperator.waitComponentShowing(true);
        lockButtonOperator.push();

        // test correct error reporting
        JTextFieldOperator backupSourceTextFieldOperator =
                new JTextFieldOperator(frameOperator,
                new NameComponentChooser("backupSourceTextField"));
        backupSourceTextFieldOperator.setText(
                System.getProperty("user.home"));
        Thread.sleep(1000);
        tabbedPaneOperator.setSelectedComponent(
                backupCardPanelOperator.getSource());
        String configTabTitle = BUNDLE.getString(
                "BackupMainPanel.directoriesPanel.TabConstraints.tabTitle");
        String errorMessage = BUNDLE.getString("Error_Destination_Locked");
        errorMessage = MessageFormat.format(errorMessage, configTabTitle);
        assertEquals(errorMessage, backupErrorLabelOperator.getText());
        tabbedPaneOperator.setSelectedComponent(
                directoriesPanelOperator.getSource());

        // try to unlock with wrong password
        lockButtonOperator.waitText(BUNDLE.getString("Unlock"));
        lockButtonOperator.push();
        dialogOperator = new JDialogOperator(
                new NameComponentChooser("UnlockEncfsDialog"));
        JPasswordFieldOperator passwordFieldOperator =
                new JPasswordFieldOperator(dialogOperator,
                new NameComponentChooser("passwordField"));
        passwordFieldOperator.setText(password + "x");
        okButtonOperator = new JButtonOperator(
                dialogOperator, new NameComponentChooser("okButton"));
        okButtonOperator.push();
        // this triggers an JOptionPane with an error message
        dialogOperator = new JDialogOperator();
        okButtonOperator = new JButtonOperator(dialogOperator);
        okButtonOperator.push();
        // and reopens the unlock dialog
        dialogOperator = new JDialogOperator(
                new NameComponentChooser("UnlockEncfsDialog"));
        JButtonOperator cancelButtonOperator = new JButtonOperator(
                dialogOperator, new NameComponentChooser("cancelButton"));
        cancelButtonOperator.push();

        // now try to decrypt
        JButtonOperator decryptionButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("decryptionButton"));
        decryptionButtonOperator.push();

        // the DecryptEncfsDialog must pop up now, not the JOptionPane!!!
        dialogOperator = new JDialogOperator(
                new NameComponentChooser("DecryptEncfsDialog"));
    }

    private void encrypt(String password) {
        // encrypt destination directory
        encryptionButtonOperator.waitComponentShowing(true);
        encryptionButtonOperator.pushNoBlock();
        JDialogOperator dialogOperator = new JDialogOperator(
                new NameComponentChooser("NewEncfsDialog"));
        JPasswordFieldOperator passwordField1Operator =
                new JPasswordFieldOperator(dialogOperator,
                new NameComponentChooser("passwordField1"));
        JPasswordFieldOperator passwordField2Operator =
                new JPasswordFieldOperator(dialogOperator,
                new NameComponentChooser("passwordField2"));
        passwordField1Operator.setText(password);
        passwordField2Operator.setText(password);
        JButtonOperator okButtonOperator = new JButtonOperator(dialogOperator,
                new NameComponentChooser("okButton"));
        okButtonOperator.push();
    }
}
