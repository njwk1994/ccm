
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>UDFTypeFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="UDFTypeFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="DataType"/&gt;
 *     &lt;enumeration value="IsCalculated"/&gt;
 *     &lt;enumeration value="IsConditional"/&gt;
 *     &lt;enumeration value="IsSecureCode"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="SubjectArea"/&gt;
 *     &lt;enumeration value="Title"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "UDFTypeFieldType")
@XmlEnum
public enum UDFTypeFieldType {

    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("DataType")
    DATA_TYPE("DataType"),
    @XmlEnumValue("IsCalculated")
    IS_CALCULATED("IsCalculated"),
    @XmlEnumValue("IsConditional")
    IS_CONDITIONAL("IsConditional"),
    @XmlEnumValue("IsSecureCode")
    IS_SECURE_CODE("IsSecureCode"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("SubjectArea")
    SUBJECT_AREA("SubjectArea"),
    @XmlEnumValue("Title")
    TITLE("Title");
    private final String value;

    UDFTypeFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UDFTypeFieldType fromValue(String v) {
        for (UDFTypeFieldType c: UDFTypeFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
