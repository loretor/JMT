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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

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
                nserversPanel.setVisible(true);
            }
            else{
                nserversPanel.setVisible(false);
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

    private Frame parent; //parent of this JDialog
    
    private JComboBox<String> policyJComboBox;
    private JPanel nserversPanel; //important to set visible = false, when other than queue policy is selected

    private JLabel distributionLabel;
    private JButton editDistributiButton;

    private JOptionPane optionPane;

    private String btnString1 = "Enter";

    //-------------------parameters setted---------------------
    private List<String> policiesList;
    private List<List<String>> algorithmsList; //List of algorithms at position x correspond to policy at position x in polices
    private Distribution distribution;


    /**
     * Create the Dialog
     */
    public DialogStaticVariables(Frame aFrame) {
        super(aFrame, true);
        parent = aFrame;

        policiesList = new ArrayList<>();
        algorithmsList = new ArrayList<>();
        addPolicy( "Queue Policy", new ArrayList<String>(Arrays.asList("First In First Out", "Last In Last Out","Priority")));
        addPolicy( "Routing Policy", new ArrayList<String>(Arrays.asList("Round Robin", "Join the Shortest Job Queue","Probabilistic Routing")));

        initGUI();

        //do not move those 3 lines of code
        setSize(300,300);
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
        setTitle("Chose Policy and set static parameters");

        //--- policy panel
        JPanel policyPanel = new JPanel(new FlowLayout());
        JLabel p = new JLabel(); //p is the JLabel for titles, use it only in this method, since it is not useful to save in class variables the Labels of the titles
        p.setText("<html><b>"+"Policy Type: "+"</b></html>");
        policyPanel.add(p);
        policyJComboBox = new JComboBox<String>(policiesList.toArray(new String[policiesList.size()]));
        policyJComboBox.addActionListener(policyChange); 
        policyPanel.add(policyJComboBox);

        nserversPanel = new JPanel(new FlowLayout()); 
        p = new JLabel("N.servers:");
        nserversPanel.add(p);
        SpinnerModel model = new SpinnerNumberModel(1,1,2,1);
        JSpinner serversSpinner = new JSpinner(model);
        nserversPanel.add(serversSpinner);
        nserversPanel.setVisible(true);
        policyPanel.add(nserversPanel);

        policyPanel.setBackground(Color.BLUE);

        //-- distribution panel
        JPanel distributionPanel = new JPanel(new FlowLayout());
        p = new JLabel();
        p.setText("<html><b>"+"Job Duration Distribution: "+"</b></html>");
        distributionLabel = new JLabel("not chosen");
        distributionPanel.add(p);
        editDistributiButton = new JButton(editDistribution);
        distributionPanel.add(distributionLabel);
        distributionPanel.add(editDistributiButton);
        distributionPanel.setBackground(Color.GREEN);

        Object[] array = {policyPanel, distributionPanel};
        Object[] options = {btnString1};

        optionPane = new JOptionPane(array, JOptionPane.DEFAULT_OPTION, JOptionPane.OK_OPTION, null, options, options[0]);
        setContentPane(optionPane);
  
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //avoid closing the window

        // Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    /** 
     * React to changes inside the JDialog
    */
    public void propertyChange(PropertyChangeEvent e) {
        if (isVisible() && (e.getSource() == optionPane)) {
            Object value = optionPane.getValue();

            if (btnString1.equals(value)) {
                clearAndHide();
            }
        }
    }

    /** 
     * Clear the dialog and close it
     */
    public void clearAndHide() {
        setVisible(false);
    }

    /*
     * Add a new policy and the list of algorithms associated to this policy
     */
    private void addPolicy(String policy, List<String> algorithms){
        policiesList.add(policy);
        algorithmsList.add(algorithms);
    }
}
