package raas.r3server.smr.delivery;

import java.io.IOException;

/**
 *
 * @author paola
 */
public class DeliveryFactory {

    public static ConsensusDelivery createDelivery(String protocol, CommandListener listener, int replicaId) throws IOException {
        switch (protocol) {
            case "bftsmart":
                return new BFTDelivery(listener, replicaId);
            case "spaxos":
                return new SDelivery(listener, replicaId);
        }
        return null;
    }
}
