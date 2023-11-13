
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>DownloadFilesResponse complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="DownloadFilesResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NumberOfFiles" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DownloadFilesResponse", propOrder = {
    "numberOfFiles"
})
public class DownloadFilesResponse {

    @XmlElement(name = "NumberOfFiles")
    protected int numberOfFiles;

    /**
     * 获取numberOfFiles属性的值。
     *
     */
    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    /**
     * 设置numberOfFiles属性的值。
     *
     */
    public void setNumberOfFiles(int value) {
        this.numberOfFiles = value;
    }

}
