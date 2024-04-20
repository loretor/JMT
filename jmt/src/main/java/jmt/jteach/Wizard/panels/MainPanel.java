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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jmt.common.exception.IncorrectDistributionParameterException;
import jmt.engine.random.Distribution;
import jmt.engine.random.Exponential;
import jmt.engine.random.ExponentialPar;
import jmt.engine.random.Parameter;
import jmt.engine.random.engine.MersenneTwister;
import jmt.engine.random.engine.RandomEngine;
import jmt.framework.gui.components.JMTMenuBar;
import jmt.framework.gui.components.JMTToolBar;
import jmt.framework.gui.help.HoverHelp;
import jmt.framework.gui.listeners.MenuAction;
import jmt.framework.gui.wizard.WizardPanel;
import jmt.gui.common.JMTImageLoader;
import jmt.jmarkov.MMQueues;
import jmt.jteach.ConstantsJTch;
import jmt.jteach.Wizard.MainWizard;
import jmt.jteach.Wizard.WizardPanelTCH;
import jmt.jteach.actionsWizard.AbstractTCHAction;
import jmt.jteach.actionsWizard.Help;
import jmt.jteach.animation.Policy;
import jmt.jteach.animation.QueuePolicyNonPreemptive;
import jmt.jteach.animation.RoutingPolicy;

/**
 * Main Panel of the MainWizard. This panel offers the possibility of choosing between a JTeach Model or Markov chain.
 *
 * @author Lorenzo Torri
 * Date: 29-mar-2024
 * Time: 15.04
 */
public class MainPanel extends WizardPanel implements WizardPanelTCH{

    private static final String PANEL_NAME = "Main Panel";
    private static final String IMG_STARTSCREEN = "StartScreenJTeach";
    private static final int BUTTONSIZE_X = 15;
	private static final int BUTTONSIZE_Y = 12;

    private MainWizard parent;
	private JMTMenuBar menu;
	private JMTToolBar toolbar;
    private HoverHelp help; //retrieve from parent the HoverHelp

	//all actions associated to the buttons of the Menu and ToolBar
    private AbstractTCHAction openHelp;

	private String toolTipPreemptive = "<html><ul>"
	+ "<li>FCFS</li>"
	+ "<li>LCFS</li>"
	+ "<li>PRIORITY</li>"
	+ "<li>SJF</li>"
	+ "<li>LJF</li>"
	+ "</ul></html>";


