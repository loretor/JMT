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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Arc2D;
import java.util.Random;

import javax.swing.JComponent;

/**
 * The amount of job completion done, inside the circle of the station
 *
 * @author Lorenzo Torri
 * Date: 25-mar-2024
 * Time: 15.35
 */
public class CircleStation extends JComponent implements JobContainer{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//information about the station
	private Station station;
	private Point sPos; 
	private int sHeight;
	private int sLength;
	private float position;
	
	//information about the job currently processed it is all in arrays
	private Color color = Color.RED;
	private float progression; //value from 0 to 1 to know how much we have completed the job duration
	private Job currentJob;
	private long entranceTime;
	private boolean isWorking; //to know if it is processing a job or not
	
	private long pauseTime; //this variable is important if we are processing a job and then the animator is paused, we need to know how much time the job remained paused and remove this value from the passedTime
	//then set this value = 0 when the job gets out from this class
	
	
	public CircleStation(Station st, int nServers, float position) { //float position is to know if the circle has to be moved from the normal position or not, since with 2 servers the circle has to be up or down
		station = st;
		sPos = st.getPosition();
		sHeight = st.getHeight();
		sLength = st.getLength();
		isWorking = false;
		this.progression = 0.0f;
		this.position = position;
		pauseTime = 0;
	}
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(color);
        Graphics2D g2d = (Graphics2D) g.create();
        int radius = sHeight/2;
        
        Arc2D arc = new Arc2D.Double((sPos.x + sLength+1), sPos.y + position, sHeight, sHeight, 90, 360*progression, Arc2D.PIE); //paint the circle
    	g2d.fill(arc);    
	}
	
	@Override
	public void refresh() {
		if(isWorking) {
			long duration = (long) (currentJob.getDuration() * Math.pow(10, 3));
			long passedTime = System.currentTimeMillis() - entranceTime - pauseTime;
			
			long diff =  duration - passedTime;
			progression = (float) diff/duration;
			
			if(progression < 0) {
				isWorking = false;
				progression = 0; //to avoid printing another cycle of the circle
				pauseTime = 0;
				
				routeJob(0);
			}
		}	
	}
	
	@Override
	public void addJob(Job newJob) {
		currentJob = newJob;
		entranceTime = System.currentTimeMillis();
		isWorking = true;
		color = newJob.getColor();
	}
	
	/**
	 * To know if the station is currently processing a job or not
	 * @return true if it is processing a job, false otherwise
	 */
	public boolean isWorking() {
		return isWorking;
	}
	
	/**
	 * To know for how long the animator has stopped
	 * @param value the time of pausing
	 */
	public void setPauseTime(long value) {
		if(isWorking) { //only if the circle is working it is important to set the pauseTime, otherwise not.
			pauseTime += value;
		}	
	}
	
	public void routeJob(int i) {
		currentJob.setDuration();
		station.getNextContainer().addJob(currentJob);
	}

	public void setPosition(Point pos) {
		sPos = pos;
	}
	
}
