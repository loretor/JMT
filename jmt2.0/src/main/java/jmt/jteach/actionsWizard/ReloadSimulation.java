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

package jmt.jteach.actionsWizard;

import java.awt.event.ActionEvent;

import jmt.jteach.Wizard.WizardPanelTCH;

/**
 * @author Lorenzo Torri
 * Date: 30-mar-2024
 * Time: 15.20
 */
public class ReloadSimulation extends AbstractTCHAction {

    private static final long serialVersionUID = 1L;

    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public ReloadSimulation(WizardPanelTCH panel) {
        super("Reload Simulation", "RotateLeft", panel);
        putValue(SHORT_DESCRIPTION, "Reload simulation");
        //putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        panel.reloadAnimation();
    }

}
   
 