	//all the AbstractActions associated to the buttons related of this panel only
	protected AbstractAction PREEMPTIVE = new AbstractAction("PREEMPTIVE") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, toolTipPreemptive);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(Policy.PREEMPTIVE);
		}
	};

	protected AbstractAction NON_PREEMPTIVE = new AbstractAction("NON PREEMPTIVE") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, toolTipPreemptive);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(Policy.NON_PREEMPTIVE);
		}
	};

	protected AbstractAction PROBABILISTIC = new AbstractAction("PROBABILISTIC") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, "Probabilistic Routing Policy");
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(RoutingPolicy.PROBABILISTIC);
		}
	};

	protected AbstractAction OTHER = new AbstractAction("...") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, "Other routing policies");
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(RoutingPolicy.JSQ);
		}
	};

	protected AbstractAction RR = new AbstractAction("RR") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, "Round Robin Routing Policy");
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(RoutingPolicy.RR);
		}
	};

	protected AbstractAction MM1 = new AbstractAction("M/M/1") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, "M/M/1 Station, 1 Server");
		}

		public void actionPerformed(ActionEvent e) {
			parent.setMMQueuesPanelEnv("mm1");	
		}
	};

	protected AbstractAction MM1K = new AbstractAction("M/M/1/k") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, "M/M/1/k Finite Capacity Station, 1 Server");
		}

		public void actionPerformed(ActionEvent e) {
			parent.setMMQueuesPanelEnv("mm1k");
		}
	};

	protected AbstractAction MMC = new AbstractAction("M/M/c") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, "M/M/c Station, c Servers");
		}

		public void actionPerformed(ActionEvent e) {
			parent.setMMQueuesPanelEnv("mmn");
		}
	};

	protected AbstractAction MMCK = new AbstractAction("M/M/c/k") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, "M/M/c/k Finite Capaicty Station, c Servers");
		}

		public void actionPerformed(ActionEvent e) {
			parent.setMMQueuesPanelEnv("mmnk");
		}
	};

	//array of all the AbstractActions in this panel
	private AbstractAction[] actions = {NON_PREEMPTIVE, PREEMPTIVE, RR, PROBABILISTIC, OTHER, MM1, MM1K, MMC, MMCK};

    public MainPanel(MainWizard main){
        this.parent = main;
		help = parent.getHoverHelp();

        openHelp = new Help(this,"JTCH");

        initGUI();
    }

    public void initGUI(){
		this.setLayout(new BorderLayout());

		//upper and bottom panels
		JPanel upper = new JPanel(new FlowLayout());
		JLabel upperLabel = new JLabel();
		upperLabel.setPreferredSize(new Dimension(300, 10));
		upper.add(upperLabel);
			
		JPanel bottom = new JPanel(new FlowLayout());
		JLabel bottomLabel = new JLabel();
		bottomLabel.setPreferredSize(new Dimension(300, 10));
		bottom.add(bottomLabel);

		this.add(upper, BorderLayout.NORTH);
		this.add(bottom, BorderLayout.SOUTH);

		//central panel
		JPanel centerPanel = new JPanel(new GridBagLayout());
      
        //flow chart schema
		JLabel imageLabel = new JLabel();
		imageLabel.setIcon(JMTImageLoader.loadImage(IMG_STARTSCREEN, new Dimension(452, 490)));
		centerPanel.add(imageLabel);

		//panel with all the buttons
		JPanel eastPanel = new JPanel(new FlowLayout());
		eastPanel.add(Box.createVerticalStrut(1), BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel(new GridLayout(9, 1, 2, 24));
		eastPanel.add(buttonPanel, BorderLayout.CENTER);

		for (int i = 0; i < actions.length; i++) {
			buttonPanel.add(createButton(actions[i], ConstantsJTch.HELP_BUTTONS_MAINPANEL[i]));
		}

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		centerPanel.add(eastPanel, gbc);

		this.add(centerPanel, BorderLayout.CENTER);
		
        createMenu();
        createToolBar();
    }

    private JButton createButton(AbstractAction action, String helpString) {
		JButton button = new JButton(action);
		button.setPreferredSize(new Dimension((int) (BUTTONSIZE_X * 8), (int) (BUTTONSIZE_Y * 3)));
		help.addHelp(button, helpString);
		return button;
	}


    @Override
    public String getName() {
        return PANEL_NAME;
    }

    /**
	 * Creates a menu to be displayed in the MainWizard
	 */
	public void createMenu() {
		menu = new JMTMenuBar(JMTImageLoader.getImageLoader());

        //File window
        MenuAction action = new MenuAction("File", new AbstractTCHAction[] { null});

        //Help window
        action = new MenuAction("Help", new AbstractTCHAction[] {openHelp, null});
		menu.addMenu(action);

        parent.setMenuBar(menu);
	}

    /**
	 * Creates a toolbar to be displayed in the MainWizard.
	 */
	public void createToolBar() {
        toolbar = new JMTToolBar(JMTImageLoader.getImageLoader());	

        //first add all the icons with their actions
        AbstractTCHAction[] actions = new AbstractTCHAction[] {null, openHelp}; // Builds an array with all actions to be put in the toolbar
		String[] helpText = {"Open the help page"};
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

    //Overrided methods for performing the actions when the buttons of JToolBar or JMenuBar are pressed
    @Override
    public void openHelp() {
        
    }

	@Override
	public void startAnimation() {
	}

	@Override
	public void pauseAnimation() {
	}

	@Override
	public void reloadAnimation() {
	}

	@Override
	public void nextStepAnimation() {

	}

	@Override
	public void exit() {
	}

	@Override
	public void stopAnimation() {
	}    
}
