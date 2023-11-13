
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RiskFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="RiskFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Cause"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="Description"/&gt;
 *     &lt;enumeration value="Effect"/&gt;
 *     &lt;enumeration value="Exposure"/&gt;
 *     &lt;enumeration value="ExposureFinishDate"/&gt;
 *     &lt;enumeration value="ExposureStartDate"/&gt;
 *     &lt;enumeration value="Id"/&gt;
 *     &lt;enumeration value="IdentifiedByResourceId"/&gt;
 *     &lt;enumeration value="IdentifiedByResourceName"/&gt;
 *     &lt;enumeration value="IdentifiedByResourceObjectId"/&gt;
 *     &lt;enumeration value="IdentifiedDate"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="Note"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectName"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="ResourceId"/&gt;
 *     &lt;enumeration value="ResourceName"/&gt;
 *     &lt;enumeration value="ResourceObjectId"/&gt;
 *     &lt;enumeration value="ResponseTotalCost"/&gt;
 *     &lt;enumeration value="RiskCategoryName"/&gt;
 *     &lt;enumeration value="RiskCategoryObjectId"/&gt;
 *     &lt;enumeration value="Score"/&gt;
 *     &lt;enumeration value="ScoreColor"/&gt;
 *     &lt;enumeration value="ScoreText"/&gt;
 *     &lt;enumeration value="Status"/&gt;
 *     &lt;enumeration value="Type"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "RiskFieldType")
@XmlEnum
public enum RiskFieldType {

    @XmlEnumValue("Cause")
    CAUSE("Cause"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("Description")
    DESCRIPTION("Description"),
    @XmlEnumValue("Effect")
    EFFECT("Effect"),
    @XmlEnumValue("Exposure")
    EXPOSURE("Exposure"),
    @XmlEnumValue("ExposureFinishDate")
    EXPOSURE_FINISH_DATE("ExposureFinishDate"),
    @XmlEnumValue("ExposureStartDate")
    EXPOSURE_START_DATE("ExposureStartDate"),
    @XmlEnumValue("Id")
    ID("Id"),
    @XmlEnumValue("IdentifiedByResourceId")
    IDENTIFIED_BY_RESOURCE_ID("IdentifiedByResourceId"),
    @XmlEnumValue("IdentifiedByResourceName")
    IDENTIFIED_BY_RESOURCE_NAME("IdentifiedByResourceName"),
    @XmlEnumValue("IdentifiedByResourceObjectId")
    IDENTIFIED_BY_RESOURCE_OBJECT_ID("IdentifiedByResourceObjectId"),
    @XmlEnumValue("IdentifiedDate")
    IDENTIFIED_DATE("IdentifiedDate"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("Note")
    NOTE("Note"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectName")
    PROJECT_NAME("ProjectName"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("ResourceId")
    RESOURCE_ID("ResourceId"),
    @XmlEnumValue("ResourceName")
    RESOURCE_NAME("ResourceName"),
    @XmlEnumValue("ResourceObjectId")
    RESOURCE_OBJECT_ID("ResourceObjectId"),
    @XmlEnumValue("ResponseTotalCost")
    RESPONSE_TOTAL_COST("ResponseTotalCost"),
    @XmlEnumValue("RiskCategoryName")
    RISK_CATEGORY_NAME("RiskCategoryName"),
    @XmlEnumValue("RiskCategoryObjectId")
    RISK_CATEGORY_OBJECT_ID("RiskCategoryObjectId"),
    @XmlEnumValue("Score")
    SCORE("Score"),
    @XmlEnumValue("ScoreColor")
    SCORE_COLOR("ScoreColor"),
    @XmlEnumValue("ScoreText")
    SCORE_TEXT("ScoreText"),
    @XmlEnumValue("Status")
    STATUS("Status"),
    @XmlEnumValue("Type")
    TYPE("Type");
    private final String value;

    RiskFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RiskFieldType fromValue(String v) {
        for (RiskFieldType c: RiskFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
