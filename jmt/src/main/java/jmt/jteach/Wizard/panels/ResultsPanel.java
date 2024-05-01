package jmt.jteach.Wizard.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellRenderer;

import jmt.framework.data.ArrayUtils;
import jmt.framework.gui.table.editors.ButtonCellEditor;
import jmt.framework.gui.wizard.WizardPanel;
import jmt.gui.common.CommonConstants;
import jmt.gui.common.JMTImageLoader;
import jmt.gui.exact.table.ComboBoxCell;
import jmt.gui.exact.table.DisabledCellRenderer;
import jmt.gui.exact.table.ExactTable;
import jmt.gui.exact.table.ExactTableModel;
import jmt.gui.exact.table.ListOp;

/**
 * Panel for showing the results of the simulations
 *
 * @author Lorenzo Torri
 * Date: 29-apr-2024
 * Time: 20.10
 */
public class ResultsPanel extends WizardPanel{
    private static final String PANEL_NAME = "Results";

    //------------ Variables of the TABLE
    private ResultTable table;
    private int nResults = 0; //n of Simulations, which is also the number of rows of the table

    private List<ListOp> classOps; //for keeping track of row deletions and insertions
	private boolean hasDeletes;
	private boolean deleting = false;

    // Column numbers
	private final static int COL_ALGO = 0;
	private final static int COL_DISTR_ARRIVAL = 1;
	private final static int COL_LAMBDA = 2;
	private final static int COL_DISTR_SERVICE = 3;
	private final static int COL_SERVICE = 4;
    private final static int COL_R = 5;
    private final static int COL_QUEUETIME = 6; 
    private final static int COL_NQUEUE = 7;
    private final static int COL_NSTATION = 8;
	private final static int COL_DELETE_BUTTON = 9;

    //for each column, an array of values
    private String[] algorithms = new String[0];
    private String[] arrivalDistibutions = new String[0];
    private double[] lambdas = new double[0];
    private String[] serviceDistributions = new String[0];
    private double[] services = new double[0];
    private double[] responseTimes = new double[0];
    private double[] queueTimes = new double[0];
    private int[] queueNumbers = new int[0];
    private int[] stationNumbers = new int[0];

