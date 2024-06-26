package jmt.gui.jwat.workloadAnalysis.clustering.fuzzyKMean.panels;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import jmt.engine.jwat.ProgressStatusListener;
import jmt.engine.jwat.VariableNumber;
import jmt.engine.jwat.input.ProgressMonitorShow;
import jmt.engine.jwat.workloadAnalysis.clustering.fuzzyKMean.MainFuzzyKMean;
import jmt.engine.jwat.workloadAnalysis.utils.ModelWorkloadAnalysis;
import jmt.framework.gui.wizard.WizardPanel;
import jmt.gui.common.CommonConstants;
import jmt.gui.common.JMTImageLoader;
import jmt.gui.jwat.MainJwatWizard;
import jmt.gui.jwat.workloadAnalysis.panels.ClusterPanel;

public class FuzzyOptPanel extends JPanel implements CommonConstants {

	private static final long serialVersionUID = 1L;

	private ClusterPanel parent;

	private final String fuzzylevelDescription = HTML_START + HTML_FONT_NORM + "Specify the fuzziness level<p>for the algorithm"
			+ HTML_FONT_NOR_END + HTML_END;
	private final String FKMEANS_DESCRIPTION = HTML_START + HTML_FONT_TITLE + "Fuzzy k-Means Clustering" + HTML_FONT_TIT_END + HTML_FONT_NORM
			+ "Specify maximum number of clusters, iterations and fuzziness that the algorithm has to perform" + HTML_FONT_NOR_END + HTML_END;
	private final String FKMEANS_OPTIONS_DESCRIPTION = HTML_START + HTML_FONT_TITLE + "Transformations" + HTML_FONT_TIT_END + HTML_FONT_NORM
			+ "Specify the transformation to apply to<p>" + "selected variables." + HTML_FONT_NOR_END + HTML_END;
	private final String FKMEANS_FLEVEL_DESCRIPTION = HTML_START + HTML_FONT_TITLE + "Fuzziness Level" + HTML_FONT_TIT_END + HTML_FONT_NORM
			+ "Fuzziness level is the fuzzy exponent and<p>" + "range from (2 to 100).It determines the<p>"
			+ "degree of fuzziness of the final solution" + HTML_FONT_NOR_END + HTML_END;

	private static final int BUTTONSIZE = 25;

	private ProgressStatusListener lst;
	private ModelWorkloadAnalysis model;
	private JRadioButton noneT;
	private JRadioButton minmaxT;
	private JRadioButton stdDevT;
	private JSpinner numOfClust;
	private JSpinner numOfIter;
	private JSpinner fuzzyLevel;

	public FuzzyOptPanel(WizardPanel parent, ProgressStatusListener p, ModelWorkloadAnalysis m) {
		this.parent = (ClusterPanel) parent;
		this.model = m;
		this.lst = p;
		setSolveAction();
		initPanel();
	}

