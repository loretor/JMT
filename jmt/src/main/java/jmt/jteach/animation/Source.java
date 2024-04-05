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
	//infromation about the Source
	private JPanel parent;
	private JobContainer next;
	private Job routeJob;

	//information about the image of the Source
	private Image sourceImg;
	private Point pos;
	private boolean centered = false;
	private int size = 50;

	private long start; //solo per debug, sarà da eliminare
	
	/** Constructor. If the Source has to be in the center of the y axis of the screen, then define pos = (0, 0) and centered = true, otherwise set centered = false and then set the position as you want */
	public Source(JPanel container,boolean centered, Point pos, JobContainer next) {
		this.parent = container;
		this.pos = pos;
		this.next = next;	
		this.centered = centered;
		sourceImg = JMTImageLoader.loadImageAwt("Source");
	}

	public void paint(Graphics g){
		super.paint(g);

		if(centered) {
			int heightPanel = parent.getHeight();	
			pos.x = parent.getX() + 20;
			pos.y = parent.getY()+(heightPanel-size)/2;
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
		}
		
	}
	
	public void setStart(long time) {
		start = time;
	}

	@Override
	public void addJob(Job newJob) {
		// TODO Auto-generated method stub
		
	}
	
	public void routeJob(int i) {
		next.addJob(routeJob);
	}
	
	
}
