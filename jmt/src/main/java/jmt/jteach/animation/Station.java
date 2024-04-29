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
import java.util.Comparator;

import javax.swing.JComponent;
import javax.swing.JPanel;

import jmt.jteach.animation.customQueue.CustomCollection;
import jmt.jteach.animation.customQueue.FIFOQueue;
import jmt.jteach.animation.customQueue.LIFOQueue;
import jmt.jteach.animation.customQueue.PRIOQueue;

/**
 * Class for representing a Node inside the Animation
 *
 * @author Lorenzo Torri
 * Date: 11-mar-2024
 * Time: 16.29
 */
public class Station extends JComponent implements JobContainer{
	private JPanel parent; //panel in which the Station is drawn
	private AnimationClass animation;
	
	//general information about the station
	private Point pos;
	private boolean xcentered;
	private boolean ycentered;
	private int height = 60;
	private int length = 120;
	private QueuePolicyNonPreemptive policyType;
	private int nServers;
	private boolean paintQueueSize = false; //this parameter is used to know if it is needed to paint the size of the queue above the station (useful for polices like JSQ
	
	//this is the queue of Jobs, see CustomCollection to understand why this and not a general Collection
	private CustomCollection<Job> jobQueue; 
	private JobContainer next;
	
	//-- subcomponents of the station, the BoxStation, and the Circle
	private BoxStation[] boxes;
	private final int sizeQueue = 5;	
	private CircleStation[] circles;

	//for the Step simulation
	private boolean nextEvent = false;
	
	/**
	 * Constructor
	 * @param animation, AnimationClass which the edge is part of
	 * @param parent, JPanel that contains this station
	 * @param xcentered, if the station is x centered with respect to the parent
	 * @param ycentered, if the station is y centered with respect to the parent
	 * @param pos, position of the station, if it is x-y centered then the respective coordinates are not considered
	 * @param next, Jobcontainer connected to this station
	 * @param type, queue policy type
	 * @param servers, number of servers
	 */
	public Station(AnimationClass anim, JPanel parent, boolean xcentered, boolean ycentered, Point pos, JobContainer next, QueuePolicyNonPreemptive type, int servers) {
		this.animation = anim;
		this.parent = parent;
		this.pos = pos; 
		this.xcentered = xcentered;
		this.ycentered = ycentered;
		
		this.nServers = servers;
		this.next = next;
		
		
		boxes = new BoxStation[sizeQueue];
		for(int i = 0; i < sizeQueue; i++) {
        	boxes[i] = new BoxStation(this, i);
        }
		
		float position = 0.0f;
		if(nServers == 2) {
			position = -height/2;
		}
		circles = new CircleStation[nServers];
		for(int i = 0; i < nServers; i++) {
			circles[i] = new CircleStation(this, position);
			position *= -1;
		}
				
		typeOfQueue(type);
	}
	
