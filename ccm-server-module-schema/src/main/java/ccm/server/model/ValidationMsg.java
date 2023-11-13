package ccm.server.model;

import ccm.server.helper.HardCodeHelper;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ValidationMsg {
    private String obid;
    private String uid;
    private String uniqueKey;
    private String name;
    private String message;

    public String getMessage() {
        if (this.message == null)
            this.message = "";
        return this.message;
    }

    private IObject failedObject;

    public ValidationMsg(IObject object, String message) {
        this.setFailedObject(object);
        this.setMessage(message);
        try {
            if (object != null) {
                if (object.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                    IRel rel = object.toInterface(IRel.class);
                    this.setObid(rel.OBID1() + "," + rel.OBID2());
                    this.setUid(rel.UID1() + "," + rel.UID1());
                    this.setUniqueKey(rel.generateUniqueKey());
                    this.setName(rel.Name1() + "," + rel.Name2());
                } else {
                    this.setObid(object.OBID());
                    this.setUid(object.UID());
                    this.setUniqueKey(object.UniqueKey());
                    this.setName(object.Name());
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
}
