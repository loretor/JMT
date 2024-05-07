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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicMenuUI;

import jmt.framework.gui.components.JMTMenuBar;
import jmt.framework.gui.components.JMTToolBar;
import jmt.framework.gui.help.HoverHelp;
import jmt.framework.gui.listeners.MenuAction;
import jmt.framework.gui.wizard.WizardPanel;
import jmt.gui.common.JMTImageLoader;
import jmt.gui.jwat.JWatWizard;
import jmt.jteach.ConstantsJTch;
import jmt.jteach.Wizard.MainWizard;
import jmt.jteach.Wizard.WizardPanelTCH;
import jmt.jteach.actionsWizard.AbstractTCHAction;
import jmt.jteach.actionsWizard.Help;
import jmt.jteach.animation.QueuePolicyNonPreemptive;
import jmt.jteach.animation.RoutingPolicy;

/**
 * Main Panel of the MainWizard. This panel offers the possibility of choosing between a JTeach Model or Markov chain.
 *
 * @author Lorenzo Torri
 * Date: 29-mar-2024
 * Time: 15.04
 */
public class MainPanel extends WizardPanel implements WizardPanelTCH{

    private static final String PANEL_NAME = "Main Panel";
    private static final String IMG_STARTSCREEN = "StartScreenJTeach";

    private MainWizard parent;
	private JMTMenuBar menu;
	private JMTToolBar toolbar;
    private HoverHelp help; //retrieve from parent the HoverHelp

	//----------- variables for the panel with all the buttons of the graph
	private final String[] data = {"NON-PREEMPTIVE", "", "PREEMPTIVE", "", "RR", "", "PROBABLISITC", "", "...", "", "MARKOV CHAINS"}; //some of them are empty because they represent the box empty between two elements of the list
	private final JList<String> list = new JList<>(data);

	//all actions associated to the buttons of the Menu and ToolBar
    private AbstractTCHAction openHelp;


