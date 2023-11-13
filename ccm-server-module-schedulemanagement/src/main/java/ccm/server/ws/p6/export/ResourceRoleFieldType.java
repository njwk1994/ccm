
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ResourceRoleFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ResourceRoleFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Proficiency"/&gt;
 *     &lt;enumeration value="ResourceId"/&gt;
 *     &lt;enumeration value="ResourceName"/&gt;
 *     &lt;enumeration value="ResourceObjectId"/&gt;
 *     &lt;enumeration value="RoleId"/&gt;
 *     &lt;enumeration value="RoleName"/&gt;
 *     &lt;enumeration value="RoleObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ResourceRoleFieldType")
@XmlEnum
public enum ResourceRoleFieldType {

    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Proficiency")
    PROFICIENCY("Proficiency"),
    @XmlEnumValue("ResourceId")
    RESOURCE_ID("ResourceId"),
    @XmlEnumValue("ResourceName")
    RESOURCE_NAME("ResourceName"),
    @XmlEnumValue("ResourceObjectId")
    RESOURCE_OBJECT_ID("ResourceObjectId"),
    @XmlEnumValue("RoleId")
    ROLE_ID("RoleId"),
    @XmlEnumValue("RoleName")
    ROLE_NAME("RoleName"),
    @XmlEnumValue("RoleObjectId")
    ROLE_OBJECT_ID("RoleObjectId");
    private final String value;

    ResourceRoleFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResourceRoleFieldType fromValue(String v) {
        for (ResourceRoleFieldType c: ResourceRoleFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
