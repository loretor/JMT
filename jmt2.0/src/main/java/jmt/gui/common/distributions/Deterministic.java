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

package jmt.gui.common.distributions;

import javax.swing.ImageIcon;

import jmt.gui.common.JMTImageLoader;

/**
 * <p>Title: Deterministic Distribution</p>
 * <p>Description: Constant distribution data structure</p>
 * 
 * @author Bertoli Marco
 *         Date: 6-lug-2005
 *         Time: 13.52.30
 */
public class Deterministic extends Distribution {

	/**
	 * Constructs a new Deterministic Distribution
	 */
	public Deterministic() {
		super("Deterministic", "jmt.engine.random.DeterministicDistr", "jmt.engine.random.DeterministicDistrPar", "Deterministic distribution");
		hasMean = true;
		hasC = false;
		isNestable = true;
	}

	/**
	 * Used to set parameters of this distribution
	 * @return distribution parameters
	 */
	@Override
	protected Parameter[] setParameters() {
		// Creates parameter array
		Parameter[] parameters = new Parameter[1];

		// Sets parameter t
		parameters[0] = new Parameter("t", "k", Double.class, new Double(1.0));
		// Checks value of t must be greater than 0
		parameters[0].setValueChecker(new ValueChecker() {
			public boolean checkValue(Object value) {
				Double d = (Double) value;
				if (d.doubleValue() > 0.0) {
					return true;
				} else {
					return false;
				}
			}
		});

		return parameters;
	}

	/**
	 * Sets illustrating figure in distribution panel
	 * @return illustrating figure
	 */
	@Override
	protected ImageIcon setImage() {
		return JMTImageLoader.loadImage("Deterministic");
	}

	/**
	 * Returns this distribution's short description
	 * @return distribution's short description
	 */
	@Override
	public String toString() {
		return "det(" + formatNumber(((Double) parameters[0].getValue()).doubleValue()) + ")";
	}

	/**
	 * Sets the mean for this distribution
	 * @param value mean value
	 */
	@Override
	public void setMean(double value) {
		if (getParameter(0).setValue(new Double(value))) {
			mean = value;
		}
	}

	/**
	 * This method is called whenever a parameter changes and <code>hasMean</code> or
	 * <code>hasC</code> is true
	 */
	@Override
	public void updateCM() {
		mean = ((Double) getParameter(0).getValue()).doubleValue();
	}

}
