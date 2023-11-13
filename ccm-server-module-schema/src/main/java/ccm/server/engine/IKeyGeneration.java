package ccm.server.engine;

import ccm.server.enums.OBIDSequenceTypes;

public interface IKeyGeneration {
    String getNextOBID(OBIDSequenceTypes obidSequenceTypes);
}
