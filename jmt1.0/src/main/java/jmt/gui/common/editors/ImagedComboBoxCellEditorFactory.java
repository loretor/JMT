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
package jmt.gui.common.editors;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jmt.gui.common.CommonConstants;
import jmt.gui.common.JMTImageLoader;
import jmt.gui.common.definitions.BlockingRegionDefinition;
import jmt.gui.common.definitions.ClassDefinition;
import jmt.gui.common.definitions.StationDefinition;

/**
 * <p>Title: Imaged ComboBox Table CellViewer/CellEditor Factory</p>
 * <p>Description: A component that creates Comboboxes with station or class names
 * and images to be used as both a viewer and an editor in a table.
 * Uses internal caching to speed up visualization.</p>
 *
 * @author Bertoli Marco
 *         Date: 11-may-2006
 *         Time: 17.03.54
 */
public class ImagedComboBoxCellEditorFactory {

	public static final int OPTION_DEFAULT = 0;
	public static final int OPTION_CLASS_TYPES = 1;
	public static final int OPTION_STATION_TYPES = 2;
	public static final int OPTION_CLASS_NAMES = 3;
	public static final int OPTION_STATION_NAMES = 4;
	public static final int OPTION_FJ_STATION_NAMES = 5;
	public static final int OPTION_TRANSITION_MODES = 6;

	/** Class Definition data structure */
	protected ClassDefinition cd;
	/** Station Definition data structure */
	protected StationDefinition sd;
	/** Instance Option **/
	protected int option;
	/** Cache for components */
	protected HashMap<Object, LabelRenderer> cache = new HashMap<Object, LabelRenderer>();
	/** Renderer instance */
	protected TableCellRenderer renderer;
	/** Editor instance */
	protected ImagedComboEditor editor;
	/** Null element renderer */
	protected LabelRenderer nullRenderer;
	/** Allows null as a valid component */
	protected boolean allowsNull;

	/**
	 * Creates a new ImagedComboBoxCellEditorFactory used to display
	 * texts without images.
	 */
	public ImagedComboBoxCellEditorFactory() {
		option = OPTION_DEFAULT;
		allowsNull = false;
	}

	/**
	 * Creates a new ImagedComboBoxCellEditorFactory used to display
	 * class name comboboxes
	 * @param cd reference to class definition data structure
	 */
	public ImagedComboBoxCellEditorFactory(ClassDefinition cd) {
		option = OPTION_CLASS_NAMES;
		allowsNull = true;
		setData(cd);
	}

	/**
	 * Creates a new ImagedComboBoxCellEditorFactory used to display
	 * station name comboboxes
	 * @param sd reference to station definition data structure
	 */
	public ImagedComboBoxCellEditorFactory(StationDefinition sd) {
		option = OPTION_STATION_NAMES;
		allowsNull = false;
		setData(sd);
	}

	/**
	 * Creates a new ImagedComboBoxCellEditorFactory used to display
	 * texts with images according to the specified option.
	 */
	public ImagedComboBoxCellEditorFactory(Object md, int option) {
		this.option = option;
		switch (option) {
		case OPTION_CLASS_TYPES:
		case OPTION_STATION_TYPES:
			allowsNull = false;
			break;
		case OPTION_CLASS_NAMES:
			allowsNull = true;
			setData((ClassDefinition) md);
			break;
		case OPTION_STATION_NAMES:
		case OPTION_FJ_STATION_NAMES:
			allowsNull = false;
			setData((StationDefinition) md);
			break;
		case OPTION_TRANSITION_MODES:
			allowsNull = true;
			break;
		default:
			allowsNull = false;
			break;
		}
	}

	/**
	 * Changes stored reference to class data structure
	 * @param cd reference to class definition data structure
	 */
	public void setData(ClassDefinition cd) {
		this.cd = cd;
		clearCache();
	}

	/**
	 * Changes stored reference to station data structure
	 * @param sd reference to station definition data structure
	 */
	public void setData(StationDefinition sd) {
		this.sd = sd;
		clearCache();
	}

