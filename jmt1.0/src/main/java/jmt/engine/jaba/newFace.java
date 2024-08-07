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

package jmt.engine.jaba;

import jmt.engine.jaba.Hull.Vertex;

//import Util;

/**
 * Created by IntelliJ IDEA.
 * User: PoliMi
 * Date: 11-giu-2005
 * Time: 14.09.45
 * To change this template use File | Settings | File Templates.
 */
public class newFace {
	private double EPSYLON = 0.00001;

	private int controllo;

	private Vertex vert1;
	private Vertex vert2;
	private Vertex vert3;

	public newFace(Vertex v1, Vertex v2, Vertex v3, int colore) {
		vert1 = v1;
		vert2 = v2;
		vert3 = v3;
		controllo = colore;
	}

	public void setContr(int settore) {
		controllo = settore;
	}

	public int getContr() {
		return controllo;
	}

// The following 3 methods return the vertices of a newFace
	public Vertex getV0() {
		return vert1;
	}

	public Vertex getV1() {
		return vert2;
	}

	public Vertex getV2() {
		return vert3;
	}

// The following 4 methods return the value of the 4 parameters of the equation
// of a plane passing through 3 points: Ax+By+Cz+D=0
	public double getPianoA() {
		int v0[] = vert1.getCoords();
		int v1[] = vert2.getCoords();
		int v2[] = vert3.getCoords();
		double x = v0[1] * (v1[2] - v2[2]) - v0[2] * (v1[1] - v2[1]) + v1[1] * v2[2] - v1[2] * v2[1];
		return x;
	}

	public double getPianoB() {
		int v0[] = vert1.getCoords();
		int v1[] = vert2.getCoords();
		int v2[] = vert3.getCoords();
		double x = -(v0[0] * (v1[2] - v2[2]) - v0[2] * (v1[0] - v2[0]) + v1[0] * v2[2] - v1[2] * v2[0]);
		return x;
	}

	public double getPianoC() {
		int v0[] = vert1.getCoords();
		int v1[] = vert2.getCoords();
		int v2[] = vert3.getCoords();
		double x = v0[0] * (v1[1] - v2[1]) - v0[1] * (v1[0] - v2[0]) + v1[0] * v2[1] - v1[1] * v2[0];
		return x;
	}

	public double getPianoD() {
		int v0[] = vert1.getCoords();
		int v1[] = vert2.getCoords();
		int v2[] = vert3.getCoords();
		double d = -(v0[0] * (v1[1] * v2[2] - v1[2] * v2[1]) - v0[1] * (v1[0] * v2[2] - v1[2] * v2[0]) + v0[2] * (v1[0] * v2[1] - v1[1] * v2[0]));
		return d;
	}

	/**
* Check if two newFace are bordering
* @param s1
* @param s2
* @return true if they are bordering
	 */
	public boolean confSect(newFace s1, newFace s2) {
// I consider two neighboring sectors if they have two vertices out of 3 in common
		int t = 0;
		if (s1.vert1 == s2.vert1) {
			t++;
		}
		if (s1.vert1 == s2.vert2) {
			t++;
		}
		if (s1.vert1 == s2.vert3) {
			t++;
		}
		if (s1.vert2 == s2.vert1) {
			t++;
		}
		if (s1.vert2 == s2.vert2) {
			t++;
		}
		if (s1.vert2 == s2.vert3) {
			t++;
		}
		if (s1.vert3 == s2.vert1) {
			t++;
		}
		if (s1.vert3 == s2.vert2) {
			t++;
		}
		if (s1.vert3 == s2.vert3) {
			t++;
		}
		if (t > 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
* Check if two newFace are coplanar
	 * @param s1
	 * @param s2
	 * @return
	 */
	public boolean Complanar(newFace s1, newFace s2) {
		if (
// For the check I use the condition that two planes are coplanar if
// have the coefficients of the equation proportional to each other.
		(((Math.abs(s2.getPianoA()) < EPSYLON) && Math.abs(s1.getPianoA()) < EPSYLON) || Math.abs((s1.getPianoA() / s2.getPianoA()) - s1.getPianoB()
				/ s2.getPianoB()) < EPSYLON)
				&& (((Math.abs(s2.getPianoA()) < EPSYLON) && Math.abs(s1.getPianoC()) < EPSYLON) || Math.abs((s1.getPianoA() / s2.getPianoA())
						- s1.getPianoC() / s2.getPianoC()) < EPSYLON)
				&& (((Math.abs(s2.getPianoA()) < EPSYLON) && Math.abs(s1.getPianoD()) < EPSYLON) || Math.abs((s1.getPianoA() / s2.getPianoA())
						- s1.getPianoD() / s2.getPianoD()) < EPSYLON)
				&& (((Math.abs(s2.getPianoC()) < EPSYLON) && Math.abs(s1.getPianoC()) < EPSYLON) || Math.abs((s1.getPianoC() / s2.getPianoC())
						- s1.getPianoB() / s2.getPianoB()) < EPSYLON)
				&& (((Math.abs(s2.getPianoD()) < EPSYLON) && Math.abs(s1.getPianoB()) < EPSYLON) || Math.abs((s1.getPianoD() / s2.getPianoD())
						- s1.getPianoB() / s2.getPianoB()) < EPSYLON)
				&& (((Math.abs(s2.getPianoC()) < EPSYLON) && Math.abs(s1.getPianoD()) < EPSYLON) || Math.abs((s1.getPianoC() / s2.getPianoC())
						- s1.getPianoD() / s2.getPianoD()) < EPSYLON)) {
			return true;
		} else {
			return false;
		}
	}
}
