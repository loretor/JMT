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

import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;

import jmt.framework.gui.components.JMTMenuBar;
import jmt.framework.gui.components.JMTToolBar;
import jmt.framework.gui.help.HoverHelp;
import jmt.framework.gui.listeners.MenuAction;
import jmt.framework.gui.wizard.WizardPanel;
import jmt.gui.common.JMTImageLoader;
import jmt.jteach.Wizard.MainWizard;
import jmt.jteach.Wizard.WizardPanelTCH;
import jmt.jteach.actions.AbstractTeachAction;
import jmt.jteach.actionsWizard.*;
import jmt.jteach.panels.SelectPanel;

/**
 * One of the panels for the JTeach Models. It is the panel for the scheduling techniques
 *
 * @author Lorenzo Torri
 * Date: 30-mar-2024
 * Time: 14.40
 */
public class SchedulingPanel extends WizardPanel implements WizardPanelTCH{
    private static final String PANEL_NAME = "Scheduling";

    private MainWizard parent;
    private HoverHelp help;

    //all the Actions of this panel
    private AbstractTCHAction exit;
    private AbstractTCHAction start;
    private AbstractTCHAction doublevelocity;
    private AbstractTCHAction pause;
    private AbstractTCHAction stop;
    private AbstractTCHAction openHelp;
    private AbstractTCHAction about;

    public SchedulingPanel(MainWizard main){
        this.parent = main;
        help = parent.getHoverHelp();

        //define all the AbstractTeachAction
        exit = new Exit(this);
        start = new StartSimulation(this);
        doublevelocity = new DoubleSimulationVelocity(this);
        pause = new PauseSimulation(this);
        stop = new StopSimulation(this);
        openHelp = new Help(this);
        about = new About(this);

        initGUI();
    }

    public void initGUI(){

        createMenu();
        createToolbar();
    }

    /**
	 * Update the menuBar for the Scheduling Window
	 */
	protected void createMenu() {
		JMTMenuBar menu = new JMTMenuBar(JMTImageLoader.getImageLoader());

        //File window
        MenuAction action = new MenuAction("File", new AbstractTCHAction[] { null, exit});
		menu.addMenu(action);

        //Solve window
		action = new MenuAction("Solve", new AbstractTCHAction[] {start, doublevelocity, pause, stop, null});
		menu.addMenu(action);

        //Help window
        action = new MenuAction("Help", new AbstractTCHAction[] {openHelp, null, about});
		menu.addMenu(action);

		parent.setMenuBar(menu);
	}

    /**
	 * Update the toolbar for the Scheduling Window
	 */
    protected void createToolbar() {
        JMTToolBar toolbar = new JMTToolBar(JMTImageLoader.getImageLoader());	

        //first add all the icons with their actions
        AbstractTCHAction[] actions = new AbstractTCHAction[] {start, doublevelocity, pause, stop, null, openHelp}; // Builds an array with all actions to be put in the toolbar
		String[] helpText = {"Start the simulation","Double the velocity of the simulation (perform this action only if there is an active simulation)",
        "Pause the simulation (perform this action only if there is an active simulation)","Stop the simulation (perform this action only if there is an active simulation)", 
        "Open the help page"};
        toolbar.populateToolbar(actions);
        ArrayList<AbstractButton> buttons = new ArrayList<AbstractButton>(); //create a list of AbstractButtons for the helpLabel
		buttons.addAll(toolbar.populateToolbar(actions));

        //add help for each Action/JComboBox with helpLabel
		for (int i = 0; i < buttons.size(); i++) {
			AbstractButton button = buttons.get(i);
			help.addHelp(button, helpText[i]);
		}
		  
		parent.setToolBar(toolbar);
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

    @Override
    public String getName() {
        return PANEL_NAME;
    }


    @Override
    public void openHelp() {
        //TODO
    }

}
