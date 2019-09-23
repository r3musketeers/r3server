package raas.r3server.performance.control;

import java.util.ArrayList;

public class ThroughputController {

    private int current_total_requisitions;
    private final ArrayList<Integer> flow_per_sec;
    private static ThroughputController instance;
    
    public static ThroughputController getInstance(){
        if(instance == null)
            instance = new ThroughputController();
        return instance;
    }

    private ThroughputController() {
        this.current_total_requisitions = 0;
        this.flow_per_sec = new ArrayList<Integer>();
    }

    public synchronized void addRequisition() {
        this.current_total_requisitions++;
    }

    public synchronized int addFlow() {
        this.flow_per_sec.add(current_total_requisitions);
        this.current_total_requisitions = 0;
        return flow_per_sec.get(flow_per_sec.size() - 1);
    }

    public void printFlows() {
        for (int flow : flow_per_sec) {
            System.out.println(flow);
        }
    }
}