	/**
	 * Clears inner component cache. Must be called each time name or
	 * type of a class/station changes.
	 */
	public void clearCache() {
		cache.clear();
		nullRenderer = null;
	}

	/**
	 * Returns an instance of editor, given search key for elements to be shown
	 * @param data array with search's key for elements to be shown
	 */
	public TableCellEditor getEditor(Object[] data) {
		if (editor == null) {
			editor = new ImagedComboEditor();
		}
		LabelRenderer[] rend;
		if (allowsNull) {
			rend = new LabelRenderer[data.length + 1];
			rend[0] = getDrawComponent(null);
			for (int i = 1; i < rend.length; i++) {
				rend[i] = getDrawComponent(data[i - 1]);
			}
		} else {
			rend = new LabelRenderer[data.length];
			for (int i = 0; i < data.length; i++) {
				rend[i] = getDrawComponent(data[i]);
			}
		}
		editor.setData(rend);
		return editor;
	}

	/**
	 * Returns an instance of editor, given search key for elements to be shown
	 * @param data vector with search's key for elements to be shown
	 */
	public TableCellEditor getEditor(List data) {
		if (editor == null) {
			editor = new ImagedComboEditor();
		}
		LabelRenderer[] rend;
		if (allowsNull) {
			rend = new LabelRenderer[data.size() + 1];
			rend[0] = getDrawComponent(null);
			for (int i = 1; i < rend.length; i++) {
				rend[i] = getDrawComponent(data.get(i - 1));
			}
		} else {
			rend = new LabelRenderer[data.size()];
			for (int i = 0; i < data.size(); i++) {
				rend[i] = getDrawComponent(data.get(i));
			}
		}
		editor.setData(rend);
		return editor;
	}

	/**
	 * Returns component to draw a station or a class in a comboBox.
	 * Provides caching functionalities.
	 * @param key search's key for component to be shown
	 * @return created component
	 */
	protected LabelRenderer getDrawComponent(Object key) {
		if (key == null) {
			if (nullRenderer == null) {
				nullRenderer = new LabelRenderer(null);
			}
			return nullRenderer;
		} else {
			LabelRenderer label;
			if (cache.containsKey(key)) {
				label = cache.get(key);
			} else {
				label = new LabelRenderer(key);
				cache.put(key, label);
			}
			return label;
		}
	}

	/**
	 * Class used to display a component in a combobox
	 */
	protected class LabelRenderer extends JLabel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected Object key;

		/**
		 * Construct a new label renderer with given object
		 * @param key search's key for class or station
		 */
		public LabelRenderer(Object key) {
			super();
			setHorizontalTextPosition(SwingConstants.RIGHT);
			setOpaque(true);
			setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
			this.key = key;
			int fontSize = this.getFont().getSize();
			Dimension iconSize = new Dimension(fontSize, fontSize);
			switch (option) {
			case OPTION_CLASS_TYPES:
				setText((String) key);
				setIcon(JMTImageLoader.loadImage((String) key + "Class", iconSize));
				break;
			case OPTION_STATION_TYPES:
				setText(CommonConstants.STATION_NAMES.get(key));
				setIcon(JMTImageLoader.loadImage((String) key + "Cell", iconSize));
				break;
			case OPTION_CLASS_NAMES:
				if (key == null) {
					setText(CommonConstants.ALL_CLASSES);
					break;
				}
				if (cd.getClassName(key) != null && !cd.getClassName(key).equals("")) {
					setText(cd.getClassName(key));
					if (cd.getClassType(key) == CommonConstants.CLASS_TYPE_OPEN) {
						setIcon(JMTImageLoader.loadImage("OpenClass", iconSize));
					} else {
						setIcon(JMTImageLoader.loadImage("ClosedClass", iconSize));
					}
				}
				break;
			case OPTION_STATION_NAMES:
				if (key != null && key.equals("")) {
					setText(CommonConstants.ALL_STATIONS);
					setIcon(JMTImageLoader.loadImage("System", iconSize));
					break;
				}
				if (sd.getStationName(key) != null && !sd.getStationName(key).equals("")) {
					setText(sd.getStationName(key));
					setIcon(JMTImageLoader.loadImage(sd.getStationType(key) + "Cell", iconSize));
				}
				if(sd.getServerTypeStationName(key) != null){
					setText(sd.getServerTypeStationName(key));
					setIcon(JMTImageLoader.loadImage(CommonConstants.STATION_TYPE_SERVER + "Cell", iconSize));
				}
				if (sd instanceof BlockingRegionDefinition) {
					BlockingRegionDefinition brd = (BlockingRegionDefinition) sd;
					if (brd.getRegionName(key) != null && !brd.getRegionName(key).equals("")) {
						setText(brd.getRegionName(key));
						setIcon(JMTImageLoader.loadImage("BlockingRegion", iconSize));
					}
				}
				if (CommonConstants.STATION_TYPE_FORK.equals(key)
						|| CommonConstants.STATION_TYPE_CLASSSWITCH.equals(key)
						|| CommonConstants.STATION_TYPE_SCALER.equals(key)
						|| CommonConstants.STATION_TYPE_TRANSITION.equals(key)) {
					setText((String) key);
					setIcon(JMTImageLoader.loadImage((String) key + "Cell", iconSize));
				}
				break;
			case OPTION_FJ_STATION_NAMES:
				if (sd.getStationName(key) != null && !sd.getStationName(key).equals("")) {
					setText(sd.getStationName(key) + " (Fork Join)");
					setIcon(JMTImageLoader.loadImage("ForkCell", iconSize));
				}
				break;
			case OPTION_TRANSITION_MODES:
				if (key == null) {
					setText(CommonConstants.ALL_MODES);
					break;
				}
				setText((String) key);
				break;
			default:
				setText((String) key);
				break;
			}
		}

