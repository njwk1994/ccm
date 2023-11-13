
package ccm.server.ws.p6.project;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>AssignProjectAsBaseline complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="AssignProjectAsBaseline"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OriginalProjectObjectId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="TargetProjectObjectId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssignProjectAsBaseline", propOrder = {
    "originalProjectObjectId",
    "targetProjectObjectId"
})
public class AssignProjectAsBaseline {

    @XmlElement(name = "OriginalProjectObjectId")
    protected int originalProjectObjectId;
    @XmlElement(name = "TargetProjectObjectId")
    protected int targetProjectObjectId;

    /**
     * 获取originalProjectObjectId属性的值。
     *
     */
    public int getOriginalProjectObjectId() {
        return originalProjectObjectId;
    }

    /**
     * 设置originalProjectObjectId属性的值。
     *
     */
    public void setOriginalProjectObjectId(int value) {
        this.originalProjectObjectId = value;
    }

    /**
     * 获取targetProjectObjectId属性的值。
     *
     */
    public int getTargetProjectObjectId() {
        return targetProjectObjectId;
    }

    /**
     * 设置targetProjectObjectId属性的值。
     *
     */
    public void setTargetProjectObjectId(int value) {
        this.targetProjectObjectId = value;
    }

}
