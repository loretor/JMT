package jmt.jteach;

import java.util.ArrayList;
import java.util.Vector;

import jmt.gui.common.CommonConstants;
import jmt.gui.common.Defaults;
import jmt.gui.common.definitions.CommonModel;
import jmt.gui.common.definitions.ServerType;
import jmt.gui.common.definitions.SimulationDefinition;
import jmt.gui.common.distributions.Deterministic;
import jmt.gui.common.distributions.Distribution;
import jmt.gui.common.distributions.Exponential;
import jmt.gui.common.distributions.Hyperexponential;
import jmt.gui.common.distributions.Uniform;
import jmt.jteach.Simulation.Simulation;
import jmt.jteach.Simulation.SimulationType;

/**
 * This class transforms the information of the Animation to match the structure of the CommonModel class.
 * CommonModel is used to obtain the results of the simulation.
 * 
 * @author Lorenzo Torri
 * Date: 06-mag-2024
 * Time: 17.28
 */
public class Solver implements CommonConstants{
    private CommonModel model;
    private Simulation simulation;
    private boolean isSingleQueue = true;

    private int classNameIndex = 1;
    private int serverNameIndex = 1;

    //------------------- keys of the model --------------------------
    private Object classKey;
    private Object sourceKey;
    private Object sinkKey;
    private Object routerKey;

    //----------- all changable fields of the simulation -------------
    private static final String[] queueStrategies = {
        QUEUE_STRATEGY_FCFS,
        QUEUE_STRATEGY_LCFS,
        QUEUE_STRATEGY_SJF,
        QUEUE_STRATEGY_LJF
    };

    private static Distribution[] distributions = { //for now the same distributions for interarrival time and service time
        new Exponential(),
        new Deterministic(),
        new Uniform(),
        new Hyperexponential()
    };


    //types of measures selectable
    //TODO: remove this part 
	protected static final String[] measureTypes = new String[] {
			"------ Select an index  ------",
			SimulationDefinition.MEASURE_QL,
			SimulationDefinition.MEASURE_QT,
			SimulationDefinition.MEASURE_RP,
			SimulationDefinition.MEASURE_RD,
			SimulationDefinition.MEASURE_AR,
			SimulationDefinition.MEASURE_X,
			SimulationDefinition.MEASURE_U,
			SimulationDefinition.MEASURE_T,
			SimulationDefinition.MEASURE_E,
			SimulationDefinition.MEASURE_L,
			"------ Advanced indexes ------",
			SimulationDefinition.MEASURE_EU,
			SimulationDefinition.MEASURE_DR,
			SimulationDefinition.MEASURE_BR,
			SimulationDefinition.MEASURE_RN,
			SimulationDefinition.MEASURE_RT,
			SimulationDefinition.MEASURE_OS,
			SimulationDefinition.MEASURE_OT,
			SimulationDefinition.MEASURE_P,
			SimulationDefinition.MEASURE_RP_PER_SINK,
			SimulationDefinition.MEASURE_X_PER_SINK,
			SimulationDefinition.MEASURE_FCR_TW,
			SimulationDefinition.MEASURE_FCR_MO,
			SimulationDefinition.MEASURE_FJ_CN,
			SimulationDefinition.MEASURE_FJ_RP,
			SimulationDefinition.MEASURE_FX,
			SimulationDefinition.MEASURE_NS
	};

    /**
     * Create a Solver with one queue (for NON PREEMPTIVE or PROCESSOR SHARING) or with 3 queues
     */
    public Solver(Simulation sim){
        model = new CommonModel();
        simulation = sim;
        isSingleQueue = simulation.getType() != SimulationType.ROUTING;
        setDistributionsParameters();
        addClass();
        
        //first add the queues, so that when you cycle the StationKeys, the first is always a server
        addQueue();
        if(!isSingleQueue){
            addQueue();
            addQueue();
            addRouter();
        }

        addSource();
        addSink();
        
        setConnections();  
        
        //TODO: change the measures, since now you have 3 different stations
        addMeasure();   
    }

    /** Set the parameters for all the distributions */
    public void setDistributionsParameters(){
        distributions[0].setMean(4.0);
        distributions[1].setMean(4.0);

        distributions[2].setMean(3.5);
        distributions[2].setC(0.14433);

        distributions[3].setMean(4.167);
        distributions[3].setC(1.039);
    }

    /** Add a class to the model (similar to the task you perform in JSIM when you click on the class button) */
    private void addClass(){
        classKey = model.addClass(Defaults.get("className") + (++classNameIndex),
            Defaults.getAsInteger("classType").intValue(),
            Defaults.getAsInteger("classPriority"),
            Defaults.getAsDouble("classSoftDeadline"),
            Defaults.getAsInteger("classPopulation"), 
            Defaults.getAsNewInstance("classDistribution")); 
    }

    /** Add a source station to the model (like adding a Source cell to the JGraph of JSIM) */
    private void addSource(){
        sourceKey = model.addStation("Source", STATION_TYPE_SOURCE, 1, new ArrayList<ServerType>());
        //model.setRoutingStrategy(sourceKey, classKey, Defaults.getAsNewInstance("stationRoutingStrategy")); //random routing by default
        model.setClassRefStation(classKey, sourceKey); 
    }
    

