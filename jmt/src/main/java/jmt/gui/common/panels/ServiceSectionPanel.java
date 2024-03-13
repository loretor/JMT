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

package jmt.gui.common.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jmt.framework.gui.table.editors.ButtonCellEditor;
import jmt.framework.gui.table.editors.ComboBoxCellEditor;
import jmt.framework.gui.table.editors.SpinnerCellEditor;
import jmt.framework.gui.wizard.WizardPanel;
import jmt.gui.common.CommonConstants;
import jmt.gui.common.Defaults;
import jmt.gui.common.definitions.ClassDefinition;
import jmt.gui.common.definitions.ServerType;
import jmt.gui.common.definitions.StationDefinition;
import jmt.gui.common.distributions.Distribution;
import jmt.gui.common.editors.DistributionsEditor;
import jmt.gui.common.editors.ImagedComboBoxCellEditorFactory;
import jmt.gui.common.editors.LDStrategyEditor;
import jmt.gui.common.serviceStrategies.DisabledStrategy;
import jmt.gui.common.serviceStrategies.LDStrategy;
import jmt.gui.common.serviceStrategies.ZeroStrategy;
import jmt.gui.jsimgraph.DialogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: orsotronIII
 * Date: 30-giu-2005
 * Time: 14.01.23
 * Modified by Bertoli Marco 7-oct-2005  --> Added load Dependent
 *                           9-jan-2006  --> ComboBoxCellEditor
 */
public class ServiceSectionPanel extends WizardPanel implements CommonConstants {

	private static final long serialVersionUID = 1L;

	protected Object stationKey;
	protected StationDefinition data;
	protected ClassDefinition classData;

	private ServiceTable serviceTable;
	private JSpinner serverNumSpinner;
	private JButton serverFeatureButton;
	private ServerFeaturePanel serverFeaturePanel;

	/** Used to display classes with icon */
	protected ImagedComboBoxCellEditorFactory classEditor;

