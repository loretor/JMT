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
import java.awt.Graphics2D;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JPanel;

/** 
 * This class is needed to understand the direction where the edge is pointing based on the starting point and the finish point.
 * It is also useful for painting the circle of the Job on top of the edge
 */
enum Direction{
	UP{
		@Override
		public int direction() {
			return -1;
		}
	},
	DOWN{
		@Override
		public int direction() {
			return 1;
		}
	},
	LEFT{
		@Override
		public int direction() {
			return -1;
		}
	},
	RIGHT{
		@Override
		public int direction() {
			// TODO Auto-generated method stub
			return 1;
		}
	};
	
	//method for understanding the direction 1 or -1
	public abstract int direction();
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
	private AnimationClass animation;
	
	//--variables of the edge
	private boolean centered;
	private boolean isArrow;
	private Point start;
	private Point finish;
	private Direction direction;
	
	private List<Job> jobList;
	private JobContainer nextContainer = null;
	
	//for the probabilistic routing
	private boolean paintPercentage = false;
	private double percentage = 0.00;

	//for the nextEvent simulation
	private boolean nextEvent = false;
	
	/**
	 * Constructor
	 * @param animation, AnimationClass which the edge is part of
	 * @param container, JPanel that contains this edge
	 * @param centered, if the edge is y centered with respect to the JPanel or not
	 * @param isArrow, if the edge is an arrow and not a simple line
	 * @param start point of the edge
	 * @param finish point of the edge
	 * @param next, JobContainer next to this edge
	 */
	public Edge(AnimationClass anim, JPanel container, boolean centered, boolean isArrow, Point start, Point finish, JobContainer next) {
		this.animation = anim;
		this.parent = container;
		this.centered = centered;
		this.isArrow = isArrow;
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
			start.y = parent.getY()+(heightPanel)/2;
			finish.y = start.y;
		}
				
		g.setColor(Color.BLACK);
		g.drawLine(start.x, start.y, finish.x, finish.y);	
		
		//only if the routing policy with probability is chosen
		if(paintPercentage) {
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			DecimalFormat df = new DecimalFormat("#.##");
			String value = "prob: "+ df.format(percentage);
			if(direction == Direction.UP || direction == Direction.DOWN) {
				g.drawString(value, start.x - 50,  start.y + direction.direction() * 20);
			}
			else {
				g.drawString(value, start.x,  start.y + direction.direction() * 20);
			}
		}

		//only if the edge is an arrow (for now the arrow is only for a direction = right)
		if(isArrow) {
			Graphics2D g2d = (Graphics2D) g;
			int[] xPoints = {finish.x+5, finish.x, finish.x}; // x coordinates
		    int[] yPoints = {finish.y, finish.y-5, finish.y+5};   // y coordinates

		    g2d.setColor(Color.BLACK);
	        g2d.fillPolygon(xPoints, yPoints, 3); //draw the triangle
		}
	}
	
	/**
	 * Method to paint the percentage near the edge. 
	 * This method is useful for Routing Probabilistic policy
	 * @param p
	 */
	public void paintPercentage(double p) {
		paintPercentage = true;
		percentage = p;
	}
	
	/** Method to understand the direction of the edge based on the values of start and finish point */
	public void setDirection() {
		if(start.x == finish.x) { //two vertical points
			if(start.y > finish.y) {
				direction = Direction.UP;
			}
			else {
				direction = Direction.DOWN;
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
	 * Method to update the current position of a job based on the direction of the edge
	 * @param j Job that has to update its position inside the edge
	 */
	private void moveJob(Job j) {
		Point current = j.getPosition();
		switch(direction) {
			case UP: 
				j.updatePosition(current.x, current.y - j.getSpeed());
				break;
			case DOWN:
				j.updatePosition(current.x, current.y + j.getSpeed());
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
	 * @return boolean, true if the job has reached the finish point, false otherwise
	 */
	private boolean checkFinish(Job j) {
		Point current = j.getPosition();
		//add to current half of the circleSize, in order to consider the center of the circle as the position of the job, and not the start drawing point
		int circleSize = j.getCircleSize();
		switch(direction) {
			case LEFT:
				if(current.x + circleSize/2 <= finish.x) {
					return true;
				}
				break;
			case RIGHT:
				if(current.x + circleSize/2 >= finish.x) {
					return true;
				}
				break;
			case UP:
				if(current.y + circleSize/2 <= finish.y) {
					return true;
				}
				break;
			case DOWN:
				if(current.y + circleSize/2 >= finish.y) {
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

		if(nextEvent) {
			animation.pause();
			animation.resetNextEvent();
		}
	}

	/**
	 * Route a job to the next JobContainer since it has arrived to the final point of the edge
	 * @param i, index of the job inside the JobList of the edge
	 */
	public void routeJob(int i) {
		Job job = jobList.remove(i);
		if(nextContainer != null) {
			if(nextContainer instanceof Edge) {
				job.setOnEdge();
			}
			else {
				job.unsetOnEdge();
			}
			nextContainer.addJob(job);	
		}
		else {
			job.unsetOnEdge();			
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
