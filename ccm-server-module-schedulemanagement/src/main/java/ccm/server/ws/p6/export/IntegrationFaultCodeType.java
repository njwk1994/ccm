
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>IntegrationFaultCodeType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="IntegrationFaultCodeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="General"/&gt;
 *     &lt;enumeration value="Presentation"/&gt;
 *     &lt;enumeration value="Network"/&gt;
 *     &lt;enumeration value="Server"/&gt;
 *     &lt;enumeration value="Business Rules"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "IntegrationFaultCodeType", namespace = "http://xmlns.oracle.com/Primavera/P6/WS/IntegrationFaultType/V1")
@XmlEnum
public enum IntegrationFaultCodeType {

    @XmlEnumValue("General")
    GENERAL("General"),
    @XmlEnumValue("Presentation")
    PRESENTATION("Presentation"),
    @XmlEnumValue("Network")
    NETWORK("Network"),
    @XmlEnumValue("Server")
    SERVER("Server"),
    @XmlEnumValue("Business Rules")
    BUSINESS_RULES("Business Rules");
    private final String value;

    IntegrationFaultCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IntegrationFaultCodeType fromValue(String v) {
        for (IntegrationFaultCodeType c: IntegrationFaultCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
