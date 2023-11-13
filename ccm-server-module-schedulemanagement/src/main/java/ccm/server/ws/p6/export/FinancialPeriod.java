
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>FinancialPeriod complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="FinancialPeriod"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Include" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Field" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}FinancialPeriodFieldType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialPeriod", propOrder = {
    "include",
    "field"
})
public class FinancialPeriod {

    @XmlElement(name = "Include")
    protected Boolean include;
    @XmlElement(name = "Field", required = true)
    @XmlSchemaType(name = "string")
    protected List<FinancialPeriodFieldType> field;

    /**
     * 获取include属性的值。
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isInclude() {
        return include;
    }

    /**
     * 设置include属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setInclude(Boolean value) {
        this.include = value;
    }

    /**
     * Gets the value of the field property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the field property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getField().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FinancialPeriodFieldType }
     *
     *
     */
    public List<FinancialPeriodFieldType> getField() {
        if (field == null) {
            field = new ArrayList<FinancialPeriodFieldType>();
        }
        return this.field;
    }

}
