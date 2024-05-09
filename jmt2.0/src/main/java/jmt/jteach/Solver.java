package jmt.jteach;

import java.util.ArrayList;

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

    private int classNameIndex = 1;

    //------------------- keys of the model --------------------------
    private Object classKey;
    private Object serverKey;
    private Object sourceKey;
    private Object sinkKey;

    //----------- all changable fields of the simulation -------------
    private static final String[] queueStrategies = {
        QUEUE_STRATEGY_FCFS,
        QUEUE_STRATEGY_LCFS,
        QUEUE_STRATEGY_SJF,
        QUEUE_STRATEGY_LJF,
    };
    private static Distribution[] distributions = { //for now the same distributions for interarrival time and service time
        new Exponential(),
        new Deterministic(),
        new Uniform(),
        new Hyperexponential()
    };


    //types of measures selectable
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
     * Create a Common model with a source, a queue, a sink all connected togheter.
     * Only one class.
     * Set also the parameters to look at in the simulation.
     */
    public Solver(){
        model = new CommonModel();
        setDistributionsParameters();
        addClass();
        addSource();
        addQueue();    
        addSink();
        setConnections();   
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
        serverKey = model.addStation("Queue", STATION_TYPE_SERVER, 1, new ArrayList<ServerType>());

        //input panel
        model.setStationQueueCapacity(serverKey, 5);
        model.setDropRule(serverKey, classKey, "Drop");
        model.setStationQueueStrategy(serverKey, STATION_QUEUE_STRATEGY_NON_PREEMPTIVE);
        model.setQueueStrategy(serverKey, classKey, Defaults.get("stationQueueStrategy"));
        model.setServiceWeight(serverKey, classKey, Defaults.getAsDouble("serviceWeight"));
        model.updateBalkingParameter(serverKey, classKey, STATION_QUEUE_STRATEGY_NON_PREEMPTIVE);
        
        //service panel
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

    
    /** Connect the components of model (like adding edges to the JGraph in JSIM) */
    private void setConnections(){
        model.setConnected(sourceKey, serverKey, true);
        model.setConnected(serverKey, sinkKey, true);
    }

    /** Add all the metrics to control (like setting the parameters in JSIM) */
    private void addMeasure(){
        model.addMeasure(SimulationDefinition.MEASURE_AR, serverKey, classKey); //0
        //service??
        model.addMeasure(SimulationDefinition.MEASURE_RP, serverKey, classKey); //1
        model.addMeasure(SimulationDefinition.MEASURE_QT, serverKey, classKey); //2
        model.addMeasure(SimulationDefinition.MEASURE_X, serverKey, classKey); //3
        model.addMeasure(SimulationDefinition.MEASURE_QL, serverKey, classKey);  //4             
    }

    public CommonModel getModel(){
        return model;
    }

    //--------------- methods to get some parameters of the simulation --------------------
    public String getQueueStrategy(){
        return model.getQueueStrategy(serverKey, classKey);
    }

    public String getInterArrivalDistribution(){
        return ((Distribution) model.getClassDistribution(classKey)).toString();
    }

    public String getServiceDistribution(){
        return ((Distribution) model.getServiceTimeDistribution(serverKey, classKey)).toString();
    }

    public double getServiceTimeMean(){
        return ((Distribution) model.getServiceTimeDistribution(serverKey, classKey)).getMean();
    }


    //--------------- methods to change the parameters of the simulation ------------------

    public void setQueueStrategy(int index){
        model.setQueueStrategy(serverKey, classKey, queueStrategies[index]);
    }

    public void setInterArrivalTime(int index){
        model.setClassDistribution(classKey, distributions[index]);
    }

    public void setServiceTime(int index){
        model.setServiceTimeDistribution(serverKey, classKey, distributions[index]);
    }

    public void setNumberOfServers(int value){
        model.setStationNumberOfServers(serverKey, value);
		model.updateNumOfServers(serverKey, value);
    }
    
}
