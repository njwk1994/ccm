
package ccm.server.ws.p6.project;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>IsProjectLocked complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="IsProjectLocked"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ObjectId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="IncludeCurrentSession" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IsProjectLocked", propOrder = {
    "objectId",
    "includeCurrentSession"
})
public class IsProjectLocked {

    @XmlElement(name = "ObjectId")
    protected int objectId;
    @XmlElement(name = "IncludeCurrentSession")
    protected boolean includeCurrentSession;

    /**
     * 获取objectId属性的值。
     *
     */
    public int getObjectId() {
        return objectId;
    }

    /**
     * 设置objectId属性的值。
     *
     */
    public void setObjectId(int value) {
        this.objectId = value;
    }

    /**
     * 获取includeCurrentSession属性的值。
     *
     */
    public boolean isIncludeCurrentSession() {
        return includeCurrentSession;
    }

    /**
     * 设置includeCurrentSession属性的值。
     *
     */
    public void setIncludeCurrentSession(boolean value) {
        this.includeCurrentSession = value;
    }

}
