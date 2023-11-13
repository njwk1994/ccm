
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>UnitOfMeasureFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="UnitOfMeasureFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Abbreviation"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="SequenceNumber"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "UnitOfMeasureFieldType")
@XmlEnum
public enum UnitOfMeasureFieldType {

    @XmlEnumValue("Abbreviation")
    ABBREVIATION("Abbreviation"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("SequenceNumber")
    SEQUENCE_NUMBER("SequenceNumber");
    private final String value;

    UnitOfMeasureFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UnitOfMeasureFieldType fromValue(String v) {
        for (UnitOfMeasureFieldType c: UnitOfMeasureFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
