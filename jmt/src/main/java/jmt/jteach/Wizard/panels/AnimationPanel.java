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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.jhlabs.image.EmbossFilter;

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
import jmt.jteach.animation.Policy;
import jmt.jteach.animation.QueuePolicyNonPreemptive;
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
    private JPanel mainPanel;
    private JEditorPane descrLabel;
    private JComboBox<String> algorithmJComboBox = null;
    private JPanel animationPanel;
    private AnimationClass animation;
    private HoverHelp help;

    //------------ variables for parameters JPanel ---------------
    private JPanel parametersPanel;
    private int spaceBetweenPanels = 5;
    private int heightPanels = 20;

    //all the Actions of this panel
    private AbstractTCHAction exit;
    private AbstractTCHAction start;
    private AbstractTCHAction pause;
    private AbstractTCHAction reload;
    private AbstractTCHAction openHelp;
    private AbstractTCHAction about;

    //properties of the animation
    private Policy policy = null;
    private QueuePolicyNonPreemptive[] nonPreemptivePolicies; //array of all possible nonPreemptive policies
    private QueuePolicyNonPreemptive queuePolicyNonPreemptive = null;
    
    private RoutingPolicy routingPolicy = null;


    //Action associated to the button Create
    protected AbstractAction CREATE = new AbstractAction("Create") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, "Create a new policy with the parameters chosen");
		}

		public void actionPerformed(ActionEvent e) {
			updateAnimationPanel();
		}
	};

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

    /**
     * To create a new AnimationPanel, call this constructor only if Policy = PREEMPTIVE or NON_PREEMPTIVE
     * @param main the main wizard
     * @param policy the type of policy
     */
    public AnimationPanel(MainWizard main, Policy policy){
        this(main);
        this.policy = policy;
        this.queuePolicyNonPreemptive = QueuePolicyNonPreemptive.FIFO; //set this equal to FIFO by default
        this.routingPolicy = null;
        start.setEnabled(false); //cannot start a simulation without setting the parameters
        initGUI();
    }

    /**
     * To create a new AnimationPanel, call this constructor for routing policies
     * @param main the main wizard
     * @param policy the type of policy
     */
    public AnimationPanel(MainWizard main, RoutingPolicy routingpolicy){
        this(main);
        this.routingPolicy = routingpolicy;
        this.policy = Policy.ROUTING;
        initGUI();
    }

    public void initGUI(){
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
        mainPanel = new JPanel();
        if(policy == Policy.NON_PREEMPTIVE){
            mainPanel.setBorder(new TitledBorder(new EtchedBorder(), policy.toString() + " Scheduling"));
        }
        else if(policy == Policy.PREEMPTIVE){

        }
        else{
            mainPanel.setBorder(new TitledBorder(new EtchedBorder(), policy.toString() + "-" + routingPolicy.toString()));
        }
        
        mainPanel.setLayout(new BorderLayout());

        //divide the main panels in three columns
        JPanel leftPanel = new JPanel();
        leftPanel.setMaximumSize(new Dimension(275, mainPanel.getHeight()));
        leftPanel.setPreferredSize(new Dimension(275, mainPanel.getHeight()));
        mainPanel.add(leftPanel, BorderLayout.WEST);
       
        JPanel rightPanel = new JPanel();
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        JPanel paddingPanel = new JPanel(); //this one is used only to create some padding on the left part
        paddingPanel.setMaximumSize(new Dimension(10, mainPanel.getHeight()));
        paddingPanel.setPreferredSize(new Dimension(10, mainPanel.getHeight()));
        mainPanel.add(paddingPanel, BorderLayout.EAST);
        

        //------------------LEFT PART
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
        leftPanel.setLayout(new BorderLayout());

        //description of the policy
        descrLabel = new JEditorPane();
        descrLabel.setContentType("text/html");
        if(policy == Policy.ROUTING){
            descrLabel.setText("<html><p style='text-align:justify;'>"+routingPolicy.getDescription()+"</p></html>");
        }
        else{
            descrLabel.setText("<html><p style='text-align:justify;'> No description, set parameters first</p></html>");
        }
        descrLabel.setEditable(false);
        descrLabel.setPreferredSize(leftPanel.getSize());
        descrLabel.setBackground(null);
        leftPanel.add(descrLabel, BorderLayout.CENTER);

        //paramters panel
        createParameters(leftPanel);;
        leftPanel.add(parametersPanel, BorderLayout.SOUTH);

        //------------------RIGHT PART
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0)); //handle padding correctly, since it seems to move all the objects of the animation in one direction
        rightPanel.setLayout(new BorderLayout());

        animationPanel = new JPanel(new BorderLayout());
        //based on the type of Policy passed in the constructor, create a new Animation
        if(policy == Policy.NON_PREEMPTIVE){
            animation = new SingleQueueNetAnimation(animationPanel, QueuePolicyNonPreemptive.FIFO);
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
     * Method called by the Create button to update this panel.
     * It differenciates the changes based on the type of policy, three things have to be updated, the title, the description, the animation 
     */
    private void updateAnimationPanel(){
        animation.pause();
        reloadAnimation();
        start.setEnabled(true);

        if(policy == Policy.NON_PREEMPTIVE){
            queuePolicyNonPreemptive = nonPreemptivePolicies[algorithmJComboBox.getSelectedIndex()]; //the index of the JComboBox is the same of the array of queue policies Non preemptive
            mainPanel.setBorder(new TitledBorder(new EtchedBorder(), policy.toString() + " Scheduling - " + queuePolicyNonPreemptive.toString()));
            descrLabel.setText("<html><p style='text-align:justify;'>"+queuePolicyNonPreemptive.getDescription()+"</p></html>");
            animation.update(queuePolicyNonPreemptive);
        }
        else if(policy == Policy.PREEMPTIVE){

        }
        else{ //in case of routing only the animation must be updated
            animation = new MultipleQueueNetAnimation(animationPanel, routingPolicy);
        }
    }


    /**
     * Update the JPanel of the paramters in the left panel of this window
     */
    private void createParameters(JPanel container){
        parametersPanel = new JPanel();
        parametersPanel.setBorder(new TitledBorder(new EtchedBorder(), "Parameters"));
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));

        EmptyBorder paddingBorder = new EmptyBorder(0, 10, 0, 10); //padding right and left for all the panels inside the JPanel (top and bottom = 0 otherwise it does not show other panels)
        
        //algorithm Panel (this one is displayed only for Scheduling Policies)
        if(policy == Policy.NON_PREEMPTIVE || policy == Policy.PREEMPTIVE){
            JPanel algorithmPanel = createPanel(paddingBorder, false, spaceBetweenPanels, ConstantsJTch.HELP_PARAMETERS_PANELS[0]);
            algorithmPanel.setLayout(new GridLayout(1,2));
            algorithmPanel.add(new JLabel("Algorithm :"));

            if(policy == Policy.NON_PREEMPTIVE){
                nonPreemptivePolicies = QueuePolicyNonPreemptive.values();
                String[] options = new String[nonPreemptivePolicies.length];
                for (int i = 0; i < options.length; i++) {
                    options[i] = nonPreemptivePolicies[i].toString();
                }
                algorithmJComboBox = new JComboBox<String>(options);
                algorithmPanel.add(algorithmJComboBox);
            }
            
            parametersPanel.add(algorithmPanel);
        }

        //N servers panel (this one is displayed only for Scheduling Policies)
        if(policy == Policy.NON_PREEMPTIVE || policy == Policy.PREEMPTIVE){
            JPanel nserversPanel = createPanel(paddingBorder, true, spaceBetweenPanels, ConstantsJTch.HELP_PARAMETERS_PANELS[1]);
            nserversPanel.setLayout(new GridLayout(1,2));
            nserversPanel.add(new JLabel("N.servers:"));
            SpinnerNumberModel model = new SpinnerNumberModel(1,1,2,1);
            JSpinner serversSpinner = new JSpinner(model);
            nserversPanel.add(serversSpinner);
        }

        //Inter arrival time panel
        JPanel interAPanel = createPanel(paddingBorder, true, spaceBetweenPanels, ConstantsJTch.HELP_PARAMETERS_PANELS[2]);
        interAPanel.setLayout(new GridLayout(1,2));
        interAPanel.add(new JLabel("Inter Arrival:"));
        interAPanel.add(new JComboBox<String>());
        parametersPanel.add(interAPanel);

        //Service Time panel
        JPanel serviceTPanel = createPanel(paddingBorder, true, spaceBetweenPanels, ConstantsJTch.HELP_PARAMETERS_PANELS[3]);
        serviceTPanel.setLayout(new GridLayout(1,2));
        serviceTPanel.add(new JLabel("Service Time:"));
        serviceTPanel.add(new JComboBox<String>());
        parametersPanel.add(serviceTPanel);

        //create button
        paddingBorder = new EmptyBorder(0,80,0,80);
        JPanel createPanel = createPanel(paddingBorder, true, spaceBetweenPanels*2, null);
        createPanel.setLayout(new BorderLayout());
        JButton createButton = new JButton(CREATE);
        help.addHelp(createButton, ConstantsJTch.HELP_PARAMETERS_PANELS[4]);
        createPanel.add(createButton, BorderLayout.CENTER);
        parametersPanel.add(Box.createVerticalStrut(spaceBetweenPanels));
    }

    /**
     * Create a new JPanel inside the ParamtersPanel
     * @param paddingBorder border left and right for all the panels
     * @param padding boolean if it is needed to add a padding over the panel (the first panel does not need the padding)
     * @param space how much padding with the upper panel
     * @param helpText the string to be displayed on the bottom of the JDialog
     * @return a new Panel
     */
    private JPanel createPanel(EmptyBorder paddingBorder, boolean padding, int space, String helpText) {
        if(padding){
            parametersPanel.add(Box.createVerticalStrut(space));
        }
        JPanel p = new JPanel();  
        p.setBorder(paddingBorder);
        parametersPanel.add(p);
        help.addHelp(p, helpText);
        return p;
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

    //----------------- toolbar buttons actions
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
