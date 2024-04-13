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
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import jmt.framework.gui.components.JMTFrame;
import jmt.framework.gui.components.JMTMenuBar;
import jmt.framework.gui.components.JMTToolBar;
import jmt.framework.gui.controller.Manager;
import jmt.framework.gui.layouts.MultiBorderLayout;
import jmt.gui.common.Defaults;


 
/**
 * JTeachMain contains the main window of the jteach project
 *
 * @author Lorenzo Torri
 * Date: 11-mar-2024
 * Time: 09.40
 */
public class JTeachMain extends JMTFrame {
 
    private static final long serialVersionUID = 1L;
 
    private static final String TITLE = "JTeach - A tool for helping new users";

    public JFrame thisFrame;
    
    protected MediatorTeach mediator; // mediator between components of the application
    protected JMTMenuBar menu; //main menu
    protected JMTToolBar toolbar;//main toolbar

    protected JPanel mainPane; //panel that contains the animationPanel and the helpBar (animPanel CENTER + helpBar SOUTH)
    private JPanel animPanel; //panel of the animations

    /** 
     * Creates the new Main window of the application.
     */
    public JTeachMain() {
        //super(true);
        thisFrame = this;
        
        initGUI();
        setVisible(true); 
        setResizable(false);

        initPanelvariables();    
    }

    /**
     * Initialize all the GUI of the JTeach
     */
    public void initGUI(){   
        //setIconImage(JMTImageLoader.loadImage("JMODELIcon").getImage()); /* prepare the icon for JTeach */
        setTitle(TITLE);  
        centerWindow(Defaults.getAsInteger("JSIMWindowWidth").intValue(), Defaults.getAsInteger("JSIMWindowHeight").intValue()); //If I want to change the size of the window create new values inside Defaults or CommonCostant     

        //object responsible for all the GUI components
        mediator = new MediatorTeach(this); 
        
        //create menu
        menu = mediator.createMenu();
		setJMenuBar(menu);

        //create the toolbar
        toolbar = mediator.createToolbar();
        getContentPane().setLayout(new MultiBorderLayout());
        getContentPane().add(toolbar, BorderLayout.NORTH);
        
        //add the panel that contains the text and also the possible animation 
        //the JPanel is defined here and not in Mediator only because in the other way around it is not working properly
		defineMainJPanel();

        //add the helper bar on the bottom
        mainPane.add(mediator.createHelpLabel(), BorderLayout.SOUTH);
    }

    /**
     * Define the main Panel with Text and animaiton
     */
    public void defineMainJPanel(){
        mainPane = new JPanel(new BorderLayout());
        animPanel = new JPanel();
        mediator.setAnimationPanel(animPanel); //call this method of the mediator, since all the logic is done by the mediator not here

        getContentPane().add(mainPane, BorderLayout.CENTER);
        mainPane.add(animPanel, BorderLayout.CENTER);
    }

    /**
     * Create the Dialog for the static variables, displayed on top of the main panel of JTeach
     */
    public void initPanelvariables(){
        new DialogStaticVariables(this, this);
    }
    

    /** 
     * main function it activates the application .
     * @param args
    */
    public static void main(String[] args) {
        try {
			JTeachMain inst = new JTeachMain();
			inst.setVisible(true);
			Manager.addWindow(inst);
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 

    /**
     * Call this method from JDialog to save the parameters setted in the JDialog inside the class in the Main
     * @param info object containing all the information of the simulation
     */
    protected void setInformation(SimInformation info){
        mediator.setInformation(info);
    }
}
 
