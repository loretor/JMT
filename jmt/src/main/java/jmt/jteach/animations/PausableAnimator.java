package jmt.jteach.animations;

import jmt.gui.common.animation.Animation;

public class PausableAnimator extends Thread {
	private final Object mutex = new Object(); // Used to wait for termination

	
	private boolean isWorking = false; //tells whether this animation is running.

	
	private long sleepTime; //number of millisecs between two frames of the animation


	protected Animation animation = null;

	/**Creates a new instance of Animator.
	 * Frame rate is set by default at 30 frames per seconds.
	 * @param a: Animation to be updated by this animator.{@see animation}
	 */
	public PausableAnimator(Animation a) {
		this(30, a);
	}

	/**Creates a new instance of Animator with explicit definition of framerate.
	 * @param fps: framerate for this animation.
	 * @param a: Animation to be updated by this animator.{@see animation}
	 */
	public PausableAnimator(double fps, Animation a) {
		super();
		animation = a;
		sleepTime = (long) (1000 / fps);
	}

    /**Display the animation */
    public void display(){
        animation.init();
    }

	/**Starts the handled animation*/
	@Override
	public void start() {
		super.start();
		isWorking = true;
	}
    

	/**Performs update of the handled animation as far as terminate() method has not been called.*/
	@Override
	public void run() {
		synchronized (mutex) {
			while (isWorking) {
				try {
					mutex.wait(sleepTime);
				} catch (InterruptedException e) {
					this.terminate();
				}
				if (isWorking) {
					animation.refresh();
				}
			}
		}
	}

	/**Terminates current animation*/
	public void terminate() {
		isWorking = false;
		synchronized (mutex) {
			mutex.notifyAll();
		}
	}

}
