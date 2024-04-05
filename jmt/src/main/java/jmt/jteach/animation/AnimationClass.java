package jmt.jteach.animation;

import javax.swing.JComponent;

/**
 * SuperClass of all the Animations, the implementation is done in all the classes that extends this class (different types of animations)
 *
 * @author Lorenzo Torri
 * Date: 31-mar-2024
 * Time: 19.53
 */
public class AnimationClass extends JComponent implements Animation{

    @Override
    public void refresh() {
    }

    @Override
    public void start() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void reload() {
    }

    @Override
    public void updatePause(long pause) {
    }

}
