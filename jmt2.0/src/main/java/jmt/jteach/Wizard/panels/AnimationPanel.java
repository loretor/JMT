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
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import jmt.framework.gui.components.JMTMenuBar;
import jmt.framework.gui.components.JMTToolBar;
import jmt.framework.gui.graph.MeasureValue;
import jmt.framework.gui.help.HoverHelp;
import jmt.framework.gui.listeners.MenuAction;
import jmt.framework.gui.wizard.WizardPanel;
import jmt.gui.common.JMTImageLoader;
import jmt.gui.common.controller.DispatcherThread;
import jmt.gui.common.definitions.GuiInterface;
import jmt.gui.common.definitions.MeasureDefinition;
import jmt.gui.common.definitions.ResultsModel;
import jmt.gui.common.xml.XMLWriter;
import jmt.jteach.ConstantsJTch;
import jmt.jteach.Distributions;
import jmt.jteach.Solver;
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
 * Panel for JTeach models.
 * It is the same class for all types of Queueing Network.
 *
 * @author Lorenzo Torri
 * Date: 30-mar-2024
 * Time: 14.40
 */
public class AnimationPanel extends WizardPanel implements WizardPanelTCH, GuiInterface{
    private static final String PANEL_NAME = "Simulation";

    //------------ components of the panel -----------------------
    private MainWizard parent;
    private JPanel mainPanel;
    private JLabel descrLabel;
    private JComboBox<String> algorithmJComboBox = null;
    private JComboBox<Distributions> interAComboBox;
    private JComboBox<Distributions> serviceComboBox;
    private JSpinner serversSpinner;
    private JSpinner prob1 = null; //those two spinners are instanciated only if Probabilisitic routing is selected
    private JSpinner prob2 = null;
    private JButton createButton;
    private JPanel animationPanel;
    private HoverHelp help;

    JLabel label;

    //------------ variables for parameters JPanel ---------------
    private JPanel parametersPanel;
    private final int spaceBetweenPanels = 5;
    private final Distributions[] distributions = Distributions.values(); 
    private JCheckBox infDuration;
    private JSpinner duration;
    private int maxJobs = -1; //-1, means simulation runs infinitely, otherwise > 0 means an upper limit

    //-------------all the Actions of this panel------------------
    private AbstractTCHAction exit;
    private AbstractTCHAction start;
    private AbstractTCHAction pause;
    private AbstractTCHAction reload;
    private AbstractTCHAction nextStep;
    private AbstractTCHAction openHelp;
    private AbstractTCHAction about;

    //--------------properties of the animation------------------
    private AnimationClass animation;
    private Policy policy = null;
    private QueuePolicyNonPreemptive[] nonPreemptivePolicies; //array of all possible nonPreemptive policies
    private QueuePolicyNonPreemptive queuePolicyNonPreemptive = null;
    private RoutingPolicy routingPolicy = null;

    //------------- engine simulation --------------------------
    private Solver solver;


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

    //this is the change listener associated to the two spinner for the probabilities in routing prob
    private ChangeListener changeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if(policy == Policy.ROUTING && routingPolicy == RoutingPolicy.PROBABILISTIC && prob1 != null && prob2 != null){
                double value1 = (double) prob1.getValue();
                double value2 = (double) prob2.getValue();
                if (value1 + value2 <= 1) { 
                    createButton.setEnabled(true);
                } else {
                    createButton.setEnabled(false);
                }
            }           
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
        nextStep = new NextStepSimulation(this);
        openHelp = new Help(this,"JTCH");
        about = new About(this);

        solver = new Solver();

