
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>FileTypeType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="FileTypeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="GZIP"/&gt;
 *     &lt;enumeration value="XML"/&gt;
 *     &lt;enumeration value="ZIP"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "FileTypeType")
@XmlEnum
public enum FileTypeType {

    GZIP,
    XML,
    ZIP;

    public String value() {
        return name();
    }

    public static FileTypeType fromValue(String v) {
        return valueOf(v);
    }

}
