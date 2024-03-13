/**
 * Copyright (C) 2016, Laboratorio di Valutazione delle Prestazioni - Politecnico di Milano

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package jmt.jteach.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import jmt.jteach.MediatorTeach;

/**
 * @author Lorenzo Torri
 * Date: 11-mar-2024
 * Time: 17.10
*/
public class StartSimulation extends AbstractTeachAction {

    private static final long serialVersionUID = 1L;

    /**
     * Defines an <code>Action</code> object with a default
    * description string and default icon.
    */
    public StartSimulation(MediatorTeach mediator) {
        super("Start Simulation", "Sim", mediator);
        putValue(SHORT_DESCRIPTION, "Start simulation");
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
        //setEnabled(false);
    }

    /**
     * Invoked when an action occurs.
    */
    public void actionPerformed(ActionEvent e) {
        mediator.startSimulation();
    }

}

