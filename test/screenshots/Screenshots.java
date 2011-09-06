/*
 * Screenshots.java
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
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Created on 29. Oktober 2006, 08:41
 *
 */
package screenshots;

import ch.fhnw.jbackpack.BackupMainPanel;
import ch.fhnw.jbackpack.JBackpack;
import ch.fhnw.jbackpack.LogLevel;
import ch.fhnw.jbackpack.PreferencesDialog;
import ch.fhnw.jbackpack.chooser.TestEnvironment;
import ch.fhnw.util.CurrentOperatingSystem;
import ch.fhnw.util.OperatingSystem;
import java.awt.CardLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JComponentOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JMenuOperator;
import org.netbeans.jemmy.operators.JPasswordFieldOperator;
import org.netbeans.jemmy.operators.JProgressBarOperator;
import org.netbeans.jemmy.operators.JRadioButtonOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.NameComponentChooser;
import org.netbeans.jemmy.util.PNGEncoder;

/**
 *
 * @author Ronny.Standtke@gmx.net
 */
public final class Screenshots {

    /**
     * if true, the unit tests just create screenshots
     */
    public static final boolean UPDATE_SCREENSHOTS = false;

    private Screenshots() {
        // singleton
    }

    /**
     * creates all screenshots
     * @param locale the locale to use
     * @param screenshotPath the path where to store the screenshots
     * @param backupSourcePath the backup source path
     * @param mainWindowFileName the file name of the main window screen shot
     * screenshot
     * @param remoteUserName the remote user name
     * @param remoteDestinationPath the remote destination path
     * @param sshScreenshotFileName the filename for the ssh screenshot
     * @param sshLoggedInFileName the filename for the logged in screenshot
     * @param smbShare the name of the SMB share to show
     * @param smbScreenshotFileName  the filename for the smb screenshot
     * @param encryptionFileName the filename for the encryption screenshot
     * @param encryptionControlFileName the filename for the encryption control
     * screenshot
     * @param basicBackupFileName the filename for the basic backup screenshot
     * @param excludesFileName the filename for the excludes screenshot
     * @param backupFileName the file to show for the running backup screenshot
     * @param runningBackupFilenName the filename of a running backup screenshot
     * @param backupStatisticsFileName the filename of a backup statistics
     * screenshot
     * @param restoreFileName the filename for the restore screenshot
     * @param advancedSettingsFileName the filename for the advanced settings
     * screenshot
     * @param fileMenuFileName the filename of the file menu screenshot
     * @param preferences1FileName the first preferences dialog screenshot
     * @param preferences2FileName the second preferences dialog screenshot
     * @throws Exception if an exception occurs
     */
    public static void doScreenShots(Locale locale, String screenshotPath,
            String backupSourcePath, String mainWindowFileName,
            String remoteUserName, String remoteDestinationPath,
            String sshScreenshotFileName, String sshLoggedInFileName,
            String smbShare, String smbScreenshotFileName,
            String encryptionFileName, String encryptionControlFileName,
            String basicBackupFileName, String excludesFileName,
            String backupFileName, String runningBackupFilenName,
            String backupStatisticsFileName, String restoreFileName,
            String advancedSettingsFileName, String fileMenuFileName,
            String preferences1FileName, String preferences2FileName)
            throws Exception {

        // make sure that path exists
        File directory = new File(screenshotPath);
        if ((!directory.exists()) && (!directory.mkdir())) {
            throw new IOException(
                    "Could not create directory \"" + screenshotPath + "\"");
        }

        Locale.setDefault(locale);
        ResourceBundle bundle = ResourceBundle.getBundle(
                "ch/fhnw/jbackpack/Strings", locale);

        String remoteServer = "www.imedias.ch";

        // customize some preferences before starting application
        Preferences preferences =
                Preferences.userNodeForPackage(BackupMainPanel.class);
        preferences.remove(BackupMainPanel.SOURCE);
        preferences.put(BackupMainPanel.DESTINATION, "local");
        preferences.put(BackupMainPanel.LOCAL_DESTINATION_DIRECTORY, "");
        preferences.putBoolean(BackupMainPanel.EXCLUDES, false);
        preferences.put(BackupMainPanel.SSH_SERVER, remoteServer);
        preferences.put(BackupMainPanel.SMB_SERVER, remoteServer);
        preferences.put(BackupMainPanel.SSH_USER, remoteUserName);
        preferences.put(BackupMainPanel.SMB_USER, remoteUserName);
        preferences.put(BackupMainPanel.SMB_SHARE, smbShare);
        preferences.put(BackupMainPanel.SSH_BASE, "");
        preferences.putBoolean(BackupMainPanel.PASSWORD_AUTHENTICATION, true);
        preferences.putBoolean(JBackpack.SHOW_REMINDER, false);
        preferences.remove(BackupMainPanel.EXCLUDE_LARGE_FILES);
        preferences.remove(BackupMainPanel.MAX_FILE_SIZE);
        preferences.remove(BackupMainPanel.MAX_FILE_SIZE_UNIT);
        preferences.remove(BackupMainPanel.EXCLUDE_SMALL_FILES);
        preferences.remove(BackupMainPanel.MIN_FILE_SIZE);
        preferences.remove(BackupMainPanel.MIN_FILE_SIZE_UNIT);
        preferences.putBoolean(BackupMainPanel.INCLUDES, true);
        Thread.sleep(1000);

        ClassReference classReference = new ClassReference(
                "ch.fhnw.jbackpack.JBackpack");
        classReference.startApplication();
        JFrameOperator frameOperator = new JFrameOperator();
        JTabbedPaneOperator tabbedPaneOperator = new JTabbedPaneOperator(
                frameOperator, new NameComponentChooser("mainTabbedPane"));
        JComponentOperator directoriesPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("directoriesPanel"));
        JTextFieldOperator backupSourceTextFieldOperator =
                new JTextFieldOperator(frameOperator,
                new NameComponentChooser("backupSourceTextField"));

