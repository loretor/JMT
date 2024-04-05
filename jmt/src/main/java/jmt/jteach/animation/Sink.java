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
public class Sink extends JComponent{
    private JPanel parent;
    
    private Image sinkImg;
    private boolean centered = false;
    private Point pos;
    private int size = 50;

    /** Constructor. If the sink has to be in the center of the y axis of the screen, then define pos = (x, 0) and centered = true, otherwise set centered = false and then set the position as you want */
    public Sink(JPanel container, boolean centered, Point pos){
        this.parent = container;
        this.centered = centered;
        this.pos = pos;

        sinkImg = JMTImageLoader.loadImageAwt("Sink");
    }

    public void paint(Graphics g){
        super.paint(g);

		if(centered) {
			int heightPanel = parent.getHeight();	
            int widthPanel = parent.getWidth();
            pos.x = parent.getX() + widthPanel - size - 20;
			pos.y = parent.getY()+(heightPanel-size)/2;
		}
		
		g.drawImage(sinkImg, pos.x, pos.y, size, size, null);
    }

}
