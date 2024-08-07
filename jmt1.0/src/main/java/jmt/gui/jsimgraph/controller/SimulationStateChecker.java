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

package jmt.gui.jsimgraph.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import jmt.common.exception.NetException;
import jmt.engine.NodeSections.PSServer;
import jmt.engine.NodeSections.Server;
import jmt.engine.QueueNet.JobClass;
import jmt.engine.QueueNet.JobClassList;
import jmt.engine.QueueNet.NetNode;
import jmt.engine.QueueNet.NodeList;
import jmt.engine.QueueNet.NodeSection;
import jmt.engine.QueueNet.QueueNetwork;
import jmt.engine.simDispatcher.DispatcherJSIMschema;
import jmt.gui.jsimgraph.JGraphMod.GraphicalQueueState;

/**
 * <p>Title: SimulationStateChecker </p>
 * <p>Description: this class is used to check the state of the model </p>
 *
 * @author Francesco D'Aquino
 *         Date: 7-nov-2005
 *         Time: 12.22.37
 */
public class SimulationStateChecker {
	private boolean initialized;

	private double maxJobCount;

	private Vector<Object> servers;
	private Vector<Object> classes;
	private Mediator mediator;
	private DispatcherJSIMschema dispatcher;
	//It implements a mapping between the graphical representation (the CommonModel Key) and the
	//representation at simulation engine level. The key of the HashMap is the Key of
	//graphical representation
	HashMap<Object, NetNode> serverMap;
	//It implements a mapping between the graphical representation (the CommonModel Key) and the
	//representation at simulation engine level. The key of the HashMap is the Key of
	//graphical representation
	HashMap<Object, JobClass> classMap;
	//Hashmap containing for each server (identified by the graphical Key) a
	//singleServerContentQueues HashMap
	ModelSnapshot serversContent;
	//Hashmap containing for each server (identified by the graphical Key) a
	//singleServerContentUtilizations HashMap
	ModelSnapshot serversUtilization;
	//HashMap containing for each class the number of resident jobs
	HashMap<Object, Integer> singleServerContentQueues;
	//HashMap containing for each class the value of utilization
	HashMap<Object, Double> singleServerContentUtilizations;
	//used to draw the queue and utilization state
	GraphicalQueueState graphicalRepresentation;

	int iteration;

	public SimulationStateChecker(Mediator mediator, DispatcherJSIMschema dispatcher) {
		maxJobCount = Double.MIN_VALUE;
		initialized = false;
		this.mediator = mediator;
		this.dispatcher = dispatcher;
		serverMap = new HashMap<Object, NetNode>(0);
		classMap = new HashMap<Object, JobClass>(0);
		singleServerContentQueues = new HashMap<Object, Integer>(0);
		singleServerContentUtilizations = new HashMap<Object, Double>(0);
		servers = mediator.getStationDefinition().getStationKeysServer();
		classes = mediator.getClassDefinition().getClassKeys();
		graphicalRepresentation = new GraphicalQueueState(mediator);
		serversContent = new ModelSnapshot(servers, classes);
		serversUtilization = new ModelSnapshot(servers, classes);
		iteration = 1;
	}

	private void reset() {
		maxJobCount = Double.MIN_VALUE;
		initialized = false;
		mediator = null;
		dispatcher = null;
		serverMap = null;
		classMap = null;
		singleServerContentQueues.clear();
		singleServerContentQueues = null;
		singleServerContentUtilizations.clear();
		singleServerContentUtilizations = null;
		servers.clear();
		servers = null;
		classes.clear();
		classes = null;
		serversContent.reset();
		serversContent = null;
		serversUtilization.reset();
		serversUtilization = null;
	}

	/**
	 * Initializes the stationMap and classMap variable. It realizes a mapping between
	 * the representation of classes and servers at CommonModel level (by a Key) and
	 * the representation at engine level
	 */
	public void initialize() {
		QueueNetwork net = dispatcher.getSimulation().getNetwork();
		NodeList nodeList = net.getNodes();
		JobClassList classList = net.getJobClasses();
		TreeMap<String, Object> tm = new TreeMap<String, Object>();
		//a TreeMap is used to speedup the following code
		for (int j = 0; j < servers.size(); j++) {
			Object thisKey = servers.get(j);
			String thisName = mediator.getStationDefinition().getStationName(thisKey);
			tm.put(thisName, thisKey);
		}
		//performs the server mapping
		for (int i = 0; i < nodeList.size(); i++) {
			NetNode temp = nodeList.get(i);
			NodeSection serviceSection = null;
			try {
				serviceSection = temp.getSection(NodeSection.SERVICE);
			} catch (NetException ne) {
			}
			if (serviceSection instanceof Server || serviceSection instanceof PSServer) {
				serverMap.put(tm.get(temp.getName()), temp);
			}
		}
		tm.clear();
		//performs the class mapping
		for (int j = 0; j < classes.size(); j++) {
			Object thisKey = classes.get(j);
			String thisName = mediator.getClassDefinition().getClassName(thisKey);
			tm.put(thisName, thisKey);
		}
		for (int i = 0; i < classList.size(); i++) {
			JobClass temp = classList.get(i);
			classMap.put(tm.get(temp.getName()), temp);
		}
		//set the initialized state
		initialized = true;
	}

