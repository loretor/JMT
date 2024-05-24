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
import java.util.Random;

import javax.swing.JComponent;

import jmt.common.exception.IncorrectDistributionParameterException;
import jmt.jteach.Distributions;

public class Job extends JComponent{
	//--variables needed only for the movement along the edges
	private Point pos;
	private int speed = 2;	
	private boolean onEdge = false; //this parameter is used to know if the current job is on a Edge or not
	
	private int circleSize = 15;
	private int boxWidth = 12;
	private int boxHeight = 30;
	
	private double duration;
	private Color color;
	private int priority;
	
	//those values are only for debug now
	public int maxValue = 10; //this value is for the conversion from the duration to a colored box (try to select a max value accordingly to the type of distribution)
	public Distributions service;
	
	private long entrance; //this value is the associated long value for the entrance of a job inside a JComponent
	private int velocityFactor = 1;
	
	public Job(Distributions service) {	
		color = getRandomColor();
		priority = new Random().nextInt(5)+1; //set priority always > 1 otherwise it does not work properly in the BoxStation
		this.service = service;
		try {
			duration = service.nextRand(); //duration = new Random().nextInt(maxValue+1) + 1;
		} catch (IncorrectDistributionParameterException e) {
			//this will never happen if the parameters of the distribution are correclty chosen
			duration = 0.0;
		}
	}
	
	/** This paint needs to know if we are on an edge or not, because if we are not on an edge, then we do not need to paint anything */
	public void paint(Graphics g) {
		super.paint(g);
		
		if(onEdge) {
			g.setColor(color);
			g.fillOval(pos.x, pos.y, circleSize, circleSize);		
			
			//then draw also the box with the filled rectangle
			g.setColor(Color.BLACK);
			g.drawRect(pos.x + 15, pos.y - 30, boxWidth, boxHeight);

			//TODO:remove those two lines, only for debugging
			//g.setFont(new Font("Arial", Font.BOLD, 13));
			//g.drawString(String.valueOf(duration), pos.x - 15, pos.y-40);
			
			//to convert the duration to the size of the above box
			g.setColor(color);
			int factor = boxHeight/maxValue;
			int result = (int) Math.round(duration*factor >= boxHeight? boxHeight-1: duration*factor); //this solution provides the problem of having jobs with duration > maximum height of the box
			g.fillRect(pos.x + 16, pos.y - 30 + (boxHeight-result), boxWidth-1, result);
		}	
	}

	/** Create a random color associated to each job */
    private Color getRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        return new Color(r, g, b); 
    }
    
    public void setEntrance() {
    	entrance = System.currentTimeMillis()*velocityFactor;
    }
    
    public long getEntrance() {
    	return entrance;
    }
    
    public int getPriority() {
    	return priority;
    }
    
    public Color getColor() {
    	return color;
    }
    
    public double getDuration() {
    	return duration;
    }
    
    /**
     * Method to set the staring point position of a job. It is called by an edge that have this new Job on its route.
     * @param x x position of the Job
     * @param y y position of the Job
     */
    public void setStartingPosition(int x, int y) {
    	pos = new Point(x - circleSize/2, y - circleSize/2); //remove circleSize/2 to adapt the position, since g.fillOval always draws considering the point as the left up corner of the square that contains the circle
    }
    
    /**
     * Method to update the position of a moving job.
     * It is different than the setStartingPoisition, since here we do not have to remove the circleSize/2 part
     * @param x x position of the Job
     * @param y y position of the Job
     */
    public void updatePosition(int x, int y) {
    	pos = new Point(x, y);
    }
    
    public Point getPosition() {
    	return pos;
    }
    
    public int getSpeed() {
    	return speed;
    }
    
    public int getCircleSize() {
    	return circleSize;
    }
    
    /** Called once the Job is going outside of a station */
    public void setDuration() {
    	duration = 0;
    }
    
    public void setOnEdge() {
    	onEdge = true;
    }
    
    public void unsetOnEdge() {
    	onEdge = false;
    }

	public void setVelocityFactor(int value) {
    	speed *= value;
    	velocityFactor = value;
    }

	public void resetVelocityFactor() {
    	speed /= velocityFactor;
    	velocityFactor = 1;
    }
}
