

package jmt.jteach.animation;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import jmt.jteach.Distributions;
import jmt.jteach.Simulation.Simulation;

/**
 * This class is a super class for all the animations.
 * It is needed, since it is a way of having a class that represents all the animations, being also at the same time a JComponent
 *
 * @author Lorenzo Torri
 * Date: 22-mar-2024
 * Time: 10.30
 */
public class AnimationClass extends JComponent implements Animation{
	//each Animation must have a jobList of the Jobs inside the Animation. This because the rendering must be performed by the animation, so that it can paint the jobs over all the other components
	protected List<Job> jobList; //instanciate it inside the constructor of the subclass
	protected Animator anim;

	protected Simulation simulation;

	public AnimationClass(){
		jobList = new ArrayList<>();
	}
	
	@Override
	public void refresh() {}

	@Override
	public void paint(Graphics g) {}

	@Override
	public void start() {}

	@Override
	public void pause() {}

	@Override
	public void reload() {}

	@Override
	public void stop() {}

	@Override
	public void next() {}

	/** To set all the subcomponents with nextEvent = false (because we want, after an event, the possibility to continue the simulation in normal mode or with nextEvent way) */
	public void resetNextEvent() { }

	@Override
	public void updatePause(long pause) {}
	
	/**
	 * This method adds a new job inside the list of jobs of the Animation.
	 * It is usually called by the source of the Animation
	 * @param job to add in the list
	 */
	public void addNewJob(Job job) {
		jobList.add(job);
	}
	
	/**
	 * This method removes a job inside the list of jobs of the Animation.
	 * It is usually called by the sink of the Animation
	 * @param job to remove from the list
	 */
	public void removeJob(Job job) {
		jobList.remove(job);
	}

	/**
	 * Method for updating the SingleQueue (called when the create button is pressed)
	 * @param sim new info about the simulation
	 * @param nservers number of servers
	 * @param serviceTime distribution of the service time
	 * @param interArrivalTime distribution of the inter-arrival time
	 */
	public void updateSingle(Simulation sim, int nservers, Distributions serviceTime, Distributions interArrivalTime){ }

	/**
	 * Method for updating the MultipleQueue
	 * @param sim new info about the simulation
	 * @param serviceTime distribution of the service time
	 * @param interArrivalTime distribution of the inter-arrival time
	 */
	public void updateMultiple(Simulation sim, Distributions serviceTime, Distributions interArrivalTime){}

	/**
	 * Method for updating the MultipleQueue with routing Policy = PROBABILISTIC
	 * @param sim new info about the simulation
	 * @param percentages new array of percentages associated to the outgoing arcs from the router
	 * @param serviceTime distribution of the service time
	 * @param interArrivalTime distribution of the inter-arrival time
	 */
	public void updateMultiple(Simulation sim, double[] percentages, Distributions serviceTime, Distributions interArrivalTime){}


	public Animator getAnimator(){
		return anim;
	}

	public Simulation getSimulation(){
		return simulation;
	}
}
