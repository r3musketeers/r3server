package raas.r3server.smr.delivery;

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
