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

package jmt.jteach;

/**
 * Class that contains most of the descriptions and help labels of JTeach
 * 
 * @author Lorenzo Torri
 * Date: 29-mar-2024
 * Time: 13.40
 */
public class Constants {

    //all the algorithms
    public static final String FCFS = "FCFS";
    public static final String LCFS = "LCFS";
    public static final String SJF = "SJF";
    public static final String LJF = "LJF";

    public static final String PS = "PS";

    public static final String RR = "RR";
    public static final String PROBABILISTIC = "PROBABILISTIC";
    public static final String JSQ = "JSQ";

    //descriptions of all the policies
    public static final String FCFS_DESCRIPTION = 
        "The FCFS (First come first served) sheduling Policy is a scheduling algorithm that orders the jobs inside the queue of a station based on their arrival time. The first task that arrives is the first to be executed and subsequent tasks are executed in the order of their arrival.";
    public static final String LCFS_DESCRIPTION = 
        "The LCFS (Last come first served) sheduling Policy is a scheduling algorithm that orders the jobs inside the queue of a station based on their arrival time. Most recently arrived tasks are executed before any others. It operates on a 'last come, first served' basis, similar to a stack data structure.";
    public static final String SJF_DESCRIPTION =
        "The SJF (Shortest Job First) scheduling Policy is a scheduling algorithm that orders the job inside the queue based on their service time. Jobs with smaller service time are prioritized with respect to the others inside the queue.";
    public static final String LJF_DESCRIPTION =
        "The LJF (Longest Job First) scheduling Policy is a scheduling algorithm that orders the job inside the queue based on their service time. Jobs with higher service time are prioritized with respect to the others inside the queue.";
    public static final String FCFS_PR_DESCRIPTION =
        "The FCFS-PR (First come first served with priority) scheduling Policy is a scheduling algorithm that orders the job inside the queue based on their priority. In the simulation each job has a priority from 1 to 5. Jobs with same priority are ordered with FCFS policy.";
    
    public static final String RR_DESCRIPTION =
        "The RR (Round Robin) routing policy is a routing algorithm to distribute tasks when they arrive at the router. Jobs are routed randomly to one of the edges connected to the router.";
    public static final String PROB_DESCRIPTION =
        "The Probabilistic routing policy is a routing algorithm to distribute tasks when they arrive at the router. Jobs are routed based on probabilities to one of the edges connected to the router.";
    public static final String JSQ_DESCRIPTION =
        "The JSQ (Join the Shortes Job Queue) routing policy is a routing algorithm to distribute tasks when they arrive at the router. Jobs are routed based on the station connected to the router with less jobs in its queue";
    
    //introduction AnimationPanel
    public static final String INTRODUCTION_SIMULATION = "<html><body>"
        + "<p style='text-align:justify;'>"
        + "<font size=\"4\"><b>Simulation</b></font><br>"
        + "<font size=\"3\">In this panel, you can simulate a queue or routing policy. "
        + "Parameters can be set in the <i>Parameters</i> panel and once selected, click on <i>Create</i> button. "
        + "At this point, simulation can be executed. <br>"
        + "At any time, simulation parameters can be altered to initiate a new simulation. <br>"
        + "Each time a simulation with different parameters is initiated, new results will be displayed in the <i>Results</i> panel.</font>"
        + "</p></body></html>";

    public static final String NO_DESCRIPTION = "<html><body>"
        + "<p style='text-align:justify;'>"
        + "<font size=\"3\"> <i>No description, set parameters first</i>"
        + "</p></body></html>";

    //introduction ResultsPanel
    public static final String INTRODUCTION_RESULTS = "<html><body>"
        + "<p style='text-align:justify;'>"
        + "<font size=\"4\"><b>Simulation Results</b></font><br>"
        + "<font size=\"3\">In this panel, it is possible to visualize all the results of the simulations and to compare them. "
        + "When a new simulation is created and started, the results of the simulation will be displayed in this table. "
        + "Simulation results are ordered chronologically based on the time the associated simulation was run. <br>"
        + "Simulations results can also be removed from the table. <br>"
        + "Please note that returning to the <i>Main Panel</i> will result in the loss of all table data.</font>"
        + "</p></body></html>";
    
    //tooltips of the MainPanel
    public static final String[] PREEMPTIVE_TOOLTIPS = {
        "First Come First Served",
        "Last Come First Served",
        "Shortest Job First",
        "Longest Job First",
        "First Come First Served with Priority"
    };

    public static final String[] ROUTING_TOOLTIPS = {
        "Round Robin",
        "Probabilistic",
        "Join the Shortest Queue"
    };

    public static final String[] MARKOV_TOOLTIPS = {
        "M/M/1 Station, 1 Server",
        "M/M/1/k Finite Capacity Station, 1 Server",
        "M/M/c Station, c Servers",
        "M/M/c/k Finite Capacity Station, c Servers"
    };
    
    //help text
    public static final String[] HELP_BUTTONS_ANIMATIONS = {
        "Start the simulation or reload it, if it was paused", 
        "Pause the running simulation",
        "Reload the simulation",  
        "Perform a next step in the simulation",
        "Open the help page"
    };
        
    public static final String[] HELP_BUTTONS_MAINPANEL = {
        "Select the algorithm for the Non Preemptive Scheduling Simulation",
        "",
        "Select the algorithm for the Preemptive Scheduling Simulation",
        "",
        "Opens a new panel with a Round Robin simulation",
        "",
        "Opens a new panel with a the Probabilistic Routing simulation",
        "",
        "Select the algorithm for a Routing simulation",       
        "", 
        "Select the type of Queue Markov Chain",
    };
    
    public static final String[] HELP_PARAMETERS_PANELS = {
        "Choose the type of Algorithm for the simulation",
        "Select how many servers are available for each station",
        "Select the type of distribution for the inter arrival time between jobs",
        "Select the type of distribution for the service time for each station in the simulation",
        "By default, the simulation runs indefinitely. To change this behavior, uncheck the box and set the number of jobs to generate",
        "Once all the parameters are setted, click this button to create the animation. At this point you can start the animation"
    };

    public static final String[] HELP_PROBABILITIES = {
        "Set the first two probabilities associated to the three outgoing edges of the router. The third probability is computed by the tool as 1.00 - p1 - p2. The create button is blocked if p1 + p2 > 1",
        "Set the probability associated to the first outgoing edge of the router. Probability = 0, means that no traffic is routed towards this edge, while = 1 means all the traffic is routed towards it",
        "Set the probability associated to the second outgoing edge of the router. Probability = 0, means that no traffic is routed towards this edge, while = 1 means all the traffic is routed towards it"
    };

    public static final String[] HELP_BUTTONS_MARKOV = {
        "Start the simulation or reload it, if it was paused", 
        "Pause the running simulation",
        "Stop the running simulation",
        "Open the help page"
    };
}
