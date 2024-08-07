package jmt.jmva.analytical.solvers.multiSolverAlgorithms;

import jmt.engine.random.engine.RandomEngine;
import jmt.jmva.analytical.solvers.SolverMulti;
import jmt.jmva.analytical.solvers.dataStructures.QNModel;
import jmt.jmva.analytical.solvers.exceptions.BTFMatrixErrorException;
import jmt.jmva.analytical.solvers.exceptions.InconsistentLinearSystemException;
import jmt.jmva.analytical.solvers.exceptions.InternalErrorException;
import jmt.jmva.analytical.solvers.queueingNet.QNSolver;
import jmt.jmva.analytical.solvers.queueingNet.RGFSolver;

/**
 * Solves a multiclass closed model, using RGF algorithm.
 */
public class SolverMultiClosedRGF extends SolverMulti {

	//Array containing population for each class
	protected int[] population;
	protected Integer scale = 1000000;

	/**
	 * Contains the queueing network model
	 */
	protected QNModel qnm;
	/**
	 * Number of threads that the MoMSolver should use
	 */
	protected int nThreads;

	private RandomEngine engine = RandomEngine.makeDefault();

	/** Creates new SolverMultiClosedRGF
	 *  @param  stations    number of service centers
	 *  @param  classes     number of classes
	 *  @param  population  array of class populations
	 */
	public SolverMultiClosedRGF(int classes, int stations, int[] population) {
		super(classes, stations);
		this.classes = classes;
		this.stations = stations;
		this.population = population;
	}

	/** Initializes the solver with the system parameters.
	 * It must be called before trying to solve the model.
	 *  @param  t   array of the types (LD or LI) of service centers
	 *  @param  s   matrix of service times of the service centers
	 *  @param  v   array of visits to the service centers
	 *  @param  nThr The number of threads the solver should use
	 *  @return True if the operation is completed with success
	 */
	public boolean input(int[] t, double[][][] s, double[][] v, int nThr) {
		if ((t.length > stations) || (s.length > stations) || (v.length > stations)) {
			// wrong input.
			return false;
		}
		nThreads = nThr;
		visits = new double[stations][classes];
		for (int i = 0; i < stations; i++) {
			System.arraycopy(v[i], 0, visits[i], 0, classes);
		}

		servTime = new double[stations][classes][1];
		for (int i = 0; i < stations; i++) {
			for (int r = 0; r < classes; r++) {
				servTime[i][r][0] = s[i][r][0] * scale;
			}
		}

		try {
			int M = 0, R = this.classes;
			double[] Z = new double[classes];
			for (int r = 0; r < R; r++) {
				Z[r] = 0;
			}

			// Discover delay times (think times)
			for (int i = 0; i < stations; i++) {
				if (t[i] == LI) {
					M++;
				} else if (t[i] == LD) {
					for (int r = 0; r < classes; r++) {
						Z[r] += (int) (servTime[i][r][0] * visits[i][r]);
					}
				} else {
					return false;
				}
			}
			// Now Z contains the delay times

			// Discover service demands
			double[][] D = new double[M][R];
			int mIndex = 0; // current queue
			for (int i = 0; i < stations; i++) {
				if (t[i] == LI) {
					for (int r = 0; r < classes; r++) {
						D[mIndex][r] = (int) (servTime[i][r][0] * visits[i][r]);
					}
					mIndex++;
				}
			}
			// Now D contains service demands

			// Create queue multiplicities array
			// All multiplicities are set to 1, as JMT does not seem to use queue multiplicities

			Integer[] multiplicities = new Integer[M];
			for (int m = 0; m < M; m++) {
				multiplicities[m] = 1;
			}

			// Transform population from int[] to Integer[]
			Integer[] N = new Integer[R];
			for (int r = 0; r < R; r++) {
				N[r] = population[r];
			}

			// Instantiate queueing network model
			qnm = new QNModel(R, M, t, N, Z, multiplicities, D);
		} catch (Exception ex) {
			ex.printStackTrace();
			// Return false if initialisation fails for any reason.
			return false;
		}
		return true;
	}

	/**
	 * Solves the system through the RGF algorithm.
	 * input(...) must have been called first.
	 *
	 * @throws InternalErrorException
	 * @throws BTFMatrixErrorException
	 * @throws InconsistentLinearSystemException
	 * @throws OperationNotSupportedException
	 */
	@Override
	public void solve() {
		QNSolver c = null;
		try {
			c = new RGFSolver(qnm);
			c.computeNormalisingConstant();
		} catch (Exception e) {
			e.printStackTrace();
			double D[][]=new double[qnm.M][qnm.R];
			int T[]=new int[qnm.M];
			double Z[]=new double[qnm.R];
			Integer mi[]=new Integer[qnm.M];
			Integer N[]=new Integer[qnm.R];
			Integer pert = 1;
			Integer newscale = scale  * pert;
			for (int r=0; r<qnm.R; r++) {
				Z[r] = qnm.getDelay(r) * pert;
				N[r] = qnm.getPopulationVector().get(r);
				for (int i=0; i<qnm.M; i++) {
					T[i] = qnm.getQueueType(i).ordinal();
					mi[i] = qnm.getMultiplicitiesVector().get(i);
					// Replaced Math.random
					D[i][r] = (qnm.getDemand(i, r) * pert + (Integer) (i + 1) + (Integer) (int) Math.round((int)100*engine.raw2())); // i is a perturbation of 10^-6 magnitude
				}
			}
			scale = newscale;

			QNModel qnm2 = null;
			try {
				qnm2 = new QNModel(qnm.R, qnm.M, T, N, Z, mi, D);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			qnm = qnm2;
			try {
				c = new RGFSolver(qnm);
				c.computeNormalisingConstant();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		try {
			c.computePerformanceMeasures();
		} catch (InternalErrorException e) {
			e.printStackTrace();
		}

		clsThroughput = qnm.getMeanThroughputsAsDoubles();
		queueLen = qnm.getMeanQueueLengthsAsDoubles();

		throughput = new double[stations][classes];
		utilization = new double[stations][classes];
		scThroughput = new double[stations];
		scUtilization = new double[stations];
		scResidTime = new double[stations];
		scQueueLen = new double[stations];
		for (int m = 0; m < qnm.M; m++) {
			for (int r = 0; r < qnm.R; r++) {
				throughput[m][r] = clsThroughput[r] * visits[m][r] * scale;
				utilization[m][r] = throughput[m][r] * servTime[m][r][0] / scale; // Umc=Xmc*Smc
				residenceTime[m][r] = queueLen[m][r] / clsThroughput[r] / scale;
				scThroughput[m] += throughput[m][r];
				scUtilization[m] += utilization[m][r];
				scResidTime[m] += residenceTime[m][r];
				scQueueLen[m] += queueLen[m][r];
			}
		}

		//NEW
		//@author Stefano Omini
		//compute system parameters
		sysResponseTime = 0;
		sysNumJobs = 0;

		//for (c = 0; c < classes; c++) {
		//	for (m = 0; m < stations; m++) {
		//		clsRespTime[c] += residenceTime[m][c];
		//		sysNumJobs += queueLen[m][c];
		//	}
		//	sysResponseTime += clsRespTime[c];
		//}

		sysThroughput = sysNumJobs / sysResponseTime;
		//end NEW
	}

	@Override
	public boolean hasSufficientProcessingCapacity() {
		// only closed class - no saturation problem
		return true;
	}

}
