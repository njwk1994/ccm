
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.*;


/**
 * <p>ExportProject complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="ExportProject"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Encoding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="FileType" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}FileTypeType" minOccurs="0"/&gt;
 *         &lt;element name="LineSeparator" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}LineSeparator" minOccurs="0"/&gt;
 *         &lt;element name="ProjectObjectId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="SpreadPeriodType" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}SpreadPeriodType" minOccurs="0"/&gt;
 *         &lt;element name="Spacing" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="BusinessObjectOptions" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}BusinessObjectOptions" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExportProject", propOrder = {
    "encoding",
    "fileType",
    "lineSeparator",
    "projectObjectId",
    "spreadPeriodType",
    "spacing",
    "businessObjectOptions"
})
public class ExportProject {

    @XmlElement(name = "Encoding")
    protected String encoding;
    @XmlElement(name = "FileType")
    @XmlSchemaType(name = "string")
    protected FileTypeType fileType;
    @XmlElement(name = "LineSeparator")
    @XmlSchemaType(name = "string")
    protected LineSeparator lineSeparator;
    @XmlElement(name = "ProjectObjectId")
    protected int projectObjectId;
    @XmlElement(name = "SpreadPeriodType")
    @XmlSchemaType(name = "string")
    protected SpreadPeriodType spreadPeriodType;
    @XmlElement(name = "Spacing")
    protected String spacing;
    @XmlElement(name = "BusinessObjectOptions")
    protected BusinessObjectOptions businessObjectOptions;

    /**
     * 获取encoding属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * 设置encoding属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEncoding(String value) {
        this.encoding = value;
    }

    /**
     * 获取fileType属性的值。
     *
     * @return
     *     possible object is
     *     {@link FileTypeType }
     *
     */
    public FileTypeType getFileType() {
        return fileType;
    }

    /**
     * 设置fileType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link FileTypeType }
     *
     */
    public void setFileType(FileTypeType value) {
        this.fileType = value;
    }

    /**
     * 获取lineSeparator属性的值。
     *
     * @return
     *     possible object is
     *     {@link LineSeparator }
     *
     */
    public LineSeparator getLineSeparator() {
        return lineSeparator;
    }

    /**
     * 设置lineSeparator属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link LineSeparator }
     *
     */
    public void setLineSeparator(LineSeparator value) {
        this.lineSeparator = value;
    }

    /**
     * 获取projectObjectId属性的值。
     *
     */
    public int getProjectObjectId() {
        return projectObjectId;
    }

    /**
     * 设置projectObjectId属性的值。
     *
     */
    public void setProjectObjectId(int value) {
        this.projectObjectId = value;
    }

    /**
     * 获取spreadPeriodType属性的值。
     *
     * @return
     *     possible object is
     *     {@link SpreadPeriodType }
     *
     */
    public SpreadPeriodType getSpreadPeriodType() {
        return spreadPeriodType;
    }

    /**
     * 设置spreadPeriodType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link SpreadPeriodType }
     *
     */
    public void setSpreadPeriodType(SpreadPeriodType value) {
        this.spreadPeriodType = value;
    }

    /**
     * 获取spacing属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSpacing() {
        return spacing;
    }

    /**
     * 设置spacing属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSpacing(String value) {
        this.spacing = value;
    }

    /**
     * 获取businessObjectOptions属性的值。
     *
     * @return
     *     possible object is
     *     {@link BusinessObjectOptions }
     *
     */
    public BusinessObjectOptions getBusinessObjectOptions() {
        return businessObjectOptions;
    }

    /**
     * 设置businessObjectOptions属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BusinessObjectOptions }
     *
     */
    public void setBusinessObjectOptions(BusinessObjectOptions value) {
        this.businessObjectOptions = value;
    }

}
