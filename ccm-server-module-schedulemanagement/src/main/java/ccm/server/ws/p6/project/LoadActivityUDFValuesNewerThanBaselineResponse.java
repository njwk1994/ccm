
package ccm.server.ws.p6.project;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>LoadActivityUDFValuesNewerThanBaselineResponse complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="LoadActivityUDFValuesNewerThanBaselineResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="UDFValueObjectIds" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LoadActivityUDFValuesNewerThanBaselineResponse", propOrder = {
    "udfValueObjectIds"
})
public class LoadActivityUDFValuesNewerThanBaselineResponse {

    @XmlElement(name = "UDFValueObjectIds", required = true)
    protected List<String> udfValueObjectIds;

    /**
     * Gets the value of the udfValueObjectIds property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the udfValueObjectIds property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUDFValueObjectIds().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getUDFValueObjectIds() {
        if (udfValueObjectIds == null) {
            udfValueObjectIds = new ArrayList<String>();
        }
        return this.udfValueObjectIds;
    }

}
