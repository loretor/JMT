package jmt.jteach.animations;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import jmt.gui.common.animation.Animator;
import jmt.gui.common.animation.DefaultIconsToolkit;
import jmt.gui.common.animation.QueueNetAnimation;
import jmt.gui.common.animation.StationAnimation;

/**
 * Class for controlling the animation of Queue Policy
 * @author Lorenzo Torri
 * Date: 22-mar-2024
 * Time: 08.55
*/
public class QNASingleQueue extends QueueNetAnimation{

    PausableAnimator animator;

    public QNASingleQueue(){
        super(new DefaultIconsToolkit());    
        setBounds(new Rectangle(198, 98));   
        createNet();

        animator = new PausableAnimator(30, this);
    }

    public void createNet(){
        StationAnimation station = new StationAnimation("server", new Rectangle(200,200), 20);
        addStation(station);
    }

    public void start(){
        animator.display();
    }
}
