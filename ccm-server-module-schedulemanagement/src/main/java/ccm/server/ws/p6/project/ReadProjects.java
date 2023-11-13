
package ccm.server.ws.p6.project;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>ReadProjects complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="ReadProjects"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Field" type="{http://xmlns.oracle.com/Primavera/P6/WS/Project/V2}ProjectFieldType" maxOccurs="unbounded"/&gt;
 *         &lt;element name="Filter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OrderBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadProjects", propOrder = {
    "field",
    "filter",
    "orderBy"
})
public class ReadProjects {

    @XmlElement(name = "Field", required = true)
    @XmlSchemaType(name = "string")
    protected List<ProjectFieldType> field;
    @XmlElement(name = "Filter")
    protected String filter;
    @XmlElement(name = "OrderBy")
    protected String orderBy;

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
     * {@link ProjectFieldType }
     *
     *
     */
    public List<ProjectFieldType> getField() {
        if (field == null) {
            field = new ArrayList<ProjectFieldType>();
        }
        return this.field;
    }

    /**
     * 获取filter属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFilter() {
        return filter;
    }

    /**
     * 设置filter属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFilter(String value) {
        this.filter = value;
    }

    /**
     * 获取orderBy属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * 设置orderBy属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrderBy(String value) {
        this.orderBy = value;
    }

}