        /**
         * main window screenshot
         */
        JRadioButtonOperator sshRadioButtonOperator =
                new JRadioButtonOperator(frameOperator,
                new NameComponentChooser("sshRadioButton"));
        sshRadioButtonOperator.setEnabled(true);
        if (mainWindowFileName != null) {
            tabbedPaneOperator.setSelectedComponent(
                    directoriesPanelOperator.getSource());
            backupSourceTextFieldOperator.setText(backupSourcePath);
            Thread.sleep(1000);
            PNGEncoder.captureScreen(frameOperator.getBounds(),
                    screenshotPath + mainWindowFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // ssh screenshot
        sshRadioButtonOperator.setSelected(true);
        JComponentOperator destinationCardPanelOperator =
                new JComponentOperator(frameOperator,
                new NameComponentChooser("destinationCardPanel"));
        JPasswordFieldOperator sshPasswordFieldOperator =
                new JPasswordFieldOperator(frameOperator,
                new NameComponentChooser("sshPasswordField"));
        sshPasswordFieldOperator.setText("password");
        JTextFieldOperator sshStorageTextFieldOperator =
                new JTextFieldOperator(frameOperator,
                new NameComponentChooser("sshStorageTextField"));
        sshStorageTextFieldOperator.setText(null);
        if (sshScreenshotFileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(destinationCardPanelOperator.getSource(),
                    screenshotPath + sshScreenshotFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // fake log in
        JTextFieldOperator sshServerTextFieldOperator = new JTextFieldOperator(
                frameOperator, new NameComponentChooser("sshServerTextField"));
        sshServerTextFieldOperator.setEditable(false);
        JTextFieldOperator sshUserNameTextFieldOperator =
                new JTextFieldOperator(frameOperator,
                new NameComponentChooser("sshUserNameTextField"));
        sshUserNameTextFieldOperator.setText(remoteUserName);
        sshUserNameTextFieldOperator.setEditable(false);
        JTextFieldOperator sshBaseDirTextFieldOperator = new JTextFieldOperator(
                frameOperator, new NameComponentChooser("sshBaseDirTextField"));
        sshBaseDirTextFieldOperator.setEditable(false);
        JRadioButtonOperator sshPasswordRadioButtonOperator =
                new JRadioButtonOperator(frameOperator,
                new NameComponentChooser("sshPasswordRadioButton"));
        sshPasswordRadioButtonOperator.setEnabled(false);
        sshPasswordFieldOperator.setEditable(false);
        sshPasswordFieldOperator.setEnabled(false);
        JRadioButtonOperator sshPublicKeyRadioButtonOperator =
                new JRadioButtonOperator(frameOperator,
                new NameComponentChooser("sshPublicKeyRadioButton"));
        sshPublicKeyRadioButtonOperator.setEnabled(false);
        JButtonOperator sshLogInOutButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("sshLogInOutButton"));
        sshLogInOutButtonOperator.setText(bundle.getString("Logout"));
        JProgressBarOperator sshLoginProgressBarOperator =
                new JProgressBarOperator(frameOperator,
                new NameComponentChooser("sshLoginProgressBar"));
        sshLoginProgressBarOperator.setString(bundle.getString("Logged_In"));
        JLabelOperator sshStorageLabelOperator = new JLabelOperator(
                frameOperator, new NameComponentChooser("sshStorageLabel"));
        sshStorageLabelOperator.setEnabled(true);
        sshStorageTextFieldOperator.setEnabled(true);
        sshStorageTextFieldOperator.setText(remoteDestinationPath);
        JButtonOperator sshStorageButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("sshStorageButton"));
        sshStorageButtonOperator.setEnabled(true);
        if (sshLoggedInFileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(destinationCardPanelOperator.getSource(),
                    screenshotPath + sshLoggedInFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // smb screenshot
        JRadioButtonOperator smbRadioButtonOperator = new JRadioButtonOperator(
                frameOperator, new NameComponentChooser("smbRadioButton"));
        smbRadioButtonOperator.setSelected(true);
        JPasswordFieldOperator smbPasswordFieldOperator =
                new JPasswordFieldOperator(frameOperator,
                new NameComponentChooser("smbPasswordField"));
        smbPasswordFieldOperator.setText("password");
        JPasswordFieldOperator smbSudoPasswordFieldOperator =
                new JPasswordFieldOperator(frameOperator,
                new NameComponentChooser("smbSudoPasswordField"));
        smbSudoPasswordFieldOperator.setText("smbSudoPassword");
        JTextFieldOperator smbStorageTextFieldOperator =
                new JTextFieldOperator(frameOperator,
                new NameComponentChooser("smbStorageTextField"));
        smbStorageTextFieldOperator.setText(null);
        if (smbScreenshotFileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(destinationCardPanelOperator.getSource(),
                    screenshotPath + smbScreenshotFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // encryption panel screenshot
        JComponentOperator encryptionCardPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("encryptionCardPanel"));
        JPanel encryptionCardPanel =
                (JPanel) encryptionCardPanelOperator.getSource();
        CardLayout encryptionCardPanelLayout =
                (CardLayout) encryptionCardPanel.getLayout();
        encryptionCardPanelLayout.show(encryptionCardPanel, "encryptionPanel");
        JButtonOperator encryptionButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("encryptionButton"));
        encryptionButtonOperator.setEnabled(true);
        if (encryptionFileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(encryptionCardPanel,
                    screenshotPath + encryptionFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // encryption control panel screenshot
        encryptionCardPanelLayout.show(encryptionCardPanel, "unlockPanel");
        JButtonOperator lockButtonOperator = new JButtonOperator(frameOperator,
                new NameComponentChooser("lockButton"));
        lockButtonOperator.setText(bundle.getString("Lock"));
        lockButtonOperator.setIcon(new ImageIcon(Screenshots.class.getResource(
                "/ch/fhnw/jbackpack/icons/16x16/encrypted.png")));
        if (encryptionControlFileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(encryptionCardPanelOperator.getSource(),
                    screenshotPath + encryptionControlFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // backup panel screenshot
        tabbedPaneOperator.setSelectedIndex(0);
        JComponentOperator backupCardPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("backupCardPanel"));
        JPanel backupCardPanel = (JPanel) backupCardPanelOperator.getSource();
        CardLayout backupCardPanelLayout =
                (CardLayout) backupCardPanel.getLayout();
        backupCardPanelLayout.show(backupCardPanel, "backupPanel");
        Rectangle rectangle = frameOperator.getBounds();
        rectangle.height = 220;
        if (basicBackupFileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(rectangle,
                    screenshotPath + basicBackupFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // excludes screenshot
        JCheckBoxOperator excludeCheckBoxOperator = new JCheckBoxOperator(
                frameOperator, new NameComponentChooser("excludeCheckBox"));
        excludeCheckBoxOperator.setSelected(true);
        JComponentOperator excludesPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("excludesPanel"));
        if (excludesFileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(excludesPanelOperator.getSource(),
                    screenshotPath + excludesFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // running backup screenshot
        JComponentOperator backupMainPanelOperator = new JComponentOperator(
                frameOperator, new NameComponentChooser("backupMainPanel"));
        BackupMainPanel backupMainPanel =
                (BackupMainPanel) backupMainPanelOperator.getSource();
        CardLayout backupMainPanelCardLayout =
                (CardLayout) backupMainPanel.getLayout();
        backupMainPanelCardLayout.show(backupMainPanel, "progressPanel");
        JLabelOperator progressLabelOperator = new JLabelOperator(
                frameOperator, new NameComponentChooser("progressLabel"));
        String progressMessage = bundle.getString("Backing_Up_File");
        progressMessage = MessageFormat.format(progressMessage, 42);
        progressLabelOperator.setText(progressMessage);
        JLabelOperator filenameLabelOperator = new JLabelOperator(
                frameOperator, new NameComponentChooser("filenameLabel"));
        filenameLabelOperator.setText(backupSourcePath + backupFileName);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        JLabelOperator timeLabelOperator = new JLabelOperator(
                frameOperator, new NameComponentChooser("timeLabel"));
        timeLabelOperator.setText(timeFormat.format(new Date(1002000)));
        JCheckBoxOperator shutdownCheckBoxOperator = new JCheckBoxOperator(
                frameOperator, new NameComponentChooser("shutdownCheckBox"));
        shutdownCheckBoxOperator.setEnabled(true);
        shutdownCheckBoxOperator.setSelected(true);
        JPasswordFieldOperator shutdownPasswordFieldOperator =
                new JPasswordFieldOperator(frameOperator,
                new NameComponentChooser("shutdownPasswordField"));
        shutdownPasswordFieldOperator.setText("password");
        JButtonOperator cancelButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("cancelButton"));
        Point progressLabelLocation =
                progressLabelOperator.getLocationOnScreen();
        Point cancelButtonLocation = cancelButtonOperator.getLocationOnScreen();
        Point shutdownCheckBoxLocation =
                shutdownCheckBoxOperator.getLocationOnScreen();
        int inset = 30;
        int x = shutdownCheckBoxLocation.x - inset;
        int y = progressLabelLocation.y - inset;
        int width = shutdownCheckBoxOperator.getWidth() + (2 * inset);
        int height = cancelButtonLocation.y
                + cancelButtonOperator.getHeight()
                - progressLabelLocation.y
                + (2 * inset);
        if (runningBackupFilenName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(new Rectangle(x, y, width, height),
                    screenshotPath + runningBackupFilenName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // backup summary screenshot
        Method fillStatisticsTextFieldMethod =
                BackupMainPanel.class.getDeclaredMethod(
                "fillStatisticsTextField", Map.class, String.class);
        fillStatisticsTextFieldMethod.setAccessible(true);
        Map<String, String> backupSessionStatistics =
                new HashMap<String, String>();
        backupSessionStatistics.put("NewFiles", "587");
        backupSessionStatistics.put("DeletedFiles", "42");
        backupSessionStatistics.put("ChangedFiles", "1396");
        backupSessionStatistics.put("TotalDestinationSizeChange", "574836921");
        String timeString = timeFormat.format(new Date(2002000));
        fillStatisticsTextFieldMethod.invoke(
                backupMainPanel, backupSessionStatistics, timeString);
        backupMainPanelCardLayout.show(
                backupMainPanel, "sessionStatisticsPanel");
        JButtonOperator quitButtonOperator = new JButtonOperator(
                frameOperator, new NameComponentChooser("quitButton"));
        JButton quitButton = (JButton) quitButtonOperator.getSource();
        quitButton.requestFocusInWindow();
        JComponentOperator statisticsTextFieldScrollPaneOperator =
                new JComponentOperator(frameOperator,
                new NameComponentChooser("statisticsTextFieldScrollPane"));
        JLabelOperator statisticsLabelOperator = new JLabelOperator(
                frameOperator, new NameComponentChooser("statisticsLabel"));
        Point statisticsTextFieldScrollPaneLocation =
                statisticsTextFieldScrollPaneOperator.getLocationOnScreen();
        Point statisticsLabelLocation =
                statisticsLabelOperator.getLocationOnScreen();
        Point continueButtonLocation =
                quitButtonOperator.getLocationOnScreen();
        x = statisticsTextFieldScrollPaneLocation.x - inset;
        y = statisticsLabelLocation.y - inset;
        width = statisticsTextFieldScrollPaneOperator.getWidth() + (2 * inset);
        height = continueButtonLocation.y
                + quitButtonOperator.getHeight()
                - statisticsLabelLocation.y
                + (2 * inset);
        if (backupStatisticsFileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(new Rectangle(x, y, width, height),
                    screenshotPath + backupStatisticsFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // restore screenshot
        backupSourceTextFieldOperator.setText(System.getProperty("user.home"));
        JRadioButtonOperator localRadioButtonOperator =
                new JRadioButtonOperator(frameOperator,
                new NameComponentChooser("localRadioButton"));
        localRadioButtonOperator.setSelected(true);
        TestEnvironment testEnvironment = new TestEnvironment();
        File backupDir = testEnvironment.getBackupDirectory();
        JTextFieldOperator localStorageTextFieldOperator =
                new JTextFieldOperator(frameOperator,
                new NameComponentChooser("localStorageTextField"));
        localStorageTextFieldOperator.setText(backupDir.getPath());
        backupMainPanelCardLayout.show(backupMainPanel, "mainTabbedPane");
        tabbedPaneOperator.setSelectedIndex(1);
        JListOperator backupsListOperator = new JListOperator(frameOperator,
                new NameComponentChooser("backupsList"));
        backupsListOperator.setSelectedIndex(1);
        if (restoreFileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(frameOperator.getBounds(),
                    screenshotPath + restoreFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // advanced settings screenshot
        tabbedPaneOperator.setSelectedIndex(3);
        JTextFieldOperator tempDirTextFieldOperator = new JTextFieldOperator(
                frameOperator, new NameComponentChooser("tempDirTextField"));
        rectangle = frameOperator.getBounds();
        String windowsTempDir = "C:\\Temp\\";
        switch (CurrentOperatingSystem.OS) {
            case Windows:
                tempDirTextFieldOperator.setText(windowsTempDir);
                rectangle.height = 320;
                break;
            default:
                rectangle.height = 360;
        }

        if (advancedSettingsFileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(rectangle,
                    screenshotPath + advancedSettingsFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // file menu screen shot
        backupMainPanelCardLayout.show(
                backupMainPanel, "sessionStatisticsPanel");
        JMenuOperator fileMenuOperator = new JMenuOperator(
                frameOperator, new NameComponentChooser("fileMenu"));
        fileMenuOperator.setSelected(true);
        fileMenuOperator.setPopupMenuVisible(true);
        JPopupMenu popupMenu = fileMenuOperator.getPopupMenu();
        Rectangle frameRectangle = frameOperator.getBounds();
        Point menuLocation = popupMenu.getLocationOnScreen();
        Rectangle menuRectangle = popupMenu.getBounds();
        x = frameRectangle.x;
        y = frameRectangle.y;
        width = menuLocation.x + menuRectangle.width + inset
                - frameRectangle.x;
        height = menuLocation.y + menuRectangle.height + inset
                - frameRectangle.y;
        // this does not work on Mac OS X...
        if ((CurrentOperatingSystem.OS != OperatingSystem.Mac_OS_X)
                && (fileMenuFileName != null)) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(new Rectangle(x, y, width, height),
                    screenshotPath + fileMenuFileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // program settings screenshots
        String logFilePrefix = null;
        if (CurrentOperatingSystem.OS == OperatingSystem.Windows) {
            logFilePrefix = windowsTempDir;
        } else {
            logFilePrefix = System.getProperty("java.io.tmpdir");
        }

        final PreferencesDialog preferencesDialog = new PreferencesDialog(null,
                logFilePrefix + "jbackpack_" + remoteUserName + ".0",
                LogLevel.INFO, true);
        new Thread() {

            @Override
            public void run() {
                preferencesDialog.setVisible(true);
            }
        }.start();
        JDialogOperator dialogOperator = new JDialogOperator();
        if (preferences1FileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(dialogOperator.getBounds(),
                    screenshotPath + preferences1FileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }
        JListOperator menuListOperator = new JListOperator(
                dialogOperator, new NameComponentChooser("menuList"));
        menuListOperator.setSelectedIndex(1);
        JCheckBoxOperator plainBackupWarningCheckBoxOperator =
                new JCheckBoxOperator(dialogOperator,
                new NameComponentChooser("plainBackupWarningCheckBox"));
        if (CurrentOperatingSystem.OS == OperatingSystem.Windows) {
            plainBackupWarningCheckBoxOperator.setEnabled(true);
            plainBackupWarningCheckBoxOperator.setForeground(
                    UIManager.getColor("Button.enabledForeground"));
        }
        if (preferences2FileName != null) {
            Thread.sleep(1000);
            PNGEncoder.captureScreen(dialogOperator.getBounds(),
                    screenshotPath + preferences2FileName + ".png",
                    PNGEncoder.COLOR_MODE);
            Thread.sleep(1000);
        }

        // finish
        frameOperator.setVisible(false);
    }
}
