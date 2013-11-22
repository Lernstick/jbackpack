/**
 * ModalDialogHandler.java
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
 * Created on 30.08.2010, 08:57:49
 */
package ch.fhnw.util;

import java.awt.EventQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import javax.swing.JDialog;

/**
 * A tool class for handling concurrent showing/hiding of modal dialogs
 *
 * @author Ronny Standtke <Ronny.Standtke@gmx.net>
 */
public class ModalDialogHandler {

    private final static Logger LOGGER
            = Logger.getLogger(ModalDialogHandler.class.getName());
    private final JDialog dialog;
    private final Lock showingLock;
    // if the dialog still must be shown when show() is called
    private boolean showDialog;
    // if the dialog must be closed when hide() is called (i.e. if the dialog 
    // was shown before at all)
    private boolean closeDialog;

    /**
     * creates a new ModalDialogHandler
     *
     * @param dialog the dialog to show/hide
     */
    public ModalDialogHandler(JDialog dialog) {
        this.dialog = dialog;
        showDialog = true;
        closeDialog = false;
        showingLock = new ReentrantLock();
    }

    /**
     * tries to show the dialog, if hide() was not called in between
     */
    public void show() {
        showingLock.lock();
        try {
            if (showDialog) {
                LOGGER.fine("hide() was NOT called in between"
                        + " -> adding setVisible call to EventQueue...");
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        showingLock.lock();
                        if (showDialog) {
                            LOGGER.fine("hide() was NOT called in between"
                                    + " -> calling setVisible(true)");
                            closeDialog = true;
                            showingLock.unlock();
                            dialog.setVisible(true);
                        } else {
                            LOGGER.fine("hide() was called in between"
                                    + " -> skipping the call to setVisible(true)");
                            showingLock.unlock();
                        }
                    }
                });
            } else {
                LOGGER.fine("hide() was called in between -> nothing left to do...");
            }
        } finally {
            showingLock.unlock();
        }
    }

    /**
     * tries to hide the dialog if show() was called in between
     */
    public void hide() {
        showingLock.lock();
        try {
            showDialog = false;
            if (closeDialog) {
                LOGGER.fine("call to setVisible(true) was already added to the "
                        + "EventQueue -> adding setVisible(false) call...");
                EventQueue.invokeLater(new Runnable() {
                    
                    public void run() {
                        dialog.setVisible(false);
                        dialog.dispose();
                    }
                });
            }
        } finally {
            showingLock.unlock();
        }
    }
}