    /** Add a queue station to the model (like adding a Station cell to the JGraph in JSIM) */
    private void addQueue(){
        Object serverKey = model.addStation("Queue "+(serverNameIndex), STATION_TYPE_SERVER, 1, new ArrayList<ServerType>());
        serverNameIndex++;

        model.setStationQueueCapacity(serverKey, 5);
        model.setDropRule(serverKey, classKey, "Drop");
        model.setStationQueueStrategy(serverKey, STATION_QUEUE_STRATEGY_NON_PREEMPTIVE);
        model.setQueueStrategy(serverKey, classKey, Defaults.get("stationQueueStrategy"));
        model.setServiceWeight(serverKey, classKey, Defaults.getAsDouble("serviceWeight"));
        model.updateBalkingParameter(serverKey, classKey, STATION_QUEUE_STRATEGY_NON_PREEMPTIVE);

        
        model.setStationNumberOfServers(serverKey, 1);
		model.updateNumOfServers(serverKey, 1);
        Exponential exp = new Exponential();
        exp.setMean(1);
        model.setServiceTimeDistribution(serverKey, classKey, exp);
    }

    /** Add a sink station to the model (like adding a Sink cell to teh JGraph in JSIM) */
    private void addSink(){
        sinkKey = model.addStation("Sink", STATION_TYPE_SINK, 1, new ArrayList<ServerType>());
    }

    private void addRouter(){
        routerKey = model.addStation("Router", STATION_TYPE_ROUTER, 1, new ArrayList<ServerType>());
        Object strategy = null;
        switch(simulation.getName()){
            case "RR":
                strategy = ROUTING_ROUNDROBIN;
        }
        model.setRoutingStrategy(routerKey, classKey, strategy);
    }

    
    /** Connect the components of model (like adding edges to the JGraph in JSIM) */
    private void setConnections(){
        if(isSingleQueue){
            model.setConnected(sourceKey, model.getStationKeys().get(0), true);
            model.setConnected(model.getStationKeys().get(0), sinkKey, true);
        }
        else{
            model.setConnected(sourceKey, routerKey, true);
            model.setConnected(routerKey, model.getStationKeys().get(0), true);
            model.setConnected(routerKey, model.getStationKeys().get(1), true);
            model.setConnected(routerKey, model.getStationKeys().get(2), true);
            model.setConnected(model.getStationKeys().get(0), sinkKey, true);
            model.setConnected(model.getStationKeys().get(1), sinkKey, true);
            model.setConnected(model.getStationKeys().get(2), sinkKey, true);
        }
    }

    /** Add all the metrics to control (like setting the parameters in JSIM) */
    private void addMeasure(){
        model.addMeasure(SimulationDefinition.MEASURE_AR, model.getStationKeys().get(0), classKey); //0
        //service??
        model.addMeasure(SimulationDefinition.MEASURE_RP, model.getStationKeys().get(0), classKey); //1
        model.addMeasure(SimulationDefinition.MEASURE_QT, model.getStationKeys().get(0), classKey); //2
        model.addMeasure(SimulationDefinition.MEASURE_X, model.getStationKeys().get(0), classKey); //3
        model.addMeasure(SimulationDefinition.MEASURE_QL, model.getStationKeys().get(0), classKey);  //4             
    }

    public CommonModel getModel(){
        return model;
    }

    //--------------- methods to get some parameters of the simulation --------------------
    public String getQueueStrategy(){
        return model.getQueueStrategy(model.getStationKeys().get(0), classKey);
    }

    public String getInterArrivalDistribution(){
        return ((Distribution) model.getClassDistribution(classKey)).toString();
    }

    public String getServiceDistribution(){
        return ((Distribution) model.getServiceTimeDistribution(model.getStationKeys().get(0), classKey)).toString();
    }

    public double getServiceTimeMean(){
        return ((Distribution) model.getServiceTimeDistribution(model.getStationKeys().get(0), classKey)).getMean();
    }


    //--------------- methods to change the parameters of the simulation ------------------
    
    /* To change the type of simulation and the algorithm */
    private void setStrategy(){
        String strategy = "";
        String algorithm = simulation.getName();

        switch(simulation.getType()){ //setting the correct string of strategy and algorithm from CommonConstants
            case NON_PREEMPTIVE:
                strategy = STATION_QUEUE_STRATEGY_NON_PREEMPTIVE;
                break;
            case PROCESSOR_SHARING:
                strategy = STATION_QUEUE_STRATEGY_PSSERVER;
                break; 
            case ROUTING:
                strategy = STATION_QUEUE_STRATEGY_NON_PREEMPTIVE;
                algorithm = QUEUE_STRATEGY_FCFS;
            default: 
                break;
        }

        int lenght = isSingleQueue ? 1 : 3;
        for(int i = 0; i < lenght; i++){
            Object serverKey = model.getStationKeys().get(i);
            model.setStationQueueStrategy(serverKey, strategy);
            model.setQueueStrategy(serverKey, classKey, algorithm);         
            model.setServiceWeight(serverKey, classKey, Defaults.getAsDouble("serviceWeight"));
            model.updateBalkingParameter(serverKey, classKey, strategy); 
        }
    }

    /**
     * Update the simulation of the solver, this method also updates all the solver accordingly to the new simulation
     * @param sim the new simulation
     * @param indexInter index of the new inter arrival time distribution
     * @param indexService index of the new service time distribution
     * @param nservers new number of servers
     */
    public void updateSolver(Simulation sim, int indexInter, int indexService, int nservers){
        simulation = sim;
        setStrategy();

        model.setClassDistribution(classKey, distributions[indexInter]);

        int lenght = isSingleQueue ? 1 : 3;
        for(int i = 0; i < lenght; i++){
            Object serverKey = model.getStationKeys().get(i);
            model.setServiceTimeDistribution(serverKey, classKey, distributions[indexService]);

            model.setStationNumberOfServers(serverKey, nservers);
            model.updateNumOfServers(serverKey, nservers);
        }
    }
    
}