    /** This action is to add a new row in the table */
    private AbstractAction addResult = new AbstractAction("New Simulation") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
			putValue(Action.SHORT_DESCRIPTION, "Adds a new Simulation to Model");
		}

		public void actionPerformed(ActionEvent e) {
			addRow();
		}
	};

    /** This Action is only for displaying the X in each row */
    private AbstractAction deleteOneResult = new AbstractAction("") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			putValue(Action.SHORT_DESCRIPTION, "Delete This Simulation");
			putValue(Action.SMALL_ICON, JMTImageLoader.loadImage("Delete"));
		}

		public void actionPerformed(ActionEvent e) {
		}
	};

    /** This action is for deleting more rows in one time */
    private AbstractAction deleteResults = new AbstractAction("Delete selected classes") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false));
			putValue(Action.SHORT_DESCRIPTION, "Deletes selected classes from the system");
		}

		public void actionPerformed(ActionEvent e) {
			deleteSelectedRows();
		}
	};

    private void addRow() {
		setNumberOfResults(nResults + 1);
	}

    /** Change the size of data structures updating also the table */
    private void setNumberOfResults(int number) {
		table.stopEditing();
		nResults = number;

        //TODO: remove this value only for debugging
		algorithms = ArrayUtils.resize(algorithms, nResults, null);
        algorithms[nResults-1] = new String("SJF");
		arrivalDistibutions = ArrayUtils.resize(arrivalDistibutions, nResults, null);
        arrivalDistibutions[nResults-1] = new String("Exp");
		lambdas = ArrayUtils.resize(lambdas, nResults, 0.0);
        lambdas[nResults-1] = 3.0;
		serviceDistributions = ArrayUtils.resize(serviceDistributions, nResults, null);
        serviceDistributions[nResults-1] = new String("Exp");
        services = ArrayUtils.resize(services, nResults, 0.0);
        services[nResults-1] =  4.0;
        responseTimes = ArrayUtils.resize(responseTimes, nResults, 0.0);
        responseTimes[nResults-1] = 5.0;
        queueTimes = ArrayUtils.resize(queueTimes, nResults, 0.0);
        queueTimes[nResults-1] = 6.0;
        queueNumbers = ArrayUtils.resize(queueNumbers, nResults, 0);
        queueNumbers[nResults-1] = 7;
        stationNumbers = ArrayUtils.resize(stationNumbers, nResults, 0);
        stationNumbers[nResults-1] = 7;

		updateTable();
	}

    /** Update the changes on the table */
    private void updateTable(){
        table.updateStructure();
		if (!deleting) {
			classOps.add(ListOp.createResizeOp(nResults));
		}

		table.updateDeleteCommand();
    }


    public ResultsPanel(){
        classOps = new ArrayList<ListOp>();

        initGUI();
    }

    public void initGUI(){
        table = new ResultTable();

        Box descrBox = Box.createHorizontalBox();
        JLabel spinnerLabel = new JLabel("ciao");  
        descrBox.add(spinnerLabel);
        descrBox.add(Box.createHorizontalStrut(10));

        Box numberBox = Box.createVerticalBox();
        numberBox.add(new JButton(addResult));
        descrBox.add(numberBox);

        Box classBox = Box.createVerticalBox();
		classBox.add(Box.createVerticalStrut(30));
        JScrollPane tablePane = new JScrollPane(table);
		tablePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        classBox.add(tablePane);
		classBox.add(Box.createVerticalStrut(30));

        Box totalBox = Box.createHorizontalBox();
        classBox.add(descrBox);
		classBox.add(Box.createVerticalStrut(10));
		totalBox.add(Box.createHorizontalStrut(20));
		totalBox.add(classBox);
		totalBox.add(Box.createHorizontalStrut(20));

		setLayout(new BorderLayout());
		this.add(totalBox, BorderLayout.CENTER);

        addRow();
    }

    /** Adds the possibility of deleting multiple rows by selecting them with 2 clicks of the mouse + Shift */
    private void deleteSelectedRows() {
		int[] selectedRows = table.getSelectedRows();
		int nrows = selectedRows.length;
		int left = table.getRowCount() - nrows;
		if (left < 1) {
			table.removeRowSelectionInterval(selectedRows[nrows - 1], selectedRows[nrows - 1]);
			deleteSelectedRows();
			return;
		}
		deleteResults(selectedRows);
	}

    /** 
     * Remove selected Rows from arrays 
     * @param idx, array of indices of the rows selected to be deleted
     */
	private void deleteResults(int[] idx) {
		deleting = true;
		Arrays.sort(idx);
		for (int i = idx.length - 1; i >= 0; i--) {
			deleteResult(idx[i]);
		}
		updateTable();
		deleting = false;
	}

    /** 
     * Delete a single Result 
     * @param i, the index of the row to be deleted
     */
	private void deleteResult(int i) {
		nResults--;

        algorithms = ArrayUtils.delete(algorithms, i);
		arrivalDistibutions = ArrayUtils.delete(arrivalDistibutions, i);
		lambdas = ArrayUtils.delete(lambdas, i);
		serviceDistributions = ArrayUtils.delete(serviceDistributions, i);
        services = ArrayUtils.delete(services, i);
        responseTimes = ArrayUtils.delete(responseTimes, i);
        queueTimes = ArrayUtils.delete(queueTimes, i);
        queueNumbers = ArrayUtils.delete(queueNumbers, i);
        stationNumbers = ArrayUtils.delete(stationNumbers, i);

		classOps.add(ListOp.createDeleteOp(i));
		hasDeletes = true;
	}

    private void updateSizes() {
		setNumberOfResults(nResults);
	}

    @Override
    public String getName() {
        return PANEL_NAME;
    }

    public int getRows(){
        return nResults;
    }

    //END of ResultsPanel logic

    /*
        ------------------------------------------------------------------
        ResultTable needs access to the data structures of the ResultPanel,
        so having it as an inner class is *much* more practical
        ------------------------------------------------------------------
    */

    /*
     * The big table
     */
    private class ResultTable extends ExactTable {

        TableCellRenderer disabledCellRenderer;
        JButton deleteButton;
        ButtonCellEditor deleteButtonCellRenderer;

        public ResultTable() {
            super(new ResultsTableModel());
            setName("ResultTable");
			disabledCellRenderer = new DisabledCellRenderer();

            
			deleteButton = new JButton(deleteOneResult);
			deleteButtonCellRenderer = new ButtonCellEditor(deleteButton);
			enableDeletes();
			rowHeader.setRowHeight(CommonConstants.ROW_HEIGHT);
			setRowHeight(CommonConstants.ROW_HEIGHT);

            setDefaultRenderer(DisabledCellRenderer.class, disabledCellRenderer);

            setRowSelectionAllowed(true);
			setColumnSelectionAllowed(false);

            installKeyboardAction(getInputMap(), getActionMap(), deleteResults); //add the action of deleting a row 

            //add popup menu when right clicking
			mouseHandler = new ExactTable.MouseHandler(makeMouseMenu());
			mouseHandler.install();
        }  

        @Override
		public TableCellRenderer getCellRenderer(int row, int column) {
            //different renderers based on the column
            if (column == COL_DELETE_BUTTON) {
				return deleteButtonCellRenderer;
			} else {
				return disabledCellRenderer;
			}
		}

        /*enables deleting operations with last column's button*/
		private void enableDeletes() {
			deleteOneResult.setEnabled(nResults > 1);
			
			this.addMouseListener(new MouseAdapter() { //detection of the rows selected
				@Override
				public void mouseClicked(MouseEvent e) {
					if ((columnAtPoint(e.getPoint()) == getColumnCount() - 1) && getRowCount() > 1) {
						setRowSelectionInterval(rowAtPoint(e.getPoint()), rowAtPoint(e.getPoint()));
						deleteSelectedRows();
					}
				}
			});
			getColumnModel().getColumn(getColumnCount() - 1).setMinWidth(20);
			getColumnModel().getColumn(getColumnCount() - 1).setMaxWidth(20);
		}

        @Override
		protected JPopupMenu makeMouseMenu() { //this is for popping the menu when right clicking on selected rows
			JPopupMenu menu = new JPopupMenu();
			menu.add(deleteResults);
			return menu;
		}

        /** Called by ResultPanel to update the Delete Buttons */
        protected void updateDeleteCommand() {
			deleteOneResult.setEnabled(nResults > 1);
			getColumnModel().getColumn(getColumnCount() - 1).setMinWidth(20);
			getColumnModel().getColumn(getColumnCount() - 1).setMaxWidth(20);
		} 

        @Override
		protected void updateActions() {
			boolean isEnabled = nResults > 1 && getSelectedRowCount() > 0;
			deleteResults.setEnabled(isEnabled);
			deleteOneResult.setEnabled(nResults > 1);
		}
    }

    /** The model for the table */
    private class ResultsTableModel extends ExactTableModel {
        private final int nColumns = 10;

        //index, algorithm, distribution arrival, lambda, distribution service, service, response time, queue times, queue numbers, station numbers, delete
        private Object[] prototypes = { "10000", new String(new char[30]), new String(new char[10]), 100.00, new String(new char[10]), 100.00, 100.00, 100.00, 10, 10, "" };

		@Override
		public Object getPrototype(int columnIndex) {
			return prototypes[columnIndex + 1];
		}

        @Override
        public String getColumnName(int index) {
            switch (index) {
                case COL_ALGO:
                    return "Algorithm";
                case COL_DISTR_ARRIVAL:
                    return "Arrival Distr.";
                case COL_LAMBDA:
                    return "\u03BB";
                case COL_DISTR_SERVICE:
                    return "Service Distr.";
                case COL_SERVICE:
                    return "S";
                case COL_R:
                    return "Mean Response Time";
                case COL_QUEUETIME:
                    return "Mean Queue Time";
                case COL_NQUEUE:
                    return "Mean Numbers of Job in Queue";
                case COL_NSTATION:
                    return "Number of stations";           
                default:
                    return null;
            }
        }

        @Override
        public int getRowCount() {
            return nResults;
        }

        @Override
        public int getColumnCount() {
            return nColumns;
        }

        @Override
        protected Object getValueAtImpl(int rowIndex, int columnIndex) {
            switch (columnIndex) { //select the column, then the value in the row
                case COL_ALGO:
                    return algorithms[rowIndex];
                case COL_DISTR_ARRIVAL:
                    return arrivalDistibutions[rowIndex];
                case COL_LAMBDA:
                    return lambdas[rowIndex];
                case COL_DISTR_SERVICE:
                    return serviceDistributions[rowIndex];
                case COL_SERVICE:
                    return services[rowIndex];
                case COL_R:
                    return responseTimes[rowIndex];
                case COL_QUEUETIME:
                    return queueTimes[rowIndex];
                case COL_NQUEUE:
                    return queueNumbers[rowIndex];
                case COL_NSTATION:
                    return stationNumbers[rowIndex];           
                default:
                    return null;
            }
        }

        @Override
        protected Object getRowName(int rowIndex) {
            return rowIndex+1;
        }

    }
}