	/**
	 * Gets the number of residence jobs in each station and fills the serversContent
	 * HashMap
	 */
	public void getModelState() {
		serversContent.clear();
		serversUtilization.clear();
		//maxJobCount = Double.MIN_VALUE;
		//for each server ...
		for (int i = 0; i < servers.size(); i++) {
			Object thisServerKey = servers.get(i);
			// get the mapped NetNode ...
			NetNode thisNode = serverMap.get(thisServerKey);
			if (thisNode == null) {
				continue;
			}
			// get the input section and the service section ...
			NodeSection thisQueue = null;
			NodeSection thisServer = null;
			try {
				thisQueue = thisNode.getSection(NodeSection.INPUT);
				thisServer = thisNode.getSection(NodeSection.SERVICE);
			} catch (NetException ne) {
			}
			singleServerContentQueues.clear();
			//for each class ...
			for (int j = 0; j < classes.size(); j++) {
				try {
					Object thisClassKey = classes.get(j);
					JobClass thisClass = classMap.get(thisClassKey);
					if (thisClass == null) {
						continue;
					}
					if (thisQueue != null) {
						// get the jobs of this class inside the queue
						int jobsNumber = thisQueue.getIntSectionProperty(NodeSection.PROPERTY_ID_RESIDENT_JOBS, thisClass);
						//if the job number is greater than the maximum update it
						if (jobsNumber > maxJobCount) {
							maxJobCount = jobsNumber;
						}
						//put the number of jobs for this class inside the treemap
						singleServerContentQueues.put(thisClassKey, new Integer(jobsNumber));
					}
					if (thisServer != null) {
						//get the utilization for this class
						double utilization = thisServer.getDoubleSectionProperty(NodeSection.PROPERTY_ID_AVERAGE_UTILIZATION, thisClass);
						//put the utilization this class inside the treemap
						singleServerContentUtilizations.put(thisClassKey, new Double(utilization));
					}
				} catch (NetException ne) {
				}
			}
			//put the singleServerContentQueues inside the serversContent
			serversContent.put(thisServerKey, singleServerContentQueues.clone());
			//put the singleServerContentUtilizations inside the serversUtilization
			serversUtilization.put(thisServerKey, singleServerContentUtilizations.clone());
		}

		serversContent.setMaxValue(this.maxJobCount);
	}

	/**
	 * Used only for test purpose
	 */
	public void print() {
		System.out.println("Iteration: " + iteration);

		for (int i = 0; i < servers.size(); i++) {
			Object thisServerKey = servers.get(i);
			if (!serverMap.containsKey(thisServerKey)) {
				continue;
			}
			String thisServerName = mediator.getStationDefinition().getStationName(thisServerKey);
			Map perServerMapQueues = (Map) this.serversContent.get(thisServerKey);
			Map perServerMapUtilizations = (Map) this.serversUtilization.get(thisServerKey);
			//System.out.println("***********************************************");
			System.out.println("    " + thisServerName + ":");
			System.out.println("        Queues state:");
			for (int j = 0; j < classes.size(); j++) {
				Object thisClassKey = classes.get(j);
				if (!classMap.containsKey(thisClassKey)) {
					continue;
				}
				String thisClassName = mediator.getClassDefinition().getClassName(thisClassKey);
				String jobs = Integer.toString(((Integer) perServerMapQueues.get(thisClassKey)).intValue());
				System.out.println("                Jobs of " + thisClassName + " at " + thisServerName + ": " + jobs);
			}
			System.out.println("        Utilization state:");
			for (int j = 0; j < classes.size(); j++) {
				Object thisClassKey = classes.get(j);
				if (!classMap.containsKey(thisClassKey)) {
					continue;
				}
				String thisClassName = mediator.getClassDefinition().getClassName(thisClassKey);
				String utilization = Float.toString(((Double) perServerMapUtilizations.get(thisClassKey)).floatValue());
				System.out.println("                Utilization of " + thisClassName + " at " + thisServerName + ": " + utilization);

			}
			System.out.println("        Degrees per class:");
			int start = 0;
			for (int j = 0; j < classes.size(); j++) {
				Object thisClassKey = classes.get(j);
				if (!classMap.containsKey(thisClassKey)) {
					continue;
				}
				String thisClassName = mediator.getClassDefinition().getClassName(thisClassKey);
				int degrees = (int) (((Double) perServerMapUtilizations.get(thisClassKey)).floatValue() * 360);
				System.out.println("                " + thisClassName + " at " + thisServerName + ":    from " + start + "°" + " to "
						+ (start + degrees) + "°");
				start += degrees;
			}
		}
		System.out.println("\n    Max n° job: " + Integer.toString((int) this.maxJobCount));
		System.out.println("***********************************************\n");
		iteration++;
	}

	/**
	 * Calls the draw method from  to draw queues and utilizations
	 * @param detectChanges 
	 * @throws Exception 
	 */
	public void forceDraw() throws Exception {
		graphicalRepresentation.draw(serversContent, serversUtilization);
	}

	/**
	 * Forces mediator to repaint the JGraph
	 */
	public void forceRepaint() {
		mediator.graphRepaint();
	}

	/**
	 * Check for the state of SimulationStateChecker
	 * @return true if SimulationStateChecker has been initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}

	public ModelSnapshot getServersContent() {
		return serversContent;
	}

	public void setServersContent(ModelSnapshot serversContent) {
		this.serversContent = serversContent;
	}

	public ModelSnapshot getServersUtilization() {
		return serversUtilization;
	}

	public void setServersUtilization(ModelSnapshot serversUtilization) {
		this.serversUtilization = serversUtilization;
	}
	
}
