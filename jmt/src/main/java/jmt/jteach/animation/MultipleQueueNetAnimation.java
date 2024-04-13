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

/**
 * Class with all the JComponents for the Animation of Routing Policy
 *
 * @author Lorenzo Torri
 * Date: 02-apr-2024
 * Time: 09.10
 */
public class MultipleQueueNetAnimation extends AnimationClass{
	private JPanel parent;
	
	//-- all the classes contained in the Animation, like stations, edges...
	private Source source;
	private Animator anim;
	private Router router;
	private Sink sink;
	private List<Station> stationList;
	private List<Edge> edgeList;
	private double[] probabilities = {0.00,0.00,0.00};
	
	
	//--all the characteristics of the Animation
	private QueuePolicyNonPreemptive queuePolicy = QueuePolicyNonPreemptive.FIFO; //it is setted by default = FIFO
	private RoutingPolicy routingPolicy;
	int nServers = 1;
	
	/** Constructor */
	public MultipleQueueNetAnimation(JPanel container, RoutingPolicy routingPolicy) {
		this.parent = container;
		this.routingPolicy = routingPolicy;
		initGUI(container);
	}
	
	public void initGUI(JPanel container) {
		anim = new Animator(30, this);
		edgeList = new ArrayList<>();
		stationList = new ArrayList<>();
		jobList = new ArrayList<>();
		
		sink = new Sink(container, true, new Point(800,0), this);
		
		edgeList.add(new Edge(container, true, true, new Point(550,0), new Point(600,0), sink));
		
		//two vertical edges
		edgeList.add(new Edge(container, false, false, new Point(550,100+30), new Point(550,230), edgeList.get(0)));
		edgeList.add(new Edge(container, false, false, new Point(550,370+30), new Point(550,260), edgeList.get(0)));
		
		//three horizontal edges
		edgeList.add(new Edge(container, false, false, new Point(500,100+30), new Point(550,100+30), edgeList.get(1)));		
		edgeList.add(new Edge(container, true, false, new Point(500,0), new Point(550,0),  edgeList.get(0)));
		edgeList.add(new Edge(container, false, false, new Point(500,370+30), new Point(550,370+30), edgeList.get(2)));
		
		//three stations
		stationList.add(new Station(container, false, false, new Point(300,100), edgeList.get(3), queuePolicy, nServers));
		stationList.add(new Station(container, false, true, new Point(300,0), edgeList.get(4), queuePolicy, nServers));
		stationList.add(new Station(container, false, false, new Point(300,370), edgeList.get(5), queuePolicy, nServers));
		
		//three horizontal edges
		edgeList.add(new Edge(container, false, true, new Point(180,100+30), new Point(280,100+30), stationList.get(0)));
		edgeList.add(new Edge(container, true, true, new Point(210,0), new Point(280,0), stationList.get(1)));
		edgeList.add(new Edge(container, false, true, new Point(180,370+30), new Point(280,370+30), stationList.get(2)));
		
		//two vertical edges
		edgeList.add(new Edge(container, false, false, new Point(180,222), new Point(180,100+30), edgeList.get(6)));
		edgeList.add(new Edge(container, false, false, new Point(180,302), new Point(180,370+30),  edgeList.get(8)));
		
		//create the two Lists for the router
		int l = edgeList.size();
		List<Edge> eList = new ArrayList<>(Arrays.asList(edgeList.get(l-2), edgeList.get(l-4), edgeList.get(l-1)));
		router = new Router(container, false, true, new Point(160,0), eList, stationList, routingPolicy, probabilities);
		
		edgeList.add(new Edge(container, true, true, new Point(80,0), new Point(150,0), router));
		
		source = new Source(container, true, new Point(10,0), edgeList.get(edgeList.size()-1), this);
		
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
		source.setStart(System.currentTimeMillis());
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

	@Override
	public void updatePause(long pause) {
		//update all those elements that work with System.currentMillis() like CircleStation
		for(Station st: stationList) {
			st.updatePause(pause);
		}
	}

	@Override
	public void updateMultiple(double[] percentages){
		probabilities[0] = percentages[0];
		probabilities[1] = percentages[1];
		probabilities[2] = 1.00 - percentages[0] - percentages[1];
		router.changeProbabilities(probabilities);
	}
}
