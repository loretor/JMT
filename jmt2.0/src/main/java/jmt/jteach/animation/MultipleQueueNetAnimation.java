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

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import jmt.jteach.Distributions;
import jmt.jteach.Simulation.Simulation;
import jmt.jteach.Wizard.distributions.AnimDistribution;
import jmt.jteach.Wizard.panels.AnimationPanel;

/**
 * Class with all the JComponents for the Animation of Routing Policy
 *
 * @author Lorenzo Torri
 * Date: 02-apr-2024
 * Time: 09.10
 */
public class MultipleQueueNetAnimation extends AnimationClass{
	private AnimationPanel animPanel;
	private JPanel parent;
	
	//-- all the classes contained in the Animation, like stations, edges...
	private Source source;
	private Router router;
	private Sink sink;
	private List<Station> stationList;
	private List<Edge> edgeList;
	private double[] probabilities = {0.00,0.00,0.00};
	
	
	//--all the characteristics of the Animation
	private int nServers = 1; //by default = 1
	private AnimDistribution interArrival; //by default the two distributions are DETERMINSTIC
	private AnimDistribution service;
	
	/** Constructor */
	public MultipleQueueNetAnimation(AnimationPanel animPanel, JPanel container, Simulation sim) {
		this.parent = container;
		this.animPanel = animPanel;
		simulation = sim;	
		initGUI(container);
	}
	
	public void initGUI(JPanel container) {	
		anim = new Animator(30, this);	
		edgeList = new ArrayList<>();
		stationList = new ArrayList<>();
		sink = new Sink(container, true, new Point(800,0), this);
		jobList = new ArrayList<>(); //do not move this line in the super class
		
		edgeList.add(new Edge(this,container, true, true, new Point(550,0), new Point(580,0), sink));
		
		//two vertical edges
		edgeList.add(new Edge(this,container, false, false, new Point(550,50+30), new Point(550,200), edgeList.get(0)));
		edgeList.add(new Edge(this, container, false, false, new Point(550,287+30), new Point(550,200), edgeList.get(0)));
		
		//three horizontal edges
		edgeList.add(new Edge(this, container, false, false, new Point(500,50+30), new Point(550,50+30), edgeList.get(1)));		
		edgeList.add(new Edge(this, container, true, false, new Point(500,0), new Point(550,0),  edgeList.get(0)));
		edgeList.add(new Edge(this, container, false, false, new Point(500,287+30), new Point(550,287+30), edgeList.get(2)));
		
		//three stations
		stationList.add(new Station(this, container, false, false, new Point(300,50), edgeList.get(3), simulation, nServers));
		stationList.add(new Station(this, container, false, true, new Point(300,0), edgeList.get(4), simulation, nServers));
		stationList.add(new Station(this, container, false, false, new Point(300, 287), edgeList.get(5), simulation, nServers));
		
		//three horizontal edges
		edgeList.add(new Edge(this, container, false, true, new Point(180,50+30), new Point(280,50+30), stationList.get(0)));
		edgeList.add(new Edge(this, container, true, true, new Point(210,0), new Point(280,0), stationList.get(1)));
		edgeList.add(new Edge(this, container, false, true, new Point(180,287+30), new Point(280,287+30), stationList.get(2)));
		
		//two vertical edges
		edgeList.add(new Edge(this, container, false, false, new Point(180,200 - 30), new Point(180, 50+30), edgeList.get(6)));
		edgeList.add(new Edge(this, container, false, false, new Point(180,200 + 30), new Point(180, 287+30),  edgeList.get(8)));
		
		//create the two Lists for the router
		int l = edgeList.size();
		List<Edge> eList = new ArrayList<>(Arrays.asList(edgeList.get(l-2), edgeList.get(l-4), edgeList.get(l-1)));
		router = new Router(container, false, true, new Point(160,0), eList, stationList, simulation, probabilities);
		
		edgeList.add(new Edge(this, container, true, true, new Point(80,0), new Point(150,0), router));
		
		source = new Source(this, container, true, new Point(10,0), edgeList.get(edgeList.size()-1), interArrival, service);
		
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		source.paint(g);
		router.paint(g);
		sink.paint(g);
		
		for(Station st: stationList) {
			st.paint(g);
		}		
		for(Edge e: edgeList) {
			e.paint(g);
		}
		for(Job j: jobList) {
			j.paint(g);
		}
	}

	@Override
	public void refresh() {
		source.refresh();
		router.refresh();
		
		for(Station st: stationList) {
			st.refresh();
		}
		for(Edge e: edgeList) {
			e.refresh();
		}

		repaint();	
	}

	

	@Override
	public void start() {
		if(!anim.isPaused()) { //if it's the first time the animation is started, otherwise it is a simple restart after a pause
			source.setStart(System.currentTimeMillis());			
		}
		anim.start();
	}
	
	@Override
	public void pause() {
		anim.pause();
	}
	
	@Override
	public void reload() {
		initGUI(parent);
		repaint();
	}


	//TODO: stop for multiple

	@Override
	public void updatePause(long pause) {
		//update all those elements that work with System.currentMillis() like CircleStation
		for(Station st: stationList) {
			st.updatePause(pause);
		}
		source.updatePause(pause);
	}

	
	@Override
	public void stop(){
		anim.terminate();
		animPanel.stopAnimation();
	}

	@Override
	public void next() {
		//first update the velocity of each component that works with time
		source.setVelocityFactor(5);
		for(Station st: stationList){
			st.setVelocityFactor(5);
		}
		for(Job j: jobList) {
			j.setVelocityFactor(5);
		}
		
		anim.start();
		
		for(Edge e: edgeList) {
			e.nextEvent();
		}
		for(Station st: stationList){
			st.nextEvent();
		}	
	}

	public void resetNextEvent() {
		//reset all the velocity factors
		source.setVelocityFactor(1);
		for(Station st: stationList){
			st.setVelocityFactor(0);
		}
		for(Job j: jobList) {
			j.resetVelocityFactor();
		}
		
		for(Edge e: edgeList) {
			e.resetNextEvent();
		}
		for(Station st: stationList){
			st.resetNextEvent();
		}

		//reset the buttons of the animationPanel
		animPanel.resetNextStepAnimation();
	} 

	@Override
	public void updateMultiple(Simulation sim, AnimDistribution service, AnimDistribution interA){
		simulation = sim;
		interArrival = interA;
		this.service = service;

		source.updateDistribution(service, interA);	
	}

	@Override
	public void updateMultiple(Simulation sim, double[] percentages, AnimDistribution service, AnimDistribution interA){
		updateMultiple(sim, service, interA);
		probabilities[0] = percentages[0];
		probabilities[1] = percentages[1];
		probabilities[2] = 1.00 - percentages[0] - percentages[1];
		router.changeProbabilities(probabilities);
	}
}
