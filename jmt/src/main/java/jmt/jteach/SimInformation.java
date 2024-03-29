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

import java.util.ArrayList;
import java.util.List;

import jmt.gui.common.distributions.Distribution;

/**
 * Class to save all the informations of a Simulation.
 * It is first instanciated in the DialogStaticVariables, when the Dialog is closed.
 * 
 * @author Lorenzo Torri
 * Date: 20-mar-2024
 * Time: 14.36
 */
public class SimInformation {
    //list of parameters setted in the JDialog
    private String policy;
    private List<String> algorithms;
    private int nServers = 0;
    private Distribution distribution;
    private int N;
    private int Z;

    //parameters setted after the JDialog when pressing the Create button
    private String algorithmChosen;


    public SimInformation(String p, List<String> algorithms, int n, Distribution d, int N, int Z){
        this.policy = p;
        this.algorithms = new ArrayList<>(algorithms);
        this.nServers = n;
        this.distribution = d;
        this.N = N;
        this.Z = Z;
    }

    /**
     * Retrieve the policy
     * @return policy
     */
    public String getPolicy(){
        return policy;
    }

    /**
     * Retrieve the List of algorithms
     * @return List of algorithms
     */
    public List<String> getAlgorithms(){
        return algorithms;
    }

    /**
     * Retrieve the algorithm chosen. Use this method only after the create button is pressed.
     * Othterwise the algorithm is not yet setted.
     * @return type of algorithm
     */
    public String getAlgorithm(){
        return algorithmChosen;
    }

    /**
     * Chose the type of algorithm
     * @param algo algorithm chosen
     */
    public void setAlgorithmChosen(String algo){
        this.algorithmChosen = algo;
    }

    
}
