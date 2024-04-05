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
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

/** 
 * This class is needed to understand the direction where the edge is pointing based on the starting point and the finish point.
 * It is also useful for painting the circle of the Job on top of the edge
 */
enum Direction{
	UP,DOWN,LEFT,RIGHT;
}

/**
 * Class for representing the edge inside the Animation. 
 * Edges can only be straight from a start point to a finish point, so be careful when creating them, they must have either start.x = finish.x or start.y = finish.y
 * When a job arrives to the finish point then it is routed to the next element associated to this edge.
 *
 * @author Lorenzo Torri
 * Date: 26-mar-2024
 * Time: 13.40
 */
public class Edge extends JComponent implements JobContainer{
	private JPanel parent;
	
	//--variables of the edge
	private boolean centered;
	private Point start;
	private Point finish;
	private Direction direction;
	
	private List<Job> jobList;
	private JobContainer nextContainer = null;
	
	/** Constructor for the Queue Policy where edges must be centered in the JPanel */
	public Edge(JPanel container, boolean centered, Point start, Point finish, JobContainer next) {
		this.parent = container;
		this.centered = centered;
		this.start = start;
		this.finish = finish;
		this.nextContainer = next;
		
		jobList = new ArrayList<>();
		
		setDirection();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		if(centered) {
			int heightPanel = parent.getHeight();	
			start.y = parent.getY()+(heightPanel)/2; //need to add -30 which is half of the height of the station
			finish.y = start.y;
		}
		
		g.setColor(Color.BLACK);
		g.drawLine(start.x, start.y, finish.x, finish.y);
		
		for(Job j: jobList) {
			j.paint(g);
		}	
	}
	
	/**
	 * Method to understand the direction of the edge based on the values of start and finish point
	 */
	public void setDirection() {
		if(start.x == finish.x) { //two vertical points
			if(start.y > finish.y) {
				direction = Direction.DOWN;
			}
			else {
				direction = Direction.UP;
			}
		}
		else { //two horizontal points
			if(start.x > finish.x) {
				direction = Direction.LEFT;
			}
			else {
				direction = Direction.RIGHT;
			}
		}
	}

	@Override
	public void refresh() {
		//check for each job if it is still in the edge, if not route it to the next element, otherwise update its position
		for(int i = 0; i < jobList.size(); i++) {
			Job j = jobList.get(i);
			if(checkFinish(j)) {
				routeJob(i);
			}
			else {
				moveJob(j);
			}			
		}		
	}
	
	/**
	 * Method to update the current position of a job.
	 * First check if the current job position is over the finish point, if yes, then route the job to the nextJobContainer, otherwise update its position
	 * The new position of the job depends on the direction where the Edge is going and the velocity of the Job
	 * @param j Job that has to update its position inside the edge
	 */
	private void moveJob(Job j) {
		Point current = j.getPosition();
		switch(direction) {
			case UP: 
				j.updatePosition(current.x, current.y + j.getSpeed());
				break;
			case DOWN:
				j.updatePosition(current.x, current.y - j.getSpeed());
				break;
			case RIGHT:
				j.updatePosition(current.x + j.getSpeed(), current.y);
				break;
			case LEFT:	
				j.updatePosition(current.x - j.getSpeed(), current.y);
				break;
		}
	}
	
	/**
	 * Check whether the Job has completed its path along the edge.
	 * The check is performed based on the direction of the edge and on the position of the job
	 * @param j Job to check
	 * @return a boolean, true if the job has reached the finish point, false otherwise
	 */
	private boolean checkFinish(Job j) {
		Point current = j.getPosition();
		switch(direction) {
			case LEFT:
				if(current.x <= finish.x) {
					return true;
				}
				break;
			case RIGHT:
				if(current.x >= finish.x) {
					return true;
				}
				break;
			case UP:
				if(current.y >= finish.y) {
					return true;
				}
				break;
			case DOWN:
				if(current.y <= finish.y) {
					return true;
				}
				break;
		}
		return false;
	}
	
	@Override
	public void addJob(Job newJob) {
		newJob.setStartingPosition(start.x, start.y); //when a new Job is added, then its position is the starting point of the edge
		jobList.add(newJob);
	}

	public void routeJob(int i) {
		Job job = jobList.remove(i);
		if(nextContainer != null) {
			nextContainer.addJob(job);	
		}		
	}
}
