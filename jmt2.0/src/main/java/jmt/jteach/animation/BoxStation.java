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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;

/**
 * Class that represents a single space inside the queue of the station
 *
 * @author Lorenzo Torri
 * Date: 25-mar-2024
 * Time: 15.03
 */
public class BoxStation extends JComponent implements JobContainer{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//information about the station
	private Station station;
	private int index; //this is the index of this box inside the array of boxes of the Station
	private Point sPos; 
	private int sLength;
	private int sHeight;
	private int queueSize;
	
	//information about the current Job inside the BoxStation
	private Color color;
	private double duration;
	private int maxValue = 10; //this value is for the conversion from the duration to a colored box (try to select a max value accordingly to the type of distribution)
	private int priority;
	
	private boolean isWorking = false; //to know if there is a job in this BoxStation or not to print the circle above
	private int sizeCircle = 10; //circle above the box if there is a job 
	
	/**
	 * Constructor
	 * @param st, station of this boxstation
	 * @param i, index in the list of boxstations
	 */
	public BoxStation(Station st, int i) {
		this.station = st;
		this.index = i;
		sPos = st.getPosition();
		sLength = st.getLength();
		sHeight = st.getHeight();
		queueSize = st.getsizeQueue();
		duration = 0;
		priority = 0;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		int size = sLength/queueSize;
		
		g.setColor(color); //set the color of the job
		int factor = sHeight/maxValue; 
		int result = (int) Math.round(duration * factor >= sHeight ? sHeight - 1: duration*factor); //this one provides the problem of having duration greater than the max size of the box
		g.fillRect(sPos.x + (queueSize-index-1)*size + 1, sPos.y + (sHeight-result), size - 1, result); //+1 - 1 values are used to avoid clipping with other figures, remove *10
		
		//paint the circle above
		if(isWorking) {
			g.setColor(color);
			g.fillOval(sPos.x + (queueSize-index-1)*size + (sLength/queueSize)/2 - sizeCircle/2, sPos.y - sizeCircle - 10, sizeCircle, sizeCircle); 
		}

		//if the queue is ordered with the priority algorithm, then display on the BoxStation also the priority of the job
		/*if(station.getQueuePolicy() == QueuePolicyNonPreemptive.FCFS_PR && priority != 0) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 13));
			g.drawString(String.valueOf(priority), sPos.x + (queueSize-index-1)*size + 3,  sPos.y - 2); //+3 and -2 only for estetic purposes
		} */
	}
	
	@Override
	public void addJob(Job newJob) {
		color = newJob.getColor();
		duration = newJob.getDuration();
		isWorking = true;
		priority = newJob.getPriority();
	}
	
	/**
	 * Paint the box white, since we do not have any job to display
	 */
	public void clear() {
		duration = 0;
		isWorking = false;
		priority = 0;
	}

	@Override
	public void refresh() {
		
	}
	
	public void setPosition(Point pos) {
		sPos = pos;
	}

}
