
package ccm.server.ws.p6.project;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>getFieldLengthProject complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="getFieldLengthProject"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Field" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getFieldLengthProject", propOrder = {
    "field"
})
public class GetFieldLengthProject {

    @XmlElement(name = "Field", required = true)
    protected String field;

    /**
     * 获取field属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getField() {
        return field;
    }

    /**
     * 设置field属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setField(String value) {
        this.field = value;
    }

}
