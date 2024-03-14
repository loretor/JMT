/**
 * Copyright (C) 2016, Laboratorio di Valutazione delle Prestazioni - Politecnico di Milano
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package jmt.jteach.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jmt.framework.gui.help.HoverHelp;
import jmt.framework.gui.listeners.AbstractJMTAction;
import jmt.jteach.MediatorTeach;


/**
 * Panel that contains the JComboBox for the selection of the type of Policy to perfrom
 *
 * @author Lorenzo Torri
 * Date: 11-mar-2024
 * Time: 16.29
 */
public class SelectPanel extends JPanel{

    protected MediatorTeach mediator;
    private HoverHelp help;

    private JLabel policyLabel; //label before the ComboBox
    private JComboBox<String> policyJComboBox; //selection tool 
    
    private JLabel algorithmLabel;
    private JComboBox<String> algorithmJComboBox;

    //those two Lists correspond to the possible policies (first JComboBox), and for each policy a List of possible algoirithms (second JComboBox)
    private List<String> policiesList;
    private List<List<String>> algorithmsList; //List of algorithms at position x correspond to policy at position x in polices


    /*
     * ActionListener associated to the policyJComboBox
     */
    private ActionListener ACTION_CHANGE_POLICY = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //JComboBox<?> algorithmList = (JComboBox<?>) e.getSource();
            //selezione = algorithmsList.get(policyJComboBox.getSelectedIndex()).get(0);
            updateAlgorithmJComboBox(); //based on the value chosen, update the list of possible algorithms
            //update();
        }
    };

    /*
     * AbstractJMTAction associated to the JButton
     */
    protected AbstractJMTAction ACTION_CREATE = new AbstractJMTAction("Create") {

		private static final long serialVersionUID = 1L;
		{
			setTooltipText("Create Model");
		}

		public void actionPerformed(ActionEvent e) {
			//TODO: create a new animation
		}
	};

    public SelectPanel(MediatorTeach mediator) {
        super(new BorderLayout());
        this.mediator = mediator;
        help = mediator.getHelp(); //retrieve HoverHelp from mediator
        
        policiesList = new ArrayList<>();
        algorithmsList = new ArrayList<>();
        initialize();
    }

    /**
     * Initialize this panel
     */
    private void initialize() {
        // first add all the policies and the types of algorithms
        addPolicy( "Queue Policy", new ArrayList<String>(Arrays.asList("First In First Out", "Last In Last Out","Priority")));
        addPolicy( "Routing Policy", new ArrayList<String>(Arrays.asList("Round Robin", "Join the Shortest Job Queue","Probabilistic Routing")));
          
        JPanel mainPanel = new JPanel(new FlowLayout());
        mainPanel.add(policyLabel("Policy")); //policy label + JComboBox
        mainPanel.add(policyList());

        mainPanel.add(Box.createHorizontalStrut(10)); //add some spacing
        mainPanel.add(algLabel("Algorithm")); //algorithms label + JComboBox
        mainPanel.add(algList());

        //then add the button to create the animation
        mainPanel.add(Box.createHorizontalStrut(15)); //add some spacing
        JButton createAnimation = new JButton(ACTION_CREATE);
        mainPanel.add(createAnimation);
        help.addHelp(createAnimation, "Press this button when you are ready to set up the environment of the simulation");

        /*mainPanel.add(maxSampleLabel());
        mainPanel.add(maxSamples()); */
        this.add(mainPanel, BorderLayout.WEST); 
    }

    /**
     * Create the Label before the JComboBox
     * @return JLabel before the JComboBox
     */
    private JComponent policyLabel(String label) {
        Dimension d = new Dimension(65, 30);
        policyLabel = new JLabel(label);
        policyLabel.setMaximumSize(d);
        policyLabel.setFocusable(false);
        help.addHelp(policyLabel, "Select the type of policy for the simulation (by default 'Queue Policy' is selected)");
        return policyLabel;
    }

    /**
     * Create the JComboBox for the type of policy
     * @return JComboBox for the type of policy
     */
    private JComponent policyList() {
        policyJComboBox = new JComboBox<String>(policiesList.toArray(new String[policiesList.size()]));

        Dimension d = new Dimension(160, 30);
        policyJComboBox.setMaximumSize(d);
        policyJComboBox.setSelectedIndex(0);
        policyJComboBox.addActionListener(ACTION_CHANGE_POLICY);
        policyJComboBox.setVisible(true);
        
        //?? setRender ?? lo dobbiamo andare a fare solo se ci sono solamente alcune opzioni che sono selezionabili (ne vogliamo alcune che facciano da raggruppamento)

        return policyJComboBox;
    }

    /**
     * Create the Label after the JComboBox for the type of algorithm to set
     * @return JLabel before the JComboBox
     */
    private JComponent algLabel(String label) {
        Dimension d = new Dimension(70, 30);
        algorithmLabel = new JLabel(label);
        algorithmLabel.setMaximumSize(d);
        algorithmLabel.setFocusable(false);
        //algLabel.setVisible(status.toleranceVisible);
        help.addHelp(algorithmLabel, "Select the algorithm type for the simulation (changing the policy type will result in a different list of algorithms)");
        return algorithmLabel;
    }

    /**
     * Create the JComboBox for the type of algorithm according to the type of policy chosen at the beginnning
     * @return JComboBox for the type of algorithm 
     */
    private JComponent algList() {
        int selectedPolicy = policyJComboBox.getSelectedIndex(); //get the i-th policy chosen
        algorithmJComboBox = new JComboBox<String>(algorithmsList.get(selectedPolicy).toArray(new String[algorithmsList.get(selectedPolicy).size()])); //select the list of algorithms in position i-th

        Dimension d = new Dimension(160, 30);
        algorithmJComboBox.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXX");
        algorithmJComboBox.setMaximumSize(d);
        algorithmJComboBox.setSelectedIndex(0);
        //policyList.addActionListener(ACTION_CHANGE_ALGORITHM);
        algorithmJComboBox.setVisible(true);
        
        //?? setRender ?? lo dobbiamo andare a fare solo se ci sono solamente alcune opzioni che sono selezionabili (ne vogliamo alcune che facciano da raggruppamento)

        return algorithmJComboBox;
    }

    /**
     * Updates the choices of the second JComboBox based on the value chosen for the first JComboBox.
     * Must be called in the ActionListener of the first JComboBox
     */
    private void updateAlgorithmJComboBox(){
        algorithmJComboBox.removeAllItems();
        int selectedPolicy = policyJComboBox.getSelectedIndex();
        for(String s: algorithmsList.get(selectedPolicy)){
            algorithmJComboBox.addItem(s);
        }
    }

    /*
     * Update the JComboBox for the selection of the type of policy and the correspondant algorithm
     * Add a new policy and the list of algorithms associated to this policy
     */
    private void addPolicy(String policy, List<String> algorithms){
        policiesList.add(policy);
        algorithmsList.add(algorithms);
    }
}
