package jmt.jteach;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import jmt.framework.gui.components.JMTMenuBar;
import jmt.framework.gui.components.JMTToolBar;
import jmt.framework.gui.help.HoverHelp;
import jmt.framework.gui.listeners.MenuAction;
import jmt.gui.common.JMTImageLoader;
import jmt.gui.common.panels.AboutDialogFactory;
import jmt.jteach.actions.*;
import jmt.jteach.panels.SelectPanel;


/**
 * This class maintains a reference to all the main components of the Gui,
 * in this way it is possible to divide the responsibility of the actions &
 * every object know only about of himself & the mediator.
 * Other actions are made through the mediator without knowing who will actually
 * do it.
 * 
 * @author Lorenzo Torri
 * Date: 11-mar-2024
 * Time: 11.13
 */
public class MediatorTeach {

    private JTeachMain mainWindow;

    //all the Actions of the Mediator
    private AbstractTeachAction exit;
    private AbstractTeachAction start;
    private AbstractTeachAction doublevelocity;
    private AbstractTeachAction pause;
    private AbstractTeachAction stop;
    private AbstractTeachAction openHelp;
    private AbstractTeachAction about;

    //reference of components defined inside this class
    private SelectPanel selectPanel;

    private JPanel animdescrPanel;
    private JPanel descriptionPanel;
    private JPanel animationPanel;

    private JLabel helpLabel;
	private HoverHelp help;
    
    public MediatorTeach(JTeachMain mainWindow){
        this.mainWindow = mainWindow;
        help = new HoverHelp(); 

        //define all the AbstractTeachAction
        exit = new Exit(this);
        start = new StartSimulation(this);
        doublevelocity = new DoubleSimulationVelocity(this);
        pause = new PauseSimulation(this);
        stop = new StopSimulation(this);
        openHelp = new Help(this);
        about = new About(this);
    }


    /**
	 * Creates a menu to be displayed in main window.
	 * @return created menu.
	 */
	protected JMTMenuBar createMenu() {
		JMTMenuBar menu = new JMTMenuBar(JMTImageLoader.getImageLoader());

        //File window
        MenuAction action = new MenuAction("File", new AbstractTeachAction[] { null, exit});
		menu.addMenu(action);

        //Solve window
		action = new MenuAction("Solve", new AbstractTeachAction[] {start, doublevelocity, pause, stop, null});
		menu.addMenu(action);

        //Help window
        action = new MenuAction("Help", new AbstractTeachAction[] {openHelp, null, about});
		menu.addMenu(action);

		return menu;
	}

    /**
	 * Creates a toolbar to be displayed in main window.
	 * @return created toolbar.
	 */
	protected JMTToolBar createToolbar() {
        JMTToolBar toolbar = new JMTToolBar(JMTImageLoader.getImageLoader());	

        //first add all the icons with their actions
        AbstractTeachAction[] actions = new AbstractTeachAction[] {start, doublevelocity, pause, stop, null, openHelp}; // Builds an array with all actions to be put in the toolbar
		String[] helpText = {"Start the simulation","Double the velocity of the simulation (perform this action only if there is an active simulation)",
        "Pause the simulation (perform this action only if there is an active simulation)","Stop the simulation (perform this action only if there is an active simulation)", 
        "Open the help page"};
        toolbar.populateToolbar(actions);
        ArrayList<AbstractButton> buttons = new ArrayList<AbstractButton>(); //create a list of AbstractButtons for the helpLabel
		buttons.addAll(toolbar.populateToolbar(actions));

        //then add the selection panel
        selectPanel = new SelectPanel(this);
        toolbar.add(selectPanel); 

        //add help for each Action/JComboBox with helpLabel
		for (int i = 0; i < buttons.size(); i++) {
			AbstractButton button = buttons.get(i);
			help.addHelp(button, helpText[i]);
		}
		  
		return toolbar;
	}

    /**
     * Create the JPanel containing the text and the animation. The creation is perfomed in the MediatorTech to separate the logic around this panel from the class JTeachMain
     * @return JPanel containing the text and the animation
     */
    protected void setAnimationPanel(JPanel panel){
        animdescrPanel = panel;
        animdescrPanel.setBackground(Color.RED);
        /*animdescrPanel = new JPanel(new BorderLayout());
        animdescrPanel.setBackground(Color.BLUE);
        animdescrPanel.setBorder(BorderFactory.createEtchedBorder());
        
         
        descriptionPanel = new JPanel();
        descriptionPanel.setBackground(Color.RED);
        animdescrPanel.add(descriptionPanel, BorderLayout.NORTH);


        animationPanel = new JPanel();
        animationPanel.setBackground(Color.WHITE);
        animdescrPanel.add(animationPanel, BorderLayout.CENTER);

        
        return animdescrPanel; */

        
/* Box hBox = Box.createHorizontalBox();
        hBox.setBackground(Color.RED);
		hBox.add(Box.createHorizontalStrut(20));
		//Horizontal box containing Description label and buttons
		Box descrBox = Box.createVerticalBox();
		descrBox.add(new JLabel("ciao questa sarebbe la descrizione"));
		descrBox.add(Box.createHorizontalStrut(10));
		descrBox.setPreferredSize(new Dimension(220, 1000));
		descrBox.setMinimumSize(new Dimension(200, 200));
        descrBoxBox.setBackground(Color.YELLOW);
        hBox.add(descrBox);
        

        Box totalBox = Box.createVerticalBox();
		totalBox.add(Box.createVerticalStrut(30));
		totalBox.add(hBox);
		totalBox.add(Box.createVerticalStrut(30));	
        totalBox.setBackground(Color.BLUE);	
		mainPane.add(totalBox);
		hBox.add(descrBox);
		hBox.add(Box.createHorizontalStrut(10));
		JScrollPane visitTablePane = new JScrollPane();
		visitTablePane.setPreferredSize(new Dimension(1000, 1000));
		visitTablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		visitTablePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		hBox.add(visitTablePane);
		hBox.add(Box.createHorizontalStrut(20)); */    
        
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
        
        return labelbox;
    }

    //from this point we have some methods used to retrive some information of the Mediator
    
    /**
     * Retrieve HoverHelp from another class created in the Mediator, like the SelectPanel
     */
    public HoverHelp getHelp(){
        return help;
    }

    //from this point we define all the methods called from the actions performed by the buttons in JMTMenuBar or JMTToolBar

    /**
	 * Sends an exit signal to main window, so that it can close JTeach and go back to the JMT main menu
	 */
	public void exit() {
		mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
	}
    
    /*
     * Start the simulation
     */
    public  void startSimulation(){

    }

    /*
     * Increase the speed of the simulation
     */
    public void doubleVelocity() {
        
    }

    /*
     * Pause the simulation
     */
    public void pauseSimulation() {
        
    }

    /*
     * Stop the simulation and remove it from the panel
     */
    public void stopSimulation() {
        
    }

    /**
	 * Shows about window
	 */
	public void about() {
		AboutDialogFactory.showJMODEL(mainWindow);
	}
}
