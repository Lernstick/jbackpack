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
import ch.fhnw.util.OperatingSystem;
import junit.framework.TestCase;
import org.junit.Test;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.NameComponentChooser;
import screenshots.Screenshots;

/**
 * some tests for the main panel
 *
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class BackupMainPanelTest extends TestCase {

    /**
     * tests focus handling when switching tabs
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testFocusOnTabSwitch() throws Exception {

        assertFalse("screenshots only", Screenshots.UPDATE_SCREENSHOTS);

        ClassReference classReference = new ClassReference(
                "ch.fhnw.jbackpack.JBackpack");
        classReference.startApplication();
        JFrameOperator frameOperator = new JFrameOperator();
        JTabbedPaneOperator tabbedPaneOperator = new JTabbedPaneOperator(
                frameOperator, new NameComponentChooser("mainTabbedPane"));
        JComponentOperator directoriesPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("directoriesPanel"));
        JComponentOperator backupPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("backupCardPanel"));
        JTextFieldOperator backupSourceTextFieldOperator =
                new JTextFieldOperator(frameOperator,
                new NameComponentChooser("backupSourceTextField"));
        JRadioButtonOperator localRadioButtonOperator =
                new JRadioButtonOperator(frameOperator,
                new NameComponentChooser("localRadioButton"));
        JRadioButtonOperator sshRadioButtonOperator =
                new JRadioButtonOperator(frameOperator,
                new NameComponentChooser("sshRadioButton"));
        JRadioButtonOperator sshPasswordRadioButtonOperator =
                new JRadioButtonOperator(frameOperator,
                new NameComponentChooser("sshPasswordRadioButton"));
        JTextFieldOperator localStorageTextFieldOperator =
                new JTextFieldOperator(frameOperator,
                new NameComponentChooser("localStorageTextField"));
        JTextFieldOperator sshUserNameTextFieldOperator =
                new JTextFieldOperator(frameOperator,
                new NameComponentChooser("sshUserNameTextField"));
        JTextFieldOperator sshServerTextFieldOperator = new JTextFieldOperator(
                frameOperator, new NameComponentChooser("sshServerTextField"));
        JPasswordFieldOperator sshPasswordFieldOperator =
                new JPasswordFieldOperator(frameOperator,
                new NameComponentChooser("sshPasswordField"));
        JButtonOperator sshLogInOutButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("sshLogInOutButton"));
        JButtonOperator lockButtonOperator = new JButtonOperator(frameOperator,
                new NameComponentChooser("lockButton"));

        tabbedPaneOperator.setSelectedComponent(
                directoriesPanelOperator.getSource());

        if (lockButtonOperator.isShowing()
                && "unlock".equals(lockButtonOperator.getActionCommand())) {
            // the unlock dialog pops up
            JDialogOperator dialogOperator = new JDialogOperator();
            dialogOperator.setVisible(false);
            dialogOperator.dispose();
        }

        // make sure we are logged out
        if ("logout".equals(sshLogInOutButtonOperator.getActionCommand())) {
            sshLogInOutButtonOperator.push();
        }
        if (!localRadioButtonOperator.isSelected()) {
            localRadioButtonOperator.setSelected(false);
        }
        // use password authentication
        sshPasswordRadioButtonOperator.setSelected(true);

        tabbedPaneOperator.setSelectedComponent(
                backupPanelOperator.getSource());

        // check with empty backupSourceTextField
        backupSourceTextFieldOperator.setText(null);
        Thread.sleep(500);
        tabbedPaneOperator.setSelectedComponent(
                directoriesPanelOperator.getSource());
        backupSourceTextFieldOperator.waitHasFocus();
        tabbedPaneOperator.setSelectedComponent(
                backupPanelOperator.getSource());

        // check with empty localStorageTextField
        localRadioButtonOperator.setSelected(true);
        backupSourceTextFieldOperator.setText("xxx");
        localStorageTextFieldOperator.setText(null);
        Thread.sleep(100);
        tabbedPaneOperator.setSelectedComponent(
                directoriesPanelOperator.getSource());
        localStorageTextFieldOperator.waitHasFocus();

        // the following tests do not work on Windows
        // because of missing sshfs support
        if (CurrentOperatingSystem.OS == OperatingSystem.Windows) {
            return;
        }
        sshRadioButtonOperator.setSelected(true);
        Thread.sleep(100);
        tabbedPaneOperator.setSelectedComponent(
                backupPanelOperator.getSource());

        // check with empty serverTextField
        sshServerTextFieldOperator.setText(null);
        Thread.sleep(100);
        tabbedPaneOperator.setSelectedComponent(
                directoriesPanelOperator.getSource());
        sshServerTextFieldOperator.waitHasFocus();
        tabbedPaneOperator.setSelectedComponent(
                backupPanelOperator.getSource());

        // check with empty userNameTextField
        sshServerTextFieldOperator.setText("xxx");
        sshUserNameTextFieldOperator.setText(null);
        tabbedPaneOperator.setSelectedComponent(
                directoriesPanelOperator.getSource());
        sshUserNameTextFieldOperator.waitHasFocus();
        tabbedPaneOperator.setSelectedComponent(
                backupPanelOperator.getSource());

        // check with empty passwordField
        sshUserNameTextFieldOperator.setText("xxx");
        sshPasswordFieldOperator.setText(null);
        tabbedPaneOperator.setSelectedComponent(
                directoriesPanelOperator.getSource());
        sshPasswordFieldOperator.waitHasFocus();
        tabbedPaneOperator.setSelectedComponent(
                backupPanelOperator.getSource());

        // check without login
        sshPasswordFieldOperator.setText("xxx");
        Thread.sleep(100);
        tabbedPaneOperator.setSelectedComponent(
                directoriesPanelOperator.getSource());
        sshLogInOutButtonOperator.waitHasFocus();
        tabbedPaneOperator.setSelectedComponent(
                backupPanelOperator.getSource());
    }
}
