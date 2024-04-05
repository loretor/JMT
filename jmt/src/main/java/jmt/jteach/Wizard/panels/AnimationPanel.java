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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Queue;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import jmt.framework.gui.components.JMTMenuBar;
import jmt.framework.gui.components.JMTToolBar;
import jmt.framework.gui.help.HoverHelp;
import jmt.framework.gui.listeners.MenuAction;
import jmt.framework.gui.wizard.WizardPanel;
import jmt.gui.common.JMTImageLoader;

import jmt.jteach.ConstantsJTch;
import jmt.jteach.Wizard.MainWizard;
import jmt.jteach.Wizard.WizardPanelTCH;
import jmt.jteach.actionsWizard.*;
import jmt.jteach.animation.AnimationClass;
import jmt.jteach.animation.MultipleQueueNetAnimation;
import jmt.jteach.animation.QueuePolicy;
import jmt.jteach.animation.RoutingPolicy;
import jmt.jteach.animation.SingleQueueNetAnimation;

/**
 * One of the panels for the JTeach Models. It is the panel for the scheduling techniques
 *
 * @author Lorenzo Torri
 * Date: 30-mar-2024
 * Time: 14.40
 */
public class AnimationPanel extends WizardPanel implements WizardPanelTCH{
    private static final String PANEL_NAME = "Scheduling";

    private MainWizard parent;
    private AnimationClass animation;
    private HoverHelp help;

    //all the Actions of this panel
    private AbstractTCHAction exit;
    private AbstractTCHAction start;
    private AbstractTCHAction pause;
    private AbstractTCHAction reload;
    private AbstractTCHAction openHelp;
    private AbstractTCHAction about;

    //properties of the animation
    private QueuePolicy queuePolicy = null;
    private RoutingPolicy routingPolicy = null;

    private AnimationPanel(MainWizard main){
        this.parent = main;
        help = parent.getHoverHelp();

        //define all the AbstractTeachAction
        exit = new Exit(this);
        start = new StartSimulation(this);
        pause = new PauseSimulation(this);
        reload = new ReloadSimulation(this);
        openHelp = new Help(this);
        about = new About(this);

        pause.setEnabled(false);
        reload.setEnabled(false);
    }

    public AnimationPanel(MainWizard main, QueuePolicy queuepolicy){
        this(main);
        this.queuePolicy = queuepolicy;
        this.routingPolicy = null;
        initGUI();
    }

    public AnimationPanel(MainWizard main, RoutingPolicy routingpolicy){
        this(main);
        this.routingPolicy = routingpolicy;
        this.queuePolicy = null;
        initGUI();
    }

    public void initGUI(){
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
        JPanel mainPanel = new JPanel();
        if(queuePolicy != null){
            mainPanel.setBorder(new TitledBorder(new EtchedBorder(), queuePolicy.toString()));
        }
        else{
            mainPanel.setBorder(new TitledBorder(new EtchedBorder(), routingPolicy.toString()));
        }
        
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        //divide the main panels in two columns
        JPanel leftPanel = new JPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.30; //description panel occupies only 30% of the horizontal component of mainPanel
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(leftPanel, gbc);
       
        JPanel rightPanel = new JPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.70;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(rightPanel, gbc);

        //add all the things on the left part
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
        leftPanel.setLayout(new BorderLayout());
        JEditorPane descrLabel;
        if(queuePolicy != null){
            descrLabel = new JEditorPane("text/html", "<html><p style='text-align:justify;'>"+queuePolicy.getDescription()+"</p></html>");
        }
        else{
            descrLabel = new JEditorPane("text/html", "<html><p style='text-align:justify;'>"+routingPolicy.getDescription()+"</p></html>");
        }
        
        descrLabel.setEditable(false);
        descrLabel.setPreferredSize(leftPanel.getSize());
        descrLabel.setBackground(null);
        leftPanel.add(descrLabel, BorderLayout.CENTER);

        //add all the things on the right part
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0)); //handle padding correctly, since it seems to move all the objects of the animation in one direction
        rightPanel.setLayout(new BorderLayout());

        JPanel animationPanel = new JPanel(new BorderLayout());
        //based on the type of Policy passed in the constructor, create a new Animation
        if(queuePolicy != null){
            animation = new SingleQueueNetAnimation(animationPanel, queuePolicy);
        }
        else{
            animation = new MultipleQueueNetAnimation(animationPanel, routingPolicy);
        }
        animationPanel.add(animation, BorderLayout.CENTER);
        animationPanel.setBackground(Color.WHITE);
        rightPanel.add(animationPanel, BorderLayout.CENTER);

        this.add(mainPanel, BorderLayout.CENTER);
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
		action = new MenuAction("Solve", new AbstractTCHAction[] {start, pause, reload, null});
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
        AbstractTCHAction[] actions = new AbstractTCHAction[] {start, pause, reload, null, openHelp}; // Builds an array with all actions to be put in the toolbar	
        toolbar.populateToolbar(actions);
        ArrayList<AbstractButton> buttons = new ArrayList<AbstractButton>(); //create a list of AbstractButtons for the helpLabel
		buttons.addAll(toolbar.populateToolbar(actions));

        //add help for each Action/JComboBox with helpLabel
		for (int i = 0; i < buttons.size(); i++) {
			AbstractButton button = buttons.get(i);
			help.addHelp(button, ConstantsJTch.HELP_BUTTONS_ANIMATIONS[i]);
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

    @Override
    public void startAnimation() {
        animation.start();
        start.setEnabled(false);
        pause.setEnabled(true);
        reload.setEnabled(false);      
    }

    @Override
    public void pauseAnimation() {
        animation.pause();
        start.setEnabled(true);
        pause.setEnabled(false);
        reload.setEnabled(true);
    }

    @Override
    public void reloadAnimation() {
        animation.reload();
        start.setEnabled(true);
        pause.setEnabled(false);
        reload.setEnabled(false);
    }

}
