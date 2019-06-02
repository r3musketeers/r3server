/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r3libserver.smr.delivery;

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