	private void initPanel() {
		this.setLayout(new BorderLayout());

		//UPDATE 13 Nov 2006
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(Box.createHorizontalStrut(5), BorderLayout.EAST);
		northPanel.add(new JLabel(FKMEANS_DESCRIPTION), BorderLayout.CENTER);
		this.add(northPanel, BorderLayout.NORTH);

		JPanel centralPanel = new JPanel(new BorderLayout());
		this.add(centralPanel, BorderLayout.CENTER);
		//NORTH Panel options
		JPanel northCPanel = new JPanel(new GridLayout(2, 1));

		JPanel clustNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));
		clustNorth.add(new JLabel("number of clusters:    "));
		numOfClust = new JSpinner(new SpinnerNumberModel(3, 2, 37, 1));
		clustNorth.add(numOfClust);

		JPanel iterNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));
		iterNorth.add(new JLabel("number of iterations: "));
		numOfIter = new JSpinner(new SpinnerNumberModel(4, 2, 50, 1));
		iterNorth.add(numOfIter);

		northCPanel.add(clustNorth);
		northCPanel.add(iterNorth);

		centralPanel.add(northCPanel, BorderLayout.NORTH);

		JPanel fuzzyPanel = new JPanel(new BorderLayout());
		fuzzyPanel.setBorder(new TitledBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Fuzzy options")));

		JPanel option = new JPanel(new BorderLayout());
		JPanel northOpt = new JPanel(new FlowLayout(FlowLayout.LEFT));
		northOpt.add(new JLabel("fuzziness level: "));
		fuzzyLevel = new JSpinner(new SpinnerNumberModel(2, 2.0, 50.0, 0.1));
		northOpt.add(fuzzyLevel);
		option.add(northOpt, BorderLayout.NORTH);
		option.add(new JLabel(fuzzylevelDescription), BorderLayout.CENTER);

		JPanel descrFuzzy = new JPanel();
		descrFuzzy.add(new JLabel(FKMEANS_FLEVEL_DESCRIPTION));

		fuzzyPanel.add(option, BorderLayout.WEST);
		fuzzyPanel.add(descrFuzzy, BorderLayout.CENTER);

		centralPanel.add(fuzzyPanel, BorderLayout.CENTER);

		JPanel transf = new JPanel(new BorderLayout()); //Terzo panel
		transf.setBorder(new TitledBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Transformations")));
		JPanel options = new JPanel(new GridLayout(3, 1)); //Panel ratio buttons
		noneT = new JRadioButton("none");
		minmaxT = new JRadioButton("(value - min) / (max - min)");
		stdDevT = new JRadioButton("(value - mean) / std. dev.");
		ButtonGroup op = new ButtonGroup();
		op.add(noneT);
		minmaxT.setSelected(true);
		op.add(minmaxT);
		op.add(stdDevT);
		options.add(noneT);
		options.add(minmaxT);
		options.add(stdDevT);
		transf.add(options, BorderLayout.EAST);
		transf.add(new JLabel(FKMEANS_OPTIONS_DESCRIPTION), BorderLayout.WEST);

		centralPanel.add(transf, BorderLayout.SOUTH);
	}

	private void setSolveAction() {
		Action buttonAction = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (parent.getVarSelected().length >= 2) {
					short trasf = VariableNumber.NONE;
					if (minmaxT.isSelected()) {
						trasf = VariableNumber.MINMAX;
					}
					if (stdDevT.isSelected()) {
						trasf = VariableNumber.STDEV;
					}
					MainFuzzyKMean eng = new MainFuzzyKMean(new ProgressMonitorShow(parent, "Processing...", 1), model.getMatrix(),
							parent.getVarSelected(), ((Integer) numOfClust.getValue()).intValue(), ((Integer) numOfIter.getValue()).intValue(),
							((Double) fuzzyLevel.getValue()).intValue(), trasf);
					eng.addStatusListener(lst);
					eng.start();
				} else {
					JOptionPane.showMessageDialog(parent, "Select at least two variables to proceed with clustering", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}
			}

		};
		((MainJwatWizard) parent.getParentWizard()).setActionButton("Solve", buttonAction);

		Action itemAction = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			{
				putValue(Action.SHORT_DESCRIPTION, "Clusterize");
				putValue(Action.SMALL_ICON, JMTImageLoader.loadImage("Sim", new Dimension(BUTTONSIZE + 10,BUTTONSIZE + 10)));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
				putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
			}

			public void actionPerformed(ActionEvent e) {
				if (parent.getVarSelected().length >= 2) {
					short trasf = VariableNumber.NONE;
					if (minmaxT.isSelected()) {
						trasf = VariableNumber.MINMAX;
					}
					if (stdDevT.isSelected()) {
						trasf = VariableNumber.STDEV;
					}
					MainFuzzyKMean eng = new MainFuzzyKMean(new ProgressMonitorShow(parent, "Processing...", 1), model.getMatrix(),
							parent.getVarSelected(), ((Integer) numOfClust.getValue()).intValue(), ((Integer) numOfIter.getValue()).intValue(),
							((Double) fuzzyLevel.getValue()).intValue(), trasf);
					eng.addStatusListener(lst);
					eng.start();
				} else {
					JOptionPane.showMessageDialog(parent, "Select at least two variables to proceed with clustering", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}
			}

		};
		((MainJwatWizard) parent.getParentWizard()).setActionMenuBar("Clusterize", itemAction);
		((MainJwatWizard) parent.getParentWizard()).setActionToolBar("Clusterize", itemAction);

		((MainJwatWizard) parent.getParentWizard()).setEnableButton("Solve", true);
	}

}
