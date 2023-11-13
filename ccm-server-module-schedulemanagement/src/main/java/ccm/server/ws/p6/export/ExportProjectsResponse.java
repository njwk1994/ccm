
package ccm.server.ws.p6.export;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.*;


/**
 * <p>ExportProjectsResponse complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="ExportProjectsResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ProjectData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExportProjectsResponse", propOrder = {
    "projectData"
})
public class ExportProjectsResponse {

    @XmlElement(name = "ProjectData", required = true)
    @XmlMimeType("application/*")
    protected DataHandler projectData;

    /**
     * 获取projectData属性的值。
     *
     * @return
     *     possible object is
     *     {@link DataHandler }
     *
     */
    public DataHandler getProjectData() {
        return projectData;
    }

    /**
     * 设置projectData属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *
     */
    public void setProjectData(DataHandler value) {
        this.projectData = value;
    }

}
