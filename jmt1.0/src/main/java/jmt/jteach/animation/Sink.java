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
 * Class for representing the sink of the animation.
 *
 * @author Lorenzo Torri
 * Date: 01-apr-2024
 * Time: 11.31
 */
public class Sink extends JComponent implements JobContainer{
    private JPanel parent;
    
    private Image sinkImg;
    private boolean centered = false;
    private Point pos;
    private int size = 50;
    
    AnimationClass anim;

    private int maxJobs = -1; //if maxJobs != -1 then there an upper limit to the number of jobs in the system
    private int counterJobs = 0;

    /**
     * Constructor
     * @param container, JPanel that contains this sink
     * @param centered, if the component is centered with respect to the Jpanel
     * @param pos, position of the component. If it is centered then this pos is not important, it can be anything
     * @param anim, AnimationClass associated to this component. It is needed in order to remove the job arrived to the sink also from the list of jobs of the AnimationClass
     */
    public Sink(JPanel container, boolean centered, Point pos, AnimationClass anim){
        this.parent = container;
        this.centered = centered;
        this.pos = pos;
        this.anim = anim;

        sinkImg = JMTImageLoader.loadImageAwt("Sink");
    }

    public void paint(Graphics g){
        super.paint(g);

        if(centered) {
          int widthPanel = parent.getWidth();
          int heightPanel = parent.getHeight();	
          pos.y = parent.getY()+(heightPanel - size)/2 ;
          pos.x = parent.getX()+ widthPanel - 20 - size;
        }
        
        g.drawImage(sinkImg, pos.x, pos.y, size, size, null);
    }
    

	@Override
	public void refresh() {
		if(maxJobs != -1 && counterJobs == 0){
            anim.stop();
        }
	}

	@Override
	public void addJob(Job newJob) {
		//here the addJob is removing the job from the list of jobs
		anim.removeJob(newJob);

        if(maxJobs != -1){
            counterJobs -= 1;
        }  
	}

    public void setMaxJobs(int j) {
        maxJobs = j;
        counterJobs = j;
    }
}