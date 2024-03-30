package jmt.jteach.Wizard;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;


import jmt.framework.gui.help.HoverHelp;
import jmt.framework.gui.wizard.Wizard;


public class JTchWizard extends Wizard{

    private static final long serialVersionUID = 1L;
	private HoverHelp help;
	private JLabel helpLabel;
	private JButton[] btnList;

	protected JMenuBar menuBar;
    protected JToolBar toolBar;

	public HoverHelp getHelp() {
		return help;
	}

    /**
     * Method of Wizard of creating the Button Panel.
     * Ovveride it to add also the helpLabel.
     */
	@Override
	protected JComponent makeButtons() {
		help = new HoverHelp();
		helpLabel = help.getHelpLabel();

		helpLabel.setBorder(BorderFactory.createEtchedBorder());

		ACTION_FINISH.putValue(Action.NAME, "Solve");
		ACTION_CANCEL.putValue(Action.NAME, "Exit");

		JPanel buttons = new JPanel();
		btnList = new JButton[5];

		/* Added first pane of all */

		JButton button_finish = new JButton(ACTION_FINISH);
		help.addHelp(button_finish, "Validates choices and solve");
		JButton button_cancel = new JButton(ACTION_CANCEL);
		help.addHelp(button_cancel, "Exits the wizard discarding all changes");
		JButton button_next = new JButton(ACTION_NEXT);
		help.addHelp(button_next, "Moves on to the next step");
		JButton button_previous = new JButton(ACTION_PREV);
		help.addHelp(button_previous, "Goes back to the previous step");
		buttons.add(button_previous);
		btnList[0] = button_previous;
		buttons.add(button_next);
		btnList[1] = button_next;
		buttons.add(button_finish);
		btnList[2] = button_finish;
		buttons.add(button_cancel);
		btnList[3] = button_cancel;
		
		JPanel labelbox = new JPanel();
		labelbox.setLayout(new BorderLayout());
		labelbox.add(Box.createVerticalStrut(30), BorderLayout.WEST);
		labelbox.add(helpLabel, BorderLayout.CENTER);

		Box buttonBox = Box.createVerticalBox();
		buttonBox.add(buttons);
		buttonBox.add(labelbox);
		return buttonBox;
	}

    public void setEnableButton(String button, boolean enabled) {
		for (JButton element : btnList) {
			if (element.getText().equals(button)) {
				element.setEnabled(enabled);
				break;
			}
		}
    }

    public HoverHelp getHoverHelp(){
        return help;
    }
}
