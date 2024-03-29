package jmt.jteach.animations;

import java.awt.Point;
import java.awt.Rectangle;

import jmt.gui.common.animation.StationAnimation;

public class QueueStation extends StationAnimation{

    public QueueStation(Point location, long residenceTime) {
		super("Server", new Rectangle(location.x - 20, location.y - 10, 40, 20), residenceTime);
	}
}
