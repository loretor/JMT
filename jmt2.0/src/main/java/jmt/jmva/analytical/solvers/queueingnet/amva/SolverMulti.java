package jmt.jmva.analytical.solvers.queueingNet.amva;

import java.io.PrintWriter;

/**
 * SolverMulti is an abstract class, which contains the methods
 * initialize and solve multiclass models.
 * @author Federico Granata, Stefano Omini

 */
public abstract class SolverMulti {

	/**---------------CONSTANTS DEFINITION------------------------*/

	//NEW
	//before modifying this class, these constants were imported from single class solver
	//@author Stefano Omini
	/** constant for Load Dependent service center  */
	public static final int LD = 0;

	/** constant for Load Independent service center  */
	public static final int LI = 1;

	/** constant for delay center*/
	public static final int DELAY = 2;

	//end NEW

	/** constant for Open Class */
	public static final int OPEN_CLASS = 0;

	/** constant for Closed Class  */
	public static final int CLOSED_CLASS = 1;

	/**---------------MODEL DEFINITION------------------------*/

	/** number of service centers */
	protected int stations = 0;

	/** number of classes  */
	protected int classes = 0;

	/** array of names of service centers*/
	protected String[] name;

	/** array of types of service centers */
	protected int[] type;

	/** array of number of servers for each service center */
	protected int[] servers;

	/** service times for each service station, class, population<br>
	 * [station] [class] [population] */
	protected double[][][] servTime;

	/** visits for each service station, class<br>
	 * [station] [class] */
	protected double[][] visits;

	/**---------------MODEL SOLUTION------------------------*/

	/** throughput for each service station, class<br>
	 * [station] [class] */
	protected double[][] throughput;

	/** utilization for each service station, class<br>
	 * [station] [class] */
	protected double[][] utilization;

	/** queue length for each service station, class<br>
	 * [station] [class] */
	protected double[][] queueLen;

	/** residence time for each service station, class<br>
	 * [station] [class] */
	protected double[][] residenceTime;

	/** throughputs of the service centers<br>
	 *  [station] */
	protected double[] scThroughput;

	/** utilization of the service centers<br>
	 *  [station] */
	protected double[] scUtilization;

	/** queue length of the service centers<br>
	 *  [station] */
	protected double[] scQueueLen;

	/** residence time of the service centers<br>
	 *  [station] */
	protected double[] scResidTime;

	/** response time for each class<br>
	 * [class] */
	protected double[] clsRespTime;

	/** throughput for each class<br>
	 * [class] */
	protected double[] clsThroughput;

	/** average number of users for each class<br>
	 * [class] */
	protected double[] clsNumJobs;

	/** System response time  */
	protected double sysResponseTime = 0;

	/** System throughput */
	protected double sysThroughput = 0;

	/** Number of jobs in the system */
	protected double sysNumJobs = 0;

	/** Printer writer: print formatted representations of objects to a text-output stream */
	protected PrintWriter pw = new PrintWriter(System.out, true);

	/**
	 * Creates a multiclasss solver
	 * @param classes number of classes.
	 * @param stations number of stations.
	 */
	public SolverMulti(int classes, int stations) {
		this.classes = classes;
		this.stations = stations;
		name = new String[stations];
		type = new int[stations];
		servTime = new double[stations][classes][];
		visits = new double[stations][classes];

		throughput = new double[stations][classes];
		queueLen = new double[stations][classes];
		utilization = new double[stations][classes];
		residenceTime = new double[stations][classes];

		scThroughput = new double[stations];
		scUtilization = new double[stations];
		scQueueLen = new double[stations];
		scResidTime = new double[stations];

		clsRespTime = new double[classes];
		clsThroughput = new double[classes];
		clsNumJobs = new double[classes];
	}

	/** Initializes the Multiclass solver with the model parameters.
	 *  @param  n   array of names of service centers.
	 *  @param  t   array of the types (LD or LI) of service centers.
	 *  @param  s   matrix of service time of the service centers.
	 *  @param  v   matrix of visits to the service centers.
	 *  @return true if the operation is completed with success
	 */
	public boolean input(String[] n, int[] t, double[][][] s, double[][] v) {

		if ((n.length != stations) || (t.length != stations) || (s.length != stations) || (v.length != stations)) {
			return false; // wrong input.
		}

		System.arraycopy(n, 0, name, 0, stations);
		System.arraycopy(t, 0, type, 0, stations);

		for (int i = 0; i < stations; i++) {

			System.arraycopy(v[i], 0, visits[i], 0, classes);

			for (int j = 0; j < classes; j++) {
				if (t[i] != LD) {
					servTime[i][j] = new double[1];
				}
				System.arraycopy(s[i][j], 0, servTime[i][j], 0, s[i][j].length);
			}
		}
		return true;
	}

	/**
	 * Must be implemented to create a multi class model solver.
	 */
	public abstract void solve();

	//NEW
	//@author Stefano Omini

