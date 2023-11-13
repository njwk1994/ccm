package ccm.server.schema.model;

import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.relCollectionTypes;
import ccm.server.schema.interfaces.IRel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Data
@Slf4j
public class relEndObj {
    private String obid;
    private String uid;
    private String name;
    private String domainUid;
    private String classDefinitionUid;

    public boolean isHint(String propertyDef, String value) {
        if (StringUtils.isEmpty(propertyDef))
            propertyDef = propertyDefinitionType.OBID.toString();
        if (propertyDef.equalsIgnoreCase(propertyDefinitionType.OBID.toString()))
            return obid.equalsIgnoreCase(value);
        else if (propertyDef.equalsIgnoreCase(propertyDefinitionType.Name.toString()))
            return name.equalsIgnoreCase(value);
        else if (propertyDef.equalsIgnoreCase(propertyDefinitionType.UID.toString()))
            return uid.equalsIgnoreCase(value);
        else if (propertyDef.equalsIgnoreCase(propertyDefinitionType.ClassDefinitionUID.toString()))
            return classDefinitionUid.equalsIgnoreCase(value);
        else if (propertyDef.equalsIgnoreCase(propertyDefinitionType.DomainUID.toString()))
            return domainUid.equalsIgnoreCase(value);
        return false;
    }

    public relEndObj(IRel rel, relCollectionTypes collectionTypes) throws Exception {
        if (rel != null) {
            if (collectionTypes == null)
                collectionTypes = relCollectionTypes.End1s;
            switch (collectionTypes) {
                case End1s:
                    this.obid = rel.OBID1();
                    this.uid = rel.UID1();
                    this.classDefinitionUid = rel.ClassDefinitionUID1();
                    this.name = rel.Name1();
                    this.domainUid = rel.DomainUID1();
                    break;
                case End2s:
                    this.obid = rel.OBID2();
                    this.uid = rel.UID2();
                    this.classDefinitionUid = rel.ClassDefinitionUID2();
                    this.name = rel.Name2();
                    this.domainUid = rel.DomainUID2();
                    break;
            }
        }
    }
}
