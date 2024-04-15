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
package jmt.jteach.Wizard.panels;

import javax.swing.JOptionPane;

import jmt.framework.gui.wizard.WizardPanel;
import jmt.jteach.Wizard.MMQueues;
import jmt.jteach.Wizard.MainWizard;

/**
 * Panel of the main Wizard for the Markov chains
 *
 * @author Lorenzo Torri
 * Date: 15-apr-2024
 * Time: 14.29
 */
public class MMQueuesPanel extends WizardPanel{
    private MainWizard parent;
    private String selectedMethod;

    public MMQueuesPanel(MainWizard main, String selectedMethod){
        this.parent = main;
        this.selectedMethod = selectedMethod;
        
        MMQueues mq = new MMQueues(main, this, selectedMethod);
        this.add(mq);
    }

    @Override
    public String getName() {
        String name;
        if (selectedMethod == "mm1") {
			name = "M/M/1";
		} else if (selectedMethod == "mm1k") {
			name = "M/M/1/k";
		} else if (selectedMethod == "mmn Finite Capacity Station") {
			name = "M/M/n";
		} else{
			name = "M/M/n/k";
		}
        return name;
    }

    @Override
    public boolean canGoBack() {
		if (JOptionPane.showConfirmDialog(this, "Are you sure you want to go back to start screen?", "Back operation", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
			return false;
		}		
		parent.resetScreen(); //remove this panel and go back to the intitial MainPanel
		return true;
	}

}
