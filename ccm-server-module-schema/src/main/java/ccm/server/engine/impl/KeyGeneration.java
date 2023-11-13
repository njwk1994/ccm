package ccm.server.engine.impl;

import ccm.server.engine.IKeyGeneration;
import ccm.server.enums.OBIDSequenceTypes;
import ccm.server.util.ReentrantLockUtility;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service("keyGeneration")
@Slf4j
public class KeyGeneration implements IKeyGeneration {

    private final DefaultIdentifierGenerator defaultIdentifierGenerator = new DefaultIdentifierGenerator();

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public String getNextOBID(OBIDSequenceTypes obidSequenceTypes) {
        String lstrOBID = "";
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            lstrOBID = defaultIdentifierGenerator.nextId(null).toString();
        } catch (Exception exception) {
            log.error("get next OBID acquire lock failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }

        if (StringUtils.isEmpty(lstrOBID))
            lstrOBID = UUID.randomUUID().toString();

        switch (obidSequenceTypes) {
            case ObjectOBIDs:
                lstrOBID = "OBJ" + lstrOBID;
                break;
            case PropertyOBIDs:
                lstrOBID = "OBJPR" + lstrOBID;
                break;
            case InterfaceOBIDs:
                lstrOBID = "OBJIF" + lstrOBID;
                break;
            case RelOBIDs:
                lstrOBID = "REL" + lstrOBID;
                break;
        }
        return lstrOBID;
    }
}