	/**
	 * A system is said to have sufficient capacity to process a given load
	 * <tt>lambda</tt> if no service center is saturated as a result of the combined loads
	 * of all the classes.
	 * <br>
	 * Must be implemented to create a multi class model solver.
	 * <br>
	 * WARNING: This method should be called before solving the system.
	 * @return true if sufficient capacity exists for the given workload, false otherwise
	 *
	 *
	 */
	public abstract boolean hasSufficientProcessingCapacity();

	//end NEW

	/** Returns the throughput of the given class for the requested station
	 *  @param  cent    the number of the service center
	 *  @param  cls    the customer class
	 *  @return the throughput
	 */
	public double getThroughput(int cent, int cls) {
		return throughput[cent][cls];
	}

	/**
	 * Returns the throughput of each class for each station
	 * @return the throughput[center][class] matrix for the system
	 */
	public double[][] getThroughput() {
		return throughput;
	}

	/** Returns the utilization of the given class for the requested station
	 *  @param  cent    the number of the service center
	 *  @param  cls    the customer class
	 *  @return the utilization
	 */
	public double getUtilization(int cent, int cls) {
		return utilization[cent][cls];
	}

	/**
	 * Returns the throughput of each class for each station
	 *  @return the utilization[center][class] matrix for the system
	 */
	public double[][] getUtilization() {
		return utilization;
	}

	/** Returns the queue length of the given class for the requested station
	 *  @param  cent    the number of the service center
	 *  @param  cls    the customer class
	 *  @return the queue length
	 */
	public double getQueueLen(int cent, int cls) {
		return queueLen[cent][cls];
	}

	/**
	 *  Returns the queue length of each class for each station
	 *  @return the queueLen[center][class] matrix for the system
	 */
	public double[][] getQueueLen() {
		return queueLen;
	}

	/** Returns the residence time of the given class for the requested station
	 *  @param  cent    the number of the service center
	 *  @param  cls    the customer class
	 *  @return the residence time
	 */
	public double getResTime(int cent, int cls) {
		return residenceTime[cent][cls];
	}

	/**
	 * Returns the residence time of each class for each station
	 * @return the residenceTime[center][class] matrix for the system
	 */
	public double[][] getResTime() {
		return residenceTime;
	}

	/** Returns the throughput for the requested service center
	 *  @param  cent    the number of the service center
	 *  @return the throughput
	 */
	public double getAggrThroughput(int cent) {
		return scThroughput[cent];
	}

	/**
	 * Returns the throughput for each service center
	 * @return the throughput for each service center in the system
	 */
	public double[] getAggrThroughput() {
		return scThroughput;
	}

	/** Returns the utilization for the requested service center
	 **  @param  cent    the number of the service center
	 *  @return the utilization
	 */
	public double getAggrUtilization(int cent) {
		return scUtilization[cent];
	}

	/**
	 * Returns the utilization for each service center
	 * @return the utilization for each service center in the system
	 */
	public double[] getAggrUtilization() {
		return scUtilization;
	}

	/** Returns the queue length for the requested service center
	 *  @param  cent    the number of the service center
	 *  @return the queue length
	 */
	public double getAggrQueueLen(int cent) {
		return scQueueLen[cent];
	}

	/**
	 *  Returns the queue length for each service center
	 *  @return the queue length for each service center in the system
	 */
	public double[] getAggrQueueLen() {
		return scQueueLen;
	}

	/** Returns the residence time for the requested service center
	 *  @param  cent    the number of the service center
	 *  @return the residence time
	 */
	public double getAggrResTime(int cent) {
		return scResidTime[cent];
	}

	/**
	 * Returns the residence time for each service center
	 *  @return the residence time for each service center in the system
	 */
	public double[] getAggrResTime() {
		return scResidTime;
	}

	/** Returns the response time of the whole system for the requested customer class
	 *  @param  cls    the customer class
	 *  @return the response time
	 */
	public double getClsResTime(int cls) {
		return clsRespTime[cls];
	}

	/**
	 * Returns the response time of the whole system for each customer class
	 * @return the overall response time for each customer class in the system
	 */
	public double[] getClsResTime() {
		return clsRespTime;
	}

	/** Returns the throughput of the whole system for the requested customer class
	 *  @param  cls    the customer class
	 *  @return the overall throughput
	 */
	public double getClsThroughput(int cls) {
		return clsThroughput[cls];
	}

	/**
	 * Returns the throughput of the whole system for each customer class
	 * @return the overall throughput for each customer class in the system
	 */
	public double[] getClsThroughput() {
		return clsThroughput;
	}

	/** Returns the response time of the system
	 *  @return the response time
	 */
	public double getTotResTime() {
		return sysResponseTime;
	}

	/** Returns the throughput of the system
	 *  @return the throughput
	 */
	public double getTotThroughput() {
		return sysThroughput;
	}

	/** Prints all the calculated indices in the system.out.
	 *  It is used to debug or test the class.
	 */
	public void printIndices() {
		for (int i = 0; i < stations; i++) {
			pw.println("Indices of " + name[i]);
			for (int j = 0; j < classes; j++) {
				pw.println("of class " + j);
				pw.println("throughput        : " + throughput[i][j]);
				pw.println("utilization       : " + utilization[i][j]);
				pw.println("mean queue length : " + queueLen[i][j]);
			}
		}
	}

}