	//all the AbstractActions associated to the buttons related of this panel only
	protected AbstractAction FCFS = new AbstractAction("FCFS") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.PREEMPTIVE_TOOLTIPS[0]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(QueuePolicyNonPreemptive.FIFO);
		}
	};

	protected AbstractAction LCFS = new AbstractAction("LCFS") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.PREEMPTIVE_TOOLTIPS[1]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(QueuePolicyNonPreemptive.LIFO);
		}
	};

	protected AbstractAction PRIO = new AbstractAction("Priority") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.PREEMPTIVE_TOOLTIPS[2]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(QueuePolicyNonPreemptive.PRIO);
		}
	};

	protected AbstractAction SJF = new AbstractAction("SJF") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.PREEMPTIVE_TOOLTIPS[3]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(QueuePolicyNonPreemptive.SJF);
		}
	};

	protected AbstractAction LJF = new AbstractAction("LJF") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.PREEMPTIVE_TOOLTIPS[4]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(QueuePolicyNonPreemptive.LJF);
		}
	};

	//TODO: preemptive buttons

	protected AbstractAction RR = new AbstractAction("Rond Robin") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.ROUTING_TOOLTIPS[0]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(RoutingPolicy.RR);
		}
	};

	protected AbstractAction PROBABILISTIC = new AbstractAction("Probabilistic") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.ROUTING_TOOLTIPS[1]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(RoutingPolicy.PROBABILISTIC);
		}
	};

	protected AbstractAction JSQ = new AbstractAction("JSQ") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.ROUTING_TOOLTIPS[2]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setAnimationPanelEnv(RoutingPolicy.JSQ);
		}
	};

	protected AbstractAction MM1 = new AbstractAction("M/M/1") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.MARKOV_TOOLTIPS[0]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setMMQueuesPanelEnv("mm1");	
		}
	};

	protected AbstractAction MM1K = new AbstractAction("M/M/1/k") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.MARKOV_TOOLTIPS[1]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setMMQueuesPanelEnv("mm1k");
		}
	};

	protected AbstractAction MMC = new AbstractAction("M/M/c") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.MARKOV_TOOLTIPS[2]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setMMQueuesPanelEnv("mmn");
		}
	};

	protected AbstractAction MMCK = new AbstractAction("M/M/c/k") {
		private static final long serialVersionUID = 1L;
		{
			putValue(Action.SHORT_DESCRIPTION, ConstantsJTch.MARKOV_TOOLTIPS[3]);
		}

		public void actionPerformed(ActionEvent e) {
			parent.setMMQueuesPanelEnv("mmnk");
		}
	};

    public MainPanel(MainWizard main){
        this.parent = main;
		help = parent.getHoverHelp();

        openHelp = new Help(this,"JTCH");

        initGUI();
    }

    public void initGUI(){
		this.setLayout(new BorderLayout());

		//---------------upper and bottom panels
		JPanel upper = new JPanel(new FlowLayout());
		JLabel upperLabel = new JLabel();
		upperLabel.setPreferredSize(new Dimension(300, 10));
		upper.add(upperLabel);
			
		JPanel bottom = new JPanel(new FlowLayout());
		JLabel bottomLabel = new JLabel();
		bottomLabel.setPreferredSize(new Dimension(300, 10));
		bottom.add(bottomLabel);

		this.add(upper, BorderLayout.NORTH);
		this.add(bottom, BorderLayout.SOUTH);

		//---------------central panel
		JPanel centerPanel = new JPanel(new GridBagLayout());
      
        //---------------------flow chart schema
		JLabel imageLabel = new JLabel();
		imageLabel.setIcon(JMTImageLoader.loadImage(IMG_STARTSCREEN, new Dimension(540, 455)));
		centerPanel.add(imageLabel);

		//---------------------panel with all the buttons
		JPanel eastPanel = new JPanel(new FlowLayout());
		list.setCellRenderer(selectedRender(-1)); //-1 since no element is highlighted
		list.setBackground(null);

		//TODO: add help for each element of the list
		ListModel<String> model = list.getModel();
		ListCellRenderer<? super String> cellRenderer = list.getCellRenderer();
		List<Component> components = new ArrayList<>();
		for(int i = 0; i < model.getSize(); i++){
			if(i % 2 == 0){
				Component component = cellRenderer.getListCellRendererComponent(list, list.getModel().getElementAt(i), i, false, false);
				if(component != null){
					components.add(component);
				}
				
			}
		}

		for(Component c: components){
			c.setBackground(Color.RED);
			help.addHelp(c, "ciao");
		}

		help.addHelp(list, "Select the JTCH Mode");

		
		eastPanel.add(list, BorderLayout.CENTER);

		final JPopupMenu[] popupMenu = createSubMenus();

		//change the type of subMenu each time the cursor moves
        list.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
				//if the selected item in the list is -1 or is not a separator, or its subMenu has already at least one element, we can show its submenu, otherwise setVisible = false to all subMenus
                if (index != -1 && index % 2 == 0 && popupMenu[index].getSubElements().length != 0) { 
                    Rectangle cellBounds = list.getCellBounds(index, index);
                    if (cellBounds != null && cellBounds.contains(e.getPoint())) {
                        popupMenu[index].show(list, cellBounds.x + cellBounds.width, cellBounds.y);						
                        list.setCellRenderer(selectedRender(index));
                    } else {
                        popupMenu[index].setVisible(false);
						list.setCellRenderer(selectedRender(-1));
                    }
                } else {
					list.setCellRenderer(selectedRender(-1));
					for(int i = 0; i < popupMenu.length; i++){
						popupMenu[i].setVisible(false);					
					}                
                }
            }
        });


		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		centerPanel.add(eastPanel, gbc);

		this.add(centerPanel, BorderLayout.CENTER);
		
        createMenu();
        createToolBar();
    }

	/**
	 * To set the look of each element inside the JList.
	 * @param i index of the element of the JList that needs to be rendered
	 * @return ListCellRenderer, a class that is encharge of changing the look of the cell
	 */
	public ListCellRenderer<String> selectedRender(final int i){
		return new ListCellRenderer<String>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            	JLabel label = new JLabel(value);
            	
            	if(index % 2 != 0) { //index odd are divisors
            		label.setPreferredSize(new Dimension(150,40));
            		label.setMinimumSize(new Dimension(150,40));
            	}
            	else {            		
					label.setHorizontalAlignment(SwingConstants.CENTER);
					Border borderEmpty = BorderFactory.createEmptyBorder(12, 5, 12, 5);
					Border borderEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
					Border compoundBorder = BorderFactory.createCompoundBorder(borderEtched, borderEmpty);
                    label.setBorder(compoundBorder);
					if(index == i) { //if the label is selected then highlight it
            			label.setBackground(Color.DARK_GRAY);
            		}
					else{
						label.setBackground(null);
					}
            	}
                
                return label;
            }
        };
	}

	/**
	 * To create all the subMenus for each element of the JList
	 * @return the array of subMenus
	 */
	public JPopupMenu[] createSubMenus() {
		JPopupMenu[] subMenus = new JPopupMenu[11];
		
		subMenus[0] = new JPopupMenu();
		subMenus[0].add(new CustomMenuItem(FCFS, true));
		subMenus[0].add(new CustomMenuItem(LCFS, true));
		subMenus[0].add(new CustomMenuItem(PRIO, true));
		subMenus[0].add(new CustomMenuItem(SJF, true));
		subMenus[0].add(new CustomMenuItem(LJF, true));
		
		subMenus[1] = new JPopupMenu(); //subMenu for the separator
		
		subMenus[2] = new JPopupMenu();
		
		subMenus[3] = new JPopupMenu(); //subMenu for the separator
		
		subMenus[4] = new JPopupMenu();
		subMenus[4].add(new CustomMenuItem(RR, true));
		
		subMenus[5] = new JPopupMenu(); //subMenu for the separator
		
		subMenus[6] = new JPopupMenu();
		subMenus[6].add(new CustomMenuItem(PROBABILISTIC, true));
		
		subMenus[7] = new JPopupMenu(); //subMenu for the separator
		
		subMenus[8] = new JPopupMenu();
		subMenus[8].add(new CustomMenuItem(JSQ, true));
		subMenus[8].add(new CustomMenuItem("Random", false));
		subMenus[8].add(new CustomMenuItem("Shortest Response Time", false));
		subMenus[8].add(new CustomMenuItem("Least Utilization", false));
		subMenus[8].add(new CustomMenuItem("Fastest Service", false));
		subMenus[8].add(new CustomMenuItem("Load Dependend Routing", false));
		subMenus[8].add(new CustomMenuItem("Power of K", false));
		subMenus[8].add(new CustomMenuItem("Weighted Round Robin", false));
		subMenus[8].add(new CustomMenuItem("Class Switch", false));


		subMenus[9] = new JPopupMenu(); //subMenu for the separator

		subMenus[10] = new JPopupMenu();
		subMenus[10].add(new CustomMenuItem(MM1, true));
		subMenus[10].add(new CustomMenuItem(MM1K, true));
		subMenus[10].add(new CustomMenuItem(MMC, true));
		subMenus[10].add(new CustomMenuItem(MMCK, true));
		
		return subMenus;	
	}

    @Override
    public String getName() {
        return PANEL_NAME;
    }

    /**
	 * Creates a menu to be displayed in the MainWizard
	 */
	public void createMenu() {
		menu = new JMTMenuBar(JMTImageLoader.getImageLoader());

        //File window
        MenuAction action = new MenuAction("File", new AbstractTCHAction[] { null});

        //Help window
        action = new MenuAction("Help", new AbstractTCHAction[] {openHelp, null});
		menu.addMenu(action);

        parent.setMenuBar(menu);
	}

    /**
	 * Creates a toolbar to be displayed in the MainWizard.
	 */
	public void createToolBar() {
        toolbar = new JMTToolBar(JMTImageLoader.getImageLoader());	

        //first add all the icons with their actions
        AbstractTCHAction[] actions = new AbstractTCHAction[] {null, openHelp}; // Builds an array with all actions to be put in the toolbar
		String[] helpText = {"Open the help page"};
        toolbar.populateToolbar(actions);
        ArrayList<AbstractButton> buttons = new ArrayList<AbstractButton>(); //create a list of AbstractButtons for the helpLabel
		buttons.addAll(toolbar.populateToolbar(actions));

        //add help for each Action/JComboBox with helpLabel
		for (int i = 0; i < buttons.size(); i++) {
			AbstractButton button = buttons.get(i);
			help.addHelp(button, helpText[i]);
		}
		  
		parent.setToolBar(toolbar);
	}

    //Overrided methods for performing the actions when the buttons of JToolBar or JMenuBar are pressed
    @Override
    public void openHelp() {
        
    }

	@Override
	public void startAnimation() {
	}

	@Override
	public void pauseAnimation() {
	}

	@Override
	public void reloadAnimation() {
	}

	@Override
	public void nextStepAnimation() {

	}

	@Override
	public void exit() {
	}

	@Override
	public void stopAnimation() {
	}   
	
	
	@Override
	public void gotFocus() { //this method is essential for controlling if the user tries to go back to the main panel from a panel like the Result one
		if (parent.getNumbersPanel() > 0) {
			if (JOptionPane.showConfirmDialog(this, "This operation resets all data. Are you sure you want to go back to start screen?", "Back operation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.NO_OPTION){
				parent.resetScreen();
				((JWatWizard) getParentWizard()).setEnableButton("Next >", false);
			} 
		}
	}
}


/**
 * Class for a custom item of the JPopMenu
 * Two possible outcomes
 *  - a cell that is selectable (if an animation panel with the correspondant algoritm is present)
 *  - a cell that is not selectable
 */
class CustomMenuItem extends JMenuItem{
	public CustomMenuItem(Action a, boolean implemented){
		super(a);

		if(!implemented){
			this.setEnabled(false);
		}
	}

	public CustomMenuItem(String txt, boolean implemented){
		super(txt);

		if(!implemented){
			this.setEnabled(false);
		}
	}

}