	/**
	 * Create the CustomCollection instance, based on the enum QueuePolicy
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	protected void typeOfQueue(QueuePolicyNonPreemptive type) {
		this.policyType = type;
		switch(type) {
		case FIFO:
			jobQueue = new FIFOQueue<Job>();
			break;
		case LIFO:
			jobQueue = new LIFOQueue<Job>();
			break;
		case PRIO:
			jobQueue = new PRIOQueue<Job>(new Comparator<Job>() {
				@Override
				public int compare(Job o1, Job o2) {
					return Integer.compare(o1.getPriority(), o2.getPriority());
				}
				
			});
			break;
		case SJF:
			jobQueue = new PRIOQueue<Job>(new Comparator<Job>() {
				@Override
				public int compare(Job o1, Job o2) {
					return Double.compare(o1.getDuration(), o2.getDuration());
				}
				
			});
			break;
		case LJF:
			jobQueue = new PRIOQueue<Job>(new Comparator<Job>() {
				@Override
				public int compare(Job o1, Job o2) {
					return Double.compare(o2.getDuration(), o1.getDuration());
				}
				
			});
			break;
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		//get the correct position for centering the station in the panel
		if(xcentered) {
			int widthPanel = parent.getWidth();	
			pos.x = parent.getX()+(widthPanel - length - height)/2;;		
		}
		if(ycentered) {
			int heightPanel = parent.getHeight();
			pos.y = parent.getY()+(heightPanel- height)/2;
		}
		
		for(BoxStation box: boxes) {
			box.setPosition(pos);
		}
		for(CircleStation circle: circles){
			circle.setPosition(pos);
		}
		
		g.setColor(Color.BLACK);
        g.drawRect(pos.x, pos.y, length, height); //create the box of the station centered in the panel
        
        //for creating the lines between in the station
        int size = length/sizeQueue;
        for(int i = 1; i < sizeQueue; i++) {
        	g.drawLine(pos.x + i*size, pos.y, pos.x + i*size, pos.y + height);
        }
        
        g.setColor(Color.BLACK);
        if(nServers == 1) {
        	g.drawOval(pos.x + length + 1, pos.y, height, height); //create the circle of the station
        }
        else {
        	g.drawOval(pos.x + length + 1, pos.y - height/2, height, height);
        	g.drawOval(pos.x + length + 1, pos.y + height/2, height, height);
        }
        
         
        //----paint all sub-components
        for(int i = 0; i < boxes.length; i++) {
			boxes[i].paint(g);
		}   
        for(int i = 0; i < nServers; i++) {
        	circles[i].paint(g);
        }  
        
        if(paintQueueSize) {
        	g.setColor(Color.BLACK);
        	g.setFont(new Font("Arial", Font.PLAIN, 10));
        	g.drawString("Queue Size: "+String.valueOf(jobQueue.size()), pos.x,  pos.y-20);
        }
	}
	
	@Override
	public void refresh() {		
		if(jobQueue.size() >= 1) { //some jobs are waiting to enter inside the circles
			for(int i = 0; i < nServers; i++) {
				if(!circles[i].isWorking()) { //if the circle is not working than the head of the queue can go inside the circle
					circles[i].addJob((Job) jobQueue.first());
					jobQueue.removeHead();
					
					if(jobQueue.size() == 0) {
						break;
					}
				}				
			}
		}
		
		for(int i = 0; i < nServers; i++) {
			circles[i].refresh();
		}
				
		//for all the jobs in the queue, we need to update the correspondent boxStation
		int index = 0;
		for(Job j: jobQueue) {
			boxes[index].addJob(j);
			index++;
		}
		
		//if other boxStations are free, then clear them
		for(int i = index; i < sizeQueue; i++) {
			boxes[i].clear();
		}
	}
	
	/**
	 * Update all the subComponents of Station that are affected by a Pause in the Animator.
	 * Currently the only one that needs to be update is the CircleStation, since the progression of the pie depends on the entrance of the job and on the current time - time of pause
	 * @param pause value for which the animator was paused
	 */
	public void updatePause(long pause) {
		for(int i = 0; i < nServers; i++) {
			circles[i].setPauseTime(pause);
		}	
	}
	
	@Override
	public void addJob(Job newJob) {
		newJob.setEntrance(); //update the entrance in the queue
		
		//try to add the job inside the queue, if the queue is full, then drop the job
		if(jobQueue.size() != sizeQueue) {
			jobQueue.addNew(newJob);
		}	

		if(nextEvent) {
			refresh();
			animation.pause();
			animation.resetNextEvent();
		}
	}

	public Point getPosition() {
		return pos;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getsizeQueue() {
		return sizeQueue;
	}
	
	public int getNumberWaitingJobs() {
		return jobQueue.size();
	}
	
	public QueuePolicyNonPreemptive getQueuePolicy() {
		return policyType;
	}
	
	public JobContainer getNextContainer() {
		return next;
	}
	
	public void paintQueueSize() {
		paintQueueSize = true;
	}

	public void updateNServers(int nservers){
		this.nServers = nservers;
		float position = 0.0f;
		if(nServers == 2) {
			position = -height/2;
		}
		circles = new CircleStation[nServers];
		for(int i = 0; i < nServers; i++) {
			circles[i] = new CircleStation(this, position);
			position *= -1;
		}
	}

	public void setVelocityFactor(int value) {
		for(CircleStation c: circles) {
			c.setVelocityFactor(value);
		}
	}

	/** Set the Step simulation on */
	public void nextEvent() {
		nextEvent = true;
	}

	/** Set the Step simulation off */
	public void resetNextEvent() {
		nextEvent = false;
	}

}
