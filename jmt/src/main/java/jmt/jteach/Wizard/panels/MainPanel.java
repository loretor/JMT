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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import jmt.framework.gui.components.JMTMenuBar;
import jmt.framework.gui.components.JMTToolBar;
import jmt.framework.gui.help.HoverHelp;
import jmt.framework.gui.listeners.MenuAction;
import jmt.framework.gui.wizard.WizardPanel;
import jmt.gui.common.JMTImageLoader;
import jmt.jteach.Wizard.MainWizard;
import jmt.jteach.Wizard.WizardPanelTCH;
import jmt.jteach.actionsWizard.AbstractTCHAction;
import jmt.jteach.actionsWizard.Help;

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
    private static final int BUTTONSIZE = 15;

    private MainWizard parent;
	private JMTMenuBar menu;
	private JMTToolBar toolbar;
    private HoverHelp help; //retrieve from parent the HoverHelp

	//all actions associated to the buttons of the Menu and ToolBar
    private AbstractTCHAction openHelp;


	//all the AbstractActions associated to the buttons related of this panel only
	protected AbstractAction FIFO = new AbstractAction("FIFO") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, "First In First Out Scheduling Policy");
		}

		public void actionPerformed(ActionEvent e) {
			parent.setSchedulingEnv();
		}
	};

	//array of all the AbstractActions in this panel
	private AbstractAction[] actions = {FIFO};

    public MainPanel(MainWizard main){
        this.parent = main;
		help = parent.getHoverHelp();

        openHelp = new Help(this);

        initGUI();
    }

    public void initGUI(){
		this.setLayout(new BorderLayout());
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

        //panel with all the buttons
		JPanel eastPanel = new JPanel(new FlowLayout());
		eastPanel.add(Box.createVerticalStrut(5), BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel(new GridLayout(12, 1, 2, 15));
		eastPanel.add(buttonPanel, BorderLayout.CENTER);
		/* TODO
		for (int i = 0; i < 12; i++) {
			buttonPanel.add(createButton());
		} */
		buttonPanel.add(createButton(actions[0], "Open a panel with a First In First Out scheduling simulation"));

        //flow chart schema
		JLabel imageLabel = new JLabel();
		imageLabel.setBorder(BorderFactory.createEmptyBorder(BUTTONSIZE, 1, 0, 0));
		imageLabel.setIcon(JMTImageLoader.loadImage(IMG_STARTSCREEN, new Dimension(475, 500)));
		imageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		imageLabel.setVerticalAlignment(SwingConstants.NORTH);


		this.add(imageLabel, BorderLayout.CENTER);
		this.add(eastPanel, BorderLayout.EAST);
		
        createMenu();
        createToolBar();
    }

    private JButton createButton(AbstractAction action, String helpString) {
		JButton button = new JButton(action);
		button.setPreferredSize(new Dimension((int) (BUTTONSIZE * 6), (int) (BUTTONSIZE * 2)));
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
}
