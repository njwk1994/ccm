
package ccm.server.ws.p6.project;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>AssignProjectAsBaselineResponse complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="AssignProjectAsBaselineResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BaselineProjectObjectId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssignProjectAsBaselineResponse", propOrder = {
    "baselineProjectObjectId"
})
public class AssignProjectAsBaselineResponse {

    @XmlElement(name = "BaselineProjectObjectId")
    protected int baselineProjectObjectId;

    /**
     * 获取baselineProjectObjectId属性的值。
     *
     */
    public int getBaselineProjectObjectId() {
        return baselineProjectObjectId;
    }

    /**
     * 设置baselineProjectObjectId属性的值。
     *
     */
    public void setBaselineProjectObjectId(int value) {
        this.baselineProjectObjectId = value;
    }

}
