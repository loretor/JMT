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
package jmt.jteach.Wizard;


import jmt.gui.common.CommonConstants;
import jmt.gui.common.JMTImageLoader;
import jmt.jteach.Wizard.panels.MainPanel;
import jmt.jteach.Wizard.panels.ResultsPanel;
import jmt.jteach.animation.Policy;
import jmt.jteach.animation.QueuePolicyNonPreemptive;
import jmt.jteach.animation.RoutingPolicy;
import jmt.jteach.Wizard.panels.AnimationPanel;
import jmt.jteach.Wizard.panels.MMQueuesPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import jmt.framework.gui.help.HoverHelp;
import jmt.framework.gui.wizard.WizardPanel;


/**
 * Main Wizard that contains all the panels for JTeach Models and Markov chains
 *
 * @author Lorenzo Torri
 * Date: 29-mar-2024
 * Time: 15.04
 */
public class MainWizard extends JTchWizard{

    //general variables for the JMTFrame
    private String IMG_JWATICON = "JMCHIcon";
    private static final String TITLE = "JTCH";
	private static final String TITLE_QUEUEING = "Queueing Network";
	private static final String TITLE_SCHEDULING = "Scheduling";
	private static final String TITLE_ROUTING = "Routing";

    //components of the panel
    private JPanel menus;
    private MainPanel mainPanel;
    private HoverHelp help;

	//list of panels after the MainPanel
	private List<WizardPanel> panelCollection = new ArrayList<>();
	
    
	public MainWizard() {
        this.setIconImage(JMTImageLoader.loadImage(IMG_JWATICON).getImage());
		this.setTitle(TITLE);
		this.setSize(CommonConstants.MAX_GUI_WIDTH_JWAT, CommonConstants.MAX_GUI_HEIGHT_JWAT);
		//this.setResizable(false);

        centerWindow();

		initGUI();
	}

    /**
	 * Initializes JTCH start screen GUI
	 */
	private void initGUI() {        
        menus = new JPanel(new BorderLayout());
        menus.setBackground(Color.BLUE);
		//help = this.getHelp();
		getContentPane().add(menus, BorderLayout.NORTH);
		mainPanel = new MainPanel(this);
		this.addPanel(mainPanel);
		setEnableButton("Solve", false);
	}

    public static void main(String[] args) {
		new MainWizard().setVisible(true);
	}

    
	/**
	 * Method to create a new AnimationPanel for Non Preemptive Scheduling
	 * @param algorithm the type of Non Preemptive algorithm
	 */
	public void setAnimationPanelEnv(QueuePolicyNonPreemptive algorithm){
		this.setTitle(TITLE + " - "+ TITLE_QUEUEING + ", "+TITLE_SCHEDULING);

		WizardPanel p = new AnimationPanel(this, Policy.NON_PREEMPTIVE, algorithm);
		this.addPanel(p);
		panelCollection.add(p);

		p = new ResultsPanel();
		this.addPanel(p);
		panelCollection.add(p);

		this.showNext();
	}

	/**
	 * Same method as before, but with a Routing policy
	 * @param policy the type of Routing Policy
	 */
	public void setAnimationPanelEnv(RoutingPolicy policy){
		this.setTitle(TITLE + " - "+ TITLE_QUEUEING + ", "+ TITLE_ROUTING);

		WizardPanel p = new AnimationPanel(this, policy);
		this.addPanel(p);
		panelCollection.add(p);

		//TODO: add here the results panel
		
		this.showNext();
	}

	/**
	 * Method to create a new MMQueuesPanel
	 */
	public void setMMQueuesPanelEnv(String selectedMethod) {
		WizardPanel p = new MMQueuesPanel(this, selectedMethod);
		String title;
		if (selectedMethod == "mm1") {
			title = "Markov Chain M/M/1 Station";
		} else if (selectedMethod == "mm1k") {
			title = "Markov Chain M/M/1/k";
		} else if (selectedMethod == "mmn Finite Capacity Station") {
			title = "Markov Chain M/M/n Station";
		} else{
			title = "Markov Chain M/M/n/k Finite Capacity Station";
		}
		this.setTitle(TITLE + " - "+ title);
		this.addPanel(p);
		panelCollection.add(p);

		this.showNext();
	}


    /**
     * To change dinamically the type of ToolBar.
     * Since different panels have different ToolBars, each panel creates its own toolbar and then it is setted correctly here
     * @param bar new JToolBar
     */
    public void setToolBar(JToolBar bar) {
		if (toolBar != null) {
			menus.remove(toolBar);
		}
		menus.add(bar, BorderLayout.SOUTH);
		toolBar = bar;
	}

    /**
     * To change dinamically the type of MenuBar.
     * Since different panels have different Menus, each panel creates its own menubar and then it is setted correctly here
     * @param bar new JTMMenuBar
     */
	public void setMenuBar(JMenuBar bar) {
		if (menuBar != null) {
			menus.remove(menuBar);
		}
		menus.add(bar, BorderLayout.NORTH);
		menuBar = bar;
	}

	/**
	 * To remove all the panels and to go back to the MainPanel.
	 * It is called by all the panels in the CanGoBack()
	 */
	public void resetScreen() {
		for (int i = 0; i < panelCollection.size(); i++) {
			tabbedPane.remove(panelCollection.get(i));
		}

		this.setTitle(TITLE);
		mainPanel.createMenu(); //update the menu and toolbar
		mainPanel.createToolBar();
		this.validate();
	}

	
}