		/**
		 * Gets search's key for rendered object (class or station)
		 * @return search's key for rendered object (class or station)
		 */
		public Object getKey() {
			return key;
		}
	}

	/**
	 * Returns an instance of Imaged combobox renderer
	 * @return an instance of Imaged combobox renderer
	 */
	public TableCellRenderer getRenderer() {
		return getRenderer(false, 0);
	}

	/** TO CORRECT
	 * Returns an instance of Imaged combobox renderer
	 * @return an instance of Imaged combobox renderer
	 */
	/**
	 * @param isGrayRenderer if it is enabled or disable renderer
	 * @param referenceColumn if it is disable renderer then the column that is use to the cell's editable status
	 * @return an instance of Imaged combobox renderer 
	 */
	public TableCellRenderer getRenderer(boolean isGrayRenderer, int referenceColumn) {
		if (renderer == null) {
			if (isGrayRenderer) {
				renderer = new ImagedComboGrayRenderer(referenceColumn);
			} else {
				renderer = new ImagedComboRenderer();
			}
		}
		return renderer;
	}

	/**
	 * This class is used to display a custom renderer into the comboBox with
	 * Jlabels generated by getDrawComponent method.
	 */
	protected class ComboImageRenderer implements ListCellRenderer {

		/**
		 * Simply uses value param as a renderer (as we pass a JLabel)
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel component = (JLabel) value;
			if (component == null) {
				component = nullRenderer;
			}
			if (isSelected) {
				component.setBackground(list.getSelectionBackground());
				component.setForeground(list.getSelectionForeground());
			} else {
				component.setBackground(list.getBackground());
				component.setForeground(list.getForeground());
			}
			return component;
		}
	}

	protected class ImagedComboGrayRenderer extends ImagedComboRenderer {
		private int referenceColumn;

		public ImagedComboGrayRenderer(int referenceColumn) {
			this.referenceColumn = referenceColumn;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (!table.isCellEditable(row, referenceColumn)) {
				renderer.setEnabled(false);
			} else {
				renderer.setEnabled(true);
			}
			return renderer;

		}
	}

	protected class ImagedComboRenderer implements TableCellRenderer {
		protected JComboBox combo = new JComboBox();

		/**
		 * Creates a new ImagedComboRenderer and sets renderer for comboBox.
		 */
		public ImagedComboRenderer() {
			combo.setRenderer(new ComboImageRenderer());
		}

		/**
		 * Returns the component used for drawing the cell.  This method is
		 * used to configure the renderer appropriately before drawing.
		 *
		 * @param    table        the <code>JTable</code> that is asking the
		 * renderer to draw; can be <code>null</code>
		 * @param    value        the value of the cell to be rendered.  It is
		 * up to the specific renderer to interpret
		 * and draw the value.  For example, if
		 * <code>value</code>
		 * is the string "true", it could be rendered as a
		 * string or it could be rendered as a check
		 * box that is checked.  <code>null</code> is a
		 * valid value
		 * @param    isSelected    true if the cell is to be rendered with the
		 * selection highlighted; false otherwise
		 * @param    hasFocus    if true, render cell appropriately.  For
		 * example, put a special border on the cell, if
		 * the cell can be edited, render in the color used
		 * to indicate editing
		 * @param    row     the row index of the cell being drawn.  When
		 * drawing the header, the value of
		 * <code>row</code> is -1
		 * @param    column     the column index of the cell being drawn
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component renderer;
			renderer = getDrawComponent(value);
			if (table.isCellEditable(row, column)) {
				// If the cell is editable, returns a comboBox
				combo.removeAllItems();
				if (value != null) {
					combo.addItem(renderer);
					combo.setSelectedItem(value);
				}
				if (!isSelected) {
					combo.setBackground(table.getBackground());
					combo.setForeground(table.getForeground());
				} else {
					combo.setBackground(table.getSelectionBackground());
					combo.setForeground(table.getSelectionForeground());
				}
				return combo;
			} else {
				// Otherwise returns the label only.
				if (!isSelected) {
					renderer.setBackground(table.getBackground());
					renderer.setForeground(table.getForeground());
				} else {
					renderer.setBackground(table.getSelectionBackground());
					renderer.setForeground(table.getSelectionForeground());
				}
				return renderer;
			}
		}
	}

	/**
	 * This is a combobox editor. It will recycle the same combobox changing items
	 */
	protected class ImagedComboEditor extends DefaultCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected JComboBox combo;

		/**
		 * Creates a new ImagedComboEditor and sets renderer for comboBox.
		 */
		public ImagedComboEditor() {
			super(new JComboBox());
			combo = (JComboBox) super.getComponent();
			combo.setRenderer(new ComboImageRenderer());
		}

		/**
		 * Changes data shown in this combobox
		 * @param data array with LabelRenderers to be shown
		 */
		public void setData(LabelRenderer[] data) {
			combo.removeAllItems();
			for (LabelRenderer element : data) {
				combo.addItem(element);
			}
		}

		/**
		 * Sets an initial <code>value</code> for the editor.  This will cause
		 * the editor to <code>stopEditing</code> and lose any partially
		 * edited value if the editor is editing when this method is called. <p>
		 * <p/>
		 * Returns the component that should be added to the client's
		 * <code>Component</code> hierarchy.  Once installed in the client's
		 * hierarchy this component will then be able to draw and receive
		 * user input.
		 *
		 * @param    table        the <code>JTable</code> that is asking the
		 * editor to edit; can be <code>null</code>
		 * @param    value        the value of the cell to be edited; it is
		 * up to the specific editor to interpret
		 * and draw the value.  For example, if value is
		 * the string "true", it could be rendered as a
		 * string or it could be rendered as a check
		 * box that is checked.  <code>null</code>
		 * is a valid value
		 * @param    isSelected    true if the cell is to be rendered with
		 * highlighting
		 * @param    row the row of the cell being edited
		 * @param    column the column of the cell being edited
		 * @return the component for editing
		 */
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			combo.setBackground(table.getBackground());
			combo.setForeground(table.getForeground());
			combo.setSelectedItem(getDrawComponent(value));
			return combo;
		}

		/**
		 * Returns the value contained in the editor.
		 *
		 * @return the value contained in the editor
		 */
		@Override
		public Object getCellEditorValue() {
			if (combo.getSelectedItem() != null) {
				return ((LabelRenderer) combo.getSelectedItem()).getKey();
			} else {
				return null;
			}
		}
	}
}
