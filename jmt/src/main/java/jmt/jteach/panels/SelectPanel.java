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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jmt.framework.gui.help.HoverHelp;
import jmt.framework.gui.listeners.AbstractJMTAction;
import jmt.jteach.MediatorTeach;
import jmt.jteach.SimInformation;


/**
 * Panel that contains the JComboBox for the selection of the type of Policy to perfrom
 *
 * @author Lorenzo Torri
 * Date: 11-mar-2024
 * Time: 16.29
 */
public class SelectPanel extends JPanel{
    
    /*
     * AbstractJMTAction associated to the JButton
     */
    protected AbstractJMTAction ACTION_CREATE = new AbstractJMTAction("Create") {

		private static final long serialVersionUID = 1L;
		{
			setTooltipText("Create Model");
		}

        //add the new algorithm chosen to the SimInformation and repaint the animationPanel of Mediator
		public void actionPerformed(ActionEvent e) {
			String algo = String.valueOf(algorithmJComboBox.getSelectedItem());
            mediator.geInformation().setAlgorithmChosen(algo);
            mediator.paintAnimationPanel();
		}
	};

    protected MediatorTeach mediator;
    private SimInformation info;
    private HoverHelp help;

    private int panelHeight = 30;
    private int spaceBetweenPanels = 10;

    private JComboBox<String> algorithmJComboBox;
    private List<String> algorithmsList; 

    //-- help strings
    private final String shelpPolicy = "Policy chosen for the simulation";
    private final String shelpAlgorithm = "List of possible algorithms for the type of policy selected";
    private final String shelpCreate = "After selecting the type of algorithm, press this button to create the animation. Then press the Start button";

    /**
     * Create the SelectPanel.
     * At the beginning the panel is empty, since there is the JDialog still open.
     * @param mediator
     */
    public SelectPanel(MediatorTeach mediator) {
        this.mediator = mediator;
        help = mediator.getHelp(); //retrieve HoverHelp from mediator
    }

    /**
     * Update the Panel with the new infomation from the JDialog
     */
    public void updateGraphics(){
        info = mediator.geInformation();
        algorithmsList = info.getAlgorithms();
        
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        //----- Policy Panel
        JPanel c1 = new JPanel(new GridBagLayout());
        help.addHelp(c1, shelpPolicy);
        populatePolicyPanel(c1);
        this.add(Box.createHorizontalStrut(spaceBetweenPanels));

        //----- Algorithms Panel
        JPanel c2 = new JPanel(new GridBagLayout());
        help.addHelp(c2, shelpAlgorithm);
        populateAlgorithmPanel(c2);
        this.add(Box.createHorizontalStrut(spaceBetweenPanels));

        //----- Create button
        JPanel c3 = new JPanel();
        JButton createButton = new JButton(ACTION_CREATE);
        c3.add(createButton);
        help.addHelp(createButton, shelpCreate);   
        this.add(c3);
    }

    /**
     * Populate the JPanel with policy label and a label with the policy chosen
     * @param c1 JPanel to populate
     */
    private void populatePolicyPanel(JPanel c1){
        //Policy Label
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.30; 
        gbc.fill = GridBagConstraints.BOTH;
        JLabel l = new JLabel("Policy:");
        c1.add(l, gbc);

        //Policy chosen
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.70; 
        gbc.fill = GridBagConstraints.BOTH;
        JLabel policy = new JLabel(info.getPolicy());
        policy.setBorder(BorderFactory.createEtchedBorder());
        policy.setHorizontalAlignment(SwingConstants.CENTER);  
        c1.add(policy,gbc);

        c1.setPreferredSize(new Dimension(150, panelHeight));
        this.add(c1);
    }

    /**
     * Populate the JPanel with algorithm label and a Jcombobox with the list of algorithms associated to the policy chosen
     * @param c1 JPanel to populate
     */
    private void populateAlgorithmPanel(JPanel c2){
        //Algorithm Label
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.30; 
        gbc.fill = GridBagConstraints.BOTH;
        JLabel l = new JLabel("Algorithm:");
        c2.add(l, gbc);

        //Algorithm JCombo
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.70;
        gbc.fill = GridBagConstraints.BOTH; 
        algList();
        c2.add(algorithmJComboBox, gbc);

        c2.setPreferredSize(new Dimension(250, 30));
        this.add(c2);
    }


    /**
     * Create the JComboBox for the type of algorithm according to the type of policy chosen at the beginnning
     */
    private void algList() {
        algorithmJComboBox = new JComboBox<String>(algorithmsList.toArray(new String[algorithmsList.size()])); 

        Dimension d = new Dimension(160, 30);
        algorithmJComboBox.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXX");
        algorithmJComboBox.setMaximumSize(d);
        algorithmJComboBox.setSelectedIndex(0);
        //policyList.addActionListener(ACTION_CHANGE_ALGORITHM);
        algorithmJComboBox.setVisible(true);
    }
}
