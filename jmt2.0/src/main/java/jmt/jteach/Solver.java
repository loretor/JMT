package jmt.jteach;

import jmt.gui.common.CommonConstants;
import jmt.gui.common.Defaults;
import jmt.gui.common.definitions.CommonModel;

/**
 * @author Lorenzo Torri
 * Date: 06-mag-2024
 * Time: 17.28
 */
public class Solver implements CommonConstants{
    private CommonModel model;

    private int classNameIndex = 1;

    private Object classKey;
    private Object serverKey;

    public Solver(){
        model = new CommonModel();
    }

    public void addClass(){
        classKey = model.addClass(Defaults.get("className") + (++classNameIndex),
            Defaults.getAsInteger("classType").intValue(), //posso anche passare 0 come tipo dato che è open
            Defaults.getAsInteger("classPriority"),
            Defaults.getAsDouble("classSoftDeadline"),
            Defaults.getAsInteger("classPopulation"), 
            Defaults.getAsNewInstance("classDistribution")); //qua andremo a passare direttamente la tipologia di distribuzione
    }

    public void createServer(){
        serverKey = model.addStation("Station","Server", 0, null); //controlla meglio cosa siano 0 e null
        //fixed part
        model.setDropRule(serverKey, classKey, "Drop");
        model.setStationQueueCapacity(serverKey, 5);

        //changable part
        model.setStationNumberOfServers(serverKey, 1);
        model.setStationQueueStrategy(serverKey, STATION_QUEUE_STRATEGY_NON_PREEMPTIVE);
        model.setQueueStrategy(serverKey, classKey, QUEUE_STRATEGY_FCFS);       
        model.setServiceTimeDistribution(serverKey, classKey, Defaults.getAsNewInstance("stationDistribution")); //serve la distribuzione come ultimo parametro, gui.common.panels.ServiceSectionPanel usa editor.getResult() editor = DistributionsEditor
    }
    
}
