
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ProjectCodeFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ProjectCodeFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CodeTypeName"/&gt;
 *     &lt;enumeration value="CodeTypeObjectId"/&gt;
 *     &lt;enumeration value="CodeValue"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="Description"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="ParentObjectId"/&gt;
 *     &lt;enumeration value="SequenceNumber"/&gt;
 *     &lt;enumeration value="Weight"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ProjectCodeFieldType")
@XmlEnum
public enum ProjectCodeFieldType {

    @XmlEnumValue("CodeTypeName")
    CODE_TYPE_NAME("CodeTypeName"),
    @XmlEnumValue("CodeTypeObjectId")
    CODE_TYPE_OBJECT_ID("CodeTypeObjectId"),
    @XmlEnumValue("CodeValue")
    CODE_VALUE("CodeValue"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("Description")
    DESCRIPTION("Description"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("ParentObjectId")
    PARENT_OBJECT_ID("ParentObjectId"),
    @XmlEnumValue("SequenceNumber")
    SEQUENCE_NUMBER("SequenceNumber"),
    @XmlEnumValue("Weight")
    WEIGHT("Weight");
    private final String value;

    ProjectCodeFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProjectCodeFieldType fromValue(String v) {
        for (ProjectCodeFieldType c: ProjectCodeFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
