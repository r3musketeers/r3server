/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r3libserver.smr.delivery;

/**
 *
 * @author paola
 */
public abstract class ConsensusDelivery {

    protected final CommandListener listener;
    protected final int replicaId;

    public ConsensusDelivery(CommandListener listener, int replicaId) {
        this.listener = listener;
        this.replicaId = replicaId;
    }

    public abstract void start();

    public abstract void configure();


}