	//editing of arrival time distribution
	protected AbstractAction editDistribution = new AbstractAction("Edit") {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.SHORT_DESCRIPTION, "Edits Service Time Distribution");
		}

		public void actionPerformed(ActionEvent e) {
			// ---- Bertoli Marco ----
			int index = serviceTable.getSelectedRow();
			if (index >= 0 && index < serviceTable.getRowCount()) {
				Object key = classData.getClassKeys().elementAt(index);
				Object service = data.getServiceTimeDistribution(stationKey, key);
				// If it is a Distribution, shows Distribution Editor
				if (service instanceof Distribution) {
					DistributionsEditor editor = DistributionsEditor.getInstance(ServiceSectionPanel.this.getParent(), (Distribution) service);
					// Sets editor window title
					editor.setTitle("Editing " + classData.getClassName(key) + " Service Time Distribution...");
					// Shows editor window
					editor.show();
					// Sets new Distribution to selected class
					data.setServiceTimeDistribution(stationKey, key, editor.getResult());

					// Updates table view. This is needed as Distribution is not contained
					// into edited cell (but in its left one)
					serviceTable.repaint();
				}
				// Otherwise shows LDStrategy Editor
				else {
					LDStrategyEditor editor = LDStrategyEditor.getInstance(ServiceSectionPanel.this.getParent(), (LDStrategy) service);
					// Sets editor window title
					editor.setTitle("Editing " + classData.getClassName(key) + " Load Dependent Service Strategy...");
					// Shows editor window
					editor.show();
					serviceTable.repaint();
				}
			}
			// ---- end ----
		}

	};

	private int getSpinnerMaxValue(Object classKey){
		int maxValue=0;
		for(int i = 0; i<data.getNumberOfDifferentServerTypes(stationKey); i++){
			ServerType server = data.getInfoForServerType(stationKey,i);
			if(server.isCompatible(classKey)){
				maxValue+=server.getNumOfServers();
			}
		}

		return maxValue;
	}

	protected AbstractAction editFeatures = new AbstractAction("Edit") {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.SHORT_DESCRIPTION, "Edits Server Configuration");
		}

		public void actionPerformed(ActionEvent e) {
			DialogFactory factory = new DialogFactory(null);
			factory.getDialog(serverFeaturePanel, "Editing Server Configuration",
					MAX_GUI_WIDTH_SERVER_FEATURES, MAX_GUI_HEIGHT_SERVER_FEATURES, false, "", "");
			updateServerPreferences();
			updateSpinnersInTable();
		}

	};

	public ServiceSectionPanel(StationDefinition sd, ClassDefinition cd, Object stationKey) {
		classEditor = new ImagedComboBoxCellEditorFactory(cd);
		setData(sd, cd, stationKey);
	}

	private void initComponents() {
		//building mainPanel
		this.setLayout(new BorderLayout(5, 5));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));

		// server configuration panel
		JPanel serverConfigPanel = new JPanel();
		serverConfigPanel.setBorder(new TitledBorder(new EtchedBorder(), "Server Configuration"));
		serverConfigPanel.add(new JLabel("Number of Servers:"));
		serverConfigPanel.add(serverNumSpinner);
		serverConfigPanel.add(new JLabel("         Advanced Features:"));
		serverConfigPanel.add(serverFeatureButton);
		this.add(serverConfigPanel, BorderLayout.NORTH);

		// service time distributions panel
		WarningScrollTable ServiceSectionTable = new WarningScrollTable(serviceTable, WARNING_CLASS);
		ServiceSectionTable.setBorder(new TitledBorder(new EtchedBorder(), "Service Time Distributions"));
		this.add(ServiceSectionTable, BorderLayout.CENTER);
	}

	public void setData(StationDefinition sd, ClassDefinition cd, Object stationKey) {
		this.removeAll();
		this.stationKey = stationKey;
		data = sd;
		classData = cd;
		serviceTable = new ServiceTable();
		serverNumSpinner = new JSpinner();
		serverNumSpinner.setPreferredSize(DIM_BUTTON_XS);
		serverFeatureButton = new JButton(editFeatures);
		serverFeatureButton.setPreferredSize(DIM_BUTTON_XS);
		//if (data.getStationType(stationKey).equals(STATION_TYPE_SERVER)) {
		serverFeaturePanel = new ServerFeaturePanel();
		serverFeaturePanel.setData(data, classData, stationKey);
		//}

		classEditor.setData(cd);
		initComponents();
		updateServerPreferences();
		addDataManagers();
	}

	private void updateServerPreferences() {
		String stationType = data.getStationType(stationKey);
		if (stationType.equals(STATION_TYPE_DELAY)) {
			serverNumSpinner.setValue(Double.POSITIVE_INFINITY);
			serverNumSpinner.setEnabled(false);
			//serverFeatureButton.setEnabled(false);
		} else {
			serverNumSpinner.setValue(data.getStationNumberOfServers(stationKey));
			//serverNumSpinner.setEnabled(true);
			String queuePolicy = data.getStationQueueStrategy(stationKey);
			//if (queuePolicy.equals(STATION_QUEUE_STRATEGY_NON_PREEMPTIVE)
			//		|| queuePolicy.equals(STATION_QUEUE_STRATEGY_NON_PREEMPTIVE_PRIORITY)) {
			serverFeatureButton.setEnabled(true);
			//} else {
			//	serverFeatureButton.setEnabled(false);
			//}
		}
	}

	private void updateSpinnersInTable(){
		ServiceTableModel stm = (ServiceTableModel) serviceTable.getModel();

		for(int i=0; i<stm.getRowCount(); i++){
			int maxValue = getSpinnerMaxValue(classData.getClassKeys().get(i));
			if((Integer) stm.getValueAt(i,4) > maxValue){
				stm.setValueAt(maxValue,i,4);
			}
		}

		serviceTable.repaint();
	}

	private void addDataManagers() {
		serverNumSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lostFocus();
				if (serverNumSpinner.getValue() instanceof Integer) {
					Integer serverNum = (Integer) serverNumSpinner.getValue();
					if (serverNum.intValue() < 1 && serverNumSpinner.isEnabled()) {
						serverNum = new Integer(1);
						serverNumSpinner.setValue(serverNum);
					}
					data.setStationNumberOfServers(stationKey, serverNum);
					data.updateNumOfServers(stationKey,serverNum);
					updateSpinnersInTable();
				}
			}
		});
	}

	@Override
	public void repaint() {
		if (serviceTable != null) {
			serviceTable.tableChanged(new TableModelEvent(serviceTable.getModel()));
		}
		super.repaint();
	}

	/**
	 * @return the panel's name
	 */
	@Override
	public String getName() {
		return "Service Section";
	}

	/**
	 * called by the Wizard before when switching to another panel
	 */
	@Override
	public void lostFocus() {
		// Aborts editing of table
		TableCellEditor editor = serviceTable.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
	}

	/**
	 * called by the Wizard when the panel becomes active
	 */
	@Override
	public void gotFocus() {
		classEditor.clearCache();
		updateServerPreferences();
	}

	protected class ServiceTable extends JTable {

		private static final long serialVersionUID = 1L;

		/**
		 * This field is used to initialize elements shown on Service type selection - Bertoli Marco
		 */
		protected Object[] serviceType = new Object[] { SERVICE_LOAD_INDEPENDENT, SERVICE_LOAD_DEPENDENT, SERVICE_ZERO, SERVICE_DISABLED };

		JButton editButton = new JButton() {

			private static final long serialVersionUID = 1L;

			{
				setText("Edit");
			}
		};

		protected Integer serverNumRequired;

		int[] columnSizes = new int[] { 80, 80, 150, 30, 30 };

		private void initializeSpinners(){
			ServiceTableModel stm = (ServiceTableModel) this.getModel();

			for(int i=0; i<stm.getRowCount(); i++){
				int serverNumRequired = 1;
				if(data.getServerNumRequired(stationKey,classData.getClassKeys().get(i)) != null){
					serverNumRequired = data.getServerNumRequired(stationKey,classData.getClassKeys().get(i));
				}
				stm.setValueAt(serverNumRequired,i,4);
			}
		}

		public ServiceTable() {
			setModel(new ServiceTableModel());
			sizeColumns();
			setRowHeight(ROW_HEIGHT);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			getTableHeader().setReorderingAllowed(false);
			initializeSpinners();
		}

		@Override
		public TableCellRenderer getCellRenderer(int row, int column) {
			if (column == 0) {
				return classEditor.getRenderer();
			} else if (column == 1) {
				return ComboBoxCellEditor.getRendererInstance();
			} else if (column == 3) {
				return new DisabledButtonCellRenderer(editButton);
			} else if (column == 4) {
				return SpinnerCellEditor.getRendererInstance();
			} else {
				return super.getCellRenderer(row, column);
			}
		}

		@Override
		public TableCellEditor getCellEditor(int row, int column) {
			if (column == 1) {
				return ComboBoxCellEditor.getEditorInstance(serviceType);
			} else if (column == 3) {
				return new ButtonCellEditor(new JButton(editDistribution));
			} else if (column == 4) {
				Object classKey = classData.getClassKeys().get(row);
				serverNumRequired = data.getServerNumRequired(stationKey,classKey);
				int maxValue = getSpinnerMaxValue(classKey);
				SpinnerCellEditor instance =  SpinnerCellEditor.getEditorInstance(serverNumRequired, maxValue);
				instance.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						ServiceSectionPanel.this.lostFocus();
					}
				});
				return instance;
			} else {
				return super.getCellEditor(row, column);
			}
		}

		private void sizeColumns() {
			for (int i = 0; i < columnSizes.length && i < getColumnCount(); i++) {
				this.getColumnModel().getColumn(i).setPreferredWidth(columnSizes[i]);
			}
		}

	}

	protected class ServiceTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private String[] columnNames = new String[] { "Class", "Strategy", "Service Time Distribution","","Parallelism" };
		private Class<?>[] columnClasses = new Class[] { String.class, String.class, String.class, Object.class, String.class };

		public int getRowCount() {
			return classData.getClassKeys().size();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnClasses[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			Object classKey = classData.getClassKeys().get(rowIndex);
			if (columnIndex == 0) {
				return false;
			} else if (columnIndex == 2) {
				return false;
			} else if (columnIndex == 3 && data.getServiceTimeDistribution(stationKey, classKey) instanceof ZeroStrategy) {
				return false;
			} else if (columnIndex == 3 && data.getServiceTimeDistribution(stationKey, classKey) instanceof DisabledStrategy) {
				return false;
			} else {
				return true;
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Object classKey = classData.getClassKeys().get(rowIndex);
			switch (columnIndex) {
				case (0):
					return classKey;
				case (1):
					// Checks if current service section is load dependent or independent
					if (data.getServiceTimeDistribution(stationKey, classKey) instanceof LDStrategy) {
						return SERVICE_LOAD_DEPENDENT;
					} else if (data.getServiceTimeDistribution(stationKey, classKey) instanceof ZeroStrategy) {
						return SERVICE_ZERO;
					} else if (data.getServiceTimeDistribution(stationKey, classKey) instanceof DisabledStrategy) {
						return SERVICE_DISABLED;
					} else {
						return SERVICE_LOAD_INDEPENDENT;
					}
				case (2):
					return data.getServiceTimeDistribution(stationKey, classKey);
				case (4):
					return data.getServerNumRequired(stationKey,classKey);
			}
			return null;
		}

		/**Puts edited values to the underlying data structure for model implementation*/
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Object classKey = classData.getClassKeys().get(rowIndex);
			switch (columnIndex) {
				// Load dependency
				case (1):
					if (((String) aValue).equals(SERVICE_LOAD_DEPENDENT)) {
						// Puts a Load Dependent Service Strategy only if previously it was different
						if (!(data.getServiceTimeDistribution(stationKey, classKey) instanceof LDStrategy)) {
							data.setServiceTimeDistribution(stationKey, classKey, new LDStrategy());
						}
					} else if (((String) aValue).equals(SERVICE_ZERO)) {
						// Puts a Zero Service Time Strategy only if previously it was different
						if (!(data.getServiceTimeDistribution(stationKey, classKey) instanceof ZeroStrategy)) {
							data.setServiceTimeDistribution(stationKey, classKey, new ZeroStrategy());
						}
					} else if (((String) aValue).equals(SERVICE_DISABLED)) {
						// Puts a Disabled Service Time Strategy only if previously it was different
						if (!(data.getServiceTimeDistribution(stationKey, classKey) instanceof DisabledStrategy)) {
							data.setServiceTimeDistribution(stationKey, classKey, new DisabledStrategy());
						}
					} else {
						// Puts the default service strategy only if previously it was different
						if (!(data.getServiceTimeDistribution(stationKey, classKey) instanceof Distribution)) {
							Object distribution = Defaults.getAsNewInstance("stationServiceStrategy");
							data.setServiceTimeDistribution(stationKey, classKey, distribution);
						}
					}
				case (4):
					Pattern pattern = Pattern.compile("[0-9]*");
					if(pattern.matcher(aValue.toString()).matches()) {
						data.setServerNumRequired(stationKey, classKey, (Integer) aValue);
					}
					repaint();
			}
		}

	}

	private class DisabledButtonCellRenderer extends ButtonCellEditor {

		private static final long serialVersionUID = 1L;

		private JButton button;

		public DisabledButtonCellRenderer(JButton jbutt) {
			super(jbutt);
			button = jbutt;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (table.isCellEditable(row, column)) {
				button.setEnabled(true);
			} else {
				button.setEnabled(false);
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

	}

}