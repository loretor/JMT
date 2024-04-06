package jmt.jteach;

public class ConstantsJTch {

    //descriptions of all the policies
    public static final String FIFO_DESCRIPTION = 
        "The FIFO (First In First Out) sheduling Policy is a scheduling algorithm that orders the jobs inside the queue of a station based on their arrival time. The first task that arrives is the first to be executed and subsequent tasks are executed in the order of their arrival.";
    public static final String LIFO_DESCRIPTION = 
        "The LIFO (Last In First Out) sheduling Policy is a scheduling algorithm that orders the jobs inside the queue of a station based on their arrival time. Most recently arrived tasks are executed before any others. It operates on a 'last come, first served' basis, similar to a stack data structure.";
    public static final String SJF_DESCRIPTION =
        "The SJF (Shortest Job First) scheduling Policy is a scheduling algorithm that orders the job inside the queue based on their service time. Jobs with smaller service time are prioritized with respect to the others inside the queue.";
    public static final String LJF_DESCRIPTION =
        "The LJF (Longest Job First) scheduling Policy is a scheduling algorithm that orders the job inside the queue based on their service time. Jobs with higher service time are prioritized with respect to the others inside the queue.";
    public static final String PRIO_DESCRIPTION =
        "The Priority scheduling Policy is a scheduling algorithm that assigns to each task a priority and the queue is ordered based on the priority of the jobs. In this simulation tasks have a priority from 1 to 5, where 1 indicates max priority and 5 the min one.";
    public static final String RR_DESCRIPTION =
        "The RR (Round Robin) routing policy is a routing algorithm to distribute tasks when they arrive at the router. Jobs are routed randomly to one of the edges connected to the router.";
    public static final String PROB_DESCRIPTION =
        "The Probabilistic routing policy is a routing algorithm to distribute tasks when they arrive at the router. Jobs are routed based on probabilities to one of the edges connected to the router.";
    public static final String JSQ_DESCRIPTION =
        "The JSQ (Join the Shortes Job Queue) routing policy is a routing algorithm to distribute tasks when they arrive at the router. Jobs are routed based on the station connected to the router with less jobs in its queue";
        
    //help text
    public static final String[] HELP_BUTTONS_ANIMATIONS = {"Start the simulation or reload it, if it was paused", "Pause the running simulation","Reload the simulation",  "Open the help page"};
    public static final String[] HELP_BUTTONS_MAINPANEL = {"Opens a new panel with Preemptive Scheduling Simulation",
                                                "Opens a new panel with Non Preemptive Scheduling Simulation",
												"Opens a new panel with a Probabilistic Routing simulation",
												"Opens a new panel with a Join Shortest Job Queue Routing simulation",
												"Opens a new panel with a Round Robin simulation",
												"Opens a new panel with a M/M/1 Queue Markov Chain",
												"Opens a new panel with a M/M/1/k Queue Markov Chain",
												"Opens a new panel with a M/M/c Queue Markov Chain",
												"Opens a new panel with a M/M/c/k Queue Markov Chain"};
}
