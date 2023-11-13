
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>LineSeparator的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="LineSeparator"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Windows"/&gt;
 *     &lt;enumeration value="Unix"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "LineSeparator")
@XmlEnum
public enum LineSeparator {

    @XmlEnumValue("Windows")
    WINDOWS("Windows"),
    @XmlEnumValue("Unix")
    UNIX("Unix");
    private final String value;

    LineSeparator(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LineSeparator fromValue(String v) {
        for (LineSeparator c: LineSeparator.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
