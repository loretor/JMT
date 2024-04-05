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
import java.awt.Image;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JPanel;

import jmt.gui.common.JMTImageLoader;

/**
 * This class is responsible for creating the jobs and routing them to the first edge of the animation
 *
 * @author Lorenzo Torri
 * Date: 31-mar-2024
 * Time: 11.30
 */
public class Source extends JComponent implements JobContainer{
	private JPanel parent;
	private Point pos;
	private JobContainer next;
	private Job routeJob;
	
	private long start; //solo per debug, sarà da eliminare
	
	private Image sourceImg;
	private boolean centered = false;
	private int size = 50;
	
	AnimationClass anim;
	
	
	/**
     * Constructor
     * @param container, JPanel that contains this sink
     * @param centered, if the component is centered with respect to the Jpanel
     * @param pos, position of the component. If it is centered then this pos is not important, it can be anything
     * @param next, next JContainer linked to the source
     * @param anim, AnimationClass associated to this component. It is needed in order to remove the job arrived to the sink also from the list of jobs of the AnimationClass
     */
	public Source(JPanel container,boolean centered, Point pos, JobContainer next, AnimationClass anim) {
		this.parent = container;
		this.pos = pos;
		this.next = next;	
		this.centered = centered;
		this.anim = anim;
		
		sourceImg = JMTImageLoader.loadImageAwt("Source");
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		if(centered) {
			//int widthPanel = parent.getWidth();
			int heightPanel = parent.getHeight();	
			pos.y = parent.getY()+(heightPanel- size)/2 ;
			pos.x = 20;
		}
		
		g.drawImage(sourceImg, pos.x, pos.y, size, size, null);
	}

	@Override
	public void refresh() {
		//every 3 seconds a new job arrives
		if(System.currentTimeMillis() - start >= 3000) {
			routeJob = new Job();
			routeJob(0);
			start = System.currentTimeMillis();
			anim.addNewJob(routeJob);
		}
		
	}
	
	public void setStart(long time) {
		start = time;
	}

	
	public void routeJob(int i) {
		if(next instanceof Edge) {
			routeJob.setOnEdge();
		}
		else {
			routeJob.unsetOnEdge();
		}
		next.addJob(routeJob);
	}

	@Override
	public void addJob(Job newJob) {
		
	}
	
	
}
