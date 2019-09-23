package raas.r3server.smr.delivery;

import raas.r3lib.communication.Command;
import raas.r3lib.communication.Response;

/**
 *
 * @author paola
 */
public interface CommandListener {
    
    public Response listenCommand(Command cmd);
    
}
