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
import java.util.List;

import javax.swing.JPanel;

/**
 * Class with all the JComponents for the Animation
 *
 * @author Lorenzo Torri
 * Date: 25-mar-2024
 * Time: 10.46
 */
public class SingleQueueNetAnimation extends AnimationClass{
	private JPanel parent;
	
	//-- all the classes contained in the Animation, like stations, edges...
	private Source source;
	private Sink sink;
	private Station station;
	private Animator anim;
	private List<Edge> edgeList;
	
	//--all the characteristics of the Animation
	private QueuePolicy type = QueuePolicy.FIFO;
	int nServers = 1;
	
	
	/** Constructor. One note, define the elements of the Animation from the last to the first, since each element must have a reference to the next one */
	public SingleQueueNetAnimation(JPanel container) {
		this.parent = container;
		initGUI(container);
	}

	public void initGUI(JPanel container){
		anim = new Animator(30, this);
		edgeList = new ArrayList<>();
		
		edgeList.add(new Edge(container, true, new Point(435,250), new Point(580,250), null));
		station = new Station(container, true, new Point(0,0), edgeList.get(0), type, nServers);
		edgeList.add(new Edge(container, true,new Point(80,250), new Point(230,250), station));
		source = new Source(container, true, new Point(0,0), edgeList.get(edgeList.size()-1));
		sink = new Sink(container, true, new Point(0,0));
	}
	
	/**
	 * This is the most important method for repainting.
	 * This one is the only one paint that needs to be called when repaint is invoked.
	 * Do not call repaint in other classes related to QNAnimation, otherwise it will not work, since in paint, there is a call to the paint methods of all the classes related to QNA
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		source.paint(g);
		sink.paint(g);
		station.paint(g);
		
		for(Edge e: edgeList) {
			e.paint(g);
		}
	}
	
	@Override
	public void refresh() {		
		source.refresh();
		station.refresh();
		
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
		station.updatePause(pause);
		
	}

}
