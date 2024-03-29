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

package jmt.jteach;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import jmt.gui.common.distributions.*;
import jmt.framework.gui.help.HoverHelp;
import jmt.gui.common.distributions.Distribution;
import jmt.gui.common.editors.DistributionsEditor;


/**
 * Custom dialog displayed on top of the JTeach main panel. It is used for selecting the type of policy and for setting the static variables of the simulation
 *
 * @author Lorenzo Torri
 * Date: 16-mar-2024
 * Time: 09.27
 */
public class DialogStaticVariables extends JDialog implements PropertyChangeListener {

    //action listener associated to the policiesJComboBox, to display the number of servers if the Queue Policy is selected
    protected ActionListener policyChange = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if(policyJComboBox.getSelectedIndex() == 0){ //queue policy is the first element of the list of policies
                serversSpinner.setEnabled(true);
            }
            else{
                serversSpinner.setEnabled(false);
            }
        }
        
    }; 

    //action associated to the editButton for changing the distribution
	protected AbstractAction editDistribution = new AbstractAction("Edit") {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.SHORT_DESCRIPTION, "Edits distribution parameters");
		}

		public void actionPerformed(ActionEvent e) {
            DistributionsEditor editor = DistributionsEditor.getInstance(parent, null);
            editor.setTitle("Editing  distribution");
            editor.show();	
            distribution = editor.getResult(); //update class variable distribution with the result of the editor
            distributionLabel.setText(distribution.getName()); //update the distributionLabel
		}
	};

    //--------------components of the JDialog-----------------
    private Frame parent; //parent of this JDialog
    private JTeachMain main;

    private JPanel mainPanel; //panel with all the parameters
    private int heightPanels = 20;
    private int spaceBetweenPanels = 5;
    
    private JComboBox<String> policyJComboBox;
    private JSpinner serversSpinner; //important since it must be set to enabled = false when routing policy is selected

    private JLabel distributionLabel;
    private JButton editDistributiButton;

    private JSpinner jobsSpinner;
    private JTextField thinktimeSpinner;

    private JOptionPane optionPane;

    private String btnString1 = "Enter";

    private JLabel helpLabel;
    private HoverHelp help;

    //-------------------parameters setted---------------------
    private List<String> policiesList;
    private List<List<String>> algorithmsList; //List of algorithms at position x correspond to policy at position x in polices
    private Distribution distribution;

    //---------------------help strings------------------------
    //search for "addHelp" to see where these strings are used
    private final String shelpMain = "Set the parameters for the simulation. Once ready, click the Enter button";
    private final String shelpPolicy = "Select the policy to be viewed in the simulation";
    private final String shelpServ = "Select the number of available servers for each station";
    private final String shelpDistr = "Select the type of distribution for the time each job needs for completion";
    private final String shelpN = "Select the number of jobs in the system";
    private final String shelpZ = "Select the Think time of the system";


    /**
     * Create the Dialog
     */
    public DialogStaticVariables(Frame aFrame, JTeachMain main) {
        super(aFrame, true);
        parent = aFrame;
        this.main = main;

        policiesList = new ArrayList<>();
        algorithmsList = new ArrayList<>();
        addPolicy( "Queue Policy", new ArrayList<String>(Arrays.asList("First In First Out", "Last In Last Out", "Priority", "Random Order", "Shortest Job First","Longest Job First")));
        addPolicy( "Routing Policy", new ArrayList<String>(Arrays.asList("Round Robin", "Join the Shortest Job Queue","Probabilistic Routing")));

        distribution = new Exponential();

        help = new HoverHelp();

        initGUI();

        //do not move those 5 lines of code
        int width = 450, height = 400; //width is 450 since otherwise you cannot see all the content of the helper bar
 		Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize(); // Centers this dialog on the screen
		setBounds((scrDim.width - width) / 2, (scrDim.height - height) / 2, width, height);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Create the GUI of the JDialog
     */
    public void initGUI(){
        /*
         * If I want to add a new component, I have to instanciate it, then add it to array[], then eventually add an actionListener 
         */
        setTitle("Setting simulation parameters...");

        mainPanel = new JPanel();
        mainPanel.setBorder(new TitledBorder(new EtchedBorder(), "Simulation Parameters"));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        help.addHelp(mainPanel, shelpMain);

        EmptyBorder paddingBorder = new EmptyBorder(0, 10, 0, 10); //padding right and left for all the panels inside the JPanel (top and bottom = 0 otherwise it does not show other panels)

        //-------- c1 = Policy + N.Servers
        JPanel c1 = createPanel(paddingBorder, null);
        c1.setLayout(new GridBagLayout()); 
        populatePolicyPanel(c1);
        
        //-------- c2 = Distribution
        JPanel c2 = createPanel(paddingBorder, shelpDistr);
        c2.setLayout(new GridBagLayout());
        populateDistributionPanel(c2);
        
        //-------- c3 = N.Jobs
        jobsSpinner = createPanelJSpinner("Number of jobs", paddingBorder, shelpN);

        //-------- c4 = Think Time
        thinktimeSpinner = createPanelJText("Think time", paddingBorder, shelpZ);

        
        Object[] array = {mainPanel};
        Object[] options = {btnString1};

        optionPane = new JOptionPane(array, JOptionPane.DEFAULT_OPTION, JOptionPane.OK_OPTION, null, options, options[0]);
        setContentPane(optionPane);
  
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //avoid closing the window

        getContentPane().add(Box.createVerticalStrut(spaceBetweenPanels));
        getContentPane().add(createHelpLabel()); //add the helper bar

        // Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    /**
     * Create a new JPanel inside the mainPanel
     * @param paddingBorder border left and right for all the panels
     * @param helpText the string to be displayed on the bottom of the JDialog
     * @return a new Panel
     */
    private JPanel createPanel(EmptyBorder paddingBorder, String helpText) {
        mainPanel.add(Box.createVerticalStrut(spaceBetweenPanels));
        JPanel p = new JPanel();  
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, heightPanels));
        p.setBorder(paddingBorder);
        mainPanel.add(p);
        help.addHelp(p, helpText);
        return p;
	}

    /**
     * Populate the first JPanel inside the mainPanel
     * @param c1 first panel in the mainPanel container
     */
    private void populatePolicyPanel(JPanel c1){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.40; //policy panel takes the 40% of the second column
        gbc.fill = GridBagConstraints.BOTH;

        //policy Panel
        JPanel policyPanel = new JPanel(new GridLayout(1,2));
        JLabel p = new JLabel("Policy Type:"); 
        policyJComboBox = new JComboBox<String>(policiesList.toArray(new String[policiesList.size()]));
        policyJComboBox.addActionListener(policyChange); 
        policyPanel.add(p);
        policyPanel.add(policyJComboBox);
        help.addHelp(policyPanel, shelpPolicy);
        c1.add(policyPanel, gbc);

        //servers Panel
        JPanel nserversPanel = new JPanel(new GridLayout(1,2));
        gbc.gridx = 1;
        gbc.weightx = 0.60; 
        gbc.insets = new Insets(0, 20, 0, 0); //add padding on the left of this component
        p = new JLabel("N.servers:");
        SpinnerModel model = new SpinnerNumberModel(1,1,2,1);
        serversSpinner = new JSpinner(model);
        nserversPanel.add(p);
        nserversPanel.add(serversSpinner);
        nserversPanel.setVisible(true);
        help.addHelp(nserversPanel, shelpServ);
        c1.add(nserversPanel, gbc);
    }

    /**
     * Populate the second JPanel inside the mainPanel
     * @param c2 second panel in the mainPanel container
     */
    private void populateDistributionPanel(JPanel c2){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.50; 
        gbc.fill = GridBagConstraints.BOTH;

        //distribution panel
        JPanel distributionPanel = new JPanel(new GridLayout(1,2));
        JLabel p = new JLabel("Job Duration Distribution:");
        distributionLabel = new JLabel(distribution.getName());
        distributionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        distributionLabel.setBorder(BorderFactory.createEtchedBorder());
        distributionPanel.add(p);
        distributionPanel.add(distributionLabel);
        c2.add(distributionPanel, gbc);

        //edit button
        gbc.gridx = 1;
        gbc.weightx = 0.50; 
        gbc.insets = new Insets(0, 20, 0, 0); //add padding on the left of this component
        editDistributiButton = new JButton(editDistribution); //add to the button the AbstractAction to open the panel of distributions
        c2.add(editDistributiButton, gbc);
    }

    /**
     * Create a new Panel inside the mainPanel container, with a Label and a TextField
     * @param text content of the Label
     * @param paddingBorder padding left and right of the new Panel
     * @param helpText the string to be displayed on the bottom of the JDialog
     * @return JTextField
     */
    private JTextField createPanelJText(String text, EmptyBorder paddingBorder, String helpText){
        JPanel p = createPanel(paddingBorder, helpText);
        p.setLayout(new GridLayout(1,2));
        JLabel l = new JLabel(text+":");
        JTextField t = new JTextField();
        t.setText("1");
        p.add(l);
        p.add(t);
        return t;
    }

    /**
     * Create a new Panel inside the mainPanel container, with a Label and a Spinner
     * @param text content of the Label
     * @param paddingBorder padding left and right of the new Panel
     * @param helpText the string to be displayed on the bottom of the JDialog
     * @return JSpinner
     */
    private JSpinner createPanelJSpinner(String text, EmptyBorder paddingBorder, String helpText){
        JPanel p = createPanel(paddingBorder, helpText);
        p.setLayout(new GridLayout(1,2));
        JLabel l = new JLabel(text+":");
        SpinnerModel model = new SpinnerNumberModel(10,1,10,1);
        JSpinner spin = new JSpinner(model);
        p.add(l);
        p.add(spin);
        return spin;
    }

    /**
     * Create a helper bar to have a description of the buttons and labels when hovered with the mouse
     * @return JLabel of the helper bar
     */
    protected JPanel createHelpLabel(){
		helpLabel = help.getHelpLabel();
        helpLabel.setBorder(BorderFactory.createEtchedBorder());

        JPanel labelbox = new JPanel();
		labelbox.setLayout(new BorderLayout());
		labelbox.add(Box.createVerticalStrut(30), BorderLayout.WEST);
		labelbox.add(helpLabel, BorderLayout.CENTER);
        labelbox.setMaximumSize(new Dimension(parent.getWidth(), 20));
        
        return labelbox;
    }


    /** 
     * React to changes inside the JDialog
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (isVisible() && (e.getSource() == optionPane)) {
            Object value = optionPane.getValue();

            if (btnString1.equals(value)) {
                sendInformationMain();
                clearAndHide();         
            }
        }
    }

    /**
     * Called when the "Enter" is pressed.
     * Before closing the JDialog send all the setting information to the JTeachMain
     */
    private void sendInformationMain(){
        String policy = String.valueOf(policyJComboBox.getSelectedItem());
        int index = policyJComboBox.getSelectedIndex();
        int servers = (int) serversSpinner.getValue();
        int N = (int) jobsSpinner.getValue();
        int Z = Integer.valueOf(thinktimeSpinner.getText());

        main.setInformation(new SimInformation(policy, algorithmsList.get(index), servers, distribution, N, Z));
    }

    /** 
     * Clear the dialog and close it
     */
    public void clearAndHide() {
        setVisible(false);
    }

    /**
     * Add a new policy and the list of algorithms associated to this policy
     * @param policy new policy
     * @param algorithms list of strings associated to the new policy
     */
    private void addPolicy(String policy, List<String> algorithms){
        policiesList.add(policy);
        algorithmsList.add(algorithms);
    }
}
