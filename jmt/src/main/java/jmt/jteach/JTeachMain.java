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
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jmt.framework.gui.components.JMTFrame;
import jmt.framework.gui.components.JMTMenuBar;
import jmt.framework.gui.components.JMTToolBar;
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
    
    protected MediatorTeach mediator; // mediator between components of the application
    protected JMTMenuBar menu; //main menu
    protected JMTToolBar toolbar;//main toolbar
    protected JPanel mainPane; //panel to display the animation
 
    /** 
     * Creates the new Main window of the application.
     */
    public JTeachMain() {
        super(true);
        //setIconImage(JMTImageLoader.loadImage("JMODELIcon").getImage()); /* prepare the icon for JTeach */
        setTitle(TITLE);

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
       
        //If I want to change the size of the window create new values inside Defaults or CommonCostant
        centerWindow(Defaults.getAsInteger("JSIMWindowWidth").intValue(), Defaults.getAsInteger("JSIMWindowHeight").intValue());
        setVisible(true); 
    }

    public void defineMainJPanel(){
        mainPane = new JPanel(new BorderLayout());
        mainPane.setBackground(Color.BLUE);
        mediator.setAnimationPanel(mainPane);

        getContentPane().add(mainPane, BorderLayout.CENTER);
    }
    

    /** 
     * main function it activates the application .
     * @param args
    */
    public static void main(String[] args) {
        new JTeachMain();
        if (args != null && args.length > 0) {
			File file = new File(args[0]);
			if (!file.isFile()) {
				System.err.print("Invalid model file: " + file.getAbsolutePath());
				System.exit(1);
			}
		}
    } 
}
 