        pause.setEnabled(false);
        reload.setEnabled(false);
    }

    /**
     * To create a new AnimationPanel, call this constructor only if Policy = PREEMPTIVE or NON_PREEMPTIVE
     * @param main the main wizard
     * @param policy the type of policy
     */
    public AnimationPanel(MainWizard main, Policy policy, QueuePolicyNonPreemptive algorithm){
        this(main);
        this.policy = policy;
        this.queuePolicyNonPreemptive = algorithm; 
        this.routingPolicy = null;
        start.setEnabled(false); //cannot start a simulation without setting the parameters
        nextStep.setEnabled(false);
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
        start.setEnabled(false);//cannot start a simulation without setting the parameters
        initGUI();
    }

    public void initGUI(){
        Box introductionBox = Box.createHorizontalBox();
        label = new JLabel(ConstantsJTch.INTRODUCTION_SIMULATION);
        introductionBox.add(label);

        this.setLayout(new BorderLayout());
        //this.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
        mainPanel = new JPanel();
        if(policy == Policy.NON_PREEMPTIVE){
            mainPanel.setBorder(new TitledBorder(new EtchedBorder(), policy.toString() + " Scheduling"));
        }
        else if(policy == Policy.PREEMPTIVE){

        }
        else{
            mainPanel.setBorder(new TitledBorder(new EtchedBorder(), routingPolicy.toString()));
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
        leftPanel.add(Box.createVerticalStrut(5), BorderLayout.NORTH);

        //description of the policy
        JPanel descrPanel = new JPanel(new BorderLayout());
        descrLabel = new JLabel();
        if(policy == Policy.ROUTING){
            descrLabel.setText("<html><body><p style='text-align:justify;'><font size=\"3\">"+routingPolicy.getDescription()+"</p></body></html>");
        }
        else{
            descrLabel.setText(ConstantsJTch.NO_DESCRIPTION);
        }
        descrPanel.add(descrLabel, BorderLayout.CENTER);
        leftPanel.add(descrPanel, BorderLayout.NORTH);

        //paramters panel
        createParameters(leftPanel);;
        leftPanel.add(parametersPanel, BorderLayout.SOUTH);

        //------------------RIGHT PART
        //rightPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0)); //handle padding correctly, since it seems to move all the objects of the animation in one direction
        rightPanel.setLayout(new BorderLayout());

        animationPanel = new JPanel(new BorderLayout());
        //based on the type of Policy passed in the constructor, create a new Animation
        if(policy == Policy.NON_PREEMPTIVE){
            animation = new SingleQueueNetAnimation(this, animationPanel, QueuePolicyNonPreemptive.FIFO);
        }
        else{
            animation = new MultipleQueueNetAnimation(animationPanel, routingPolicy);
        }
        animationPanel.add(animation, BorderLayout.CENTER);
        animationPanel.setBackground(Color.WHITE);

        rightPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        rightPanel.add(Box.createHorizontalStrut(10), BorderLayout.WEST);
        rightPanel.add(animationPanel, BorderLayout.CENTER);
        rightPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);

        Box mainBox = Box.createVerticalBox();
        mainBox.add(Box.createVerticalStrut(20));
		mainBox.add(introductionBox);
		mainBox.add(Box.createVerticalStrut(20));
        mainBox.add(mainPanel);
        mainBox.add(Box.createVerticalStrut(20));

        Box totalBox = Box.createHorizontalBox(); //this box is for adding also the horizontal padding
		totalBox.add(Box.createHorizontalStrut(20));
		totalBox.add(mainBox);
		totalBox.add(Box.createHorizontalStrut(20));
        this.add(totalBox, BorderLayout.CENTER);

        this.add(totalBox, BorderLayout.CENTER);
        createMenu();
        createToolbar();
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
                algorithmJComboBox.setSelectedItem(queuePolicyNonPreemptive.toString()); //set as selected policy the one chosen in the MainPanel when the button was pressed
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
            serversSpinner = new JSpinner(model);
            nserversPanel.add(serversSpinner);
        }

        //probability panel (displayed only for probabilistic routing)
        if(routingPolicy == RoutingPolicy.PROBABILISTIC){
            JPanel probabilitiesPanel = createPanel(paddingBorder, false, spaceBetweenPanels, ConstantsJTch.HELP_PROBABILITIES[0]);
            probabilitiesPanel.setBorder(new TitledBorder(new EtchedBorder(), "Probabilities"));
            probabilitiesPanel.setLayout(new BoxLayout(probabilitiesPanel, BoxLayout.Y_AXIS));

            JPanel p1Panel = new JPanel();
            p1Panel.setBorder(paddingBorder);
            probabilitiesPanel.add(p1Panel);
            help.addHelp(p1Panel, ConstantsJTch.HELP_PROBABILITIES[1]);
            p1Panel.setLayout(new GridLayout(1,2));
            p1Panel.add(new JLabel("P1:"));
            SpinnerNumberModel model1 = new SpinnerNumberModel(0.5,0,1,0.01);
            prob1 = new JSpinner(model1);
            p1Panel.add(prob1);
            probabilitiesPanel.add(Box.createVerticalStrut(spaceBetweenPanels));

            JPanel p2Panel = new JPanel();
            p2Panel.setBorder(paddingBorder);
            probabilitiesPanel.add(p2Panel);
            help.addHelp(p2Panel, ConstantsJTch.HELP_PROBABILITIES[2]);
            p2Panel.setLayout(new GridLayout(1,2));
            p2Panel.add(new JLabel("P2:"));
            SpinnerNumberModel model2 = new SpinnerNumberModel(0.5,0,1,0.01);
            prob2 = new JSpinner(model2);
            p2Panel.add(prob2);

            prob1.addChangeListener(changeListener);
            prob2.addChangeListener(changeListener);
        }

        //Inter arrival time panel
        JPanel interAPanel = createPanel(paddingBorder, true, spaceBetweenPanels, ConstantsJTch.HELP_PARAMETERS_PANELS[2]);
        interAPanel.setLayout(new GridLayout(1,2));
        interAPanel.add(new JLabel("Inter Arrival:"));
        interAComboBox = new JComboBox<Distributions>(distributions);
        interAPanel.add(interAComboBox);
        parametersPanel.add(interAPanel);

        //Service Time panel
        JPanel serviceTPanel = createPanel(paddingBorder, true, spaceBetweenPanels, ConstantsJTch.HELP_PARAMETERS_PANELS[3]);
        serviceTPanel.setLayout(new GridLayout(1,2));
        serviceTPanel.add(new JLabel("Service Time:"));
        serviceComboBox = new JComboBox<Distributions>(distributions);
        serviceTPanel.add(serviceComboBox);
        parametersPanel.add(serviceTPanel);

        //Simulation Duration
        JPanel simulationDuration = createPanel(paddingBorder, true, spaceBetweenPanels, ConstantsJTch.HELP_PARAMETERS_PANELS[4]);
        simulationDuration.setLayout(new GridLayout(1,2));
        simulationDuration.add(new JLabel("Max jobs generated:"));

        JPanel valuePanel = new JPanel(new BorderLayout());
        duration = new JSpinner(new SpinnerNumberModel(10, 1, 600, 1));
        duration.setEnabled(false);
        valuePanel.add(duration, BorderLayout.WEST);
        valuePanel.add(Box.createHorizontalStrut(3), BorderLayout.CENTER);     
        infDuration = new JCheckBox("infinite");
        infDuration.setSelected(true);
        infDuration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(infDuration.isSelected()){
                    duration.setEnabled(false);
                    maxJobs = -1;
                }
                else{
                    duration.setEnabled(true);
                }
            }           
        });
        valuePanel.add(infDuration, BorderLayout.EAST);
        simulationDuration.add(valuePanel);
        parametersPanel.add(simulationDuration);

        //create button
        paddingBorder = new EmptyBorder(0,80,0,80);
        JPanel createPanel = createPanel(paddingBorder, true, spaceBetweenPanels*2, ConstantsJTch.HELP_PARAMETERS_PANELS[5]);
        createPanel.setLayout(new BorderLayout());
        createButton = new JButton(CREATE);
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
		action = new MenuAction("Solve", new AbstractTCHAction[] {start, pause, reload, nextStep, null});
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
        AbstractTCHAction[] actions = new AbstractTCHAction[] {start, pause, reload, nextStep, null, openHelp}; // Builds an array with all actions	
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
		if (JOptionPane.showConfirmDialog(this, "This operation resets all data. Are you sure you want to go back to start screen?", "Back operation", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
			return false;
		}		
		parent.resetScreen(); //remove this panel and go back to the intitial MainPanel
		return true;
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
            if(infDuration.isSelected()){
                maxJobs = -1;
            }
            else{
                maxJobs = (Integer) duration.getValue();
            }
            mainPanel.setBorder(new TitledBorder(new EtchedBorder(), policy.toString() + " Scheduling - " + queuePolicyNonPreemptive.toString()));
            descrLabel.setText("<html><body><p style='text-align:justify;'><font size=\"3\">"+queuePolicyNonPreemptive.getDescription()+"</p></body></html>");
            animation.updateSingle(queuePolicyNonPreemptive, (int)serversSpinner.getValue(), (Distributions)serviceComboBox.getSelectedItem(), (Distributions)interAComboBox.getSelectedItem(), maxJobs);
        }
        else if(policy == Policy.PREEMPTIVE){

        }
        else{ //in case of routing only the animation must be updated
            if(routingPolicy == RoutingPolicy.PROBABILISTIC){
                animation.updateMultiple(new double[]{(double) prob1.getValue(), (double) prob2.getValue()}, (Distributions)serviceComboBox.getSelectedItem(), (Distributions)interAComboBox.getSelectedItem());
            }
            else{
                animation.updateMultiple((Distributions)serviceComboBox.getSelectedItem(), (Distributions)interAComboBox.getSelectedItem());
            }
        }

        getSimulationResults();
    }

    MeasureDefinition results;
    int lenght;

    /** Called each time 'Create' is pressed. Start the simulation with the engine to get the results of the simulation in the Results Panel */
    public void getSimulationResults(){ 
        //first update the solver with the new values
        solver.setQueueStrategy(algorithmJComboBox.getSelectedIndex());
        solver.setNumberOfServers((int) serversSpinner.getValue());
        solver.setServiceTime(serviceComboBox.getSelectedIndex());
        solver.setInterArrivalTime(interAComboBox.getSelectedIndex());

        File temp = null;
        DispatcherThread dispatcher = null;
        try {
            temp = File.createTempFile("~JModelSimulation", ".xml");
            temp.deleteOnExit();
            descrLabel.setText(System.getProperty("java.io.tmpdir"));         
            XMLWriter.writeXML(temp, solver.getModel());
            String logCSVDelimiter = solver.getModel().getLoggingGlbParameter("delim");
            String logDecimalSeparator = solver.getModel().getLoggingGlbParameter("decimalSeparator");
            solver.getModel().setSimulationResults(new ResultsModel(solver.getModel().getPollingInterval().doubleValue(), logCSVDelimiter, logDecimalSeparator));
            dispatcher = new DispatcherThread(this, solver.getModel());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* 
        try {
            temp = new File("C:/Users/Torros/Desktop/~JModelSimulation6196690308517869181.xml");
        } catch (Exception e) {
            showErrorMessage("TOOL_TIP_TEXT_KEY");
        } */

        try {
            dispatcher.startSimulation(temp);
        } catch (Exception e) {
            handleException(e);
        }

        
        long start = System.currentTimeMillis();
        try {
            int i = 0;
            while(!dispatcher.simulator.isFinished()){
                descrLabel.setText(String.valueOf(i));
                i++;
            }
            descrLabel.setText("finished after "+(System.currentTimeMillis()-start)/(Math.pow(10,3)));
        } catch (Exception e) {
            handleException(e);
        }
        

        results = solver.getModel().getSimulationResults();
        int[] indices = results.getQueueLengthMeasures(); //not needed, since there is only one station so index is only one and it is = 0
        String res = "-";
        for(int i = 0; i < indices.length; i++){ //this loop is not essential, since we are using just one station in each simulation
            res += "Stazione "+String.valueOf(indices[i])+"----";
            Vector<MeasureValue> values = results.getValues(indices[i]);
            res += String.valueOf(values.get(values.size()-1).getMeanValue());
        }
        
        descrLabel.setText(res); 

        parent.routeResults(solver.getQueueStrategy(), 
            solver.getInterArrivalDistribution(), 
            getLastMeasure(results, 0),
            solver.getServiceDistribution(), 
            solver.getServiceTimeMean(), 
            getLastMeasure(results, 1),
            getLastMeasure(results, 2), 
            getLastMeasure(results, 3), 
            getLastMeasure(results, 4));
    }

    public double getLastMeasure(MeasureDefinition md, int index){
        return md.getValues(index).lastElement().getMeanValue();
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
        nextStep.setEnabled(false);   
    }

    @Override
    public void pauseAnimation() {
        animation.pause();
        start.setEnabled(true);
        pause.setEnabled(false);
        reload.setEnabled(true);
        nextStep.setEnabled(true);
    }

    @Override
    public void reloadAnimation() {
        animation.reload();
        start.setEnabled(true);
        pause.setEnabled(false);
        reload.setEnabled(false);
        nextStep.setEnabled(true);
    }

    @Override
    public void nextStepAnimation(){
        animation.next();
        //disable all the buttons, they will be enabled by the AnimationClass when the step simulation is completed
        disableAllButtons();
    }

    /** Set all the buttons correclty when the Step simulation is complete (called by AnimationClass) */
    public void resetNextStepAnimation(){
        start.setEnabled(true);
        pause.setEnabled(false);
        reload.setEnabled(true);
        nextStep.setEnabled(true);
    }

    @Override
    public void stopAnimation() {
        disableAllButtons();
    }

    @Override
    public void exit(){
        parent.close();
    }

    public void disableAllButtons(){
        start.setEnabled(false);
        pause.setEnabled(false);
        reload.setEnabled(false);
        nextStep.setEnabled(false);
    }


    //-----------------------------------------------------------------------
    //-------------------- all GUI Interface methods ------------------------
    //-----------------------------------------------------------------------
    @Override
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void handleException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
		showErrorMessage(sw.toString());
    }

    @Override
    public void changeSimActionsState(boolean start, boolean pause, boolean stop) {
        
    }

    @Override
    public void setResultsWindow(JFrame rsw) {
    }

    @Override
    public void showResultsWindow() {
        //TODO:add row to the table

    }

    @Override
    public boolean isAnimationDisplayable() {
        return true;
    }

    @Override
    public void showRelatedPanel(int problemType, int problemSubType, Object relatedStation, Object relatedCLass) {
    }

    @Override
    public void setAnimationHolder(Thread thread) {

    }

    @Override
    public String getFileName() {
        return null;
    }
}
