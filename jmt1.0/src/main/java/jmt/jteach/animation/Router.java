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

package jmt.jteach.animation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.List;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JPanel;

import jmt.gui.common.JMTImageLoader;

/**
 * Class for representing a Router inside the Animation
 *
 * @author Lorenzo Torri
 * Date: 02-apr-2024
 * Time: 10.10
 */
public class Router extends JComponent implements JobContainer{
	private JPanel parent; //panel in which the Station is drawn
	
	//general information about the station
	private Point pos;
	private boolean xcentered;
	private boolean ycentered;
	private int size = 40;
	private Image routerImg;
	private List<Edge> nextEdges;
	private List<Station> nextStations;

	private RoutingPolicy policy;
	private double[] percentages;
	
	//double constructor, since we can create a Router without percentages (needed only for PROBABILISTIC Routing Policy) or with them
	
	/**
	 * Constructor
	 * @param parent, Jpanel that contains this router
	 * @param xcentered, if the router is xcentered with respect to the parent, if yes, pos.x does not matter
	 * @param ycentered, if the router is ycentered with respect to the parent, if yes, pos.y does not matter
	 * @param pos, position of the Router, if it is x-y centered then the respective coordinates are not considered
	 * @param nextEdges, the edges linked to this router
	 * @param nextStations, the stations linked to this router
	 * @param policy, type of Routing Policy
	 */
	public Router(JPanel parent, boolean xcentered, boolean ycentered, Point pos, List<Edge> nextEdges, List<Station> nextStations, RoutingPolicy policy) {
		this.parent = parent;
		this.pos = pos; 
		this.xcentered = xcentered;
		this.ycentered = ycentered;
		this.policy = policy;
		
		this.nextEdges = nextEdges;
		this.nextStations = nextStations;
		
		if(policy == RoutingPolicy.JSQ) { //if the policy is the JSQ then the stations must paint the size of their queue
			for(Station st: nextStations) {
				st.paintQueueSize();
			}
		}

		routerImg = JMTImageLoader.loadImageAwt("Router");
	}
	
	/**
	 * Constructor similar with the one before but with the array of percentage
	 * @param percentages, it must be of size = 3 and all doubles between 0 and 1, and they must sum up to 1. 
	 * This check is not performed here, since it is something that have to be checked in the TextField before creating the Animation
	 */
	public Router(JPanel parent, boolean xcentered, boolean ycentered, Point pos, List<Edge> nextEdges, List<Station> nextStations, RoutingPolicy policy, double[] percentages) {
		this(parent, xcentered, ycentered, pos, nextEdges, nextStations, policy);
		this.percentages = percentages;
		
		if(policy == RoutingPolicy.PROBABILISTIC) { //if the policy is the PROB then the edges must paint also their percentage
			for(int i = 0; i < nextEdges.size(); i++) {
				nextEdges.get(i).paintPercentage(percentages[i]);
			}
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		//get the correct position for centering the station in the panel
		if(xcentered) {
			int widthPanel = parent.getWidth();	
			pos.x = parent.getX()+(widthPanel - size)/2;;		
		}
		if(ycentered) {
			int heightPanel = parent.getHeight();
			pos.y = parent.getY()+(heightPanel- size)/2;
		}
		
		g.setColor(Color.BLACK);
        g.drawImage(routerImg, pos.x, pos.y, size, size, null);
	}
	
	@Override
	public void refresh() {
		
	}

	@Override
	public void addJob(Job newJob) {
		//choose where to route the job based on the type of policy
		int index;
		switch(policy) {
			case RR:
				index = new Random().nextInt(nextEdges.size());
				route(newJob, index);
				break;
			case PROBABILISTIC:
				index = probabilistic();
				route(newJob, index);
				break;
			case JSQ:
				index = shortestJobQueue();
				route(newJob, index);
				break;
		}
	}
	
	/**
	 * method used by the Router to know which is the edge to which routing based on probabilistic values
	 * @return the index of the edge chosen
	 */
	private int probabilistic() {
		double r = new Random().nextDouble();
		if(r < percentages[0]) {
			return 0;
		}
		else if(percentages[0] <= r && r < percentages[0]+percentages[1]) {
			return 1;
		}
		else {
			return 2;
		}
	}
	
	/**
	 * method used by the Router to know which is the station connected to the router with the shortest job queue
	 * @return the index of the station with shortest job queue
	 */
	private int shortestJobQueue() {
		int min = Integer.MAX_VALUE;
		int index = -1;
		int size;
		for(int i = 0; i < nextStations.size(); i++) {		
			size = nextStations.get(i).getNumberWaitingJobs();
			if(min > size) {
				min = size;
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * method to route a job to one of the edges connected to the Router
	 * @param job to be routed
	 * @param index of the edge inside the list of edges
	 */
	private void route(Job job, int index) {
		if(nextEdges.get(2) != null) {
			if(nextEdges.get(2) instanceof Edge) {
				job.setOnEdge();
			}
			else {
				job.unsetOnEdge();
			}
			nextEdges.get(index).addJob(job);	
		}
		
	}

	/**
	 * Method to change probabilities of the router
	 * @param probabilities new array of probabilities
	 */
	public void changeProbabilities(double[] probabilities){
		percentages = probabilities;
		//repaint all the edges
		for(int i = 0; i < nextEdges.size(); i++) {
			nextEdges.get(i).paintPercentage(percentages[i]);
		}
	}
	
	
}
