package jmt.jteach.Wizard;

/**
 * This interface is for all the methods associated to the actions.
 * Each WizardPanel has to implement those methods. If one panel does not support an action, then method can remain empty.
 *
 * @author Lorenzo Torri
 * Date: 29-mar-2024
 * Time: 15.43
 */
public interface WizardPanelTCH{
    /** Opens the panel for help */
    public void openHelp();

    /** Starts the animation */
    public void startAnimation();

    /** Pauses the animation */
    public void pauseAnimation();

    /** Reloads the animation */
    public void reloadAnimation();

    /** Updates the animation to the next step */
    public void nextStepAnimation();

    /** Goes back to the main menu */
    public void exit();

    /** Stops the simulation, used only in Markov Chains */
    public void stopAnimation();
}
