
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RoleLimitFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="RoleLimitFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="EffectiveDate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="MaxUnitsPerTime"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="RoleObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "RoleLimitFieldType")
@XmlEnum
public enum RoleLimitFieldType {

    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("EffectiveDate")
    EFFECTIVE_DATE("EffectiveDate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("MaxUnitsPerTime")
    MAX_UNITS_PER_TIME("MaxUnitsPerTime"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("RoleObjectId")
    ROLE_OBJECT_ID("RoleObjectId");
    private final String value;

    RoleLimitFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RoleLimitFieldType fromValue(String v) {
        for (RoleLimitFieldType c: RoleLimitFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
